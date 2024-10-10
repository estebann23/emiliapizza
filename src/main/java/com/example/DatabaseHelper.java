package com.example;

import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;
import javax.swing.Timer;
import java.sql.*;
import java.util.*;
import java.util.UUID;
import java.util.Date;

public class DatabaseHelper {

    private static PizzaDeliveryApp app;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/PIZZARE";
    private static final String USER = "root";
    private static final String PASS = "02072005";
    private static int currentOrderId = -1;
    private final Map<Integer, Timer> batchTimers = Collections.synchronizedMap(new HashMap<>());

    public DatabaseHelper(PizzaDeliveryApp app) {
        DatabaseHelper.app = app;
    }

    public static void setAppInstance(PizzaDeliveryApp appInstance) {
        app = appInstance;
    }

    public static String generateCustomerID() {
        return UUID.randomUUID().toString();
    }

    public static void markDriverUnavailable(String driverName) {
        updateDriverAvailability(driverName, false);
        scheduleDriverAvailabilityUpdate(driverName, 30 * 60 * 1000);
    }

    public static void markDriverAvailable(String driverName) {
        updateDriverAvailability(driverName, true);
    }

    private static void updateDriverAvailability(String driverName, boolean isAvailable) {
        String updateSQL = "UPDATE deliverydrivers SET isAvailable = ? WHERE DeliveryDriver_Name = ?";
        executeUpdate(updateSQL, isAvailable ? 1 : 0, driverName);
    }

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

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    private static void executeUpdate(String query, Object... params) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            setParameters(pstmt, params);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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

