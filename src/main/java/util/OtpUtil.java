package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public final class OtpUtil {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private OtpUtil() {
        // Utility class
    }

    public static String generateSixDigitOtp() {
        int otp = 100000 + SECURE_RANDOM.nextInt(900000);
        return Integer.toString(otp);
    }

    public static String hashOtp(String otp) {
        if (otp == null) {
            throw new IllegalArgumentException("OTP is required");
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(otp.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 algorithm not available", ex);
        }
    }

    public static boolean matches(String rawOtp, String otpHash) {
        if (rawOtp == null || otpHash == null) {
            return false;
        }
        byte[] candidate = hashOtp(rawOtp).getBytes(StandardCharsets.UTF_8);
        byte[] stored = otpHash.getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(candidate, stored);
    }
}
