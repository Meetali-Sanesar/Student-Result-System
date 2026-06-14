package com.srms.dao;

import com.srms.model.Subject;
import com.srms.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Subject entities.
 */
public class SubjectDAO {

    private static final Logger LOGGER = Logger.getLogger(SubjectDAO.class.getName());

    public Subject create(Subject subject) {
        String sql = "INSERT INTO subjects (subject_name, subject_code) VALUES (?, ?)";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, subject.getSubjectName());
            stmt.setString(2, subject.getSubjectCode());
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    subject.setSubjectId(keys.getInt(1));
                }
                return subject;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating subject", e);
        } finally {
            DatabaseConnection.getInstance().releaseConnection(conn);
        }
        return null;
    }

    public Subject findById(int subjectId) {
        String sql = "SELECT * FROM subjects WHERE subject_id = ?";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, subjectId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding subject by ID: " + subjectId, e);
        } finally {
            DatabaseConnection.getInstance().releaseConnection(conn);
        }
        return null;
    }

    public Subject findByCode(String code) {
        String sql = "SELECT * FROM subjects WHERE subject_code = ?";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding subject by code: " + code, e);
        } finally {
            DatabaseConnection.getInstance().releaseConnection(conn);
        }
        return null;
    }

    public List<Subject> findAll() {
        String sql = "SELECT * FROM subjects ORDER BY subject_name ASC";
        List<Subject> subjects = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                subjects.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all subjects", e);
        } finally {
            DatabaseConnection.getInstance().releaseConnection(conn);
        }
        return subjects;
    }

    public boolean update(Subject subject) {
        String sql = "UPDATE subjects SET subject_name = ?, subject_code = ? WHERE subject_id = ?";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, subject.getSubjectName());
            stmt.setString(2, subject.getSubjectCode());
            stmt.setInt(3, subject.getSubjectId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating subject ID: " + subject.getSubjectId(), e);
        } finally {
            DatabaseConnection.getInstance().releaseConnection(conn);
        }
        return false;
    }

    public boolean delete(int subjectId) {
        String sql = "DELETE FROM subjects WHERE subject_id = ?";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, subjectId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting subject ID: " + subjectId, e);
        } finally {
            DatabaseConnection.getInstance().releaseConnection(conn);
        }
        return false;
    }

    public int count() {
        String sql = "SELECT COUNT(*) FROM subjects";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting subjects", e);
        } finally {
            DatabaseConnection.getInstance().releaseConnection(conn);
        }
        return 0;
    }

    private Subject mapResultSet(ResultSet rs) throws SQLException {
        Subject subject = new Subject();
        subject.setSubjectId(rs.getInt("subject_id"));
        subject.setSubjectName(rs.getString("subject_name"));
        subject.setSubjectCode(rs.getString("subject_code"));
        return subject;
    }
}
