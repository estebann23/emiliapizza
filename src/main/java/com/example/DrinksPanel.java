package com.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

public class DrinksPanel extends JPanel {
    private final PizzaDeliveryApp app;
    private final JButton cartButton;

    public DrinksPanel(PizzaDeliveryApp app) {
        this.app = app;
        this.cartButton = new JButton("Cart: 0 Items");
        initialize();
    }

    private void initialize() {
        // Get the list of drink names from the database
        ArrayList<String> drinksNames = DatabaseHelper.getDrinksNames();

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Create and add the header label
        JLabel header = new JLabel("Select Your Drinks");
        header.setFont(new Font("Serif", Font.BOLD, 24));
        header.setHorizontalAlignment(JLabel.CENTER);
        add(header, BorderLayout.NORTH);

        // Create a panel to hold the drink buttons
        JPanel centerPanel = new JPanel(new GridLayout(0, 2, 15, 15)); // 2-column grid
        centerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Create buttons for each drink and add them to the center panel
        for (String drink : drinksNames) {
            JButton drinkButton = createDrinkButton(drink);
            centerPanel.add(drinkButton);
        }
        add(centerPanel, BorderLayout.CENTER);

        // Cart button in the top-right corner
        JPanel topRightPanel = new JPanel(new BorderLayout());
        cartButton.addActionListener(e -> showCartDialog());
        topRightPanel.add(cartButton, BorderLayout.EAST);
        add(topRightPanel, BorderLayout.NORTH);

        // Create and add the "Proceed to Desserts" button
        JButton nextButton = new JButton("Proceed to Desserts");
        add(nextButton, BorderLayout.SOUTH);

        // Add action listener to the "Next" button
        nextButton.addActionListener(e -> {
            if (app.getOrder().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select at least one drink", "No Selection", JOptionPane.WARNING_MESSAGE);
            } else {
                app.navigateTo(PanelNames.DESSERTS_PANEL);
            }
        });
    }

    // Method to create drink buttons
    private JButton createDrinkButton(String drinkName) {
        JButton drinkButton = new JButton(drinkName);
        drinkButton.setFont(new Font("Arial", Font.PLAIN, 16));
        drinkButton.setPreferredSize(new Dimension(150, 50));

        // Add action listener to add drink to the order and update the cart button
        drinkButton.addActionListener(e -> {
            app.getOrder().add(drinkName);
            updateCartButton();
        });

        return drinkButton;
    }

    // Method to update the cart button with the current number of items
    private void updateCartButton() {
        cartButton.setText("Cart: " + app.getOrder().size() + " Items");
    }

    // Method to show a dialog with the current order
    private void showCartDialog() {
        if (app.getOrder().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty!", "Cart", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Your Cart: \n" + String.join(", ", app.getOrder()), "Cart", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}