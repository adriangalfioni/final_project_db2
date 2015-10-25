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

            ResultSet resultSetTables = metaData.getTables(null,fstSchema, null, tipo);
            
            //System.out.println("INFORMACION DE LA BASE DE DATOS - ESQUEMA: "+fstSchema);

            while (resultSetTables.next()) {
                
                tbl = new Table();
                
                //System.out.println("\n");
                //System.out.println("Nombre: " + resultSetTables.getString(3));
                tbl.setName(resultSetTables.getString("TABLE_NAME"));
                //System.out.println("Tipo: " + resultSetTables.getString(4));

                ResultSet resutlSetPK = metaData.getPrimaryKeys(resultSetTables.getString(1), resultSetTables.getString(2), resultSetTables.getString(3));
                
                //ResultSet getImportedKeys(String catalog,String schema,String table)
                ResultSet resultSetFK = metaData.getImportedKeys(null, fstSchema, resultSetTables.getString(3));
                
                //getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern)
                ResultSet resultSetColumns = metaData.getColumns(null, fstSchema, resultSetTables.getString(3),null);
                
                
                while (resutlSetPK.next()){
                    String columnName = resutlSetPK.getString("COLUMN_NAME");
                    tbl.addPk(columnName);
                    //System.out.println("Column primary key: "+columnName);
                }
                while (resultSetFK.next()){
                    String columnName = resultSetFK.getString("PKCOLUMN_NAME");
                    tbl.addFk(columnName);
                    //System.out.println("Column foreign key: "+columnName);
                }
                while (resultSetColumns.next()){
                    String columnName = resultSetColumns.getString("COLUMN_NAME");
                    String type = resultSetColumns.getString("TYPE_NAME");
                    tbl.addColumn(columnName, type);
                    //System.out.println("Attribute: "+columnName);
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
                System.out.println("Atributo: "+tbl.getColumns().get(j)[0]+" type: "+tbl.getColumns().get(j)[1]);
            }
        }
    }
    
}    
