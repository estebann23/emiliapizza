package com.example;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Stack;

public class PizzaDeliveryApp {
    private JFrame frame;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private final ArrayList<String> order;
    private final Stack<String> panelHistory;
    private PizzaPanel pizzaPanel;
    private String currentUsername;


    public PizzaDeliveryApp() {
        panelHistory = new Stack<>();
        order = new ArrayList<>();
        initializeUI();
        updatePizzaPrices();
    }

    // Main method to run the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(PizzaDeliveryApp::new);
    }

    private void initializeUI() {
        frame = new JFrame("Emilia Pizza Delivery");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 600);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Initialize PizzaPanel and store it as an instance variable
        pizzaPanel = new PizzaPanel(this);

        // Add panels to the main panel
        mainPanel.add(new LoginPanel(this), PanelNames.LOGIN_PANEL);
        mainPanel.add(pizzaPanel, PanelNames.PIZZAS_PANEL); // Add pizzaPanel here
        mainPanel.add(new DrinksPanel(this), PanelNames.DRINKS_PANEL);
        mainPanel.add(new DessertsPanel(this), PanelNames.DESSERTS_PANEL);
        mainPanel.add(new DeliveryPanel(this), PanelNames.DELIVERY_PANEL);
        mainPanel.add(new AccountCreationPanel(this), PanelNames.CREATE_ACCOUNT_PANEL);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    public void navigateTo(String panelName) {
        if (!panelHistory.isEmpty()) {
            panelHistory.push(panelName);
        }
        cardLayout.show(mainPanel, panelName);
    }

    public void navigateToPreviousPanel() {
        if (!panelHistory.isEmpty()) {
            panelHistory.pop();
            if (!panelHistory.isEmpty()) {
                cardLayout.show(mainPanel, panelHistory.peek());
            } else {
                cardLayout.show(mainPanel, PanelNames.LOGIN_PANEL);
            }
        }
    }

    public void showCart() {
        CartPanel cartPanel = new CartPanel(this);
        cartPanel.setVisible(true);
    }

    // Getter for the order
    public ArrayList<String> getOrder() {
        return order;
    }

    // Getter for the JFrame
    public JFrame getFrame() {
        return frame;
    }

    // Getter for the PizzaPanel
    public PizzaPanel getPizzaPanel() {
        return pizzaPanel;
    }

    private void updatePizzaPrices() {
        try {
            DatabaseHelper.updatePizzaPricesForAll();
            System.out.println("Pizza prices updated successfully.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error updating pizza prices: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Navigate to the pizza details panel
    public void navigateToPizzaDetails(Pizza pizza) {
        PizzaDetailsPanel detailsPanel = new PizzaDetailsPanel(this, pizza);
        mainPanel.add(detailsPanel, PanelNames.PIZZA_DETAILS_PANEL);
        cardLayout.show(mainPanel, PanelNames.PIZZA_DETAILS_PANEL);
    }

    // Method to add pizza to the order
    public void addPizzaToOrder(String pizzaName, int quantity) {
        order.add(pizzaName);
        pizzaPanel.updateCartButton(); // Update the cart button when a pizza is added
    }

    //Current Username storing
    public void setCurrentUsername(String username) {
        this.currentUsername = username;
    }
    public String getCurrentUsername() {
        return currentUsername;
    }
}