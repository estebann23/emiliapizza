package com.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.HashMap;

public class UserPanel extends JDialog{
    private final PizzaDeliveryApp app;
    private final JLabel pizzasOrdered;

    // UserInfoPanel fields
    private JTextField nameField = new JTextField();
    private JTextField genderField = new JTextField();
    private JTextField emailField = new JTextField();
    private JTextField phoneField = new JTextField();
    private UserInfo userInfo;


    public UserPanel(PizzaDeliveryApp app) throws SQLException {
        super(app.getFrame(), "User information", true);
        this.app = app;
        this.pizzasOrdered = new JLabel();
        initialize();
    }

    public void initialize() throws SQLException {
        setLayout(new BorderLayout(10, 10));
        setPreferredSize(new Dimension(500, 400));
        setResizable(false);

        // User Info Panel settings

        JPanel userInfoPanel = new JPanel(new GridLayout(9, 2, 10, 10));
        userInfoPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        userInfoPanel.add(new JLabel("Name:"));
        userInfoPanel.add(nameField);
        userInfoPanel.add(new JLabel("Gender:"));
        userInfoPanel.add(genderField);
        userInfoPanel.add(new JLabel("Email:"));
        userInfoPanel.add(emailField);
        userInfoPanel.add(new JLabel("Phone:"));
        userInfoPanel.add(phoneField);
        add(userInfoPanel, BorderLayout.CENTER);

        // Bottom panel: For amount of pizzas ordered
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        showUserInfo();
        bottomPanel.add(pizzasOrdered);
        add(bottomPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(app.getFrame());
    }

    public void showUserInfo() {
        String username = app.getCurrentUsername();
        try {
            if (username != null) {
                int customer_id = DatabaseHelper.getCustomerIdByUsername(username);
                this.userInfo = DatabaseHelper.getUserInfo(customer_id);
                nameField.setText(userInfo.getName());
                genderField.setText(userInfo.getGender());
                emailField.setText(userInfo.getEmailAddress());
                phoneField.setText(userInfo.getPhoneNumber());

                int totalPizzas = DatabaseHelper.getTotalPizzasOrderedByCustomer(username);
                pizzasOrdered.setText("Total pizzas ordered by " + username + ": " + totalPizzas);

            } else {
                JOptionPane.showMessageDialog(this, "Error, username not fetched", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e){
            JOptionPane.showMessageDialog(this, "Username not valid or non-existent" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);

        }


    }
}