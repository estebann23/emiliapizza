package com.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;
import java.sql.Timestamp;

public class DeliveryPanel extends JPanel {
    private final PizzaDeliveryApp app;
    private final JButton cartButton;
    private final JButton userButton;
    private JTextField postcodeField;
    private JTextField streetNameField;
    private JTextField streetNumberField;
    private JButton submitPostcodeButton;
    private JTextField discountField;
    private JButton submitDiscountButton;
    private CartPanel cartPanel;
    private UserPanel userPanel;

    public DeliveryPanel(PizzaDeliveryApp app) {
        this.app = app;
        this.cartButton = new JButton("See order");
        this.userButton = new JButton("User info");
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel centerPanel = new JPanel(new GridLayout(8, 1));

        // Top Panel for buttons and title
        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel topTextPanel = new JPanel(new GridBagLayout());
        JLabel topTextLabel = new JLabel("Order Checkout");
        topTextLabel.setFont(new Font("Arial", Font.BOLD, 18));
        topTextPanel.add(topTextLabel);
        cartButton.addActionListener(e -> showCartDialog());
        userButton.addActionListener(e -> {
            try {
                showUserDialog();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        topPanel.add(topTextPanel, BorderLayout.CENTER);
        topPanel.add(cartButton, BorderLayout.WEST);
        topPanel.add(userButton, BorderLayout.EAST);
        centerPanel.add(topPanel);

        // Adding Textbox for Discount
        JPanel discountPanel = new JPanel(new FlowLayout());
        discountPanel.add(new JLabel("Enter your discount code:"));
        discountField = new JTextField(10);
        discountPanel.add(discountField);
        submitDiscountButton = new JButton("Submit Discount");
        discountPanel.add(submitDiscountButton);
        centerPanel.add(discountPanel);
        submitDiscountButton.addActionListener(e -> checkDiscount());

        // Adding Address Information (Street, Number, Postcode)
        JPanel addressPanel = new JPanel(new FlowLayout());
        addressPanel.add(new JLabel("Street Name:"));
        streetNameField = new JTextField(15);
        addressPanel.add(streetNameField);
        addressPanel.add(new JLabel("Street Number:"));
        streetNumberField = new JTextField(5);
        addressPanel.add(streetNumberField);
        centerPanel.add(addressPanel);

        JPanel postcodePanel = new JPanel(new FlowLayout());
        postcodePanel.add(new JLabel("Enter your postal code:"));
        postcodeField = new JTextField(10);
        postcodePanel.add(postcodeField);
        submitPostcodeButton = new JButton("Confirm Order");
        postcodePanel.add(submitPostcodeButton);
        centerPanel.add(postcodePanel);

        submitPostcodeButton.addActionListener(e -> confirmOrder());

        add(centerPanel, BorderLayout.CENTER);
    }

    private void confirmOrder() {
        String postcode = postcodeField.getText().trim();
        if (postcode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a postal code.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int customerId = app.getDatabaseHelper().getCustomerIdByUsername(app.getCurrentUsername());
        double totalAmount = calculateTotalAmount();
        Timestamp orderStartTime = new Timestamp(System.currentTimeMillis());

        // Pass the postcode to getOrCreateBatchForOrder
        BatchInfo batchInfo = app.getDatabaseHelper().getOrCreateBatchForOrder(orderStartTime, postcode);
        if (batchInfo == null) {
            JOptionPane.showMessageDialog(this, "No available drivers at the moment. Please try again later.", "Driver Unavailable", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String deliveryDriver = batchInfo.driverName;
        if (deliveryDriver == null) {
            JOptionPane.showMessageDialog(this, "All drivers are currently busy. Please try ordering with us later.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int orderId;
        try {
            orderId = app.getDatabaseHelper().createOrderInBatch(customerId, totalAmount, batchInfo.batchId, postcode, deliveryDriver, orderStartTime);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to create order.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (orderId != -1) {
            app.getOrder().forEach(item -> app.getDatabaseHelper().insertOrderItem(orderId, item));

            // Calculate estimated delivery time based on when the batch will be dispatched
            int estimatedDeliveryTime = calculateEstimatedDeliveryTime(batchInfo.batchId);

            app.navigateToOrderStatusPanel(deliveryDriver, estimatedDeliveryTime);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to create order.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int calculateEstimatedDeliveryTime(int batchId) {
        // Calculate the remaining time until the batch is dispatched
        int remainingTime = app.getDatabaseHelper().getRemainingTimeForBatch(batchId);

        // Add delivery time (e.g., 15 minutes) to the remaining time
        int deliveryTime = 15 * 60; // 15 minutes in seconds

        return remainingTime + deliveryTime;
    }

    private double calculateTotalAmount() {
        double totalAmount = app.getOrder().stream().mapToDouble(item -> {
            switch (item.getItemType()) {
                case PIZZA:
                    return app.getDatabaseHelper().getPizzaPriceByName(item.getName()) * item.getQuantity();
                case DESSERT:
                    return app.getDatabaseHelper().getDessertPriceByName(item.getName()) * item.getQuantity();
                case DRINK:
                    return app.getDatabaseHelper().getDrinkPriceByName(item.getName()) * item.getQuantity();
                default:
                    return 0.0;
            }
        }).sum();

        double discountValue = app.getCurrentDiscountValue();
        if (discountValue > 0) {
            totalAmount -= totalAmount * discountValue;
        }

        // Include delivery cost if any
        double deliveryCost = 3.50; // Example delivery cost
        totalAmount += deliveryCost;

        return totalAmount;
    }

    private void showCartDialog() {
        if (app.getOrder().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty!", "Cart", JOptionPane.INFORMATION_MESSAGE);
        } else {
            cartPanel = new CartPanel(app);
            cartPanel.setVisible(true);
        }
    }

    private void showUserDialog() throws SQLException {
        userPanel = new UserPanel(app);
        userPanel.setVisible(true);
    }

    public void checkDiscount() {
        String discount = discountField.getText().trim();
        if (discount.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a discount code.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (isDiscount(discount)) {
            JOptionPane.showMessageDialog(this, "Discount applied successfully!", "Discount", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Discount code not valid.", "Invalid discount code", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isDiscount(String discount) {
        java.util.List<DiscountCode> discountCodes = app.getDatabaseHelper().getDiscountCodes();
        for (DiscountCode dc : discountCodes) {
            if (dc.getCode().equalsIgnoreCase(discount) && !dc.isUsed()) {
                double discountValue = dc.getValue();
                app.setCurrentDiscountValue(discountValue);
                if (cartPanel != null) {
                    cartPanel.applyDiscount(discountValue);
                }
                // Optionally, mark the discount code as used
                // dc.setUsed(true);
                return true;
            }
        }
        return false;
    }

    // Removed the orderQueue and related methods as they are no longer needed with the updated logic

    private static class OrderQueueEntry {
        private final int customerId;
        private final double totalAmount;
        private final int batchId;
        private final String postcode;

        public OrderQueueEntry(int customerId, double totalAmount, int batchId, String postcode) {
            this.customerId = customerId;
            this.totalAmount = totalAmount;
            this.batchId = batchId;
            this.postcode = postcode;
        }
    }
}