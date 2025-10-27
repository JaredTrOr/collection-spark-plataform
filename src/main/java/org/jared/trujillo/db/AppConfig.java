package org.jared.trujillo.db;

import java.io.InputStream;
import java.util.Properties;

public class AppConfig {

    // Properties variable names of config.properties
    public static final String NAME_DB_STRING = "POSTGRESDB_URL";
    public static final String NAME_USER_STRING = "POSTGRESDB_USER";
    public static final String NAME_PASS_STRING = "POSTGRESDB_PASS";

    public static final String DB_URL;
    public static final String DB_USER;
    public static final String DB_PASS;

    static {
        Properties props = new Properties();

        try (InputStream input = AppConfig.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (input != null) {
                props.load(input);
            }
        } catch (Exception e) {
            System.err.println("Error loading config.properties. " + e.getMessage());
            System.exit(1);
        }

        DB_URL = System.getenv(NAME_DB_STRING) != null ?
                System.getenv(NAME_DB_STRING) : props.getProperty(NAME_DB_STRING);

        DB_USER = System.getenv(NAME_USER_STRING) != null ?
                System.getenv(NAME_PASS_STRING) : props.getProperty(NAME_USER_STRING);

        DB_PASS = System.getenv(NAME_PASS_STRING) != null ?
                System.getenv(NAME_PASS_STRING) : props.getProperty(NAME_PASS_STRING);

        if (DB_URL == null || DB_USER == null || DB_PASS == null) {
            System.err.println("Database credentials are not fully configured. " +
                    "Check config.properties or environment variables.");
            System.exit(1);
        }
    }

}
