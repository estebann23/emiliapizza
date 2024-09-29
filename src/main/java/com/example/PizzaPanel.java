package com.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class PizzaPanel extends JPanel {
    private final PizzaDeliveryApp app;
    private JButton cartButton;

    public PizzaPanel(PizzaDeliveryApp app) {
        this.app = app;
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(500, 600)); // Standard panel size

        List<Pizza> pizzas = DatabaseHelper.getPizzaDetails(); // Retrieve pizza list

        JPanel pizzaListPanel = new JPanel(new GridLayout(0, 1, 15, 15));
        pizzaListPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        if (pizzas.isEmpty()) {
            pizzaListPanel.add(new JLabel("No pizzas available."));
        } else {
            for (Pizza pizza : pizzas) {
                JButton pizzaButton = new JButton(pizza.getName());
                pizzaButton.setPreferredSize(new Dimension(300, 50));
                pizzaButton.addActionListener(e -> app.navigateToPizzaDetails(pizza));
                pizzaListPanel.add(pizzaButton);
            }
        }

        JScrollPane scrollPane = new JScrollPane(pizzaListPanel);
        add(scrollPane, BorderLayout.CENTER);

        // Cart button and navigation controls
        JPanel bottomPanel = new JPanel(new BorderLayout());
        cartButton = new JButton("Cart: 0 Items");
        cartButton.addActionListener(e -> showCartDialog());

        JButton nextButton = new JButton("Proceed to Drinks");
        nextButton.addActionListener(e -> app.navigateTo(PanelNames.DRINKS_PANEL));

        bottomPanel.add(cartButton, BorderLayout.WEST);
        bottomPanel.add(nextButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // Method to show the cart contents in a dialog
    private void showCartDialog() {
        List<String> order = app.getOrder();
        if (order.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty!", "Cart", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Your Cart: \n" + String.join(", ", order), "Cart", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Method to update the cart button with the current number of items
    public void updateCartButton() {
        cartButton.setText("Cart: " + app.getOrder().size() + " Items");
    }
}