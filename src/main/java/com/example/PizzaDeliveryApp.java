package com.example;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Stack;

public class PizzaDeliveryApp {
    private JFrame frame;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private final ArrayList<CartItem> order;
    private final Stack<String> panelHistory;
    private PizzaPanel pizzaPanel;
    private String currentUsername;
    private double currentDiscountValue = 0.0;
    private DatabaseHelper databaseHelper; // Added instance variable

    public PizzaDeliveryApp() {
        panelHistory = new Stack<>();
        order = new ArrayList<>();
        databaseHelper = new DatabaseHelper(this); // Initialize DatabaseHelper instance
        initializeUI();
        updatePizzaPrices();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PizzaDeliveryApp::new);
    }

    private void initializeUI() {
        frame = new JFrame("Emilia Pizza Delivery");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 600);
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        pizzaPanel = new PizzaPanel(this);
        mainPanel.add(new LoginPanel(this), PanelNames.LOGIN_PANEL);
        mainPanel.add(pizzaPanel, PanelNames.PIZZAS_PANEL);
        mainPanel.add(new AccountCreationPanel(this), PanelNames.CREATE_ACCOUNT_PANEL);
        mainPanel.add(new DrinksPanel(this), PanelNames.DRINKS_PANEL);
        mainPanel.add(new DessertsPanel(this), PanelNames.DESSERTS_PANEL);
        mainPanel.add(new AccountCreationPanel(this), PanelNames.CREATE_ACCOUNT_PANEL);
        mainPanel.add(new DeliveryPanel(this), PanelNames.DELIVERY_PANEL);
        mainPanel.add(new EarningsReportPanel(this), PanelNames.EARNINGS_PANEL);
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

    public void navigateToOrderStatusPanel(String deliveryDriver, int countdownTime) {
        // Check if an OrderStatusPanel already exists in the main panel
        OrderStatusPanel orderStatusPanel = null;
        for (Component comp : mainPanel.getComponents()) {
            if (comp instanceof OrderStatusPanel) {
                orderStatusPanel = (OrderStatusPanel) comp;
                break;
            }
        }

        // If it doesn't exist, create a new one
        if (orderStatusPanel == null) {
            orderStatusPanel = new OrderStatusPanel(this, deliveryDriver, countdownTime);
            mainPanel.add(orderStatusPanel, "OrderStatusPanel");
        } else {
            // Update the existing panel with new driver info and countdown
            orderStatusPanel = new OrderStatusPanel(this, deliveryDriver, countdownTime);
        }

        // Show the OrderStatusPanel
        cardLayout.show(mainPanel, "OrderStatusPanel");
    }

    public void showCart() {
        CartPanel cartPanel = new CartPanel(this);
        cartPanel.setVisible(true);
    }

    public ArrayList<CartItem> getOrder() {
        return order;
    }

    public JFrame getFrame() {
        return frame;
    }

    public PizzaPanel getPizzaPanel() {
        return pizzaPanel;
    }

    private void updatePizzaPrices() {
        try {
            databaseHelper.updatePizzaPricesForAll();
            System.out.println("Pizza prices updated successfully.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error updating pizza prices: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void navigateToPizzaDetails(Pizza pizza) {
        PizzaDetailsPanel detailsPanel = new PizzaDetailsPanel(this, pizza);
        mainPanel.add(detailsPanel, PanelNames.PIZZA_DETAILS_PANEL);
        cardLayout.show(mainPanel, PanelNames.PIZZA_DETAILS_PANEL);
    }

    public void addPizzaToOrder(String pizzaName, int quantity) {
        CartItem newItem = new CartItem(pizzaName, databaseHelper.getPizzaIdByName(pizzaName), CartItem.ItemType.PIZZA);
        newItem.setQuantity(quantity);
        order.add(newItem);
        pizzaPanel.updateCartButton();
    }

    public int getCustomerIdByUsername(String username) {
        return databaseHelper.getCustomerIdByUsername(username);
    }

    public void setCurrentUsername(String username) {
        this.currentUsername = username;
    }

    public String getCurrentUsername() {
        return currentUsername;
    }

    public double getCurrentDiscountValue() {
        return currentDiscountValue;
    }

    public void setCurrentDiscountValue(double discountValue) {
        this.currentDiscountValue = discountValue;
    }

    public void clearCart() {
        getOrder().clear();
        setCurrentDiscountValue(0.0);
        if (pizzaPanel != null) {
            pizzaPanel.updateCartButton();
        }
    }

    public void resetAllPanels() {
        clearCart();

        pizzaPanel = new PizzaPanel(this);
        DrinksPanel drinksPanel = new DrinksPanel(this);
        DessertsPanel dessertsPanel = new DessertsPanel(this);
        DeliveryPanel deliveryPanel = new DeliveryPanel(this);

        mainPanel.removeAll();
        mainPanel.add(pizzaPanel, PanelNames.PIZZAS_PANEL);
        mainPanel.add(drinksPanel, PanelNames.DRINKS_PANEL);
        mainPanel.add(dessertsPanel, PanelNames.DESSERTS_PANEL);
        mainPanel.add(deliveryPanel, PanelNames.DELIVERY_PANEL);

        navigateTo(PanelNames.LOGIN_PANEL);
        panelHistory.clear();
    }

    // Getter for DatabaseHelper
    public DatabaseHelper getDatabaseHelper() {
        return databaseHelper;
    }
}