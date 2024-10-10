package com.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class EarningsReportPanel extends JPanel {
    private final PizzaDeliveryApp app;
    private JComboBox<String> genderComboBox;
    private JTextField ageMinField;
    private JTextField ageMaxField;
    private JTextField postcodeField;
    private JButton generateReportButton;
    private JLabel earningsLabel;
    private JTable reportTable;
    private DefaultTableModel tableModel;
    static final String DB_URL = "jdbc:mysql://localhost:3306/pizzare";
    static final String USER = "root";
    static final String PASS = "mysql2311";

    public EarningsReportPanel(PizzaDeliveryApp app) {
        this.app = app;
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(800, 700));

        // Filter panel setup
        JPanel filterPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filter Criteria"));
        setupFilterComponents(filterPanel);

        // Button panel setup
        JPanel buttonPanel = new JPanel(new FlowLayout());
        generateReportButton = new JButton("Generate Report");
        buttonPanel.add(generateReportButton);

        // Table setup for report display
        setupReportTable();

        // Earnings label
        earningsLabel = new JLabel("Total Earnings: ");
        earningsLabel.setFont(new Font("Arial", Font.BOLD, 16));

        // Adding components to the main panel
        add(filterPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);
        add(new JScrollPane(reportTable), BorderLayout.CENTER);
        add(earningsLabel, BorderLayout.SOUTH);

        // Generate report button action
        generateReportButton.addActionListener(e -> generateEarningsReport());
    }

    private void setupFilterComponents(JPanel filterPanel) {
        filterPanel.add(new JLabel("Gender:"));
        genderComboBox = new JComboBox<>(new String[]{"All", "Male", "Female", "Non-Gender"});
        filterPanel.add(genderComboBox);

        filterPanel.add(new JLabel("Minimum Age:"));
        ageMinField = new JTextField();
        filterPanel.add(ageMinField);

        filterPanel.add(new JLabel("Maximum Age:"));
        ageMaxField = new JTextField();
        filterPanel.add(ageMaxField);

        filterPanel.add(new JLabel("Postcode:"));
        postcodeField = new JTextField();
        filterPanel.add(postcodeField);
    }

    private void setupReportTable() {
        tableModel = new DefaultTableModel();
        reportTable = new JTable(tableModel);
        tableModel.setColumnIdentifiers(new String[]{"Order ID", "Pizzas", "Drinks", "Desserts", "Total Amount", "Discount Applied"});
    }

    private void generateEarningsReport() {
        String genderFilter = genderComboBox.getSelectedItem().toString().toLowerCase();
        int ageMin;
        int ageMax;
        String postcode = postcodeField.getText().trim();

        try {
            ageMin = Integer.parseInt(ageMinField.getText().trim());
            ageMax = Integer.parseInt(ageMaxField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid age values.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Clear previous data
        tableModel.setRowCount(0);
        double totalEarnings = 0.0;

        String query = "SELECT o.order_id, " +
                "SUM(CASE WHEN oi.pizza_id IS NOT NULL THEN oi.Pizza_Quantity ELSE 0 END) AS total_pizzas, " +
                "COUNT(oi.drink_id) AS total_drinks, " +
                "COUNT(oi.dessert_id) AS total_desserts, " +
                "o.total_amount, " +
                "IF(dc.discount_code IS NOT NULL, 'Yes', 'No') AS discount_applied " +
                "FROM orders o " +
                "JOIN customers c ON o.customer_id = c.customer_id " +
                "JOIN orderitems oi ON o.order_id = oi.order_id " +
                "LEFT JOIN discountcodes dc ON o.discount_code_id = dc.discount_code_id " +
                "LEFT JOIN pizzas p ON oi.pizza_id = p.pizza_id " +
                "LEFT JOIN drinks d ON oi.drink_id = d.drink_id " +
                "LEFT JOIN desserts ds ON oi.dessert_id = ds.dessert_id " +
                "WHERE (? = 'All' OR c.gender = ?) " +
                "AND FLOOR(DATEDIFF(CURDATE(), c.birthdate) / 365.25) BETWEEN ? AND ? " +
                "AND (? = '' OR c.Postcode_ID = ?) " +
                "GROUP BY o.order_id";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, genderFilter);
            pstmt.setString(2, genderFilter);
            pstmt.setInt(3, ageMin);
            pstmt.setInt(4, ageMax);
            pstmt.setString(5, postcode);
            pstmt.setString(6, postcode);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                int totalPizzas = rs.getInt("total_pizzas");
                int totalDrinks = rs.getInt("total_drinks");
                int totalDesserts = rs.getInt("total_desserts");
                double orderTotal = rs.getDouble("total_amount");
                String discountApplied = rs.getString("discount_applied");

                totalEarnings += orderTotal;

                tableModel.addRow(new Object[]{orderId, totalPizzas, totalDrinks, totalDesserts, String.format("%.2f", orderTotal), discountApplied});
            }
            rs.close();

            // Add a final row for the total sum
            tableModel.addRow(new Object[]{"", "", "", "Total", String.format("%.2f", totalEarnings), ""});
            earningsLabel.setText("Total Earnings: $" + String.format("%.2f", totalEarnings));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean verifyOrderAmounts() {
        boolean amountsMatch = true;
        String query = "SELECT o.order_id, o.total_amount, " +
                "SUM(COALESCE(p.pizza_finalprice * oi.Pizza_Quantity, 0) + " +
                "COALESCE(d.drink_price, 0) + COALESCE(ds.dessert_price, 0)) AS calculated_total " +
                "FROM orders o " +
                "JOIN orderitems oi ON o.order_id = oi.order_id " +
                "LEFT JOIN pizzas p ON oi.pizza_id = p.pizza_id " +
                "LEFT JOIN drinks d ON oi.drink_id = d.drink_id " +
                "LEFT JOIN desserts ds ON oi.dessert_id = ds.dessert_id " +
                "GROUP BY o.order_id";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                double totalAmount = rs.getDouble("total_amount");
                double calculatedTotal = rs.getDouble("calculated_total");

                // Allowing a small margin for rounding errors
                if (Math.abs(totalAmount - calculatedTotal) > 0.01) {
                    amountsMatch = false;
                    System.out.println("Discrepancy found for Order ID: " + rs.getInt("order_id") +
                            " | Recorded: " + totalAmount + " | Calculated: " + calculatedTotal);
                }
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
            amountsMatch = false;
        }

        return amountsMatch;
    }
}