package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility for password hashing operations.
 */
public final class HashUtil {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private HashUtil() {
        // Utility class
    }

    public static String generateSalt() {
        byte[] salt = new byte[16];
        SECURE_RANDOM.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static String hashPassword(String password, String salt) {
        if (password == null || salt == null) {
            throw new IllegalArgumentException("Password and salt are required");
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest((password + salt).getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 algorithm not available", ex);
        }
    }

    public static boolean verifyPassword(String password, String storedHash, String salt) {
        if (password == null || storedHash == null || salt == null) {
            return false;
        }
        String computedHash = hashPassword(password, salt);
        return computedHash.equals(storedHash);
    }
}
