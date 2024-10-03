package com.example;

import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DeliveryPanel extends JPanel {
    private final PizzaDeliveryApp app;
    private final JButton cartButton;

    //postcode to deliverer
    private JTextField postcodeField;
    private JButton submitpostcodeButton;
    private JLabel assignedDriverLabel;

    // delivery time countdown
    private JLabel countdownLabel;
    private Timer countdownTimer;
    private int countdownSeconds;

    public DeliveryPanel(PizzaDeliveryApp app) {
        this.app = app;
        this.cartButton = new JButton("See order");
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        Panel centerPanel = new Panel(new GridLayout(8, 1));
        cartButton.addActionListener(e -> showCartDialog());
        centerPanel.add(cartButton);

        // Adding Textbox for Postcode
        JPanel postcodePanel = new JPanel(new FlowLayout());
        postcodePanel.add(new JLabel("Enter your postal code:"));
        postcodeField = new JTextField(10);
        postcodePanel.add(postcodeField);
        submitpostcodeButton = new JButton("Submit");
        postcodePanel.add(submitpostcodeButton);
        centerPanel.add(postcodePanel);

        // Showing the Delivery Driver who was assigned, based on the postcode
        assignedDriverLabel = new JLabel("Assigned Delivery Driver: ");
        centerPanel.add(assignedDriverLabel);
        add(centerPanel, BorderLayout.CENTER);

        // Countdown Label
        countdownLabel = new JLabel("Estimated Delivery Time: ");
        centerPanel.add(countdownLabel);

        // listener for postcode input
        submitpostcodeButton.addActionListener(e -> assignDeliveryDriver());

        //listener to the "Create new order" button
        JButton finishButton = new JButton("Create new order");
        add(finishButton, BorderLayout.SOUTH);
        finishButton.addActionListener(e -> {
            if (app.getOrder().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select at least one item", "No Selection", JOptionPane.WARNING_MESSAGE);
            } else {
                app.navigateTo(PanelNames.PIZZAS_PANEL);
            }
        });
    }
    private void showCartDialog() {
        if (app.getOrder().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty!", "Cart", JOptionPane.INFORMATION_MESSAGE);
        } else {
            // Open the CartPanel
            CartPanel cartPanel = new CartPanel(app);
            cartPanel.setVisible(true);
        }
    }

    private void assignDeliveryDriver() {
        String postcode = postcodeField.getText().trim();
        if (postcode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a postal code.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Use helper method to obtain delivery driver`s name fromm the DB
        String deliveryDriver = DatabaseHelper.getDeliveryDriver(postcode);
        if (deliveryDriver != null) {
            assignedDriverLabel.setText("Assigned Delivery Driver: " + deliveryDriver);
            startCountdown(15 * 60); 
        } else {
            assignedDriverLabel.setText("No delivery driver found for this postal code. Enter a valid postcode");
        }
    }

    private void startCountdown(int totalSeconds) {
        if (countdownTimer != null) {
            countdownTimer.stop();
        }
        countdownSeconds = totalSeconds;
        countdownLabel.setText("Estimated Delivery Time: " + timeformat(countdownSeconds)+" minutes");

        countdownTimer = new Timer(1000, e -> {
            countdownSeconds--;
            if (countdownSeconds <= 0) {
                countdownTimer.stop();
                countdownLabel.setText("Your order has arrived!");
            } else {
                countdownLabel.setText("Estimated Delivery Time: " + timeformat(countdownSeconds)+" minutes");
            }
        });
        countdownTimer.start();
    }
    private String timeformat(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}