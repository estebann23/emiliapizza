package com.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class DrinksPanel extends JPanel {
    private final PizzaDeliveryApp app;
    private JButton cartButton;

    public DrinksPanel(PizzaDeliveryApp app) {
        this.app = app;
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(500, 600));

        cartButton = new JButton();
        JPanel topPanel = PanelHelper.createTopPanel("Drinks", cartButton, app);
        add(topPanel, BorderLayout.NORTH);

        List<Drink> drinks = DatabaseHelper.getDrinkDetails();
        JPanel drinkListPanel = new JPanel(new GridLayout(0, 1, 15, 15));
        drinkListPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        if (drinks.isEmpty()) {
            drinkListPanel.add(new JLabel("No drinks available."));
        } else {
            for (Drink drink : drinks) {
                JButton drinkButton = new JButton(drink.getName());
                drinkButton.setPreferredSize(new Dimension(300, 50));
                drinkButton.addActionListener(e -> showDrinkDetails(drink));
                drinkListPanel.add(drinkButton);
            }
        }

        JScrollPane scrollPane = new JScrollPane(drinkListPanel);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        JButton backButton = new JButton("Back to Pizzas");
        backButton.addActionListener(e -> app.navigateTo(PanelNames.PIZZAS_PANEL));

        JButton nextButton = new JButton("Proceed to Desserts");
        nextButton.addActionListener(e -> app.navigateTo(PanelNames.DESSERTS_PANEL));

        bottomPanel.add(backButton, BorderLayout.WEST);
        bottomPanel.add(nextButton, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void showDrinkDetails(Drink drink) {
        JDialog dialog = new JDialog(app.getFrame(), "Drink Details", true);
        dialog.setSize(400, 250);
        dialog.setLayout(new BorderLayout(20, 20));

        JLabel drinkLabel = new JLabel("<html><h2>" + drink.getName() + "</h2><p>Price: $" + String.format("%.2f", drink.getPrice()) + "</p></html>", JLabel.CENTER);
        dialog.add(drinkLabel, BorderLayout.NORTH);

        JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel quantityLabel = new JLabel("Quantity:");
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        quantityPanel.add(quantityLabel);
        quantityPanel.add(quantitySpinner);
        dialog.add(quantityPanel, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton addToCartButton = new JButton("Add to Cart");
        JButton cancelButton = new JButton("Cancel");

        buttonsPanel.add(addToCartButton);
        buttonsPanel.add(cancelButton);
        dialog.add(buttonsPanel, BorderLayout.SOUTH);

        cancelButton.addActionListener(e -> dialog.dispose());

        addToCartButton.addActionListener(e -> {
            int quantity = (int) quantitySpinner.getValue();
            CartItem newItem = new CartItem(drink.getName(), DatabaseHelper.getDrinkIdByName(drink.getName()), CartItem.ItemType.DRINK);
            newItem.setQuantity(quantity);
            app.getOrder().add(newItem);
            updateCartButton();
            dialog.dispose();
        });

        dialog.setLocationRelativeTo(app.getFrame());
        dialog.setVisible(true);
    }

    public void updateCartButton() {
        cartButton.setText("Cart: " + app.getOrder().size() + " Items");
    }
}