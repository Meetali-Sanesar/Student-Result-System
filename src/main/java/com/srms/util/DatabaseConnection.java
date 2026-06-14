package com.srms.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Database connection pool manager.
 * Provides a simple connection pool using BlockingQueue.
 */
public class DatabaseConnection {

    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());
    private static DatabaseConnection instance;
    private final BlockingQueue<Connection> connectionPool;
    private final String url;
    private final String username;
    private final String password;
    private final int poolSize;

    private DatabaseConnection() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new RuntimeException("db.properties not found in classpath");
            }
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load db.properties", e);
        }

        try {
            Class.forName(props.getProperty("db.driver"));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found", e);
        }

        this.url = props.getProperty("db.url");
        this.username = props.getProperty("db.username");
        this.password = props.getProperty("db.password");
        this.poolSize = Integer.parseInt(props.getProperty("db.pool.size", "10"));
        this.connectionPool = new ArrayBlockingQueue<>(poolSize);

        initializePool();
        LOGGER.info("Database connection pool initialized with " + poolSize + " connections");
    }

    /**
     * Get the singleton instance.
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Initialize the connection pool with connections.
     */
    private void initializePool() {
        for (int i = 0; i < poolSize; i++) {
            try {
                connectionPool.add(createConnection());
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Failed to create connection " + (i + 1), e);
            }
        }
    }

    /**
     * Create a new database connection.
     */
    private Connection createConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(url, username, password);
        conn.setAutoCommit(true);
        return conn;
    }

    /**
     * Get a connection from the pool.
     */
    public Connection getConnection() throws SQLException {
        try {
            Connection conn = connectionPool.poll();
            if (conn == null || conn.isClosed() || !conn.isValid(2)) {
                conn = createConnection();
            }
            return conn;
        } catch (Exception e) {
            throw new SQLException("Failed to get database connection", e);
        }
    }

    /**
     * Return a connection back to the pool.
     */
    public void releaseConnection(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.isClosed() && conn.isValid(2)) {
                    conn.setAutoCommit(true);
                    if (!connectionPool.offer(conn)) {
                        conn.close();
                    }
                } else {
                    conn.close();
                }
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error releasing connection", e);
            }
        }
    }

    /**
     * Close all connections in the pool and shut down.
     */
    public void shutdown() {
        LOGGER.info("Shutting down database connection pool...");
        Connection conn;
        while ((conn = connectionPool.poll()) != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error closing connection during shutdown", e);
            }
        }
        instance = null;
        LOGGER.info("Database connection pool shut down successfully");
    }
}
