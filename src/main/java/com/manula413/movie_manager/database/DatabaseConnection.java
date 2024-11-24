package com.manula413.movie_manager.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;


public class DatabaseConnection {

     private static String url;
     private static String databaseUser;
     private static String databasePassword;
     private static String databaseDriver;

     static{
         try{
             FileInputStream fis = new FileInputStream("src/main/resources/db-config.properties");
             Properties properties = new Properties();
             properties.load(fis);

             // Set the Values

             url = properties.getProperty("db.url");
             databaseUser = properties.getProperty("db.username");
             databasePassword = properties.getProperty("db.password");
             databaseDriver = properties.getProperty("db.driver");

         }   catch(IOException e){
             e.printStackTrace();
         }
     }


    public Connection getConnection() throws SQLException {
        try {
            Class.forName(databaseDriver);
            return DriverManager.getConnection(url, databaseUser, databasePassword);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new SQLException("Failed to create a database connection.", e);
        }
    }

}
