package org.tchm.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnector {

    public Properties loadProperties(){
        Properties prop = new Properties();
        try (FileInputStream in = new FileInputStream("res/db.properties")) {
            prop.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop;
    }

    public Connection connectToDatabase(){
        Connection connection = null;
        Properties prop = loadProperties();
        String sqlDriverName = prop.getProperty("driver");
        String databaseUrl = prop.getProperty("url");
        String databasePassword = prop.getProperty("password");
        String databaseUsername = prop.getProperty("username");
        try {
            Class.forName(sqlDriverName);
            connection = DriverManager.getConnection(databaseUrl, databaseUsername, databasePassword);
        } catch (SQLException | ClassNotFoundException e) {

        }
        return connection;
    }

}
