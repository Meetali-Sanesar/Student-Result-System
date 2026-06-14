package com.srms.dao;

import com.srms.model.Result;
import com.srms.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Result entities.
 */
public class ResultDAO {

    private static final Logger LOGGER = Logger.getLogger(ResultDAO.class.getName());

    /**
     * Create or update a result (upsert).
     */
    public Result createOrUpdate(Result result) {
        String sql = "INSERT INTO results (student_id, total_marks, percentage, grade, rank_position, published) " +
                     "VALUES (?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE total_marks = VALUES(total_marks), percentage = VALUES(percentage), " +
                     "grade = VALUES(grade), rank_position = VALUES(rank_position)";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, result.getStudentId());
            stmt.setDouble(2, result.getTotalMarks());
            stmt.setDouble(3, result.getPercentage());
            stmt.setString(4, result.getGrade());
            stmt.setInt(5, result.getRankPosition());
            stmt.setBoolean(6, result.isPublished());
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    result.setResultId(keys.getInt(1));
                }
                return result;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating/updating result for student ID: " + result.getStudentId(), e);
        } finally {
            DatabaseConnection.getInstance().releaseConnection(conn);
        }
        return null;
    }

    /**
     * Find result by student ID (with student name and department).
     */
    public Result findByStudentId(int studentId) {
        String sql = "SELECT r.*, s.name AS student_name, s.department " +
                     "FROM results r JOIN students s ON r.student_id = s.student_id " +
                     "WHERE r.student_id = ?";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetWithStudent(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding result for student ID: " + studentId, e);
        } finally {
            DatabaseConnection.getInstance().releaseConnection(conn);
        }
        return null;
    }

    /**
     * Get all results with student details.
     */
    public List<Result> findAll() {
        String sql = "SELECT r.*, s.name AS student_name, s.department " +
                     "FROM results r JOIN students s ON r.student_id = s.student_id " +
                     "ORDER BY r.rank_position ASC";
        List<Result> results = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                results.add(mapResultSetWithStudent(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all results", e);
        } finally {
            DatabaseConnection.getInstance().releaseConnection(conn);
        }
        return results;
    }

    /**
     * Get top N students by rank.
     */
    public List<Result> getTopStudents(int limit) {
        String sql = "SELECT r.*, s.name AS student_name, s.department " +
                     "FROM results r JOIN students s ON r.student_id = s.student_id " +
                     "WHERE r.published = TRUE " +
                     "ORDER BY r.percentage DESC LIMIT ?";
        List<Result> results = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                results.add(mapResultSetWithStudent(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching top students", e);
        } finally {
            DatabaseConnection.getInstance().releaseConnection(conn);
        }
        return results;
    }

    /**
     * Publish all results.
     */
    public boolean publishAll() {
        String sql = "UPDATE results SET published = TRUE";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error publishing results", e);
        } finally {
            DatabaseConnection.getInstance().releaseConnection(conn);
        }
        return false;
    }

    /**
     * Update ranks based on percentage (DESC).
     */
    public void updateRanks() {
        String sql = "SET @rank = 0; " +
                     "UPDATE results SET rank_position = (@rank := @rank + 1) " +
                     "ORDER BY percentage DESC";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            // Execute SET and UPDATE separately
            Statement stmt1 = conn.createStatement();
            stmt1.execute("SET @rank = 0");
            stmt1.execute("UPDATE results SET rank_position = (@rank := @rank + 1) ORDER BY percentage DESC");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating ranks", e);
        } finally {
            DatabaseConnection.getInstance().releaseConnection(conn);
        }
    }

    /**
     * Count total results.
     */
    public int count() {
        String sql = "SELECT COUNT(*) FROM results";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting results", e);
        } finally {
            DatabaseConnection.getInstance().releaseConnection(conn);
        }
        return 0;
    }

    /**
     * Count passed students (percentage >= 60).
     */
    public int countPassed() {
        String sql = "SELECT COUNT(*) FROM results WHERE percentage >= 60";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting passed students", e);
        } finally {
            DatabaseConnection.getInstance().releaseConnection(conn);
        }
        return 0;
    }

    /**
     * Count failed students (percentage < 60).
     */
    public int countFailed() {
        String sql = "SELECT COUNT(*) FROM results WHERE percentage < 60";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting failed students", e);
        } finally {
            DatabaseConnection.getInstance().releaseConnection(conn);
        }
        return 0;
    }

    /**
     * Count results by grade.
     */
    public int countByGrade(String grade) {
        String sql = "SELECT COUNT(*) FROM results WHERE grade = ?";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, grade);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting by grade: " + grade, e);
        } finally {
            DatabaseConnection.getInstance().releaseConnection(conn);
        }
        return 0;
    }

    /**
     * Delete result by student ID.
     */
    public boolean deleteByStudentId(int studentId) {
        String sql = "DELETE FROM results WHERE student_id = ?";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, studentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting result for student ID: " + studentId, e);
        } finally {
            DatabaseConnection.getInstance().releaseConnection(conn);
        }
        return false;
    }

    /**
     * Get all student IDs that have marks but no results.
     */
    public List<Integer> getStudentIdsWithMarksButNoResults() {
        String sql = "SELECT DISTINCT m.student_id FROM marks m " +
                     "LEFT JOIN results r ON m.student_id = r.student_id " +
                     "WHERE r.result_id IS NULL";
        List<Integer> ids = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ids.add(rs.getInt("student_id"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding students with marks but no results", e);
        } finally {
            DatabaseConnection.getInstance().releaseConnection(conn);
        }
        return ids;
    }

    private Result mapResultSetWithStudent(ResultSet rs) throws SQLException {
        Result result = new Result();
        result.setResultId(rs.getInt("result_id"));
        result.setStudentId(rs.getInt("student_id"));
        result.setTotalMarks(rs.getDouble("total_marks"));
        result.setPercentage(rs.getDouble("percentage"));
        result.setGrade(rs.getString("grade"));
        result.setRankPosition(rs.getInt("rank_position"));
        result.setPublished(rs.getBoolean("published"));
        result.setCreatedAt(rs.getTimestamp("created_at"));
        result.setUpdatedAt(rs.getTimestamp("updated_at"));
        result.setStudentName(rs.getString("student_name"));
        result.setDepartment(rs.getString("department"));
        return result;
    }
}
