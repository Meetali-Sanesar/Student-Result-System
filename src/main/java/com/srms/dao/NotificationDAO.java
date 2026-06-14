package com.srms.dao;

import com.srms.model.Notification;
import com.srms.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Notification entities.
 */
public class NotificationDAO {

    private static final Logger LOGGER = Logger.getLogger(NotificationDAO.class.getName());

    public Notification create(Notification notification) {
        String sql = "INSERT INTO notifications (student_id, message) VALUES (?, ?)";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, notification.getStudentId());
            stmt.setString(2, notification.getMessage());
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    notification.setNotificationId(keys.getInt(1));
                }
                return notification;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating notification", e);
        } finally {
            DatabaseConnection.getInstance().releaseConnection(conn);
        }
        return null;
    }

    /**
     * Create notifications for all students.
     */
    public void createForAllStudents(String message) {
        String sql = "INSERT INTO notifications (student_id, message) " +
                     "SELECT student_id, ? FROM students";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, message);
            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating bulk notifications", e);
        } finally {
            DatabaseConnection.getInstance().releaseConnection(conn);
        }
    }

    public List<Notification> findByStudentId(int studentId) {
        String sql = "SELECT * FROM notifications WHERE student_id = ? ORDER BY created_at DESC";
        List<Notification> notifications = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                notifications.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding notifications for student ID: " + studentId, e);
        } finally {
            DatabaseConnection.getInstance().releaseConnection(conn);
        }
        return notifications;
    }

    public boolean markAsRead(int notificationId) {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE notification_id = ?";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, notificationId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error marking notification as read: " + notificationId, e);
        } finally {
            DatabaseConnection.getInstance().releaseConnection(conn);
        }
        return false;
    }

    public int getUnreadCount(int studentId) {
        String sql = "SELECT COUNT(*) FROM notifications WHERE student_id = ? AND is_read = FALSE";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting unread notifications for student ID: " + studentId, e);
        } finally {
            DatabaseConnection.getInstance().releaseConnection(conn);
        }
        return 0;
    }

    private Notification mapResultSet(ResultSet rs) throws SQLException {
        Notification n = new Notification();
        n.setNotificationId(rs.getInt("notification_id"));
        n.setStudentId(rs.getInt("student_id"));
        n.setMessage(rs.getString("message"));
        n.setRead(rs.getBoolean("is_read"));
        n.setCreatedAt(rs.getTimestamp("created_at"));
        return n;
    }
}
