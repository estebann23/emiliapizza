package com.example;

import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DatabaseHelper {
    private static PizzaDeliveryApp app;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/PIZZARE";
    private static final String USER = "root";
    private static final String PASS = "02072005";
    private static int currentOrderId = -1;
    private static Connection conn;

    // Constructor to initialize the DatabaseHelper with the app instance
    public DatabaseHelper(PizzaDeliveryApp app) {
        DatabaseHelper.app = app;
    }
    public static void setAppInstance(PizzaDeliveryApp appInstance) {
        app = appInstance;
    }

    // Generates a unique customer ID
    public static String generateCustomerID() {
        return UUID.randomUUID().toString();
    }



    public static void markDriverUnavailable(String driverName) {
        String updateSQL = "UPDATE deliverydrivers SET isAvailable = 0 WHERE DeliveryDriver_Name = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
            pstmt.setString(1, driverName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Timer timer = new Timer(30 * 60 * 1000, e -> markDriverAvailable(driverName));
        timer.setRepeats(false);
        timer.start();
    }

    public static void markDriverAvailable(String driverName) {
        String updateSQL = "UPDATE deliverydrivers SET isAvailable = 1 WHERE DeliveryDriver_Name = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
            pstmt.setString(1, driverName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Optional<Double> getItemPriceByNameAndType(String name, CartItem.ItemType itemType) {
        String query = switch (itemType) {
            case PIZZA -> "SELECT pizza_finalprice AS price FROM pizzas WHERE pizza_name = ?";
            case DRINK -> "SELECT drink_price AS price FROM drinks WHERE drink_name = ?";
            case DESSERT -> "SELECT dessert_price AS price FROM desserts WHERE dessert_name = ?";
        };

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(rs.getDouble("price"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return Optional.empty();
    }


    // Generates a unique order ID
    public static int generateUniqueOrderId() throws SQLException {
        int uniqueId = Math.abs(UUID.randomUUID().hashCode());
        String query = "SELECT COUNT(*) FROM orders WHERE Order_ID = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, uniqueId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return generateUniqueOrderId();
            }
        }
        return uniqueId;
    }
    public static void setCurrentOrderId(int orderId) {
        currentOrderId = orderId;
    }

    public static boolean updateOrderStatusToCanceled(int orderId) {
        String updateOrderSQL = "UPDATE orders SET Order_Status = 'Canceled', Order_EndTime = NOW() WHERE Order_ID = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(updateOrderSQL)) {

            pstmt.setInt(1, orderId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0; // Return true if the update was successful

        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false if the update failed
        }
    }
    // Updates an existing order with the specified details including Order_Date, Order_Status, and Order_StartTime
    public static boolean updateOrderDetails(int orderId, double totalAmount, String deliveryDriver) {
        String updateOrderSQL = "UPDATE orders SET Total_Amount = ?, DeliveryDriver_ID = (SELECT DeliveryDriver_ID FROM deliverydrivers WHERE DeliveryDriver_Name = ?), " +
                "Order_Date = NOW(), Order_Status = 'Order Confirmed', Order_StartTime = NOW() WHERE Order_ID = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(updateOrderSQL)) {

            pstmt.setDouble(1, totalAmount);
            pstmt.setString(2, deliveryDriver);
            pstmt.setInt(3, orderId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0; // Return true if the update was successful
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false if the update failed
        }
    }



    // Creates a new order, generates a unique order ID, stores it in the database, and sets it as the current order ID
    public static void createNewOrder(int customerId) {
        try {
            int orderId = generateUniqueOrderId();
            currentOrderId = orderId;

            String insertOrderSQL = "INSERT INTO orders (Order_ID, Customer_ID) " +
                    "VALUES (?, ?)";

            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                 PreparedStatement pstmt = conn.prepareStatement(insertOrderSQL)) {

                pstmt.setInt(1, orderId);
                pstmt.setInt(2, customerId);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // Returns the current order ID
    public static int getCurrentOrderId() {
        return currentOrderId;
    }

    // Creates a new account for a customer
    public static boolean createAccount(String name, String gender, String birthdate, String emailAddress, String phoneNumber, String username, String password) {
        String checkUserSQL = "SELECT COUNT(*) FROM Customers WHERE Username = ?";
        String checkEmailSQL = "SELECT COUNT(*) FROM Customers WHERE Email_Address = ?";
        String getMaxCustomerIdSQL = "SELECT MAX(Customer_ID) FROM Customers";
        String insertSQL = "INSERT INTO Customers (Customer_ID, Name, Gender, Birthdate, Email_Address, Phone_Number, Username, Password) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            // Check for duplicate username
            try (PreparedStatement checkUserStmt = conn.prepareStatement(checkUserSQL)) {
                checkUserStmt.setString(1, username);
                ResultSet rs = checkUserStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("Username already exists.");
                    return false;
                }
            }

            // Check for duplicate email
            try (PreparedStatement checkEmailStmt = conn.prepareStatement(checkEmailSQL)) {
                checkEmailStmt.setString(1, emailAddress);
                ResultSet rs = checkEmailStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("Email address already exists.");
                    return false;
                }
            }
            // Generate a new Customer_ID (max customer_id + 1)
            int newCustomerId = 1;
            try (PreparedStatement getMaxCustomerIdStmt = conn.prepareStatement(getMaxCustomerIdSQL)) {
                ResultSet rs = getMaxCustomerIdStmt.executeQuery();
                if (rs.next()) {
                    newCustomerId = rs.getInt(1) + 1;
                }
            }


            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            String customerId = generateCustomerID();

            try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
                pstmt.setInt(1, newCustomerId);
                pstmt.setString(2, name);
                pstmt.setString(3, gender);
                pstmt.setString(4, birthdate);
                pstmt.setString(5, emailAddress);
                pstmt.setString(6, phoneNumber);
                pstmt.setString(7, username);
                pstmt.setString(8, hashedPassword);
                pstmt.executeUpdate();
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }


    // Authenticates a user during login
    public static boolean authenticateUser(String username, String password) {
        String query = "SELECT Password FROM Customers WHERE Username = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedHashedPassword = rs.getString("Password");
                return BCrypt.checkpw(password, storedHashedPassword);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    // Helper method to get customer ID by username
    public static int getCustomerIdByUsername(String username) {
        String query = "SELECT Customer_ID FROM Customers WHERE Username = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("Customer_ID");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1; // Return -1 if no ID is found or if there's an error
    }

    // Method to get Pizza_ID by Pizza Name
    public static int getPizzaIdByName(String pizzaName) {
        String query = "SELECT Pizza_ID FROM Pizzas WHERE Pizza_Name = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, pizzaName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("Pizza_ID");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1; // Return -1 if Pizza_ID is not found
    }

    // Method to get Drink_ID by Drink Name
    public static int getDrinkIdByName(String drinkName) {
        String query = "SELECT Drink_ID FROM Drinks WHERE Drink_Name = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, drinkName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("Drink_ID");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1;  // Return -1 if Drink_ID is not found
    }

    // Method to get Dessert_ID by Dessert Name
    public static int getDessertIdByName(String dessertName) {
        String query = "SELECT Dessert_ID FROM Desserts WHERE Dessert_Name = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, dessertName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("Dessert_ID");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1;  // Return -1 if Dessert_ID is not found
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
                drinks.add(new Drink(name,price));
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
    public static BatchInfo getOrCreateBatchForOrder(Timestamp orderStartTime) {
        BatchInfo batchInfo = getExistingBatchForTimeWindow(orderStartTime);
        if (batchInfo == null) {
            batchInfo = createNewBatch(orderStartTime);
        }
        return batchInfo;
        }
/*
    public static int getExistingBatchForPostcode(String postcode) {
        String query = "SELECT o.Batch_ID FROM orders o " +
                "JOIN orderitems oi ON o.Order_ID = oi.Order_ID " +
                "WHERE o.Postcode = ? AND TIMESTAMPDIFF(MINUTE, o.Order_StartTime, NOW()) <= 3 " +
                "GROUP BY o.Batch_ID HAVING SUM(oi.Pizza_Quantity) <= 3 LIMIT 1";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, postcode);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("Batch_ID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

 */
    public static BatchInfo getExistingBatchForTimeWindow(Timestamp orderStartTime) {
        String query = "SELECT b.Batch_ID, b.DeliveryDriver_Name " +
                "FROM batches b " +
                "WHERE ABS(TIMESTAMPDIFF(MINUTE, b.Created_At, ?)) <= 3 " +
                "AND b.Batch_ID IN (" +
                "    SELECT Batch_ID FROM orders " +
                "    GROUP BY Batch_ID HAVING COUNT(*) < 3" +
                ") LIMIT 1";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setTimestamp(1, orderStartTime);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int batchId = rs.getInt("Batch_ID");
                String driverName = rs.getString("DeliveryDriver_Name");
                return new BatchInfo(batchId, driverName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // No existing batch found
    }

    public static BatchInfo createNewBatch(Timestamp batchCreatedAt) {
        int batchId = Math.abs(UUID.randomUUID().hashCode());
        String driverName = getAvailableDriver();
        if (driverName == null) {
            return null;
        }
        String insertSQL = "INSERT INTO batches (Batch_ID, Batch_Created_At, DeliveryDriver_Name) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setInt(1, batchId);
            pstmt.setTimestamp(2, batchCreatedAt);
            pstmt.setString(3, driverName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new BatchInfo(batchId, driverName);
    }

    public static String getAvailableDriver() {
        String query = "SELECT DeliveryDriver_Name FROM deliverydrivers WHERE isAvailable = 1 LIMIT 1";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("DeliveryDriver_Name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getExistingBatchForPostcode(String postcode) {
        String query = "SELECT Batch_ID FROM orders " +
                "WHERE Postcode = ? AND TIMESTAMPDIFF(MINUTE, Order_StartTime, NOW()) <= 3 " +
                "GROUP BY Batch_ID HAVING SUM(Pizza_Quantity) <= 3 LIMIT 1";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, postcode);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("Batch_ID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private static int generateUniqueBatchId() {
        return Math.abs(UUID.randomUUID().hashCode());
    }
    public static int getPizzaCountInBatch(int batchId) {
        String query = "SELECT SUM(Pizza_Quantity) AS pizza_count FROM orderitems oi " +
                "JOIN orders o ON oi.Order_ID = o.Order_ID " +
                "WHERE o.Batch_ID = ? AND oi.Pizza_ID IS NOT NULL";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, batchId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("pizza_count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getDeliveryDriver(String postcode) {
        String query = "SELECT DeliveryDriver_Name FROM deliverydrivers WHERE isAvailable = 1 LIMIT 1";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("DeliveryDriver_Name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static int createOrderInBatch(int customerId, double totalAmount, int batchId, String postcode, String deliveryDriver, Timestamp orderStartTime) throws SQLException {
        int orderId = generateUniqueOrderId();
        String insertOrderSQL = "INSERT INTO orders (Order_ID, Customer_ID, Total_Amount, Batch_ID, Postcode, DeliveryDriver_ID, Order_Status, Order_Date, Order_StartTime) " +
                "VALUES (?, ?, ?, ?, ?, (SELECT DeliveryDriver_ID FROM deliverydrivers WHERE DeliveryDriver_Name = ?), 'Order Confirmed', NOW(), ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(insertOrderSQL)) {
            pstmt.setInt(1, orderId);
            pstmt.setInt(2, customerId);
            pstmt.setDouble(3, totalAmount);
            pstmt.setInt(4, batchId);
            pstmt.setString(5, postcode);
            pstmt.setString(6, deliveryDriver);
            pstmt.setTimestamp(7, orderStartTime);
            pstmt.executeUpdate();
            // After inserting the order, check if batch is full
            if (isBatchFull(batchId)) {
                markDriverUnavailable(deliveryDriver);
            }
            return orderId;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static boolean isBatchFull(int batchId) {
        int orderCount = getOrderCountInBatch(batchId);
        return orderCount >= 3;
    }
    public static int getOrderCountInBatch(int batchId) {
        String query = "SELECT COUNT(*) FROM orders WHERE Batch_ID = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, batchId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
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
    public static int generateUniqueOrderItemID() {
        int uniqueID;
        boolean isUnique = false;
        String query = "SELECT COUNT(*) FROM orderitems WHERE OrderItem_ID = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            // Loop until a unique ID is found
            do {
                // Generate a random number as the unique ID
                uniqueID = (int) (Math.random() * 1000); // Adjust the range as needed

                // Check if the generated ID already exists in the database
                pstmt.setInt(1, uniqueID);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    int count = rs.getInt(1);
                    // If count is 0, then the ID is unique
                    isUnique = (count == 0);
                }
                rs.close();
            } while (!isUnique); // Repeat until a unique ID is found

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error generating unique OrderItem_ID");
        }

        return uniqueID;
    }

    public static int getTotalPizzasOrderedByCustomer(String username) {
        int totalPizzasOrdered = 0;
        String query = "SELECT SUM(Pizza_Quantity) AS total_pizzas_ordered " +
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


    // Inserts order items into the orderitems table
    public static void insertOrderItem(int orderId, CartItem item) {
        int newOrderItemID = generateUniqueOrderItemID();
        String insertItemSQL = "INSERT INTO orderitems (OrderItem_ID, Order_ID, Customer_ID, Pizza_ID, Dessert_ID, Drink_ID, Pizza_Quantity) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(insertItemSQL)) {

            conn.setAutoCommit(false); // Start transaction
            int customerId = getCustomerIdByUsername(app.getCurrentUsername());

            pstmt.setInt(1, newOrderItemID);
            pstmt.setInt(2, orderId);
            pstmt.setInt(3, customerId);

            // Handle different item types
            switch (item.getItemType()) {
                case PIZZA:
                    int pizzaId = getPizzaIdByName(item.getName());
                    pstmt.setInt(4, pizzaId); // Pizza_ID
                    pstmt.setNull(5, Types.INTEGER); // Dessert_ID
                    pstmt.setNull(6, Types.INTEGER); // Drink_ID
                    pstmt.setInt(7, item.getQuantity()); // Pizza_Quantity for pizzas
                    break;
                case DESSERT:
                    int dessertId = getDessertIdByName(item.getName());
                    pstmt.setNull(4, Types.INTEGER); // Pizza_ID
                    pstmt.setInt(5, dessertId); // Dessert_ID
                    pstmt.setNull(6, Types.INTEGER); // Drink_ID
                    pstmt.setNull(7, Types.INTEGER); // Pizza_Quantity set to null for non-pizza items
                    break;
                case DRINK:
                    int drinkId = getDrinkIdByName(item.getName());
                    pstmt.setNull(4, Types.INTEGER); // Pizza_ID
                    pstmt.setNull(5, Types.INTEGER); // Dessert_ID
                    pstmt.setInt(6, drinkId); // Drink_ID
                    pstmt.setNull(7, Types.INTEGER); // Pizza_Quantity set to null for non-pizza items
                    break;
                default:
                    throw new IllegalArgumentException("Unknown item type: " + item.getItemType());
            }

            pstmt.executeUpdate();
            conn.commit(); // Commit transaction

        } catch (SQLException e) {
            e.printStackTrace();
            // Rollback on failure
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        }
    }
    public static UserInfo getUserInfo(int customer_id) {
        UserInfo userInfo = null;
        String query = "SELECT Name, Gender, Email_Address, Phone_Number FROM customers WHERE customer_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, customer_id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String name = rs.getString("Name");
                String gender = rs.getString("Gender");
                String emailAddress = rs.getString("Email_Address");
                String phoneNumber = rs.getString("Phone_Number");

                userInfo = new UserInfo(name, gender, emailAddress, phoneNumber);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userInfo;
    }

}