    private static void setParameters(PreparedStatement pstmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            pstmt.setObject(i + 1, params[i]);
        }
    }

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
            return PizzaPriceCalculator.calculateFinalPrice(totalIngredientCost);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
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

            while (pizzaRs.next()) {
                int pizzaId = pizzaRs.getInt("pizza_id");

                selectCostStmt.setInt(1, pizzaId);
                ResultSet costRs = selectCostStmt.executeQuery();

                if (costRs.next()) {
                    double ingredientCost = costRs.getDouble("total_ingredient_cost");

                    double finalPrice = PizzaPriceCalculator.calculateFinalPrice(ingredientCost);

                    updateStmt.setDouble(1, finalPrice);
                    updateStmt.setInt(2, pizzaId);
                    updateStmt.executeUpdate();
                }
                costRs.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<DiscountCode> getDiscountCodes() {
        String query = "SELECT DiscountCode_ID, DiscountCode, Discount_Value, DiscountCode_isAvailable FROM discountcodes";
        return executeSelectQuery(query, rs -> new DiscountCode(
                rs.getInt("DiscountCode_ID"),
                rs.getString("DiscountCode"),
                rs.getDouble("Discount_Value"),
                rs.getBoolean("DiscountCode_isAvailable")
        ));
    }

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

            do {
                uniqueID = (int) (Math.random() * 1000);

                pstmt.setInt(1, uniqueID);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    int count = rs.getInt(1);
                    isUnique = (count == 0);
                }
                rs.close();
            } while (!isUnique);

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
            conn.setAutoCommit(false);
            PreparedStatement pstmt = conn.prepareStatement(insertItemSQL);

            int customerId = getCustomerIdByUsername(app.getCurrentUsername());

            pstmt.setInt(1, newOrderItemID);
            pstmt.setInt(2, orderId);
            pstmt.setInt(3, customerId);

            switch (item.getItemType()) {
                case PIZZA:
                    int pizzaId = getPizzaIdByName(item.getName());
                    pstmt.setInt(4, pizzaId);
                    pstmt.setNull(5, Types.INTEGER);
                    pstmt.setNull(6, Types.INTEGER);
                    pstmt.setInt(7, item.getQuantity());
                    break;
                case DESSERT:
                    int dessertId = getDessertIdByName(item.getName());
                    pstmt.setNull(4, Types.INTEGER);
                    pstmt.setInt(5, dessertId);
                    pstmt.setNull(6, Types.INTEGER);
                    pstmt.setNull(7, Types.INTEGER);
                    break;
                case DRINK:
                    int drinkId = getDrinkIdByName(item.getName());
                    pstmt.setNull(4, Types.INTEGER);
                    pstmt.setNull(5, Types.INTEGER);
                    pstmt.setInt(6, drinkId);
                    pstmt.setNull(7, Types.INTEGER);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown item type: " + item.getItemType());
            }

            pstmt.executeUpdate();
            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();

            try {
                if (conn != null && !conn.isClosed()) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
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
                "WHERE b.Postcode = ? AND ABS(TIMESTAMPDIFF(SECOND, b.Created_At, ?)) <= 180 " +
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
        markDriverUnavailable(driverName);
        String insertSQL = "INSERT INTO batches (Batch_ID, Created_At, DeliveryDriver_Name, Postcode, IsDispatched) VALUES (?, ?, ?, ?, 0)";
        executeUpdate(insertSQL, batchId, batchCreatedAt, driverName, postcode);

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
        String updateBatchSQL = "UPDATE batches SET IsDispatched = 1 WHERE Batch_ID = ?";
        executeUpdate(updateBatchSQL, batchId);

        markDriverUnavailable(driverName);

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

    public int createOrderInBatch(int customerId, double totalAmount, int batchId, String postcode,
                                  String deliveryDriver, Timestamp orderStartTime, int discountCodeId) throws SQLException {
        int orderId = generateUniqueOrderId();

        String insertOrderSQL = "INSERT INTO orders (Order_ID, Customer_ID, Total_Amount, Batch_ID, Postcode, "
                + "DeliveryDriver_ID, Order_Status, Order_Date, Order_StartTime, DiscountCode_ID) "
                + "VALUES (?, ?, ?, ?, ?, (SELECT DeliveryDriver_ID FROM deliverydrivers WHERE DeliveryDriver_Name = ?), ?, NOW(), ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertOrderSQL)) {
            pstmt.setInt(1, orderId);
            pstmt.setInt(2, customerId);
            pstmt.setDouble(3, totalAmount);
            pstmt.setInt(4, batchId);
            pstmt.setString(5, postcode);
            pstmt.setString(6, deliveryDriver);
            pstmt.setString(7, "Order Confirmed");
            pstmt.setTimestamp(8, orderStartTime);
            if (discountCodeId != -1) {
                pstmt.setInt(9, discountCodeId);
            } else {
                pstmt.setNull(9, Types.INTEGER);
            }
            pstmt.executeUpdate();
        }

        setCurrentOrderId(orderId);

        if (isBatchFull(batchId)) {
            Timer timer = batchTimers.get(batchId);
            if (timer != null) {
                timer.stop();
                batchTimers.remove(batchId);
            }
            dispatchBatch(batchId, deliveryDriver);
        }
        return orderId;
    }

    public void markDiscountCodeAsUsed(int discountCodeId) throws SQLException {
        String query = "UPDATE discountcodes SET DiscountCode_isAvailable = 0 WHERE DiscountCode_ID = ?";
        executeUpdate(query, discountCodeId);
    }

    public boolean isBatchFull(int batchId) {
        int pizzaCount = getPizzaCountInBatch(batchId);
        return pizzaCount >= 3;
    }

    public static int getOrderCountInBatch(int batchId) {
        String query = "SELECT COUNT(*) FROM orders WHERE Batch_ID = ?";
        return executeQueryForSingleResult(query, rs -> rs.getInt(1), batchId).orElse(0);
    }

    public Optional<CustomerBirthdayInfo> getCustomerBirthdayInfo(String username) {
        String query = "SELECT Birthdate, canBirthday FROM Customers WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Date birthdate = rs.getDate("Birthdate");
                boolean canBirthday = rs.getBoolean("canBirthday");
                return Optional.of(new CustomerBirthdayInfo(birthdate, canBirthday));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public void setCanBirthdayUsed(String username) {
        String updateSQL = "UPDATE Customers SET canBirthday = false WHERE Username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
            pstmt.setString(1, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static class CustomerBirthdayInfo {
        private final Date birthdate;
        private final boolean canBirthday;

        public CustomerBirthdayInfo(Date birthdate, boolean canBirthday) {
            this.birthdate = birthdate;
            this.canBirthday = canBirthday;
        }

        public Date getBirthdate() {
            return birthdate;
        }

        public boolean canUseBirthdayDiscount() {
            return canBirthday;
        }
    }
}