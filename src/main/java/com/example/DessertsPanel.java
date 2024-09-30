package com.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

public class DessertsPanel extends JPanel {
    private final PizzaDeliveryApp app;
    private final JButton cartButton;

    public DessertsPanel(PizzaDeliveryApp app) {
        this.app = app;
        this.cartButton = new JButton("Cart: 0 Items");
        initialize();
    }

    private void initialize() {
        // Get the list of dessert names from the database
        ArrayList<String> dessertNames = DatabaseHelper.getDessertNames();

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Create and add the header label
        JLabel header = new JLabel("Select Your Desserts");
        header.setFont(new Font("Serif", Font.BOLD, 24));
        header.setHorizontalAlignment(JLabel.CENTER);
        add(header, BorderLayout.NORTH);

        // Create a panel to hold the dessert buttons
        JPanel centerPanel = new JPanel(new GridLayout(0, 2, 15, 15)); // 2-column grid
        centerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Creates buttons for each dessert and adds them to the center panel
        for (String dessert : dessertNames) {
            JButton dessertButton = createDessertButton(dessert);
            centerPanel.add(dessertButton);
        }
        add(centerPanel, BorderLayout.CENTER);

        // Cart button in the top-right corner
        JPanel topRightPanel = new JPanel(new BorderLayout());
        cartButton.addActionListener(e -> showCartDialog());
        topRightPanel.add(cartButton, BorderLayout.EAST);
        add(topRightPanel, BorderLayout.NORTH);

        //listener to the "Finish Order" button
        JButton finishButton = new JButton("Finish Order");
        add(finishButton, BorderLayout.SOUTH);
        finishButton.addActionListener(e -> {
            if (app.getOrder().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select at least one dessert", "No Selection", JOptionPane.WARNING_MESSAGE);
            } else {
                app.navigateTo(PanelNames.DELIVERY_PANEL);
            }
        });
    }

    // Method to create dessert buttons
    private JButton createDessertButton(String dessertName) {
        JButton dessertButton = new JButton(dessertName);
        dessertButton.setFont(new Font("Arial", Font.PLAIN, 16));
        dessertButton.setPreferredSize(new Dimension(150, 50));

        // Add action listener to add dessert to the order and update the cart button
        dessertButton.addActionListener(e -> {
            app.getOrder().add(dessertName);
            updateCartButton();
        });

        return dessertButton;
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