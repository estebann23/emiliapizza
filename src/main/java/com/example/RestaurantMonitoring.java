package com.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

public class RestaurantMonitoring {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/pizzare"; // Update to your actual database name
    private static final String USER = "root";
    private static final String PASS = "02072005"; // Update with your database password

    private static JLabel timeLabel;
    private static DefaultTableModel model;
    private static Timer dataRefreshTimer;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RestaurantMonitoring::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Restaurant Monitoring");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 550); // Increased width to accommodate additional column

        // Create a panel to hold the time label at the top
        JPanel topPanel = new JPanel(new BorderLayout());
        timeLabel = new JLabel();
        timeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(timeLabel, BorderLayout.NORTH);

        // Create a table to show order data
        JTable table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);
        topPanel.add(scrollPane, BorderLayout.CENTER);
        frame.add(topPanel, BorderLayout.CENTER);

        model = new DefaultTableModel(new String[]{"Order ID", "Order Date", "Start Time", "Item Type", "Item Name", "Quantity", "Status"}, 0);
        table.setModel(model);

        frame.setVisible(true);

        // Load data from the database initially
        loadData();

        // Start the timer to update the time every second
        startClock();

        // Start the timer to refresh the data every second
        startDataRefresh();
    }

    private static void startClock() {
        Timer timer = new Timer();
        TimerTask updateClock = new TimerTask() {
            @Override
            public void run() {
                // Get the current time and format it
                LocalTime now = LocalTime.now();
                String currentTime = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                // Update the time label
                timeLabel.setText("Current Time: " + currentTime);
            }
        };
        // Schedule the task to run every 1000 milliseconds (1 second)
        timer.scheduleAtFixedRate(updateClock, 0, 1000);
    }

    private static void startDataRefresh() {
        dataRefreshTimer = new Timer();
        TimerTask refreshDataTask = new TimerTask() {
            @Override
            public void run() {
                // Reload data from the database
                SwingUtilities.invokeLater(RestaurantMonitoring::loadData);
            }
        };
        // Schedule the task to run every 1000 milliseconds (1 second)
        dataRefreshTimer.scheduleAtFixedRate(refreshDataTask, 0, 1000);
    }

    private static void loadData() {
        // Clear existing data in the table
        model.setRowCount(0);

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {

            // Get the current time and format it for SQL as HH:mm:ss
            LocalTime now = LocalTime.now();
            String currentTime = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"));

            String query = "SELECT o.Order_ID, o.Order_Date, o.Order_StartTime, 'Pizza' AS ItemType, p.pizza_name AS ItemName, oi.Pizza_Quantity AS Quantity, o.Order_Status " +
                    "FROM pizzare.orders o " +
                    "JOIN pizzare.orderitems oi ON o.Order_ID = oi.Order_ID " +
                    "JOIN pizzare.pizzas p ON oi.Pizza_ID = p.pizza_id " +
                    "WHERE o.Order_Status NOT IN ('Canceled') " + // Exclude canceled orders
                    "AND TIMEDIFF(TIME(?), TIME(o.Order_StartTime)) BETWEEN '00:00:00' AND '00:15:00' " +
                    "UNION ALL " +
                    "SELECT o.Order_ID, o.Order_Date, o.Order_StartTime, 'Dessert' AS ItemType, d.dessert_name AS ItemName, 1 AS Quantity, o.Order_Status " +
                    "FROM pizzare.orders o " +
                    "JOIN pizzare.orderitems oi ON o.Order_ID = oi.Order_ID " +
                    "JOIN pizzare.desserts d ON oi.Dessert_ID = d.dessert_id " +
                    "WHERE o.Order_Status NOT IN ('Canceled') " + // Exclude canceled orders
                    "AND TIMEDIFF(TIME(?), TIME(o.Order_StartTime)) BETWEEN '00:00:00' AND '00:15:00' " +
                    "UNION ALL " +
                    "SELECT o.Order_ID, o.Order_Date, o.Order_StartTime, 'Drink' AS ItemType, dr.drink_name AS ItemName, 1 AS Quantity, o.Order_Status " +
                    "FROM pizzare.orders o " +
                    "JOIN pizzare.orderitems oi ON o.Order_ID = oi.Order_ID " +
                    "JOIN pizzare.drinks dr ON oi.Drink_ID = dr.drink_id " +
                    "WHERE o.Order_Status NOT IN ('Canceled') " + // Exclude canceled orders
                    "AND TIMEDIFF(TIME(?), TIME(o.Order_StartTime)) BETWEEN '00:00:00' AND '00:15:00'";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, currentTime);
            preparedStatement.setString(2, currentTime);
            preparedStatement.setString(3, currentTime);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int orderId = resultSet.getInt("Order_ID");
                Date orderDate = resultSet.getDate("Order_Date");
                Time startTime = resultSet.getTime("Order_StartTime");
                String itemType = resultSet.getString("ItemType");
                String itemName = resultSet.getString("ItemName");
                int quantity = resultSet.getInt("Quantity");
                String status = resultSet.getString("Order_Status");

                model.addRow(new Object[]{orderId, orderDate, startTime, itemType, itemName, quantity, status});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error connecting to the database: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}