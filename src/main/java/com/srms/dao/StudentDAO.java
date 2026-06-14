package com.srms.dao;

import com.srms.model.Student;
import com.srms.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Student entities.
 */
public class StudentDAO {

    private static final Logger LOGGER = Logger.getLogger(StudentDAO.class.getName());

    /**
     * Create a new student.
     */
    public Student create(Student student) {
        String sql = "INSERT INTO students (name, email, phone, department, profile_image) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, student.getName());
            stmt.setString(2, student.getEmail());
            stmt.setString(3, student.getPhone());
            stmt.setString(4, student.getDepartment());
            stmt.setString(5, student.getProfileImage());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    student.setStudentId(keys.getInt(1));
                }
                return student;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating student: " + student.getName(), e);
        } finally {
            DatabaseConnection.getInstance().releaseConnection(conn);
        }
        return null;
    }

    /**
     * Find a student by ID.
     */
    public Student findById(int studentId) {
        String sql = "SELECT * FROM students WHERE student_id = ?";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding student by ID: " + studentId, e);
        } finally {
            DatabaseConnection.getInstance().releaseConnection(conn);
        }
        return null;
    }

    /**
     * Find a student by email.
     */
    public Student findByEmail(String email) {
        String sql = "SELECT * FROM students WHERE email = ?";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding student by email: " + email, e);
        } finally {
            DatabaseConnection.getInstance().releaseConnection(conn);
        }
        return null;
    }

    /**
     * Get all students ordered by name.
     */
    public List<Student> findAll() {
        String sql = "SELECT * FROM students ORDER BY name ASC";
        List<Student> students = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                students.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all students", e);
        } finally {
            DatabaseConnection.getInstance().releaseConnection(conn);
        }
        return students;
    }

    /**
     * Update a student.
     */
    public boolean update(Student student) {
        String sql = "UPDATE students SET name = ?, email = ?, phone = ?, department = ?, profile_image = ? WHERE student_id = ?";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, student.getName());
            stmt.setString(2, student.getEmail());
            stmt.setString(3, student.getPhone());
            stmt.setString(4, student.getDepartment());
            stmt.setString(5, student.getProfileImage());
            stmt.setInt(6, student.getStudentId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating student ID: " + student.getStudentId(), e);
        } finally {
            DatabaseConnection.getInstance().releaseConnection(conn);
        }
        return false;
    }

    /**
     * Delete a student by ID.
     */
    public boolean delete(int studentId) {
        String sql = "DELETE FROM students WHERE student_id = ?";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, studentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting student ID: " + studentId, e);
        } finally {
            DatabaseConnection.getInstance().releaseConnection(conn);
        }
        return false;
    }

    /**
     * Search students by name or email (partial match).
     */
    public List<Student> search(String query) {
        String sql = "SELECT * FROM students WHERE name LIKE ? OR email LIKE ? ORDER BY name ASC";
        List<Student> students = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            String searchPattern = "%" + query + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                students.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching students with query: " + query, e);
        } finally {
            DatabaseConnection.getInstance().releaseConnection(conn);
        }
        return students;
    }

    /**
     * Filter students by department.
     */
    public List<Student> filterByDepartment(String department) {
        String sql = "SELECT * FROM students WHERE department = ? ORDER BY name ASC";
        List<Student> students = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, department);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                students.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error filtering students by department: " + department, e);
        } finally {
            DatabaseConnection.getInstance().releaseConnection(conn);
        }
        return students;
    }

    /**
     * Get total number of students.
     */
    public int count() {
        String sql = "SELECT COUNT(*) FROM students";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting students", e);
        } finally {
            DatabaseConnection.getInstance().releaseConnection(conn);
        }
        return 0;
    }

    /**
     * Get distinct departments.
     */
    public List<String> getDepartments() {
        String sql = "SELECT DISTINCT department FROM students ORDER BY department ASC";
        List<String> departments = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                departments.add(rs.getString("department"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching departments", e);
        } finally {
            DatabaseConnection.getInstance().releaseConnection(conn);
        }
        return departments;
    }

    /**
     * Count students by department.
     */
    public int countByDepartment(String department) {
        String sql = "SELECT COUNT(*) FROM students WHERE department = ?";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, department);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting students by department: " + department, e);
        } finally {
            DatabaseConnection.getInstance().releaseConnection(conn);
        }
        return 0;
    }

    /**
     * Map a ResultSet row to a Student object.
     */
    private Student mapResultSet(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setStudentId(rs.getInt("student_id"));
        student.setName(rs.getString("name"));
        student.setEmail(rs.getString("email"));
        student.setPhone(rs.getString("phone"));
        student.setDepartment(rs.getString("department"));
        student.setProfileImage(rs.getString("profile_image"));
        student.setCreatedAt(rs.getTimestamp("created_at"));
        return student;
    }
}
