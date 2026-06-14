package com.srms.dao;

import com.srms.model.Mark;
import com.srms.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Mark entities.
 */
public class MarkDAO {

    private static final Logger LOGGER = Logger.getLogger(MarkDAO.class.getName());

    /**
     * Create a new mark entry.
     */
    public Mark create(Mark mark) {
        String sql = "INSERT INTO marks (student_id, subject_id, marks) VALUES (?, ?, ?)";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, mark.getStudentId());
            stmt.setInt(2, mark.getSubjectId());
            stmt.setDouble(3, mark.getMarks());
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    mark.setMarkId(keys.getInt(1));
                }
                return mark;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating mark", e);
        } finally {
            DatabaseConnection.getInstance().releaseConnection(conn);
        }
        return null;
    }

    /**
     * Find a mark by ID.
     */
    public Mark findById(int markId) {
        String sql = "SELECT m.*, s.name AS student_name, sub.subject_name, sub.subject_code " +
                     "FROM marks m " +
                     "JOIN students s ON m.student_id = s.student_id " +
                     "JOIN subjects sub ON m.subject_id = sub.subject_id " +
                     "WHERE m.mark_id = ?";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, markId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetWithNames(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding mark by ID: " + markId, e);
        } finally {
            DatabaseConnection.getInstance().releaseConnection(conn);
        }
        return null;
    }

    /**
     * Get all marks for a specific student (with subject names).
     */
    public List<Mark> findByStudentId(int studentId) {
        String sql = "SELECT m.*, s.name AS student_name, sub.subject_name, sub.subject_code " +
                     "FROM marks m " +
                     "JOIN students s ON m.student_id = s.student_id " +
                     "JOIN subjects sub ON m.subject_id = sub.subject_id " +
                     "WHERE m.student_id = ? ORDER BY sub.subject_name ASC";
        List<Mark> marks = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                marks.add(mapResultSetWithNames(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding marks for student ID: " + studentId, e);
        } finally {
            DatabaseConnection.getInstance().releaseConnection(conn);
        }
        return marks;
    }

    /**
     * Check if a mark already exists for a student-subject combination.
     */
    public Mark findByStudentAndSubject(int studentId, int subjectId) {
        String sql = "SELECT * FROM marks WHERE student_id = ? AND subject_id = ?";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, studentId);
            stmt.setInt(2, subjectId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding mark by student/subject", e);
        } finally {
            DatabaseConnection.getInstance().releaseConnection(conn);
        }
        return null;
    }

    /**
     * Update a mark.
     */
    public boolean update(Mark mark) {
        String sql = "UPDATE marks SET marks = ? WHERE mark_id = ?";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setDouble(1, mark.getMarks());
            stmt.setInt(2, mark.getMarkId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating mark ID: " + mark.getMarkId(), e);
        } finally {
            DatabaseConnection.getInstance().releaseConnection(conn);
        }
        return false;
    }

    /**
     * Delete a mark.
     */
    public boolean delete(int markId) {
        String sql = "DELETE FROM marks WHERE mark_id = ?";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, markId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting mark ID: " + markId, e);
        } finally {
            DatabaseConnection.getInstance().releaseConnection(conn);
        }
        return false;
    }

    /**
     * Get all marks (with student and subject names).
     */
    public List<Mark> findAll() {
        String sql = "SELECT m.*, s.name AS student_name, sub.subject_name, sub.subject_code " +
                     "FROM marks m " +
                     "JOIN students s ON m.student_id = s.student_id " +
                     "JOIN subjects sub ON m.subject_id = sub.subject_id " +
                     "ORDER BY s.name ASC, sub.subject_name ASC";
        List<Mark> marks = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                marks.add(mapResultSetWithNames(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all marks", e);
        } finally {
            DatabaseConnection.getInstance().releaseConnection(conn);
        }
        return marks;
    }

    private Mark mapResultSet(ResultSet rs) throws SQLException {
        Mark mark = new Mark();
        mark.setMarkId(rs.getInt("mark_id"));
        mark.setStudentId(rs.getInt("student_id"));
        mark.setSubjectId(rs.getInt("subject_id"));
        mark.setMarks(rs.getDouble("marks"));
        return mark;
    }

    private Mark mapResultSetWithNames(ResultSet rs) throws SQLException {
        Mark mark = mapResultSet(rs);
        mark.setStudentName(rs.getString("student_name"));
        mark.setSubjectName(rs.getString("subject_name"));
        mark.setSubjectCode(rs.getString("subject_code"));
        return mark;
    }
}
