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
    static String schema;
    static String fstSchema, scdSchema;
    
    public static Connection obtainConn(){
        ConnectPostgres connection = new ConnectPostgres();
        String host, dbName, user, pass;
        
        
        HashMap<String,String> info = connection.getConnectionInfo();
        host=info.get("host");
        dbName=info.get("dbname");
        schema=info.get("schema");
        user=info.get("user");
        pass=info.get("pass");
        
        return connection.connect(host, dbName, schema, user, pass);
    }
    
    public static void main(String[] args) {
        
        System.out.println("Se solicitara la información para la primer conexion:");
        fstConnection = obtainConn();
        fstSchema = schema;
        
        System.out.println("Se solicitara la información para la segunda conexion:");
        scdConnection = obtainConn();
        scdSchema = schema;
        
    
        Report report = new Report(fstConnection, scdConnection,fstSchema,scdSchema);
        report.generateReport();
        report.showReport();
    }
    
}
