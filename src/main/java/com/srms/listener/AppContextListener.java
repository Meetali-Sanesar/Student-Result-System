package com.srms.listener;

import com.srms.util.DatabaseConnection;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import java.util.logging.Logger;

/**
 * Application context listener for initialization and cleanup.
 */
public class AppContextListener implements ServletContextListener {

    private static final Logger LOGGER = Logger.getLogger(AppContextListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        LOGGER.info("===========================================");
        LOGGER.info("  Student Result Management System");
        LOGGER.info("  Starting up...");
        LOGGER.info("===========================================");

        // Initialize database connection pool
        try {
            DatabaseConnection.getInstance();
            LOGGER.info("Database connection pool initialized successfully");
        } catch (Exception e) {
            LOGGER.severe("Failed to initialize database connection pool: " + e.getMessage());
            throw new RuntimeException("Application startup failed", e);
        }

        LOGGER.info("Application started successfully");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOGGER.info("Shutting down application...");

        // Close database connections
        try {
            DatabaseConnection.getInstance().shutdown();
        } catch (Exception e) {
            LOGGER.warning("Error during database shutdown: " + e.getMessage());
        }

        LOGGER.info("Application shut down successfully");
    }
}
