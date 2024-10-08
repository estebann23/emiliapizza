package com.example;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/emiliadb";
    private static final String USER = "root";
    private static final String PASS = "mysql2311";

    public static boolean createAccount(String name, String gender, String birthdate,
                                        String emailAddress, String phoneNumber,
                                        String username, String password) {

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            // Generate next Customer_ID
            String query = "SELECT MAX(Customer_ID) FROM Customers";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            int customerId = 1;
            if (rs.next()) {
                customerId = rs.getInt(1) + 1;
            }

            String insertSQL = "INSERT INTO Customers (Customer_ID, Name, Gender, Birthdate, Email_Address, Phone_Number, Username, Password) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement pstmt = conn.prepareStatement(insertSQL);

            pstmt.setInt(1, customerId);
            pstmt.setString(2, name);
            pstmt.setString(3, gender);
            pstmt.setString(4, birthdate);
            pstmt.setString(5, emailAddress);
            pstmt.setString(6, phoneNumber);
            pstmt.setString(7, username);
            pstmt.setString(8, hashedPassword);

            pstmt.executeUpdate();
            return true;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
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

    public static ArrayList<String> getPizzaNames() {
        return executeSelectQuery("SELECT pizza_name FROM Pizzas", "pizza_name");
    }



    public static List<Pizza> getPizzaDetails() {
        List<Pizza> pizzas = new ArrayList<>();

        String query = "SELECT p.pizza_id, p.pizza_name, t.topping_name, t.topping_price, t.topping_isvegan, t.toppping_isvegetarian " +
                "FROM pizzas p " +
                "JOIN pizzatoppings pt ON p.pizza_id = pt.pizza_id " +
                "JOIN toppings t ON pt.topping_id = t.topping_id";

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
    public static List<Drink> getDrinkDetails() {
        List<Drink> drinks = new ArrayList<>();
        String query = "SELECT drink_name, drink_price FROM Drinks";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString("drink_name");
                double price = rs.getDouble("drink_price");
                drinks.add(new Drink(name, price));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return drinks;
    }

    public static ArrayList<String> getDrinksNames() {
        return executeSelectQuery("SELECT drink_name FROM Drinks", "drink_name");
    }
    public static double getDrinkPriceByName(String drinkName) {
        double price = 0.0;
        String query = "SELECT drink_price FROM Drinks WHERE drink_name = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, drinkName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                price = rs.getDouble("drink_price");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return price;
    }

    public static List<Dessert> getDessertDetails() {
        List<Dessert> desserts = new ArrayList<>();
        String query = "SELECT dessert_name, dessert_price FROM Desserts";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString("dessert_name");
                double price = rs.getDouble("dessert_price");
                desserts.add(new Dessert(name, price));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return desserts;
    }

    public static double getDessertPriceByName(String dessertName) {
        double price = 0.0;
        String query = "SELECT dessert_price FROM Desserts WHERE dessert_name = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, dessertName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                price = rs.getDouble("dessert_price");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return price;
    }

    public static ArrayList<String> getDessertNames() {
        return executeSelectQuery("SELECT dessert_name FROM Desserts", "dessert_name");
    }

    // Helper method used in DeliveryPanel class
    public static String getDeliveryDriver(String postcode) {
        String deliveryDriver = null;
        try (Connection conn = DriverManager.getConnection(DB_URL,USER,PASS);
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT d.DeliveryDriver_Name FROM deliverydrivers d " +
                             "JOIN postcode p ON d.DeliveryDriver_ID = p.DeliveryDriver_ID " +
                             "WHERE p.postcode = ?")) {
            pstmt.setString(1, postcode);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                deliveryDriver = rs.getString("DeliveryDriver_Name");
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return deliveryDriver;
    }
    public static double getPizzaPriceByName(String pizzaName) {
        double totalIngredientCost = 0.0;
        String query = "SELECT t.topping_price " +
                "FROM pizzas p " +
                "JOIN pizzatoppings pt ON p.pizza_id = pt.pizza_id " +
                "JOIN toppings t ON pt.topping_id = t.topping_id " +
                "WHERE p.pizza_name = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, pizzaName);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                totalIngredientCost += rs.getDouble("topping_price");
            }

            // Calculate the final price
            return PizzaPriceCalculator.calculateFinalPrice(totalIngredientCost);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if an error occurs
    }

    public static void updatePizzaPricesForAll() {
        String selectPizzaIdsQuery = "SELECT pizza_id FROM pizzas";
        String selectIngredientCostQuery = "SELECT SUM(t.topping_price) AS total_ingredient_cost " +
                "FROM pizzatoppings pt " +
                "JOIN toppings t ON pt.topping_id = t.topping_id " +
                "WHERE pt.pizza_id = ?";
        String updatePizzaPriceQuery = "UPDATE pizzas SET pizza_finalprice = ? WHERE pizza_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement selectPizzaStmt = conn.prepareStatement(selectPizzaIdsQuery);
             PreparedStatement selectCostStmt = conn.prepareStatement(selectIngredientCostQuery);
             PreparedStatement updateStmt = conn.prepareStatement(updatePizzaPriceQuery);
             ResultSet pizzaRs = selectPizzaStmt.executeQuery()) {

            // Loop through all pizzas
            while (pizzaRs.next()) {
                int pizzaId = pizzaRs.getInt("pizza_id");

                // Fetch the total ingredient cost for the current pizza
                selectCostStmt.setInt(1, pizzaId);
                ResultSet costRs = selectCostStmt.executeQuery();

                if (costRs.next()) {
                    double ingredientCost = costRs.getDouble("total_ingredient_cost");

                    // Calculate the final pizza price using the PizzaPriceCalculator
                    double finalPrice = PizzaPriceCalculator.calculateFinalPrice(ingredientCost);

                    // Update the pizza's price in the database
                    updateStmt.setDouble(1, finalPrice);
                    updateStmt.setInt(2, pizzaId);
                    updateStmt.executeUpdate();
                }
                costRs.close(); // Close the cost result set after processing each pizza
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<DiscountCode> getDiscountCodes() {
        List<DiscountCode> discountCodes = new ArrayList<>();
        String query = "SELECT DiscountCode, Discount_Value, DiscountCode_isAvailable FROM discountcodes";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String code = rs.getString("DiscountCode");
                double value = rs.getDouble("Discount_Value");
                boolean isUsed = rs.getBoolean("DiscountCode_isAvailable");
                discountCodes.add(new DiscountCode(code, value, isUsed));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return discountCodes;
    }


    // Helper method for querying the Items Selection from the DB
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

    public static int getTotalPizzasOrderedByCustomer(String username) {
        int totalPizzasOrdered = 0;
        String query = "SELECT SUM(oi.OrderItem_Amount) AS total_pizzas_ordered " +
                "FROM customers c " +
                "JOIN orders o ON c.customer_id = o.customer_id " +
                "JOIN orderitems oi ON o.order_id = oi.order_id " +
                "JOIN pizzas p ON oi.pizza_id = p.pizza_id " +
                "WHERE c.username = ? " +
                "GROUP BY c.username";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                totalPizzasOrdered = rs.getInt("total_pizzas_ordered");
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalPizzasOrdered;
    }
}