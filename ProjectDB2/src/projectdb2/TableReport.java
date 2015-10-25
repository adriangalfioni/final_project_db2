/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectdb2;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adrian
 */
public class TableReport {

    Connection fstConn,scdConn;
    String fstSchema, scdSchema;
    LinkedList<Table> fstConnTables, scdConnTables; 
    
    public TableReport(Connection fstConn, Connection scdConn, String fstSchema, String scdSchema) {
        this.fstConn=fstConn;
        this.scdConn=scdConn;
        this.fstSchema=fstSchema;
        this.scdSchema=scdSchema;
        fstConnTables = new LinkedList<Table>();
        scdConnTables = new LinkedList<Table>();
    }
 
    
    public void generateReport() {
        Table tbl=null;
        try {
            String[] tipo = {"TABLE"};
            DatabaseMetaData metaData = fstConn.getMetaData();

            //ResultSet getTables(String catalog, String schemaPattern, String tableNamePattern, String[] types)
            ResultSet resultSetTables = metaData.getTables(null,fstSchema, null, tipo);
            
            while (resultSetTables.next()) {
                String tableCatal = resultSetTables.getString("TABLE_CAT");
                String tableSchem = resultSetTables.getString("TABLE_SCHEM");
                String tableName = resultSetTables.getString("TABLE_NAME");
                
                tbl = new Table();
                tbl.setName(tableName);

                //ResultSet getPrimaryKeys(String catalog,String schema,String table)
                ResultSet resutlSetPK = metaData.getPrimaryKeys(tableCatal, tableSchem, tableName);
                
                //ResultSet getImportedKeys(String catalog,String schema,String table)
                ResultSet resultSetFK = metaData.getImportedKeys(null, fstSchema, tableName);
                
                //ResultSet getIndexInfo(String catalog,String schema,String table,boolean unique,boolean approximate)
                ResultSet resultSetIndex = metaData.getIndexInfo(null, fstSchema, tableName, true, true);
                
                //getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern)
                ResultSet resultSetColumns = metaData.getColumns(null, fstSchema, tableName,null);
                
                
                while (resutlSetPK.next()){
                    String columnName = resutlSetPK.getString("COLUMN_NAME");
                    tbl.addPk(columnName);
                }
                while (resultSetFK.next()){
                    String columnName = resultSetFK.getString("PKCOLUMN_NAME");
                    tbl.addFk(columnName);
                }
                while (resultSetIndex.next()){
                    String columnName = resultSetIndex.getString("COLUMN_NAME");
                    String indexName = resultSetIndex.getString("INDEX_NAME");
                    String unique = String.valueOf(!resultSetIndex.getBoolean("NON_UNIQUE"));
                    String ascOrDesc = resultSetIndex.getString("ASC_OR_DESC");
                    
                    if(notPk(columnName,tbl.getPks())) tbl.addUqk(columnName);
                    
                    tbl.addIndex(indexName, unique, columnName, ascOrDesc);
                }
                while (resultSetColumns.next()){
                    String columnName = resultSetColumns.getString("COLUMN_NAME");
                    String type = resultSetColumns.getString("TYPE_NAME");
                    tbl.addColumn(columnName, type);
                }
             
                fstConnTables.add(tbl);
                
            }

        } catch (SQLException ex) {
            Logger.getLogger(TableReport.class.getName()).log(Level.SEVERE, null, ex);
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
                System.out.println("Clave/s secundaria/s: "+tbl.getFks().get(j));
            }
            for (int j = 0; j < tbl.getUqks().size(); j++) {
                System.out.println("Clave/s unica/s: "+tbl.getUqks().get(j));
            }
            for (int j = 0; j < tbl.getIndexs().size(); j++) {
                String[] index = tbl.getIndexs().get(j);
                System.out.println("Indice - Nombre: "+index[0]+", Unique: "+index[1]+", Column name: "+index[2]+", ascOrDesc: "+index[3]);
            }
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
