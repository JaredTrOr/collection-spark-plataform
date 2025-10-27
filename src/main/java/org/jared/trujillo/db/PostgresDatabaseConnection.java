package org.jared.trujillo.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgresDatabaseConnection {

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(
                    AppConfig.DB_URL,
                    AppConfig.DB_USER,
                    AppConfig.DB_PASS
            );
        } catch(SQLException e) {
            throw new RuntimeException("Error connecting to the database", e);
        }
    }

}
