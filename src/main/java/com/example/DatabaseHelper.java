package com.example;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/PIZZARE";
    private static final String USER = "root";
    private static final String PASS = "02072005"; // Replace this with a more secure method.

    // Create a new account (no change)
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

    // Method to authenticate a user during login (no change)
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

    // Retrieve pizza names from the database (existing method)
    public static ArrayList<String> getPizzaNames() {
        return executeSelectQuery("SELECT pizza_name FROM Pizzas", "pizza_name");
    }

    public static List<Pizza> getPizzaDetails() {
        List<Pizza> pizzas = new ArrayList<>();

        String query = "SELECT p.pizza_id, p.pizza_name, t.topping_name, t.topping_price, t.topping_isvegan, t.toppping_isvegetarian " +
                "FROM pizzare.pizzas p " +
                "JOIN pizzare.pizzatoppings pt ON p.pizza_id = pt.pizza_id " +
                "JOIN pizzare.toppings t ON pt.topping_id = t.topping_id";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            Pizza currentPizza = null;
            int currentPizzaId = -1;

            while (rs.next()) {
                int pizzaId = rs.getInt("pizza_id");
                String pizzaName = rs.getString("pizza_name");

                // Debugging: Print pizza information to check if data is retrieved
                System.out.println("Pizza ID: " + pizzaId + ", Pizza Name: " + pizzaName);

                // If we're looking at a new pizza, create a new Pizza object
                if (pizzaId != currentPizzaId) {
                    currentPizza = new Pizza(pizzaName);
                    pizzas.add(currentPizza);
                    currentPizzaId = pizzaId;
                }

                // Retrieve and print topping information
                String toppingName = rs.getString("topping_name");
                double toppingPrice = rs.getDouble("topping_price");
                boolean toppingIsVegan = rs.getBoolean("topping_isvegan");
                boolean toppingIsVegetarian = rs.getBoolean("toppping_isvegetarian");

                System.out.println("Topping: " + toppingName + ", Price: " + toppingPrice);

                // Add the current topping to the pizza
                currentPizza.addTopping(new Topping(toppingName, toppingPrice, toppingIsVegan, toppingIsVegetarian));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return pizzas;
    }
    // Retrieve drinks names from the database (existing method)
    public static ArrayList<String> getDrinksNames() {
        return executeSelectQuery("SELECT drink_name FROM Drinks", "drink_name");
    }

    // Retrieve dessert names from the database (existing method)
    public static ArrayList<String> getDessertNames() {
        return executeSelectQuery("SELECT dessert_name FROM Desserts", "dessert_name");
    }

    // Helper method to execute select queries (existing method)
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