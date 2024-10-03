package com.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class DessertsPanel extends JPanel {
    private final PizzaDeliveryApp app;
    private JButton cartButton;

    public DessertsPanel(PizzaDeliveryApp app) {
        this.app = app;
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(500, 600));

        // Initialize cart button
        cartButton = new JButton();

        // Add the top panel with title and cart button
        JPanel topPanel = PanelHelper.createTopPanel("Desserts", cartButton, app);
        add(topPanel, BorderLayout.NORTH);

        // Get the list of dessert objects from the database
        List<Dessert> desserts = DatabaseHelper.getDessertDetails();

        JPanel dessertListPanel = new JPanel(new GridLayout(0, 1, 15, 15));
        dessertListPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        if (desserts.isEmpty()) {
            dessertListPanel.add(new JLabel("No desserts available."));
        } else {
            for (Dessert dessert : desserts) {
                JButton dessertButton = new JButton(dessert.getName());
                dessertButton.setPreferredSize(new Dimension(300, 50));
                dessertButton.addActionListener(e -> showDessertDetails(dessert));
                dessertListPanel.add(dessertButton);
            }
        }

        JScrollPane scrollPane = new JScrollPane(dessertListPanel);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());

        // Back button to navigate to the DrinksPanel
        JButton backButton = new JButton("Back to Drinks");
        backButton.addActionListener(e -> app.navigateTo(PanelNames.DRINKS_PANEL));

        JButton nextButton = new JButton("Checkout");
        nextButton.addActionListener(e -> app.navigateTo(PanelNames.DELIVERY_PANEL));

        bottomPanel.add(backButton, BorderLayout.WEST);
        bottomPanel.add(nextButton, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void showDessertDetails(Dessert dessert) {
        // Create a dialog for selecting dessert quantity and showing price
        JDialog dialog = new JDialog(app.getFrame(), "Dessert Details", true);
        dialog.setSize(400, 250);
        dialog.setLayout(new BorderLayout(20, 20));

        // Dessert name and price
        JLabel dessertLabel = new JLabel("<html><h2>" + dessert.getName() + "</h2><p>Price: $" + String.format("%.2f", dessert.getPrice()) + "</p></html>", JLabel.CENTER);
        dialog.add(dessertLabel, BorderLayout.NORTH);

        // Quantity selection panel
        JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel quantityLabel = new JLabel("Quantity:");
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));

        quantityPanel.add(quantityLabel);
        quantityPanel.add(quantitySpinner);
        dialog.add(quantityPanel, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton addToCartButton = new JButton("Add to Cart");
        JButton cancelButton = new JButton("Cancel");

        buttonsPanel.add(addToCartButton);
        buttonsPanel.add(cancelButton);
        dialog.add(buttonsPanel, BorderLayout.SOUTH);

        // Add action listener to cancel button to close the dialog
        cancelButton.addActionListener(e -> dialog.dispose());

        // Add action listener to the addToCart button
        addToCartButton.addActionListener(e -> {
            int quantity = (int) quantitySpinner.getValue();
            for (int i = 0; i < quantity; i++) {
                app.getOrder().add(dessert.getName());
            }
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