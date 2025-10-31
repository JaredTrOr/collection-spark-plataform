package org.jared.trujillo.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgresDatabaseConnection {

    private static final HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(AppConfig.DB_URL);
        config.setUsername(AppConfig.DB_USER);
        config.setPassword(AppConfig.DB_PASS);

        // Pool settings
        config.setMaximumPoolSize(10); // 10 connections is plenty for this app
        config.setConnectionTimeout(30000); // 30 seconds to wait for a connection
        config.setIdleTimeout(600000); // 10 minutes for a connection to be idle
        config.setMaxLifetime(1800000); // 30 minutes max lifetime

        try {
            dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            System.err.println("Failed to initialize connection pool!");
            throw new RuntimeException("Error initializing database pool", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

}
