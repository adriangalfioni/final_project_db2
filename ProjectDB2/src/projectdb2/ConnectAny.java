/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectdb2;

import java.sql.*;
import java.util.HashMap;
import java.util.Scanner;

/**
 *
 * @author adrian
 */
public abstract class ConnectAny {
    
    public abstract Connection connect(String host, String dbName, String schema, String user, String pass); 
    
    public HashMap<String,String> getConnectionInfo(){
        String host, dbName, schema, user, pass;
        HashMap<String, String> connectionInfo = new HashMap<>();

        Scanner sc = new Scanner(System.in);
        
        System.out.println("Ingrese el nombre de Host");
        host = "localhost";/*sc.nextLine();*/
        System.out.println("Ingrese el nombre de la Base de Datos");
        dbName = "postgres";/*sc.nextLine();*/
        System.out.println("Ingrese el schema a utilizar");
        schema = /*"ej1b";*/sc.nextLine();
        System.out.println("Ingrese el usuario");
        user = "postgres";/*sc.nextLine();*/
        System.out.println("Ingrese la contraseña");
        pass = "root";/*sc.nextLine();*/
        
        connectionInfo.put("host", host);
        connectionInfo.put("dbname", dbName);
        connectionInfo.put("schema", schema);
        connectionInfo.put("user", user);
        connectionInfo.put("pass", pass);
        
        return connectionInfo;
    }
    
}
