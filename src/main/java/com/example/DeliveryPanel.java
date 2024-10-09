package com.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;

public class DeliveryPanel extends JPanel {
    private static final Queue<OrderQueueEntry> orderQueue = new LinkedList<>();
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

        int customerId = app.getCustomerIdByUsername(app.getCurrentUsername());
        double totalAmount = calculateTotalAmount();

        int batchId = DatabaseHelper.getOrCreateBatchForOrder(postcode);
        if (batchId == -1) {
            JOptionPane.showMessageDialog(this, "Failed to assign a batch for this order.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String deliveryDriver = DatabaseHelper.getDeliveryDriver(postcode);

        if (deliveryDriver == null) {
            JOptionPane.showMessageDialog(this, "All drivers are currently busy. Please try ordering with us later.", "Info", JOptionPane.INFORMATION_MESSAGE);
            addToOrderQueue(customerId, totalAmount, batchId, postcode);
            return;
        } else {
            DatabaseHelper.markDriverUnavailable(deliveryDriver);
        }

        if (finalizeOrder(customerId, totalAmount, deliveryDriver, 15 * 60)) {
            app.navigateToOrderStatusPanel(deliveryDriver, 15 * 60);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update order details.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addToOrderQueue(int customerId, double totalAmount, int batchId, String postcode) {
        orderQueue.add(new OrderQueueEntry(customerId, totalAmount, batchId, postcode));
        JOptionPane.showMessageDialog(this, "Your order has been added to the queue. We will notify you when a driver is available.", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private double calculateTotalAmount() {
        double totalAmount = app.getOrder().stream().mapToDouble(item -> {
            switch (item.getItemType()) {
                case PIZZA: return DatabaseHelper.getPizzaPriceByName(item.getName()) * item.getQuantity();
                case DESSERT: return DatabaseHelper.getDessertPriceByName(item.getName()) * item.getQuantity();
                case DRINK: return DatabaseHelper.getDrinkPriceByName(item.getName()) * item.getQuantity();
                default: return 0.0;
            }
        }).sum();

        double discountValue = app.getCurrentDiscountValue();
        if (discountValue > 0) {
            totalAmount -= totalAmount * discountValue;
        }

        return totalAmount;
    }

    private boolean finalizeOrder(int customerId, double totalAmount, String deliveryDriver, int countdownTime) {
        int orderId = DatabaseHelper.getCurrentOrderId();
        if (DatabaseHelper.updateOrderDetails(orderId, totalAmount, deliveryDriver)) {
            app.getOrder().forEach(item -> DatabaseHelper.insertOrderItem(orderId, item));
            return true;
        }
        return false;
    }

    private void startQueueProcessing() {
        Timer queueTimer = new Timer(10000, e -> {
            if (!orderQueue.isEmpty()) {
                OrderQueueEntry nextOrder = orderQueue.peek();
                String deliveryDriver = DatabaseHelper.getDeliveryDriver(nextOrder.getPostcode());

                if (deliveryDriver != null) {
                    DatabaseHelper.markDriverUnavailable(deliveryDriver);
                    try {
                        int orderId = DatabaseHelper.createOrderInBatch(
                                nextOrder.getCustomerId(),
                                nextOrder.getTotalAmount(),
                                nextOrder.getBatchId(),
                                nextOrder.getPostcode(),
                                deliveryDriver
                        );
                        app.navigateToOrderStatusPanel(deliveryDriver, 20 * 60);
                        orderQueue.poll();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        queueTimer.start();
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
            cartPanel.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Discount code not valid.", "Invalid discount code", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isDiscount(String discount) {
        java.util.List<DiscountCode> discountCodes = DatabaseHelper.getDiscountCodes();
        for (DiscountCode dc : discountCodes) {
            if (dc.getCode().equalsIgnoreCase(discount)) {
                double discountValue = dc.getValue();
                app.setCurrentDiscountValue(discountValue);
                cartPanel = new CartPanel(app);
                cartPanel.applyDiscount(discountValue);
                return true;
            }
        }
        return false;
    }

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

        public int getCustomerId() {
            return customerId;
        }

        public double getTotalAmount() {
            return totalAmount;
        }

        public int getBatchId() {
            return batchId;
        }

        public String getPostcode() {
            return postcode;
        }
    }
}