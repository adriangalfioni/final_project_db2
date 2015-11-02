/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectdb2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectPostgres extends ConnectAny{

    Connection connection;
    
    @Override
    public Connection connect(String host, String dbName, String schema, String user, String pass) {
        try {
            String driver = "org.postgresql.Driver";
            String url = "jdbc:postgresql://"+host+":5432/"+dbName;
            String username = user;
            String password = pass;

            // Load database driver if not already loaded.
            Class.forName(driver);
            // Establish network connection to database.
            connection = DriverManager.getConnection(url, /*"postgres"*/username, /*"root"*/password);
            
           
        } catch (ClassNotFoundException cnfe) {
            System.err.println("Error loading driver: " + cnfe);
        } catch (SQLException sqle) {
            System.err.println("Error connecting: " + sqle);
        }
        
        return connection;
    }
    
}
