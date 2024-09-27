package com.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AccountCreationPanel extends JPanel {
    private final PizzaDeliveryApp app;

    public AccountCreationPanel(PizzaDeliveryApp app) {
        this.app = app;
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel header = new JLabel("Create New Account");
        header.setFont(new Font("Serif", Font.BOLD, 24));
        header.setHorizontalAlignment(JLabel.CENTER);
        add(header, BorderLayout.NORTH);

        JPanel createAccountPanel = new JPanel(new GridLayout(9, 2, 10, 10));
        createAccountPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JTextField nameField = new JTextField();
        JTextField genderField = new JTextField();
        JTextField birthdateField = new JTextField("YYYY-MM-DD");
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        createAccountPanel.add(new JLabel("Name:"));
        createAccountPanel.add(nameField);
        createAccountPanel.add(new JLabel("Gender:"));
        createAccountPanel.add(genderField);
        createAccountPanel.add(new JLabel("Birthdate:"));
        createAccountPanel.add(birthdateField);
        createAccountPanel.add(new JLabel("Email:"));
        createAccountPanel.add(emailField);
        createAccountPanel.add(new JLabel("Phone:"));
        createAccountPanel.add(phoneField);
        createAccountPanel.add(new JLabel("Username:"));
        createAccountPanel.add(usernameField);
        createAccountPanel.add(new JLabel("Password:"));
        createAccountPanel.add(passwordField);

        add(createAccountPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton createButton = new JButton("Create Account");
        JButton backButton = new JButton("Back to Login");

        buttonPanel.add(createButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);

        createButton.addActionListener(e -> {
            boolean accountCreated = DatabaseHelper.createAccount(
                    nameField.getText(), genderField.getText(), birthdateField.getText(),
                    emailField.getText(), phoneField.getText(), usernameField.getText(),
                    new String(passwordField.getPassword())
            );

            if (accountCreated) {
                JOptionPane.showMessageDialog(this, "Account Created!", "Success", JOptionPane.INFORMATION_MESSAGE);
                app.navigateTo(PanelNames.LOGIN_PANEL); // Redirect back to login
            } else {
                JOptionPane.showMessageDialog(this, "Error creating account", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backButton.addActionListener(e -> app.navigateTo(PanelNames.LOGIN_PANEL));
    }
}