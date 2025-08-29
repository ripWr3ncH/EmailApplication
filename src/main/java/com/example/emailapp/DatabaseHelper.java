package com.example.emailapp;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {

    private static final String URL = "jdbc:mysql://127.0.02:3306/foremailaccounts";
    private static final String USER = "root";
    private static final String PASSWORD = "@dewan2001zisan";

    // Key for AES encryption (must be 16, 24, or 32 bytes for AES)
    private static final String SECRET_KEY = "MySuperSecretKey"; // Change this key to a secure value in production

    // Establish a database connection
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Encrypt the password using AES
    private static String encryptPassword(String plainTextPassword) {
        try {
            SecretKey secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(plainTextPassword.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error while encrypting the password", e);
        }
    }

    // Decrypt the password using AES
    private static String decryptPassword(String encryptedPassword) {
        try {
            SecretKey secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedPassword));
            return new String(decryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error while decrypting the password", e);
        }
    }

    // Save or update an account in the database
    public static void saveAccount(String email, String password) {
        String query = "INSERT INTO email_accounts (email, password) VALUES (?, ?) ON DUPLICATE KEY UPDATE password = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            // Encrypt the password before saving
            String encryptedPassword = encryptPassword(password);

            // Set the query parameters
            statement.setString(1, email);
            statement.setString(2, encryptedPassword);
            statement.setString(3, encryptedPassword);

            // Execute the update
            int rowsAffected = statement.executeUpdate();
            System.out.println("Query executed successfully. Rows affected: " + rowsAffected);

        } catch (SQLException e) {
            System.err.println("Error while saving account to database.");
            e.printStackTrace();
        }
    }

    // Retrieve the original password for a given email (decrypt it)
    public static String getPasswordForEmail(String email) {
        String query = "SELECT password FROM email_accounts WHERE email = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String encryptedPassword = resultSet.getString("password");
                return decryptPassword(encryptedPassword); // Decrypt the password before returning
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if no password is found
    }

    // Check if an account is saved
    public static boolean isAccountSaved(String email) {
        String query = "SELECT COUNT(*) FROM email_accounts WHERE email = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Fetch a list of saved email accounts for suggestions
    public static List<String> getSavedEmails() {
        String query = "SELECT email FROM email_accounts";
        List<String> emails = new ArrayList<>();
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                emails.add(resultSet.getString("email"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return emails;
    }

    // Delete an account
    public static void deleteAccount(String email) {
        String query = "DELETE FROM email_accounts WHERE email = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, email);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Clear all saved accounts (optional feature)
    public static void clearSavedAccounts() {
        String query = "DELETE FROM email_accounts";
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            int rowsAffected = statement.executeUpdate(query);
            System.out.println("Rows affected: " + rowsAffected);
        } catch (SQLException e) {
            System.err.println("Error while clearing saved accounts.");
            e.printStackTrace();
        }
    }
}
