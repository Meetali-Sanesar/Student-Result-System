package com.srms.service;

import com.srms.dao.StudentDAO;
import com.srms.dao.UserDAO;
import com.srms.dao.ResultDAO;
import com.srms.exception.ResourceNotFoundException;
import com.srms.exception.ValidationException;
import com.srms.model.Student;
import com.srms.model.User;
import com.srms.security.InputValidator;
import com.srms.security.PasswordUtil;

import java.util.List;
import java.util.logging.Logger;

/**
 * Service for student management operations.
 */
public class StudentService {

    private static final Logger LOGGER = Logger.getLogger(StudentService.class.getName());
    private final StudentDAO studentDAO = new StudentDAO();
    private final UserDAO userDAO = new UserDAO();

    /**
     * Add a new student and create a corresponding user account.
     */
    public Student addStudent(Student student) {
        // Validate input
        student.setName(InputValidator.validateName(student.getName()));
        student.setEmail(InputValidator.validateEmail(student.getEmail()));
        student.setPhone(InputValidator.validatePhone(student.getPhone()));
        student.setDepartment(InputValidator.validateDepartment(student.getDepartment()));

        // Check for duplicate email
        Student existing = studentDAO.findByEmail(student.getEmail());
        if (existing != null) {
            throw new ValidationException("A student with this email already exists");
        }

        // Create student record
        Student created = studentDAO.create(student);
        if (created == null) {
            throw new RuntimeException("Failed to create student");
        }

        // Create a user account for the student
        String username = generateUsername(student.getEmail());
        String defaultPassword = PasswordUtil.hashPassword("student123");

        User user = new User();
        user.setUsername(username);
        user.setPassword(defaultPassword);
        user.setRole("STUDENT");
        user.setStudentId(created.getStudentId());
        userDAO.create(user);

        LOGGER.info("Student added: " + created.getName() + " (ID: " + created.getStudentId() + ")");
        return created;
    }

    /**
     * Update an existing student.
     */
    public Student updateStudent(int studentId, Student student) {
        Student existing = studentDAO.findById(studentId);
        if (existing == null) {
            throw new ResourceNotFoundException("Student not found with ID: " + studentId);
        }

        // Validate input
        student.setName(InputValidator.validateName(student.getName()));
        student.setEmail(InputValidator.validateEmail(student.getEmail()));
        student.setPhone(InputValidator.validatePhone(student.getPhone()));
        student.setDepartment(InputValidator.validateDepartment(student.getDepartment()));

        // Check email uniqueness (if changed)
        if (!existing.getEmail().equals(student.getEmail())) {
            Student emailCheck = studentDAO.findByEmail(student.getEmail());
            if (emailCheck != null) {
                throw new ValidationException("A student with this email already exists");
            }
        }

        student.setStudentId(studentId);
        // Preserve profile image if not explicitly set
        if (student.getProfileImage() == null) {
            student.setProfileImage(existing.getProfileImage());
        }

        boolean updated = studentDAO.update(student);
        if (!updated) {
            throw new RuntimeException("Failed to update student");
        }

        LOGGER.info("Student updated: " + student.getName() + " (ID: " + studentId + ")");
        return studentDAO.findById(studentId);
    }

    /**
     * Delete a student and their user account.
     */
    public void deleteStudent(int studentId) {
        Student existing = studentDAO.findById(studentId);
        if (existing == null) {
            throw new ResourceNotFoundException("Student not found with ID: " + studentId);
        }

        // Delete user account first
        userDAO.deleteByStudentId(studentId);

        // Delete student (cascade deletes marks, results, notifications)
        boolean deleted = studentDAO.delete(studentId);
        if (!deleted) {
            throw new RuntimeException("Failed to delete student");
        }
        
        // Recalculate student ranks
        new ResultDAO().updateRanks();
        
        LOGGER.info("Student deleted: " + existing.getName() + " (ID: " + studentId + ")");
    }

    /**
     * Get a student by ID.
     */
    public Student getStudent(int studentId) {
        Student student = studentDAO.findById(studentId);
        if (student == null) {
            throw new ResourceNotFoundException("Student not found with ID: " + studentId);
        }
        return student;
    }

    /**
     * Get all students.
     */
    public List<Student> getAllStudents() {
        return studentDAO.findAll();
    }

    /**
     * Search students by name or email.
     */
    public List<Student> searchStudents(String query) {
        if (query == null || query.trim().isEmpty()) {
            return studentDAO.findAll();
        }
        return studentDAO.search(InputValidator.sanitize(query.trim()));
    }

    /**
     * Filter students by department.
     */
    public List<Student> filterByDepartment(String department) {
        if (department == null || department.trim().isEmpty()) {
            return studentDAO.findAll();
        }
        return studentDAO.filterByDepartment(department.trim());
    }

    /**
     * Get all distinct departments.
     */
    public List<String> getDepartments() {
        return studentDAO.getDepartments();
    }

    /**
     * Get total student count.
     */
    public int getStudentCount() {
        return studentDAO.count();
    }

    /**
     * Update student profile image.
     */
    public void updateProfileImage(int studentId, String imagePath) {
        Student student = studentDAO.findById(studentId);
        if (student == null) {
            throw new ResourceNotFoundException("Student not found with ID: " + studentId);
        }
        student.setProfileImage(imagePath);
        studentDAO.update(student);
    }

    /**
     * Generate a username from email (part before @).
     */
    private String generateUsername(String email) {
        String base = email.split("@")[0].toLowerCase().replaceAll("[^a-z0-9._-]", "");
        // Check uniqueness
        if (userDAO.findByUsername(base) == null) {
            return base;
        }
        // Append number if exists
        int counter = 1;
        while (userDAO.findByUsername(base + counter) != null) {
            counter++;
        }
        return base + counter;
    }
}
