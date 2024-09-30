package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PizzaToppingInserter {

    // Database credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/emiliadb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "mysql2311";

    public static void main(String[] args) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            String sql = "INSERT INTO emiliadb.pizzatoppings (pizza_id, topping_id) VALUES (?, ?)";
            statement = connection.prepareStatement(sql);

            // Mapping pizzas to their correct toppings (Pizza ID -> Topping IDs)
            int[][] pizzaToppingData = {
                    // Margherita
                    {1, 1}, {1, 2},
                    // BBQ Chicken
                    {2, 3}, {2, 4}, {2, 6},
                    // Kebab
                    {3, 5}, {3, 7}, {3, 20},
                    // Pepperoni
                    {4, 8},
                    // Quattro Formaggi
                    {5, 1}, {5, 10}, {5, 11}, {5, 12},
                    // Tartufo Porcino
                    {6, 30}, {6, 31},
                    // Vegan Riviera
                    {7, 19}, {7, 18}, {7, 20},
                    // Tropical Delight
                    {8, 22}, {8, 23}, {8, 28},
                    // Settebello
                    {9, 27}, {9, 30}, {9, 10},
                    // Bresca
                    {10, 28}, {10, 26}
            };

            // Insert pizza-topping mappings into the database
            for (int[] entry : pizzaToppingData) {
                statement.setInt(1, entry[0]);  // pizza_id
                statement.setInt(2, entry[1]);  // topping_id
                statement.addBatch();           // Add to batch for efficiency
            }

            // Execute the batch insert
            statement.executeBatch();
            System.out.println("Pizza-topping relationships inserted successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}