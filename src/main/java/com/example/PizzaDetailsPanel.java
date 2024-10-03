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

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel pizzaNameLabel = new JLabel("<html><h2 style='text-align:center;'>" + pizza.getName() + "</h2></html>");
        pizzaNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        detailsPanel.add(pizzaNameLabel);

        detailsPanel.add(Box.createVerticalStrut(20));

        StringBuilder details = new StringBuilder("<html><div style='text-align:left;'>Toppings:<br>");
        double ingredientCost = 0.0;
        boolean isVegan = true;
        boolean isVegetarian = true;

        for (Topping topping : pizza.getToppings()) {
            details.append("- ").append(topping.getName()).append(" ($").append(String.format("%.2f", topping.getPrice())).append(")<br>");
            ingredientCost += topping.getPrice();

            if (!topping.isVegan()) {
                isVegan = false;
            }
            if (!topping.isVegetarian()) {
                isVegetarian = false;
            }
        }

        double profitMargin = ingredientCost * 0.40;
        double priceWithMargin = ingredientCost + profitMargin;
        double vat = priceWithMargin * 0.09;
        double finalPrice = priceWithMargin + vat;

        details.append("<br><strong>Price Breakdown:</strong><br>")
                .append("Ingredient Cost: $").append(String.format("%.2f", ingredientCost)).append("<br>")
                .append("Profit Margin (40%): $").append(String.format("%.2f", profitMargin)).append("<br>")
                .append("Price with Profit: $").append(String.format("%.2f", priceWithMargin)).append("<br>")
                .append("VAT (9%): $").append(String.format("%.2f", vat)).append("<br>")
                .append("<strong>Total Price (per pizza): $").append(String.format("%.2f", finalPrice)).append("</strong><br>");

        String dietaryInfo = "<br><strong>Dietary Information:</strong><br>"
                + "Vegan: " + (isVegan ? "Yes" : "No") + "<br>"
                + "Vegetarian: " + (isVegetarian ? "Yes" : "No") + "<br>";

        details.append(dietaryInfo).append("</div></html>");

        JLabel pizzaDetailsLabel = new JLabel(details.toString());
        pizzaDetailsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        detailsPanel.add(pizzaDetailsLabel);

        detailsPanel.add(Box.createVerticalStrut(20));

        JPanel quantityPanel = new JPanel();
        quantityPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JLabel quantityLabel = new JLabel("Quantity:");
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));

        quantityPanel.add(quantityLabel);
        quantityPanel.add(quantitySpinner);
        detailsPanel.add(quantityPanel);

        detailsPanel.add(Box.createVerticalStrut(20));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton addToCartButton = new JButton("Add to Cart");
        addToCartButton.addActionListener(e -> {
            int quantity = (Integer) quantitySpinner.getValue();
            app.addPizzaToOrder(pizza.getName(), quantity); // Add pizza with correct quantity
            JOptionPane.showMessageDialog(this, quantity + " " + pizza.getName() + "(s) added to cart!", "Success", JOptionPane.INFORMATION_MESSAGE);

            // Update cart button in PizzaPanel
            app.getPizzaPanel().updateCartButton();

            app.navigateTo(PanelNames.PIZZAS_PANEL);
        });


        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> app.navigateTo(PanelNames.PIZZAS_PANEL));

        buttonPanel.add(addToCartButton);
        buttonPanel.add(backButton);

        add(detailsPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}