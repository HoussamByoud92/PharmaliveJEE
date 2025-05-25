package com.live.pharmaliv.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for database operations.
 * Provides methods for getting database connections.
 */
public class DatabaseUtil {
    private static final Logger LOGGER = Logger.getLogger(DatabaseUtil.class.getName());

    // Database connection parameters - read from environment variables or use defaults
    private static final String DB_HOST = getEnvOrDefault("DB_HOST", "localhost");
    private static final String DB_PORT = getEnvOrDefault("DB_PORT", "3306");
    private static final String DB_NAME = getEnvOrDefault("DB_NAME", "pharmalive");
    private static final String JDBC_USER = getEnvOrDefault("DB_USER", "root");
    private static final String JDBC_PASSWORD = getEnvOrDefault("DB_PASSWORD", ""); // Default empty password

    // Construct JDBC URL dynamically
    private static final String JDBC_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME + "?useSSL=false&serverTimezone=UTC";

    // Static block to load the JDBC driver and log connection parameters
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Log database connection parameters (without password)
            LOGGER.log(Level.INFO, "Database connection parameters:");
            LOGGER.log(Level.INFO, "JDBC URL: {0}", JDBC_URL);
            LOGGER.log(Level.INFO, "Database Host: {0}", DB_HOST);
            LOGGER.log(Level.INFO, "Database Port: {0}", DB_PORT);
            LOGGER.log(Level.INFO, "Database Name: {0}", DB_NAME);
            LOGGER.log(Level.INFO, "Database User: {0}", JDBC_USER);
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "MySQL JDBC Driver not found", e);
            throw new RuntimeException("MySQL JDBC Driver not found", e);
        }
    }

    /**
     * Get a connection to the database.
     * @return A Connection object
     * @throws SQLException If a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        try {
            LOGGER.log(Level.FINE, "Attempting to connect to database at {0}", DB_HOST);
            return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
        } catch (SQLException e) {
            // Provide more detailed error messages for common issues
            if (e.getMessage().contains("Communications link failure")) {
                LOGGER.log(Level.SEVERE, "Database connection failed: Could not connect to {0}:{1}. " +
                        "If running in Docker, check that the container can reach the database host.", 
                        new Object[]{DB_HOST, DB_PORT});
            } else if (e.getMessage().contains("Access denied")) {
                LOGGER.log(Level.SEVERE, "Database connection failed: Access denied for user {0}. " +
                        "Check that the username and password are correct.", JDBC_USER);
            } else if (e.getMessage().contains("Unknown database")) {
                LOGGER.log(Level.SEVERE, "Database connection failed: Database {0} does not exist. " +
                        "Make sure the database is created before connecting.", DB_NAME);
            } else {
                LOGGER.log(Level.SEVERE, "Database connection failed", e);
            }
            throw e;
        }
    }

    /**
     * Close a database connection.
     * @param connection The connection to close
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error closing database connection", e);
            }
        }
    }

    /**
     * Begin a transaction.
     * @param connection The connection to use
     * @throws SQLException If a database access error occurs
     */
    public static void beginTransaction(Connection connection) throws SQLException {
        connection.setAutoCommit(false);
    }

    /**
     * Commit a transaction.
     * @param connection The connection to use
     * @throws SQLException If a database access error occurs
     */
    public static void commitTransaction(Connection connection) throws SQLException {
        connection.commit();
        connection.setAutoCommit(true);
    }

    /**
     * Rollback a transaction.
     * @param connection The connection to use
     */
    public static void rollbackTransaction(Connection connection) {
        if (connection != null) {
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error rolling back transaction", e);
            }
        }
    }

    /**
     * Get a value from environment variables or return a default value if not found.
     * @param name The name of the environment variable
     * @param defaultValue The default value to return if the environment variable is not set
     * @return The value of the environment variable or the default value
     */
    private static String getEnvOrDefault(String name, String defaultValue) {
        String value = System.getenv(name);
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }
}
