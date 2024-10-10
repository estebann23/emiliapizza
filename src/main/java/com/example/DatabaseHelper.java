package com.example;

import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;
import javax.swing.Timer;
import java.sql.*;
import java.util.*;
import java.util.UUID;

public class DatabaseHelper {

    private static PizzaDeliveryApp app;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/PIZZARE";
    private static final String USER = "root";
    private static final String PASS = "02072005";
    private static int currentOrderId = -1;
    private Map<Integer, Timer> batchTimers = new HashMap<>();

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
        updateDriverAvailability(driverName, false);
        scheduleDriverAvailabilityUpdate(driverName, 30 * 60 * 1000); // 30 minutes
    }

    public static void markDriverAvailable(String driverName) {
        updateDriverAvailability(driverName, true);
    }

    // Private helper for updating driver availability
    private static void updateDriverAvailability(String driverName, boolean isAvailable) {
        String updateSQL = "UPDATE deliverydrivers SET isAvailable = ? WHERE DeliveryDriver_Name = ?";
        executeUpdate(updateSQL, isAvailable ? 1 : 0, driverName);
    }

    // Schedule a future task to mark a driver as available
    private static void scheduleDriverAvailabilityUpdate(String driverName, int delayMillis) {
        Timer timer = new Timer(delayMillis, e -> markDriverAvailable(driverName));
        timer.setRepeats(false);
        timer.start();
    }

    public static Optional<Double> getItemPriceByNameAndType(String name, CartItem.ItemType itemType) {
        String query = switch (itemType) {
            case PIZZA -> "SELECT pizza_finalprice AS price FROM pizzas WHERE pizza_name = ?";
            case DRINK -> "SELECT drink_price AS price FROM drinks WHERE drink_name = ?";
            case DESSERT -> "SELECT dessert_price AS price FROM desserts WHERE dessert_name = ?";
        };
        return executeQueryForSingleResult(query, rs -> rs.getDouble("price"), name);
    }

    // Centralized method for creating a new connection
    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    // General utility method to execute updates
    private static void executeUpdate(String query, Object... params) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            setParameters(pstmt, params);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Utility for executing a query that returns a single result
    private static <T> Optional<T> executeQueryForSingleResult(String query, ResultSetExtractor<T> extractor, Object... params) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            setParameters(pstmt, params);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(extractor.extract(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    // Method to set parameters for a PreparedStatement
    private static void setParameters(PreparedStatement pstmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            pstmt.setObject(i + 1, params[i]);
        }
    }

    // Interface for extracting a result from a ResultSet
    @FunctionalInterface
    private interface ResultSetExtractor<T> {
        T extract(ResultSet rs) throws SQLException;
    }

    public static int generateUniqueOrderId() throws SQLException {
        int uniqueId = Math.abs(UUID.randomUUID().hashCode());
        String query = "SELECT COUNT(*) FROM orders WHERE Order_ID = ?";
        while (true) {
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, uniqueId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next() && rs.getInt(1) == 0) {
                    break;
                }
                uniqueId = Math.abs(UUID.randomUUID().hashCode());
            }
        }
        return uniqueId;
    }

    public static void setCurrentOrderId(int orderId) {
        currentOrderId = orderId;
    }

    public static boolean updateOrderStatusToCanceled(int orderId) {
        String updateOrderSQL = "UPDATE orders SET Order_Status = 'Canceled', Order_EndTime = NOW() WHERE Order_ID = ?";
        return executeUpdateWithResult(updateOrderSQL, orderId);
    }

    private static boolean executeUpdateWithResult(String query, Object... params) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            setParameters(pstmt, params);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean updateOrderDetails(int orderId, double totalAmount, String deliveryDriver) {
        String updateOrderSQL = "UPDATE orders SET Total_Amount = ?, DeliveryDriver_ID = (SELECT DeliveryDriver_ID FROM deliverydrivers WHERE DeliveryDriver_Name = ?), " +
                "Order_Date = NOW(), Order_Status = 'Order Confirmed', Order_StartTime = NOW() WHERE Order_ID = ?";
        return executeUpdateWithResult(updateOrderSQL, totalAmount, deliveryDriver, orderId);
    }

    public static void createNewOrder(int customerId) {
        try {
            int orderId = generateUniqueOrderId();
            currentOrderId = orderId;
            String insertOrderSQL = "INSERT INTO orders (Order_ID, Customer_ID) VALUES (?, ?)";
            executeUpdate(insertOrderSQL, orderId, customerId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int getCurrentOrderId() {
        return currentOrderId;
    }

    public static boolean createAccount(String name, String gender, String birthdate, String emailAddress, String phoneNumber, String username, String password) {
        String checkUserSQL = "SELECT COUNT(*) FROM Customers WHERE Username = ?";
        String checkEmailSQL = "SELECT COUNT(*) FROM Customers WHERE Email_Address = ?";
        String getMaxCustomerIdSQL = "SELECT MAX(Customer_ID) FROM Customers";
        String insertSQL = "INSERT INTO Customers (Customer_ID, Name, Gender, Birthdate, Email_Address, Phone_Number, Username, Password) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection()) {
            if (isDuplicateEntry(conn, checkUserSQL, username)) {
                System.out.println("Username already exists.");
                return false;
            }
            if (isDuplicateEntry(conn, checkEmailSQL, emailAddress)) {
                System.out.println("Email address already exists.");
                return false;
            }
            int newCustomerId = getMaxCustomerId(conn, getMaxCustomerIdSQL) + 1;
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            executeUpdate(insertSQL, newCustomerId, name, gender, birthdate, emailAddress, phoneNumber, username, hashedPassword);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean isDuplicateEntry(Connection conn, String query, String value) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, value);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    private static int getMaxCustomerId(Connection conn, String query) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public static boolean authenticateUser(String username, String password) {
        String query = "SELECT Password FROM Customers WHERE Username = ?";
        Optional<String> storedHashedPassword = executeQueryForSingleResult(query, rs -> rs.getString("Password"), username);
        return storedHashedPassword.map(hash -> BCrypt.checkpw(password, hash)).orElse(false);
    }

    public static int getCustomerIdByUsername(String username) {
        String query = "SELECT Customer_ID FROM Customers WHERE Username = ?";
        return executeQueryForSingleResult(query, rs -> rs.getInt("Customer_ID"), username).orElse(-1);
    }

    public static int getPizzaIdByName(String pizzaName) {
        String query = "SELECT Pizza_ID FROM Pizzas WHERE Pizza_Name = ?";
        return executeQueryForSingleResult(query, rs -> rs.getInt("Pizza_ID"), pizzaName).orElse(-1);
    }

    public static int getDrinkIdByName(String drinkName) {
        String query = "SELECT Drink_ID FROM Drinks WHERE Drink_Name = ?";
        return executeQueryForSingleResult(query, rs -> rs.getInt("Drink_ID"), drinkName).orElse(-1);
    }

    public static int getDessertIdByName(String dessertName) {
        String query = "SELECT Dessert_ID FROM Desserts WHERE Dessert_Name = ?";
        return executeQueryForSingleResult(query, rs -> rs.getInt("Dessert_ID"), dessertName).orElse(-1);
    }

    public static List<Pizza> getPizzaDetails() {
        String query = "SELECT p.pizza_id, p.pizza_name, t.topping_name, t.topping_price, t.topping_isvegan, t.toppping_isvegetarian " +
                "FROM pizzas p " +
                "JOIN pizzatoppings pt ON p.pizza_id = pt.pizza_id " +
                "JOIN toppings t ON pt.topping_id = t.topping_id";
        List<Pizza> pizzas = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            Map<Integer, Pizza> pizzaMap = new HashMap<>();

            while (rs.next()) {
                int pizzaId = rs.getInt("pizza_id");
                String pizzaName = rs.getString("pizza_name");
                Pizza currentPizza = pizzaMap.get(pizzaId);
                if (currentPizza == null) {
                    currentPizza = new Pizza(pizzaName);
                    pizzas.add(currentPizza);
                    pizzaMap.put(pizzaId, currentPizza);
                }

                String toppingName = rs.getString("topping_name");
                double toppingPrice = rs.getDouble("topping_price");
                boolean toppingIsVegan = rs.getBoolean("topping_isvegan");
                boolean toppingIsVegetarian = rs.getBoolean("toppping_isvegetarian");
                currentPizza.addTopping(new Topping(toppingName, toppingPrice, toppingIsVegan, toppingIsVegetarian));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pizzas;
    }

    public static List<Drink> getDrinkDetails() {
        String query = "SELECT drink_name, drink_price FROM Drinks";
        return executeSelectQuery(query, rs -> new Drink(rs.getString("drink_name"), rs.getDouble("drink_price")));
    }

    public static ArrayList<String> getDrinksNames() {
        String query = "SELECT drink_name FROM Drinks";
        return executeSelectQuery(query, rs -> rs.getString("drink_name"));
    }

    public static double getDrinkPriceByName(String drinkName) {
        String query = "SELECT drink_price FROM Drinks WHERE drink_name = ?";
        return executeQueryForSingleResult(query, rs -> rs.getDouble("drink_price"), drinkName).orElse(0.0);
    }

    public static List<Dessert> getDessertDetails() {
        String query = "SELECT dessert_name, dessert_price FROM Desserts";
        return executeSelectQuery(query, rs -> new Dessert(rs.getString("dessert_name"), rs.getDouble("dessert_price")));
    }

    public static ArrayList<String> getDessertNames() {
        String query = "SELECT dessert_name FROM Desserts";
        return executeSelectQuery(query, rs -> rs.getString("dessert_name"));
    }

    public static double getDessertPriceByName(String dessertName) {
        String query = "SELECT dessert_price FROM Desserts WHERE dessert_name = ?";
        return executeQueryForSingleResult(query, rs -> rs.getDouble("dessert_price"), dessertName).orElse(0.0);
    }

    public static double getPizzaPriceByName(String pizzaName) {
        String query = "SELECT t.topping_price " +
                "FROM pizzas p " +
                "JOIN pizzatoppings pt ON p.pizza_id = pt.pizza_id " +
                "JOIN toppings t ON pt.topping_id = t.topping_id " +
                "WHERE p.pizza_name = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, pizzaName);
            ResultSet rs = pstmt.executeQuery();
            double totalIngredientCost = 0.0;
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

        try (Connection conn = getConnection();
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
        String query = "SELECT DiscountCode, Discount_Value, DiscountCode_isAvailable FROM discountcodes";
        return executeSelectQuery(query, rs -> new DiscountCode(
                rs.getString("DiscountCode"),
                rs.getDouble("Discount_Value"),
                rs.getBoolean("DiscountCode_isAvailable")
        ));
    }

    // Helper method for querying the Items Selection from the DB
    private static <T> ArrayList<T> executeSelectQuery(String query, ResultSetExtractor<T> extractor) {
        ArrayList<T> resultList = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                resultList.add(extractor.extract(rs));
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

        try (Connection conn = getConnection();
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
        String query = "SELECT SUM(Pizza_Quantity) AS total_pizzas_ordered " +
                "FROM customers c " +
                "JOIN orders o ON c.customer_id = o.customer_id " +
                "JOIN orderitems oi ON o.order_id = oi.order_id " +
                "JOIN pizzas p ON oi.pizza_id = p.pizza_id " +
                "WHERE c.username = ? " +
                "GROUP BY c.username";
        return executeQueryForSingleResult(query, rs -> rs.getInt("total_pizzas_ordered"), username).orElse(0);
    }

    public static void insertOrderItem(int orderId, CartItem item) {
        int newOrderItemID = generateUniqueOrderItemID();
        String insertItemSQL = "INSERT INTO orderitems (OrderItem_ID, Order_ID, Customer_ID, Pizza_ID, Dessert_ID, Drink_ID, Pizza_Quantity) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Start transaction
            PreparedStatement pstmt = conn.prepareStatement(insertItemSQL);

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
                if (conn != null && !conn.isClosed()) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        } finally {
            // Ensure the connection is closed
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Reset auto-commit
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static UserInfo getUserInfo(int customer_id) {
        String query = "SELECT Name, Gender, Email_Address, Phone_Number FROM customers WHERE customer_id = ?";
        return executeQueryForSingleResult(query, rs -> new UserInfo(
                rs.getString("Name"),
                rs.getString("Gender"),
                rs.getString("Email_Address"),
                rs.getString("Phone_Number")
        ), customer_id).orElse(null);
    }

    // Methods related to batches and orders

    public BatchInfo getOrCreateBatchForOrder(Timestamp orderStartTime, String postcode) {
        BatchInfo batchInfo = getExistingBatchForTimeWindow(orderStartTime, postcode);
        if (batchInfo == null) {
            batchInfo = createNewBatch(orderStartTime, postcode);
        }
        return batchInfo;
    }
    public BatchInfo getExistingBatchForTimeWindow(Timestamp orderStartTime, String postcode) {
        String query = "SELECT b.Batch_ID, b.DeliveryDriver_Name " +
                "FROM batches b " +
                "WHERE b.Postcode = ? AND ABS(TIMESTAMPDIFF(SECOND, b.Created_At, ?)) <= 180 " + // 3 minutes window
                "AND b.IsDispatched = 0 " +
                "LIMIT 1";
        return executeQueryForSingleResult(query, rs -> new BatchInfo(rs.getInt("Batch_ID"), rs.getString("DeliveryDriver_Name"), postcode), postcode, orderStartTime).orElse(null);
    }

    public BatchInfo createNewBatch(Timestamp batchCreatedAt, String postcode) {
        int batchId = Math.abs(UUID.randomUUID().hashCode());
        String driverName = getAvailableDriver();
        if (driverName == null) {
            JOptionPane.showMessageDialog(null, "No available drivers at the moment. Please try again later.", "Driver Unavailable", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        String insertSQL = "INSERT INTO batches (Batch_ID, Created_At, DeliveryDriver_Name, Postcode, IsDispatched) VALUES (?, ?, ?, ?, 0)";
        executeUpdate(insertSQL, batchId, batchCreatedAt, driverName, postcode);

        // Start the 3-minute timer for this batch
        startBatchTimer(batchId, driverName);

        return new BatchInfo(batchId, driverName, postcode);
    }

    public int getRemainingTimeForBatch(int batchId) {
        String query = "SELECT TIMESTAMPDIFF(SECOND, NOW(), DATE_ADD(Created_At, INTERVAL 3 MINUTE)) AS remaining_time " +
                "FROM batches WHERE Batch_ID = ?";
        return executeQueryForSingleResult(query, rs -> rs.getInt("remaining_time"), batchId).orElse(0);
    }


    private void startBatchTimer(int batchId, String driverName) {
        Timer timer = new Timer(3 * 60 * 1000, e -> {
            dispatchBatch(batchId, driverName);
        });
        timer.setRepeats(false);
        timer.start();
        batchTimers.put(batchId, timer);
    }
    private void dispatchBatch(int batchId, String driverName) {
        // Mark batch as dispatched
        String updateBatchSQL = "UPDATE batches SET IsDispatched = 1 WHERE Batch_ID = ?";
        executeUpdate(updateBatchSQL, batchId);

        // Mark driver as unavailable
        markDriverUnavailable(driverName);

        // Remove the timer from the map
        batchTimers.remove(batchId);
    }
    public String getAvailableDriver() {
        String query = "SELECT DeliveryDriver_Name FROM deliverydrivers WHERE isAvailable = 1 LIMIT 1";
        return executeQueryForSingleResult(query, rs -> rs.getString("DeliveryDriver_Name")).orElse(null);
    }

    public static int getExistingBatchForPostcode(String postcode) {
        String query = "SELECT Batch_ID FROM orders " +
                "WHERE Postcode = ? AND TIMESTAMPDIFF(MINUTE, Order_StartTime, NOW()) <= 3 " +
                "GROUP BY Batch_ID HAVING SUM(Pizza_Quantity) <= 3 LIMIT 1";
        return executeQueryForSingleResult(query, rs -> rs.getInt("Batch_ID"), postcode).orElse(-1);
    }

    public int getPizzaCountInBatch(int batchId) {
        String query = "SELECT SUM(oi.Pizza_Quantity) AS pizza_count FROM orderitems oi " +
                "JOIN orders o ON oi.Order_ID = o.Order_ID " +
                "WHERE o.Batch_ID = ? AND oi.Pizza_ID IS NOT NULL";
        return executeQueryForSingleResult(query, rs -> rs.getInt("pizza_count"), batchId).orElse(0);
    }

    public static String getDeliveryDriver(String postcode) {
        String query = "SELECT DeliveryDriver_Name FROM deliverydrivers WHERE isAvailable = 1 LIMIT 1";
        return executeQueryForSingleResult(query, rs -> rs.getString("DeliveryDriver_Name")).orElse(null);
    }

    public int createOrderInBatch(int customerId, double totalAmount, int batchId, String postcode, String deliveryDriver, Timestamp orderStartTime) throws SQLException {
        int orderId = generateUniqueOrderId();
        String insertOrderSQL = "INSERT INTO orders (Order_ID, Customer_ID, Total_Amount, Batch_ID, Postcode, DeliveryDriver_ID, Order_Status, Order_Date, Order_StartTime) " +
                "VALUES (?, ?, ?, ?, ?, (SELECT DeliveryDriver_ID FROM deliverydrivers WHERE DeliveryDriver_Name = ?), 'Order Confirmed', NOW(), ?)";
        executeUpdate(insertOrderSQL, orderId, customerId, totalAmount, batchId, postcode, deliveryDriver, orderStartTime);

        // After inserting the order, check if batch is full
        if (isBatchFull(batchId)) {
            // Cancel the batch timer if it's still running
            Timer timer = batchTimers.get(batchId);
            if (timer != null) {
                timer.stop();
                batchTimers.remove(batchId);
            }
            // Dispatch the batch immediately
            dispatchBatch(batchId, deliveryDriver);
        }
        return orderId;
    }

    public boolean isBatchFull(int batchId) {
        int pizzaCount = getPizzaCountInBatch(batchId);
        return pizzaCount >= 3;
    }

    public static int getOrderCountInBatch(int batchId) {
        String query = "SELECT COUNT(*) FROM orders WHERE Batch_ID = ?";
        return executeQueryForSingleResult(query, rs -> rs.getInt(1), batchId).orElse(0);
    }
}