/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectdb2;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adrian
 */
public class Report {

    Connection fstConn,scdConn;
    String fstSchema, scdSchema;
    LinkedList<Table> fstConnTables, scdConnTables;
    LinkedList<String[]> fstConnProc, scdConnProc;
    HashMap<String,LinkedList<String>> fstConnParams,scdConnParams;
    
    public Report(Connection fstConn, Connection scdConn, String fstSchema, String scdSchema) {
        this.fstConn=fstConn;
        this.scdConn=scdConn;
        this.fstSchema=fstSchema;
        this.scdSchema=scdSchema;
        fstConnTables = new LinkedList<>();
        scdConnTables = new LinkedList<>();
        fstConnProc = new LinkedList<>();
        scdConnProc = new LinkedList<>();
        fstConnParams = new HashMap<>();
        scdConnParams = new HashMap<>();
    }
 
    
    public void generateReport(){
        obtainTableInfo(fstConn, fstSchema, fstConnTables);
        obtainProcInfo(fstConn, fstConnProc, fstConnParams);
        
        obtainTableInfo(scdConn, scdSchema, scdConnTables);
        obtainProcInfo(scdConn, scdConnProc, scdConnParams);
        Writer writer = null;
    
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream("Report.txt"), "utf-8")); //crea el archivo de los reportes en ProjectDB2
            writer.write("-------------------- Diferencias entre "+ DBComparator.fstSchema +" y " + DBComparator.scdSchema + "--------------------\n\n");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Report.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Report.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Report.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (int i = 0; i < fstConnTables.size(); i++) {
            Table aux = fstConnTables.get(i);
            String eq = "";
            for (int j = 0; j < scdConnTables.size(); j++) {
                eq += aux.checkDif(scdConnTables.get(j));
            }
            try {
                if(eq.equalsIgnoreCase("")){
                    writer.write("---------------------------------------\n");
                    writer.write("La tabla "+aux.getName()+" en "+fstSchema+" no se encuentra en "+scdSchema+"\n\n");
                }else{                         
                    writer.write("---------------------------------------\n");
                    writer.write("Información sobre la tabla: "+aux.getName()+" \n\n"+eq);
                }
            }catch (IOException ex) {
                Logger.getLogger(Report.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        for (int i = 0; i < scdConnTables.size(); i++) {
            Table aux = scdConnTables.get(i);
            boolean find = false;
            for (int j = 0; j < fstConnTables.size(); j++) {
                if (aux.getName().equalsIgnoreCase(fstConnTables.get(j).getName())){
                    find = true;
                }
            }
            if(!find){
                try {
                    writer.write("---------------------------------------\n");
                    writer.write("La tabla "+aux.getName()+" en "+scdSchema+" no se encuentra en "+fstSchema+"\n\n");
                } catch (IOException ex) {
                    Logger.getLogger(Report.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        try {
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(Report.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void obtainProcInfo(Connection conn, LinkedList<String[]> procs, HashMap<String,LinkedList<String>> params){
        try {
            String query = "SELECT routine_name, data_type as return_type, specific_name"
                    + " as unique_name FROM information_schema.routines WHERE specific_schema NOT IN"
                    + " ('pg_catalog', 'information_schema') AND type_udt_name != 'trigger';";
            
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet resultSetProcedure = statement.executeQuery();
            
            while (resultSetProcedure.next()) {
                String[] proc = new String[3];
                proc[0] = resultSetProcedure.getString("routine_name");
                proc[1] = resultSetProcedure.getString("return_type");
                proc[2] = resultSetProcedure.getString("unique_name");
                procs.add(proc);
                
                String param_query = "SELECT parameter_name as p_name, data_type as p_type FROM information_schema.parameters where specific_name='"+proc[2]+"';";
                PreparedStatement p_statement = conn.prepareStatement(param_query);
                ResultSet resultSetParam = p_statement.executeQuery();
                
                LinkedList<String> paramsObt = new LinkedList<>();
                while (resultSetParam.next()) {
                    paramsObt.add(resultSetParam.getString("p_name")+" "+resultSetParam.getString("p_type"));
                }
                if(paramsObt.size() != 0){
                    params.put(proc[2], paramsObt);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Report.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void obtainTableInfo(Connection conn, String schem, LinkedList<Table> tables) {
        Table tbl=null;
        try {
            String[] tipo = {"TABLE"};
            DatabaseMetaData metaData = conn.getMetaData();

            //ResultSet getTables(String catalog, String schemaPattern, String tableNamePattern, String[] types)
            ResultSet resultSetTables = metaData.getTables(null,schem, null, tipo);
            
            while (resultSetTables.next()) {
                String tableName = resultSetTables.getString("TABLE_NAME");
                
                tbl = new Table();
                tbl.setName(tableName);
                
                pkReport(tbl, schem, tableName, metaData);
                fkReport(tbl, schem, tableName, metaData);
                indexAndUniquekReport(tbl, schem, tableName, metaData);
                columnsReport(tbl, schem, tableName, metaData);
                triggerReport(tbl, schem, tableName,conn);
                
                tables.add(tbl);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Report.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void columnsReport(Table tbl, String schema, String tableName, DatabaseMetaData metaData){
        try {
            //getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern)
            ResultSet resultSetColumns = metaData.getColumns(null, schema, tableName,null);
            
            while (resultSetColumns.next()){
                String columnName = resultSetColumns.getString("COLUMN_NAME");
                String type = resultSetColumns.getString("TYPE_NAME");
                tbl.addColumn(columnName, type);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(Report.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void pkReport(Table tbl, String schema, String tableName, DatabaseMetaData metaData){
        try {
            //ResultSet getPrimaryKeys(String catalog,String schema,String table)
            ResultSet resutlSetPK = metaData.getPrimaryKeys(null, schema, tableName);
            
            while (resutlSetPK.next()){
                String columnName = resutlSetPK.getString("COLUMN_NAME");
                tbl.addPk(columnName);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Report.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void fkReport(Table tbl, String schema, String tableName, DatabaseMetaData metaData){
        try {
            //ResultSet getImportedKeys(String catalog,String schema,String table)
            ResultSet resultSetFK = metaData.getImportedKeys(null, schema, tableName);
            
            while (resultSetFK.next()){
                String columnName = resultSetFK.getString("PKCOLUMN_NAME");
                String tableRef = resultSetFK.getString("PKTABLE_NAME");
                tbl.addFk(columnName,tableRef);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Report.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void indexAndUniquekReport(Table tbl, String schema, String tableName, DatabaseMetaData metaData){
        try {
            //ResultSet getIndexInfo(String catalog,String schema,String table,boolean unique,boolean approximate)
            ResultSet resultSetIndex = metaData.getIndexInfo(null, schema, tableName, true, true);
            
            while (resultSetIndex.next()){
                String columnName = resultSetIndex.getString("COLUMN_NAME");
                String indexName = resultSetIndex.getString("INDEX_NAME");
                String unique = String.valueOf(!resultSetIndex.getBoolean("NON_UNIQUE"));
                String ascOrDesc = resultSetIndex.getString("ASC_OR_DESC");
                    
                if(notPk(columnName,tbl.getPks())) tbl.addUqk(columnName);
                    
                tbl.addIndex(indexName, unique, columnName, ascOrDesc);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Report.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    private void triggerReport(Table tbl, String schema, String tableName, Connection conn){
        try {
            String query = "SELECT tr.trigger_name as name, tr.action_timing as timing"
                    + ", tr.event_manipulation as condition, tr.event_object_table"
                    + " as on_table FROM information_schema.triggers tr WHERE "
                    + "tr.trigger_schema='"+schema+"' AND tr.event_object_table='"+tableName+"';";
            
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet resultSetTrigger = statement.executeQuery();
            
            while (resultSetTrigger.next()) {
                String name = resultSetTrigger.getString("name");
                String timing = resultSetTrigger.getString("timing");
                String condition = resultSetTrigger.getString("condition");
                String on_table = resultSetTrigger.getString("on_table");
                tbl.addTrigger(name,timing,condition,on_table);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Report.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public void showReport(){
        System.out.println("INFORMACION DE LA BASE DE DATOS - ESQUEMA: "+fstSchema);
        Table tbl;
        for (int i = 0; i < fstConnTables.size(); i++) {
            tbl = fstConnTables.get(i);
            System.out.println("\n");
            System.out.println("Nombre: "+tbl.getName());
            for (int j = 0; j < tbl.getColumns().size(); j++) {
                System.out.println("Atributo: "+tbl.getColumns().get(j)[0]+", type: "+tbl.getColumns().get(j)[1]);
            }
            for (int j = 0; j < tbl.getPks().size(); j++) {
                System.out.println("Clave/s primaria/s: "+tbl.getPks().get(j));
            }
            for (int j = 0; j < tbl.getFks().size(); j++) {
                String[] fk = tbl.getFks().get(j);
                System.out.println("Clave/s foranea/s: "+fk[0]+" aa "+fk[1]);
            }
            for (int j = 0; j < tbl.getUqks().size(); j++) {
                System.out.println("Clave/s unica/s: "+tbl.getUqks().get(j));
            }
            for (int j = 0; j < tbl.getIndexs().size(); j++) {
                String[] index = tbl.getIndexs().get(j);
                System.out.println("Indice - Nombre: "+index[0]+", Unique: "+index[1]+", Column name: "+index[2]+", ascOrDesc: "+index[3]);
            }
            for (int j = 0; j < tbl.getTriggers().size(); j++) {
                String[] trigger = tbl.getTriggers().get(j);
                System.out.println("Trigger - Nombre: "+trigger[0]+", Tiempo: "+trigger[1]+", Condicion: "+trigger[2]+", Sobre tabla: "+trigger[3]);
            }
        }
        System.out.println(" ");
        System.out.println("Procedimientos:");
        System.out.println(" ");
        for (int j = 0; j < fstConnProc.size(); j++) {
            String[] procInfo = fstConnProc.get(j);
            System.out.print("Nombre: "+procInfo[0]+", Tipo retorno: "+procInfo[1]+", Parametro/s: ");
            if(fstConnParams.get(procInfo[2]) != null){
                for (int k = 0; k < fstConnParams.get(procInfo[2]).size(); k++) {
                    System.out.print(fstConnParams.get(procInfo[2]).get(k)+" - ");
                }
            }else{
                System.out.print("---");
            }
            System.out.println("");
        }
        
        
        
        
        System.out.println("INFORMACION DE LA BASE DE DATOS - ESQUEMA: "+scdSchema);
        Table tbl2;
        for (int i = 0; i < scdConnTables.size(); i++) {
            tbl2 = scdConnTables.get(i);
            System.out.println("\n");
            System.out.println("Nombre: "+tbl2.getName());
            for (int j = 0; j < tbl2.getColumns().size(); j++) {
                System.out.println("Atributo: "+tbl2.getColumns().get(j)[0]+", type: "+tbl2.getColumns().get(j)[1]);
            }
            for (int j = 0; j < tbl2.getPks().size(); j++) {
                System.out.println("Clave/s primaria/s: "+tbl2.getPks().get(j));
            }
            for (int j = 0; j < tbl2.getFks().size(); j++) {
                String[] fk = tbl2.getFks().get(j);
                System.out.println("Clave/s foranea/s: "+fk[0]+" aa "+fk[1]);
            }
            for (int j = 0; j < tbl2.getUqks().size(); j++) {
                System.out.println("Clave/s unica/s: "+tbl2.getUqks().get(j));
            }
            for (int j = 0; j < tbl2.getIndexs().size(); j++) {
                String[] index = tbl2.getIndexs().get(j);
                System.out.println("Indice - Nombre: "+index[0]+", Unique: "+index[1]+", Column name: "+index[2]+", ascOrDesc: "+index[3]);
            }
            for (int j = 0; j < tbl2.getTriggers().size(); j++) {
                String[] trigger = tbl2.getTriggers().get(j);
                System.out.println("Trigger - Nombre: "+trigger[0]+", Tiempo: "+trigger[1]+", Condicion: "+trigger[2]+", Sobre tabla: "+trigger[3]);
            }
        }
        System.out.println(" ");
        System.out.println("Procedimientos:");
        System.out.println(" ");
        for (int j = 0; j < scdConnProc.size(); j++) {
            String[] procInfo = scdConnProc.get(j);
            System.out.print("Nombre: "+procInfo[0]+", Tipo retorno: "+procInfo[1]+", Parametro/s: ");
            if(scdConnParams.get(procInfo[2]) != null){
                for (int k = 0; k < scdConnParams.get(procInfo[2]).size(); k++) {
                    System.out.print(scdConnParams.get(procInfo[2]).get(k)+" - ");
                }
            }else{
                System.out.print("---");
            }
            System.out.println("");
        }
    }
    
    private boolean notPk(String colunmName, LinkedList<String> pks){
        boolean control = true;
        for (int k = 0; k < pks.size(); k++) {
            if (colunmName.equalsIgnoreCase(pks.get(k))) control = false;
        }
        return control;
    }
    
}