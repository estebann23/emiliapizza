package com.example;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseHelper {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/PIZZARE";
    private static final String USER = "root";
    private static final String PASS = "02072005"; // You should replace this with a more secure way of handling passwords.

    // Create a new account
    public static boolean createAccount(String name, String gender, String birthdate,
                                        String emailAddress, String phoneNumber,
                                        String username, String password) {
        String insertSQL = "INSERT INTO Customers (Name, Gender, Birthdate, Email_Address, Phone_Number, Username, Password) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            // Hash the password with BCrypt before storing it
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            // Set parameters for the PreparedStatement
            pstmt.setString(1, name);
            pstmt.setString(2, gender);
            pstmt.setString(3, birthdate);
            pstmt.setString(4, emailAddress);
            pstmt.setString(5, phoneNumber);
            pstmt.setString(6, username);
            pstmt.setString(7, hashedPassword); // Store the hashed password

            // Execute the query
            pstmt.executeUpdate();
            return true;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false; // Return false if there is an error during account creation
        }
    }

    // Method to authenticate a user during login
    public static boolean authenticateUser(String username, String password) {
        String query = "SELECT Password FROM Customers WHERE Username = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedHashedPassword = rs.getString("Password");
                return BCrypt.checkpw(password, storedHashedPassword); // Check if the password matches
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    // Retrieve pizza names from the database
    public static ArrayList<String> getPizzaNames() {
        return executeSelectQuery("SELECT pizza_name FROM Pizzas", "pizza_name");
    }

    // Retrieve drinks names from the database
    public static ArrayList<String> getDrinksNames() {
        return executeSelectQuery("SELECT drink_name FROM Drinks", "drink_name");
    }

    // Retrieve dessert names from the database
    public static ArrayList<String> getDessertNames() {
        return executeSelectQuery("SELECT dessert_name FROM Desserts", "dessert_name");
    }

    // Helper method to execute select queries
    private static ArrayList<String> executeSelectQuery(String query, String columnLabel) {
        ArrayList<String> resultList = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                resultList.add(rs.getString(columnLabel));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultList;
    }
}