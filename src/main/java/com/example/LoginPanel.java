package com.example;

import com.example.DatabaseHelper;
import com.example.PanelNames;
import com.example.PizzaDeliveryApp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;


public class LoginPanel extends JPanel {
    private final PizzaDeliveryApp app;

    public LoginPanel(PizzaDeliveryApp app) {
        this.app = app;
        initialize();
    }

    public void initialize() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel header = createHeaderLabel("Welcome to Emilia Pizza!");
        add(header, BorderLayout.NORTH);

        JPanel loginPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        loginPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        loginPanel.add(new JLabel("Username:"));
        loginPanel.add(usernameField);
        loginPanel.add(new JLabel("Password:"));
        loginPanel.add(passwordField);

        add(loginPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton loginButton = createButton("Log In", Color.GREEN);
        JButton createAccountButton = createButton("Create Account", Color.RED);
        createAccountButton.addActionListener(e -> app.navigateTo(PanelNames.CREATE_ACCOUNT_PANEL));

        buttonPanel.add(loginButton);
        buttonPanel.add(createAccountButton);
        add(buttonPanel, BorderLayout.SOUTH);

        loginButton.addActionListener(e -> {
            boolean authenticated = DatabaseHelper.authenticateUser(usernameField.getText(), new String(passwordField.getPassword()));
            if (authenticated) {
                if(usernameField.getText().equalsIgnoreCase("bass")) {
                    app.navigateTo(PanelNames.EARNINGS_PANEL);
                } else {
                    app.setCurrentUsername(usernameField.getText());
                    int customerId = app.getCustomerIdByUsername(usernameField.getText());
                    DatabaseHelper.createNewOrder(customerId);
                    app.navigateTo(PanelNames.PIZZAS_PANEL);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid login", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        createAccountButton.addActionListener(e -> app.navigateTo(PanelNames.CREATE_ACCOUNT_PANEL));
    }

    private JLabel createHeaderLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Serif", Font.BOLD, 24));
        label.setHorizontalAlignment(JLabel.CENTER);
        return label;
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        return button;
    }
}