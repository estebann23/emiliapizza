package com.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;

public class UserPanel extends JDialog{
    private final PizzaDeliveryApp app;
    private final JLabel pizzasOrdered;

    // UserInfoPanel fields
    private JTextField nameField = new JTextField();
    private JTextField genderField = new JTextField();
    private JTextField birthdateField = new JTextField();
    private JTextField emailField = new JTextField();
    private JTextField phoneField = new JTextField();


    public UserPanel(PizzaDeliveryApp app) {
        super(app.getFrame(), "User information", true);
        this.app = app;
        this.pizzasOrdered = new JLabel();
        initialize();
    }

    public void initialize() {
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
        userInfoPanel.add(new JLabel("Birthdate:"));
        userInfoPanel.add(birthdateField);
        userInfoPanel.add(new JLabel("Email:"));
        userInfoPanel.add(emailField);
        userInfoPanel.add(new JLabel("Phone:"));
        userInfoPanel.add(phoneField);
        setUserInfo();
        add(userInfoPanel, BorderLayout.CENTER);

        // Bottom panel: For amount of pizzas ordered
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        showTotalPizzasOrdered();
        bottomPanel.add(pizzasOrdered);
        add(bottomPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(app.getFrame());
    }
    public void setUserInfo() {
        nameField.setText("actual name");
    }
    public void showTotalPizzasOrdered() {
        String username = app.getCurrentUsername();
        if (username != null) {
            int totalPizzas = DatabaseHelper.getTotalPizzasOrderedByCustomer(username);
            pizzasOrdered.setText("Total pizzas ordered by " + username + ": " + totalPizzas);
        } else {
            JOptionPane.showMessageDialog(this, "Error, username not fetched", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
