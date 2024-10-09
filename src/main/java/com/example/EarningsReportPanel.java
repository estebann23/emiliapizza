package com.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.awt.Dimension;


public class EarningsReportPanel extends JPanel {
    private final PizzaDeliveryApp app;
    private JComboBox<String> genderComboBox;
    private JTextField ageMinField;
    private JTextField ageMaxField;
    private JButton generateReportButton;
    private JLabel earningsLabel;

    static final String DB_URL = "jdbc:mysql://localhost:3306/emiliadb";
    static final String USER = "root";
    static final String PASS = "mysql2311";

    public EarningsReportPanel(PizzaDeliveryApp app) {
        this.app = app;
        initialize();
    }

    private void initialize() {
        setLayout(new GridLayout(5, 2, 10, 10));
        setPreferredSize(new Dimension(500, 600));

        add(new JLabel("Gender:"));
        genderComboBox = new JComboBox<>(new String[]{"All", "Male", "Female", "non-gender"});
        add(genderComboBox);

        add(new JLabel("Minimum Age:"));
        ageMinField = new JTextField();
        add(new JLabel("Maximum Age:"));
        ageMaxField = new JTextField();
        add(ageMinField);
        add(ageMaxField);

        generateReportButton = new JButton("Generate Report");
        add(generateReportButton);

        earningsLabel = new JLabel("Total Earnings: ");
        add(earningsLabel);

        // Action listener for the button
        generateReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateEarningsReport();
            }
        });
    }

    private void generateEarningsReport() {
        String genderFilter = genderComboBox.getSelectedItem().toString().toLowerCase();
        int ageMin;
        int ageMax;

        // Validate age inputs
        try {
            ageMin = Integer.parseInt(ageMinField.getText().trim());
            ageMax = Integer.parseInt(ageMaxField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid age values.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double totalEarnings = getTotalEarnings(genderFilter, ageMin, ageMax);

        if (totalEarnings > 0) {
            earningsLabel.setText("Total Earnings: $" + String.format("%.2f", totalEarnings));
        } else {
            earningsLabel.setText("No earnings found for the selected filters.");
        }
    }

    private double getTotalEarnings(String genderFilter, int ageMin, int ageMax) {
        double totalEarnings = 0.0;
        String query = "SELECT SUM(oi.OrderItem_Amount * COALESCE(p.pizza_finalprice, d.drink_price, ds.dessert_price, 0)) AS total_earnings " +
                "FROM orders o " +
                "JOIN customers c ON o.customer_id = c.customer_id " +
                "JOIN orderitems oi ON o.order_id = oi.order_id " +
                "LEFT JOIN pizzas p ON oi.pizza_id = p.pizza_id " +
                "LEFT JOIN drinks d ON oi.drink_id = d.drink_id " +
                "LEFT JOIN desserts ds ON oi.dessert_id = ds.dessert_id " +
                "WHERE (? = 'All' OR c.gender = ?) " +
                "AND FLOOR(DATEDIFF(CURDATE(), c.birthdate) / 365.25) BETWEEN ? AND ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, genderFilter);
            pstmt.setString(2, genderFilter);
            pstmt.setInt(3, ageMin);
            pstmt.setInt(4, ageMax);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                totalEarnings = rs.getDouble("total_earnings");
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalEarnings;
    }
}
