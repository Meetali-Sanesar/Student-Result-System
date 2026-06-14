package com.srms.dao;

import com.srms.model.AuditLog;
import com.srms.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Audit Log entries.
 */
public class AuditLogDAO {

    private static final Logger LOGGER = Logger.getLogger(AuditLogDAO.class.getName());

    public AuditLog create(AuditLog log) {
        String sql = "INSERT INTO audit_logs (admin_id, action, details) VALUES (?, ?, ?)";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, log.getAdminId());
            stmt.setString(2, log.getAction());
            stmt.setString(3, log.getDetails());
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    log.setLogId(keys.getInt(1));
                }
                return log;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating audit log", e);
        } finally {
            DatabaseConnection.getInstance().releaseConnection(conn);
        }
        return null;
    }

    public List<AuditLog> findAll() {
        String sql = "SELECT al.*, u.username AS admin_username FROM audit_logs al " +
                     "JOIN users u ON al.admin_id = u.user_id " +
                     "ORDER BY al.action_time DESC";
        List<AuditLog> logs = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                logs.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching audit logs", e);
        } finally {
            DatabaseConnection.getInstance().releaseConnection(conn);
        }
        return logs;
    }

    public List<AuditLog> findRecentLogs(int limit) {
        String sql = "SELECT al.*, u.username AS admin_username FROM audit_logs al " +
                     "JOIN users u ON al.admin_id = u.user_id " +
                     "ORDER BY al.action_time DESC LIMIT ?";
        List<AuditLog> logs = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                logs.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching recent audit logs", e);
        } finally {
            DatabaseConnection.getInstance().releaseConnection(conn);
        }
        return logs;
    }

    private AuditLog mapResultSet(ResultSet rs) throws SQLException {
        AuditLog log = new AuditLog();
        log.setLogId(rs.getInt("log_id"));
        log.setAdminId(rs.getInt("admin_id"));
        log.setAction(rs.getString("action"));
        log.setDetails(rs.getString("details"));
        log.setActionTime(rs.getTimestamp("action_time"));
        try {
            log.setAdminUsername(rs.getString("admin_username"));
        } catch (SQLException ignored) {
            // Column may not be present in all queries
        }
        return log;
    }
}
