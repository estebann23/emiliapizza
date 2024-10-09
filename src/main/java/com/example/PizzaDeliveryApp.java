package com.example;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Stack;

import static com.example.DatabaseHelper.getPizzaIdByName;

public class PizzaDeliveryApp {
    private JFrame frame;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private final ArrayList<CartItem> order;
    private final Stack<String> panelHistory;
    private PizzaPanel pizzaPanel;
    private String currentUsername;
    private double currentDiscountValue = 0.0;



    public PizzaDeliveryApp() {
        panelHistory = new Stack<>();
        order = new ArrayList<>();
        initializeUI();
        DatabaseHelper.setAppInstance(this);
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
        mainPanel.add(new DrinksPanel(this), PanelNames.DRINKS_PANEL);
        mainPanel.add(new DessertsPanel(this), PanelNames.DESSERTS_PANEL);
        mainPanel.add(new DeliveryPanel(this), PanelNames.DELIVERY_PANEL);
        mainPanel.add(new EarningsReportPanel(this), PanelNames.EARNINGS_PANEL);
        mainPanel.add(new OrderStatusPanel(this, "Default Driver"), "OrderStatusPanel");
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

    public void navigateToOrderStatusPanel(String deliveryDriver) {
        OrderStatusPanel orderStatusPanel = new OrderStatusPanel(this, deliveryDriver);
        mainPanel.add(orderStatusPanel, "OrderStatusPanel");
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
            DatabaseHelper.updatePizzaPricesForAll();
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
        CartItem newItem = new CartItem(pizzaName, DatabaseHelper.getPizzaIdByName(pizzaName), CartItem.ItemType.PIZZA);
        newItem.setQuantity(quantity);
        order.add(newItem);
        pizzaPanel.updateCartButton();
    }

    public int getCustomerIdByUsername(String username) {
        return DatabaseHelper.getCustomerIdByUsername(username);
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
        setCurrentDiscountValue(0.0); // Reset the discount
        if (pizzaPanel != null) {
            pizzaPanel.updateCartButton(); // Update the cart button to reflect the cleared cart
        }
    }

    public void resetAllPanels() {
        // Clear the cart and reset the discount
        clearCart();

        // Reset the state of individual panels if needed
        pizzaPanel = new PizzaPanel(this);
        DrinksPanel drinksPanel = new DrinksPanel(this);
        DessertsPanel dessertsPanel = new DessertsPanel(this);
        DeliveryPanel deliveryPanel = new DeliveryPanel(this);

        // Re-add the panels to the main panel to reset their state
        mainPanel.removeAll();
        mainPanel.add(pizzaPanel, PanelNames.PIZZAS_PANEL);
        mainPanel.add(drinksPanel, PanelNames.DRINKS_PANEL);
        mainPanel.add(dessertsPanel, PanelNames.DESSERTS_PANEL);
        mainPanel.add(deliveryPanel, PanelNames.DELIVERY_PANEL);

        // Navigate to the initial panel (e.g., Login)
        navigateTo(PanelNames.LOGIN_PANEL);

        // Clear panel history
        panelHistory.clear();
    }
}