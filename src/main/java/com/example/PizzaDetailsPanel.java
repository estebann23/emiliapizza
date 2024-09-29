package com.example;

import javax.swing.*;
import java.awt.*;

public class PizzaDetailsPanel extends JPanel {
    private final PizzaDeliveryApp app;
    private final Pizza pizza;

    public PizzaDetailsPanel(PizzaDeliveryApp app, Pizza pizza) {
        this.app = app;
        this.pizza = pizza;
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(500, 600)); // Standard panel size

        // Add padding to the whole panel
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Padding around the panel

        // Pizza name (centered)
        JLabel pizzaNameLabel = new JLabel("<html><h2 style='text-align:center;'>" + pizza.getName() + "</h2></html>");
        pizzaNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        detailsPanel.add(pizzaNameLabel);

        // Add some vertical space after the title
        detailsPanel.add(Box.createVerticalStrut(20));

        // Build pizza details
        StringBuilder details = new StringBuilder("<html><div style='text-align:left;'>Toppings:<br>");
        double ingredientCost = 0.0;
        boolean isVegan = true;
        boolean isVegetarian = true;

        for (Topping topping : pizza.getToppings()) {
            details.append("- ").append(topping.getName()).append(" ($").append(String.format("%.2f", topping.getPrice())).append(")<br>");
            ingredientCost += topping.getPrice();

            // Update dietary information based on toppings
            if (!topping.isVegan()) {
                isVegan = false;
            }
            if (!topping.isVegetarian()) {
                isVegetarian = false;
            }
        }

        // Calculate final price with profit margin and VAT
        double profitMargin = ingredientCost * 0.40;   // 40% profit margin
        double priceWithMargin = ingredientCost + profitMargin;
        double vat = priceWithMargin * 0.09;           // 9% VAT
        double finalPrice = priceWithMargin + vat;

        // Add price breakdown to the details
        details.append("<br><strong>Price Breakdown:</strong><br>");
        details.append("Ingredient Cost: $").append(String.format("%.2f", ingredientCost)).append("<br>");
        details.append("Profit Margin (40%): $").append(String.format("%.2f", profitMargin)).append("<br>");
        details.append("Price with Profit: $").append(String.format("%.2f", priceWithMargin)).append("<br>");
        details.append("VAT (9%): $").append(String.format("%.2f", vat)).append("<br>");
        details.append("<strong>Total Price (per pizza): $").append(String.format("%.2f", finalPrice)).append("</strong><br>");

        // Dietary Information
        String dietaryInfo = "<br><strong>Dietary Information:</strong><br>";
        dietaryInfo += "Vegan: " + (isVegan ? "Yes" : "No") + "<br>";
        dietaryInfo += "Vegetarian: " + (isVegetarian ? "Yes" : "No") + "<br>";

        details.append(dietaryInfo);
        details.append("</div></html>");

        JLabel pizzaDetailsLabel = new JLabel(details.toString());
        pizzaDetailsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        detailsPanel.add(pizzaDetailsLabel);

        // Add some vertical space before the number spinner
        detailsPanel.add(Box.createVerticalStrut(20));

        // Number of pizzas selection
        JPanel quantityPanel = new JPanel();
        quantityPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JLabel quantityLabel = new JLabel("Quantity:");
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1)); // Default 1, min 1, max 100

        quantityPanel.add(quantityLabel);
        quantityPanel.add(quantitySpinner);
        detailsPanel.add(quantityPanel);

        // Add some vertical space before buttons
        detailsPanel.add(Box.createVerticalStrut(20));

        // Create a panel for the buttons with proper alignment
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10)); // Center the buttons with spacing

        // Add to Cart Button
        JButton addToCartButton = new JButton("Add to Cart");
        addToCartButton.addActionListener(e -> {
            int quantity = (Integer) quantitySpinner.getValue(); // Get the selected quantity
            for (int i = 0; i < quantity; i++) {
                app.addPizzaToOrder(pizza.getName());
            }
            JOptionPane.showMessageDialog(this, quantity + " " + pizza.getName() + "(s) added to cart!", "Success", JOptionPane.INFORMATION_MESSAGE);
            app.navigateTo(PanelNames.PIZZAS_PANEL); // Navigate back to pizza list after adding to cart
        });

        // Back Button
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> app.navigateTo(PanelNames.PIZZAS_PANEL)); // Go back to pizza list

        // Add buttons to the button panel
        buttonPanel.add(addToCartButton);
        buttonPanel.add(backButton);

        // Add the details panel and button panel to the main layout
        add(detailsPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}