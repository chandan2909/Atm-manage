package Atm.management;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

public class HashUtil {
    // Using a more secure and consistent salt value
    private static final String SALT = "ATM_SYSTEM_SALT_2024";

    public static String hashPin(String pin) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String saltedPin = SALT + pin;
            byte[] hash = md.digest(saltedPin.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
} 