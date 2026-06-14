package com.srms.service;

import com.srms.dao.UserDAO;
import com.srms.exception.AuthException;
import com.srms.model.User;
import com.srms.model.Student;
import com.srms.security.PasswordUtil;
import com.srms.security.SessionManager;
import jakarta.servlet.http.HttpServletRequest;

import java.util.logging.Logger;

/**
 * Service for authentication operations.
 */
public class AuthService {

    private static final Logger LOGGER = Logger.getLogger(AuthService.class.getName());
    private final UserDAO userDAO = new UserDAO();

    /**
     * Authenticate user with username and password.
     * Creates session on success.
     * @return the authenticated User (password field cleared)
     */
    public User login(HttpServletRequest request, String username, String password, boolean rememberMe) {
        if (username == null || username.trim().isEmpty()) {
            throw new AuthException("Username is required");
        }
        if (password == null || password.isEmpty()) {
            throw new AuthException("Password is required");
        }

        User user = userDAO.findByUsername(username.trim().toLowerCase());
        if (user == null) {
            throw new AuthException("Invalid username or password");
        }

        if (!PasswordUtil.verifyPassword(password, user.getPassword())) {
            throw new AuthException("Invalid username or password");
        }

        // Create session
        SessionManager.createSession(request, user);

        // Manage Remember Me token
        if (rememberMe) {
            String token = java.util.UUID.randomUUID().toString();
            userDAO.updateRememberToken(user.getUserId(), token);
            user.setRememberToken(token);
        } else {
            userDAO.updateRememberToken(user.getUserId(), null);
            user.setRememberToken(null);
        }

        // Clear password before returning
        user.setPassword(null);
        LOGGER.info("User logged in: " + username + " (role: " + user.getRole() + ")");
        return user;
    }

    /**
     * Log out the current user by destroying the session.
     */
    public void logout(HttpServletRequest request) {
        User user = SessionManager.getLoggedInUser(request);
        if (user != null) {
            userDAO.updateRememberToken(user.getUserId(), null);
            LOGGER.info("User logged out: " + user.getUsername());
        }
        SessionManager.destroySession(request);
    }

    /**
     * Get the current session user.
     */
    public User getCurrentUser(HttpServletRequest request) {
        User user = SessionManager.getLoggedInUser(request);
        if (user != null) {
            user.setPassword(null);
        }
        return user;
    }

    /**
     * Register a new student account (blocks admin signup).
     */
    public User registerStudent(String name, String email, String phone, String department, String username, String password) {
        if (username == null || username.trim().isEmpty()) throw new com.srms.exception.ValidationException("Username is required");
        if (password == null || password.trim().isEmpty()) throw new com.srms.exception.ValidationException("Password is required");

        // Assign default values if they are not provided (simplified registration form)
        if (name == null || name.trim().isEmpty()) {
            String cleanedName = username.replaceAll("[^A-Za-z\\s.'-]", "").trim();
            if (cleanedName.length() < 2) {
                cleanedName = "Student User";
            }
            name = cleanedName;
        }
        if (email == null || email.trim().isEmpty()) {
            email = username.trim().toLowerCase().replaceAll("[^a-z0-9._-]", "") + "@srms.com";
        }
        if (department == null || department.trim().isEmpty()) {
            department = "Computer Science";
        }

        // Check if username already exists
        if (userDAO.findByUsername(username.trim().toLowerCase()) != null) {
            throw new com.srms.exception.ValidationException("Username is already taken");
        }

        // Check if email already exists
        com.srms.dao.StudentDAO studentDAO = new com.srms.dao.StudentDAO();
        if (studentDAO.findByEmail(email.trim()) != null) {
            throw new com.srms.exception.ValidationException("Email is already registered");
        }

        // Create student
        Student student = new Student();
        student.setName(com.srms.security.InputValidator.validateName(name));
        student.setEmail(com.srms.security.InputValidator.validateEmail(email));
        student.setPhone(com.srms.security.InputValidator.validatePhone(phone));
        student.setDepartment(com.srms.security.InputValidator.validateDepartment(department));

        Student createdStudent = studentDAO.create(student);
        if (createdStudent == null) {
            throw new RuntimeException("Failed to create student record");
        }

        // Create user
        User user = new User();
        user.setUsername(username.trim().toLowerCase());
        user.setPassword(com.srms.security.PasswordUtil.hashPassword(password));
        user.setRole("STUDENT"); // Restrict to STUDENT role only
        user.setStudentId(createdStudent.getStudentId());

        User createdUser = userDAO.create(user);
        if (createdUser == null) {
            throw new RuntimeException("Failed to create user account");
        }

        createdUser.setPassword(null);
        return createdUser;
    }
}
