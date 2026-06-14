package com.srms.security;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility for password hashing and verification using BCrypt.
 */
public class PasswordUtil {

    private static final int BCRYPT_ROUNDS = 10;

    private PasswordUtil() {}

    /**
     * Hash a plain text password using BCrypt.
     * @param plainPassword the plain text password
     * @return the BCrypt hash
     */
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_ROUNDS));
    }

    /**
     * Verify a plain text password against a BCrypt hash.
     * @param plainPassword the plain text password to check
     * @param hashedPassword the stored BCrypt hash
     * @return true if the password matches
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            return false;
        }
    }
}
