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
    
    // Database connection parameters
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/pharmalive?useSSL=false&serverTimezone=UTC";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = ""; // Set your MySQL password here
    
    // Static block to load the JDBC driver
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
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
        return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
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
}