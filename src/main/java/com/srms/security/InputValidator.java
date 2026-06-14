package com.srms.security;

import com.srms.exception.ValidationException;

import java.util.regex.Pattern;

/**
 * Input validation and sanitization utilities.
 * Provides XSS protection and data validation.
 */
public class InputValidator {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^[0-9]{10,15}$");
    private static final Pattern NAME_PATTERN =
            Pattern.compile("^[A-Za-z\\s.'-]{2,100}$");
    private static final Pattern SUBJECT_CODE_PATTERN =
            Pattern.compile("^[A-Z]{2,6}[0-9]{2,4}$");
    private static final Pattern USERNAME_PATTERN =
            Pattern.compile("^[a-zA-Z0-9._-]{3,50}$");

    private InputValidator() {}

    /**
     * Sanitize input to prevent XSS attacks.
     * Escapes HTML special characters.
     */
    public static String sanitize(String input) {
        if (input == null) return null;
        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;")
                .replace("/", "&#x2F;");
    }

    /**
     * Validate and sanitize a required string field.
     */
    public static String validateRequired(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " is required");
        }
        return sanitize(value.trim());
    }

    /**
     * Validate email format.
     */
    public static String validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email is required");
        }
        email = email.trim().toLowerCase();
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException("Invalid email format");
        }
        return email;
    }

    /**
     * Validate phone number format.
     */
    public static String validatePhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return null; // Phone is optional
        }
        phone = phone.trim().replaceAll("[\\s-]", "");
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new ValidationException("Invalid phone number. Must be 10-15 digits");
        }
        return phone;
    }

    /**
     * Validate person name.
     */
    public static String validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Name is required");
        }
        name = name.trim();
        if (!NAME_PATTERN.matcher(name).matches()) {
            throw new ValidationException("Invalid name. Must be 2-100 characters, letters only");
        }
        return sanitize(name);
    }

    /**
     * Validate username.
     */
    public static String validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new ValidationException("Username is required");
        }
        username = username.trim().toLowerCase();
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new ValidationException("Invalid username. Must be 3-50 characters, alphanumeric with . _ -");
        }
        return username;
    }

    /**
     * Validate subject code format (e.g., MATH101).
     */
    public static String validateSubjectCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new ValidationException("Subject code is required");
        }
        code = code.trim().toUpperCase();
        if (!SUBJECT_CODE_PATTERN.matcher(code).matches()) {
            throw new ValidationException("Invalid subject code format. Example: MATH101");
        }
        return code;
    }

    /**
     * Validate marks value (0-100).
     */
    public static double validateMarks(double marks) {
        if (marks < 0 || marks > 100) {
            throw new ValidationException("Marks must be between 0 and 100");
        }
        return marks;
    }

    /**
     * Validate department name.
     */
    public static String validateDepartment(String department) {
        if (department == null || department.trim().isEmpty()) {
            throw new ValidationException("Department is required");
        }
        return sanitize(department.trim());
    }

    /**
     * Validate a positive integer ID.
     */
    public static int validateId(String idStr, String fieldName) {
        if (idStr == null || idStr.trim().isEmpty()) {
            throw new ValidationException(fieldName + " is required");
        }
        try {
            int id = Integer.parseInt(idStr.trim());
            if (id <= 0) {
                throw new ValidationException(fieldName + " must be a positive integer");
            }
            return id;
        } catch (NumberFormatException e) {
            throw new ValidationException(fieldName + " must be a valid integer");
        }
    }

    /**
     * Validate password strength.
     */
    public static String validatePassword(String password) {
        if (password == null || password.length() < 6) {
            throw new ValidationException("Password must be at least 6 characters long");
        }
        if (password.length() > 100) {
            throw new ValidationException("Password must be at most 100 characters long");
        }
        return password;
    }
}
