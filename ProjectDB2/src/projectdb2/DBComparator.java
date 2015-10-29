/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectdb2;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adrian
 */
public class DBComparator {
    
    static Connection fstConnection;
    static Connection scdConnection;
    static String schema, schema2;
    static String fstSchema, scdSchema;
    
    public static Connection obtainConn(){
        ConnectPostgres connection = new ConnectPostgres();
        String host, dbName, user, pass;
 
        HashMap<String,String> info = null;
                
        info = connection.getConnectionInfo();

        host=info.get("host");
        dbName=info.get("dbname");
        schema=info.get("schema");
        user=info.get("user");
        pass=info.get("pass");
        
        return connection.connect(host, dbName, schema, user, pass);
    }
    
    public static Connection[] obtainDefaultsConns(){
        ConnectPostgres connection = new ConnectPostgres();
        String host, dbName, user, pass;
        String host2, dbName2, user2, pass2;
        HashMap<String,String> info = null;
                
        try {
            info = connection.getDefaultConnection();
        } catch (IOException ex) {
            Logger.getLogger(DBComparator.class.getName()).log(Level.SEVERE, null, ex);
        }
        host=info.get("host");
        dbName=info.get("dbname");
        schema=info.get("schema");
        user=info.get("user");
        pass=info.get("pass");
        host2=info.get("host2");
        dbName2=info.get("dbname2");
        schema2=info.get("schema2");
        user2=info.get("user2");
        pass2=info.get("pass2");
        Connection[] conns = new Connection[2];
        conns[0] = connection.connect(host, dbName, schema, user, pass);
        conns[1] = connection.connect(host2, dbName2, schema2, user2, pass2);
        return conns;
    }
    
    public static void main(String[] args) {
        
        String userResponse = "";
        Scanner sc = new Scanner(System.in);
        System.out.println("Usar coneccion por defecto? S/N");
        userResponse = sc.nextLine();
        if(userResponse.equalsIgnoreCase("S")){
            Connection[] conns = null;
            conns = obtainDefaultsConns();
            fstConnection = conns[0];
            fstSchema = schema;
            scdConnection = conns[1];
            scdSchema = schema2;
        }else{
            System.out.println("Se solicitara la información para la primer conexion:");
            fstConnection = obtainConn();
            fstSchema = schema;

            System.out.println("Se solicitara la información para la segunda conexion:");
            scdConnection = obtainConn();
            scdSchema = schema;
        }
        
    
        Report report = new Report(fstConnection, scdConnection,fstSchema,scdSchema);
        report.generateReport();
        report.showReport();
    }
    
}
