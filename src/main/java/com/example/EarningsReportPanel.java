package com.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

public class EarningsReportPanel extends JPanel {
    private final PizzaDeliveryApp app;
    private JComboBox<String> genderComboBox;
    private JTextField ageMinField;
    private JTextField ageMaxField;
    private JTextField postcodeField;
    private JComboBox<String> monthComboBox;
    private JComboBox<String> yearComboBox;
    private JButton generateReportButton;
    private JLabel earningsLabel;
    private JTable reportTable;
    private DefaultTableModel tableModel;
    static final String DB_URL = "jdbc:mysql://localhost:3306/PIZZARE";
    static final String USER = "root";
    static final String PASS = "02072005";

    public EarningsReportPanel(PizzaDeliveryApp app) {
        this.app = app;
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(800, 700));

        // Filter panel setup
        JPanel filterPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filter Criteria"));
        setupFilterComponents(filterPanel);

        // Table setup for report display
        setupReportTable();

        // Button and earnings label panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        generateReportButton = new JButton("Generate Report");
        bottomPanel.add(generateReportButton);

        earningsLabel = new JLabel("Total Earnings: ");
        earningsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        bottomPanel.add(earningsLabel);

        // Adding components to the main panel
        add(filterPanel, BorderLayout.NORTH);
        add(new JScrollPane(reportTable), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

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

        filterPanel.add(new JLabel("Month:"));
        monthComboBox = new JComboBox<>(new String[]{"All Months", "January", "February", "March", "April", "May",
                "June", "July", "August", "September", "October", "November", "December"});
        filterPanel.add(monthComboBox);

        filterPanel.add(new JLabel("Year:"));
        yearComboBox = new JComboBox<>();
        int currentYear = Year.now().getValue();
        for (int year = currentYear; year >= currentYear - 10; year--) {
            yearComboBox.addItem(String.valueOf(year));
        }
        filterPanel.add(yearComboBox);
    }

    private void setupReportTable() {
        tableModel = new DefaultTableModel();
        reportTable = new JTable(tableModel);
        tableModel.setColumnIdentifiers(new String[]{"Order ID", "Pizzas", "Drinks", "Desserts", "Total Amount", "Discount Applied"});
    }

    private void generateEarningsReport() {
        String genderFilter = genderComboBox.getSelectedItem().toString();
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

        // Get selected month and year
        String selectedMonth = monthComboBox.getSelectedItem().toString();
        int month = 0; // 0 indicates all months
        if (!selectedMonth.equals("All Months")) {
            month = monthComboBox.getSelectedIndex(); // January is index 1
        }

        int year = Integer.parseInt(yearComboBox.getSelectedItem().toString());

        // Clear previous data
        tableModel.setRowCount(0);
        double totalEarnings = 0.0;

        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT o.order_id, " +
                "SUM(CASE WHEN oi.pizza_id IS NOT NULL THEN oi.Pizza_Quantity ELSE 0 END) AS total_pizzas, " +
                "COUNT(oi.drink_id) AS total_drinks, " +
                "COUNT(oi.dessert_id) AS total_desserts, " +
                "o.total_amount, " +
                "IF(dc.DiscountCode IS NOT NULL, 'Yes', 'No') AS discount_applied " +
                "FROM orders o " +
                "JOIN customers c ON o.customer_id = c.customer_id " +
                "JOIN orderitems oi ON o.order_id = oi.order_id " +
                "LEFT JOIN discountcodes dc ON o.DiscountCode_ID = dc.DiscountCode_ID " +
                "LEFT JOIN pizzas p ON oi.pizza_id = p.pizza_id " +
                "LEFT JOIN drinks d ON oi.drink_id = d.drink_id " +
                "LEFT JOIN desserts ds ON oi.dessert_id = ds.dessert_id " +
                "LEFT JOIN postcode pc ON o.postcode = pc.postcode " + // Added join with postcodes
                "WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        if (!genderFilter.equalsIgnoreCase("All")) {
            queryBuilder.append(" AND c.gender = ? ");
            params.add(genderFilter);
        }

        queryBuilder.append(" AND FLOOR(DATEDIFF(CURDATE(), c.birthdate) / 365.25) BETWEEN ? AND ? ");
        params.add(ageMin);
        params.add(ageMax);

        if (!postcode.isEmpty()) {
            queryBuilder.append(" AND LOWER(pc.postcode) = LOWER(?) "); // Adjusted to use pc.postcode
            params.add(postcode.toLowerCase());
        }


        if (month != 0) {
            queryBuilder.append(" AND MONTH(o.order_date) = ? ");
            params.add(month);
        }

        queryBuilder.append(" AND YEAR(o.order_date) = ? ");
        params.add(year);

        queryBuilder.append(" GROUP BY o.order_id");

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(queryBuilder.toString())) {

            // Set parameters
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = pstmt.executeQuery();
            boolean hasResults = false;
            while (rs.next()) {
                hasResults = true;
                int orderId = rs.getInt("order_id");
                int totalPizzas = rs.getInt("total_pizzas");
                int totalDrinks = rs.getInt("total_drinks");
                int totalDesserts = rs.getInt("total_desserts");
                double orderTotal = rs.getDouble("total_amount");
                String discountApplied = rs.getString("discount_applied");

                totalEarnings += orderTotal;

                tableModel.addRow(new Object[]{orderId, totalPizzas, totalDrinks, totalDesserts,
                        String.format("%.2f", orderTotal), discountApplied});
            }
            rs.close();
            if (!hasResults) {
                JOptionPane.showMessageDialog(this, "No results found for the given filters.", "No Results", JOptionPane.INFORMATION_MESSAGE);
                earningsLabel.setText("Total Earnings: $0.00");
            } else {
                // Add a final row for the total sum
                tableModel.addRow(new Object[]{"", "", "", "Total", String.format("%.2f", totalEarnings), ""});
                earningsLabel.setText("Total Earnings: $" + String.format("%.2f", totalEarnings));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}