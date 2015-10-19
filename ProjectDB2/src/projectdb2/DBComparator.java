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
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adrian
 */
public class DBComparator {
    
    static Connection fstConnection;
    static Connection scdConnection;
    
    public static void getInfo(Connection conn) {
        try {
            String[] tipo = {"TABLE"};
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet resultSetTables = metaData.getTables("procedimientos", "public", null, tipo);
            System.out.println(" TABLAS DE LA BASE DE DATOS ");

            while (resultSetTables.next()) {
                System.out.println(" catalogo: " + resultSetTables.getString(1));
                System.out.println(" esquema: " + resultSetTables.getString(2));
                System.out.println(" nombre: " + resultSetTables.getString(3));
                System.out.println(" tipo: " + resultSetTables.getString(4));
                System.out.println(" comentarios: " + resultSetTables.getString(5));
                System.out.println("\n");

            }

            ResultSet resultSetTables1 = metaData.getProcedures("procedimientos", "public", null);
            System.out.println(" PROCEDIMIENTOS DE LA BASE DE DATOS ");

            while (resultSetTables1.next()) {
                System.out.println(" catalogo: " + resultSetTables1.getString(1));
                System.out.println(" esquema: " + resultSetTables1.getString(2));
                System.out.println(" nombre: " + resultSetTables1.getString(3));
                System.out.println(" tipo: " + resultSetTables1.getString(4));
                System.out.println(" tipo in/out parametro: " + resultSetTables1.getString(5));
                System.out.println(" tipo parametro: " + resultSetTables1.getString(6));
                System.out.println("\n");

            }

            ResultSet resultSetTables2 = metaData.getProcedures("procedimientos", "public", null);
            System.out.println(" tablas de la base de datos (DOS) ");

            while (resultSetTables2.next()) {
                System.out.println(" catalogo: " + resultSetTables2.getString(1));
                System.out.println(" esquema: " + resultSetTables2.getString(2));
                System.out.println(" nombre: " + resultSetTables2.getString(3));
                System.out.println(" tipo: " + resultSetTables2.getString(4));
                System.out.println(" tipo in/out parametro: " + resultSetTables2.getString(5));
                System.out.println(" tipo parametro: " + resultSetTables2.getString(6));
                System.out.println("\n");
            }

            ResultSet resultSetTables3 = metaData.getIndexInfo(null, "public", "ninio", true, false);
            System.out.println(" ------------------------------");
            System.out.println(" claveunica de la base de datos \n");

            while (resultSetTables3.next()) {
                System.out.println(" catalogo: " + resultSetTables3.getString(1));
                System.out.println(" esquema: " + resultSetTables3.getString(2));
                System.out.println(" nombre: " + resultSetTables3.getString(3));
                System.out.println(" tipo: " + resultSetTables3.getString(4));
                System.out.println(" tipo in/out parametro: " + resultSetTables3.getString(5));
                System.out.println(" tipo parametro: " + resultSetTables3.getString(6));

            }
        } catch (SQLException ex) {
            Logger.getLogger(DBComparator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static Connection obtainConn(){
        ConnectPostgres connection = new ConnectPostgres();
        String host, dbName, user, pass;
        
        
        HashMap<String,String> info = connection.getConnectionInfo();
        host=info.get("host");
        dbName=info.get("dbname");
        user=info.get("user");
        pass=info.get("pass");
        
        return connection.connect(host, dbName, user, pass);
    }
    
    public static void main(String[] args) {
        
        System.out.println("Se solicitara la información para la primer conexion:");
        fstConnection = obtainConn();
        
        System.out.println("Se solicitara la información para la segunda conexion:");
        scdConnection = obtainConn();
        
        getInfo(fstConnection);
    }
    
}
