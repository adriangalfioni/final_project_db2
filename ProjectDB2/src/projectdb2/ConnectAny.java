/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectdb2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
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
        String[] results = new String[5];
        HashMap<String, String> connectionInfo = new HashMap<>();
        Scanner sc = new Scanner(System.in);
        System.out.println("Ingrese el nombre de Host");
        results[0] = "localhost";/*sc.nextLine();*/
        System.out.println("Ingrese el nombre de la Base de Datos");
        results[1] = "postgres";/*sc.nextLine();*/
        System.out.println("Ingrese el schema a utilizar");
        results[2] = /*"ej1b";*/sc.nextLine();
        System.out.println("Ingrese el usuario");
        results[3] = "postgres";/*sc.nextLine();*/
        System.out.println("Ingrese la contraseña");
        results[4] = "root";/*sc.nextLine();*/ 
     
        connectionInfo.put("host", results[0]);
        connectionInfo.put("dbname", results[1]);
        connectionInfo.put("schema", results[2]);
        connectionInfo.put("user", results[3]);
        connectionInfo.put("pass", results[4]);
        
        return connectionInfo;
    }
    
    public HashMap<String,String> getDefaultConnection() throws FileNotFoundException, IOException{
        String[] results = new String[10];
        HashMap<String, String> connectionInfo = new HashMap<>();
        URL path = ConnectAny.class.getResource("DefaultConnection.txt");
        File f = new File(path.getFile());
        BufferedReader br = new BufferedReader(new FileReader(f));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            int count = 0;
            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                if(count<results.length)
                    results[count] = line.split(":")[1].replaceAll("\\s+","");
                count++;
                line = br.readLine();                
            }
        } finally {
            br.close();
        }

        connectionInfo.put("host", results[0]);
        connectionInfo.put("dbname", results[1]);
        connectionInfo.put("schema", results[2]);
        connectionInfo.put("user", results[3]);
        connectionInfo.put("pass", results[4]);
          connectionInfo.put("host2", results[5]);
        connectionInfo.put("dbname2", results[6]);
        connectionInfo.put("schema2", results[7]);
        connectionInfo.put("user2", results[8]);
        connectionInfo.put("pass2", results[9]);
        
        return connectionInfo;
    }
    
}
