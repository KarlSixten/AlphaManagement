package org.example.alphamanagement.repository.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
    private static Connection connection;

    private ConnectionManager() {}

    public static Connection getConnection(String url, String user, String password) {
        if (connection != null) return connection;

        try {
            connection = DriverManager.getConnection(url, user, password);

        } catch (SQLException e) {
            System.out.println("Couldn't connect to database.");
            e.printStackTrace();
        }
        return connection;
    }
}