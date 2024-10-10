package com.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.sql.Timestamp;
import com.example.DiscountCode;
import java.util.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

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
    private DiscountCode appliedDiscountCode;
    private boolean isOrderConfirmed = false;

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

        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel topTextPanel = new JPanel(new GridBagLayout());
        JLabel topTextLabel = new JLabel("Order Checkout");
        topTextLabel.setFont(new Font("Arial", Font.BOLD, 18));
        topTextPanel.add(topTextLabel);
        cartButton.addActionListener(e -> {
            checkDiscount();
            showCartDialog();
        });
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

        JPanel discountPanel = new JPanel(new FlowLayout());
        discountPanel.add(new JLabel("Enter your discount code:"));
        discountField = new JTextField(10);
        discountPanel.add(discountField);
        submitDiscountButton = new JButton("Submit Discount");
        discountPanel.add(submitDiscountButton);
        centerPanel.add(discountPanel);
        submitDiscountButton.addActionListener(e -> checkDiscount());

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

        submitPostcodeButton.addActionListener(e -> {
            try {
                confirmOrder();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        add(centerPanel, BorderLayout.CENTER);
    }

    private void confirmOrder() throws SQLException {
        if (isOrderConfirmed) {
            return;
        }

        // Check if at least one pizza is in the cart
        boolean hasPizza = app.getOrder().stream().anyMatch(item -> item.getItemType() == CartItem.ItemType.PIZZA);
        if (!hasPizza) {
            JOptionPane.showMessageDialog(this, "You must have at least one pizza in your cart to place an order.", "No Pizza in Cart", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String postcode = postcodeField.getText().trim();
        if (postcode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a postal code.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int customerId = app.getDatabaseHelper().getCustomerIdByUsername(app.getCurrentUsername());
        double totalAmount = calculateTotalAmount();
        Timestamp orderStartTime = new Timestamp(System.currentTimeMillis());

        // Get or create a batch for the order
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

        int discountCodeId = (appliedDiscountCode != null) ? appliedDiscountCode.getId() : -1;

        int orderId;
        try {
            orderId = app.getDatabaseHelper().createOrderInBatch(customerId, totalAmount, batchInfo.batchId, postcode,
                    deliveryDriver, orderStartTime, discountCodeId);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to create order.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (orderId != -1) {
            app.getOrder().forEach(item -> app.getDatabaseHelper().insertOrderItem(orderId, item));

            // Mark the discount code as used if applicable
            if (appliedDiscountCode != null) {
                app.getDatabaseHelper().markDiscountCodeAsUsed(appliedDiscountCode.getId());
            }

            int estimatedDeliveryTime = calculateEstimatedDeliveryTime(batchInfo.batchId);

            isOrderConfirmed = true;
            submitPostcodeButton.setEnabled(false);

            app.navigateToOrderStatusPanel(deliveryDriver, estimatedDeliveryTime);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to create order.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int calculateEstimatedDeliveryTime(int batchId) {
        int remainingTime = app.getDatabaseHelper().getRemainingTimeForBatch(batchId);
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

        double percentageDiscountValue = app.getCurrentDiscountValue();
        if (percentageDiscountValue > 0) {
            totalAmount -= totalAmount * percentageDiscountValue;
        }
        double fixedDiscountAmount = app.getCurrentFixedDiscountAmount();
        if (fixedDiscountAmount > 0) {
            totalAmount -= fixedDiscountAmount;
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
            cartPanel = new CartPanel(app, true);
            cartPanel.setVisible(true);
        }
    }

    private void showUserDialog() throws SQLException {
        userPanel = new UserPanel(app);
        userPanel.setVisible(true);
    }

    public void checkDiscount() {
        String discountCode = discountField.getText().trim();
        boolean discountApplied = false;

        if (!discountCode.isEmpty() && isDiscount(discountCode)) {
            JOptionPane.showMessageDialog(this, "Discount code applied successfully!", "Discount", JOptionPane.INFORMATION_MESSAGE);
            discountApplied = true;
        } else if (!discountCode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Discount code not valid.", "Invalid Discount Code", JOptionPane.ERROR_MESSAGE);
        }

        String username = app.getCurrentUsername();
        Optional<DatabaseHelper.CustomerBirthdayInfo> birthdayInfoOpt = app.getDatabaseHelper().getCustomerBirthdayInfo(username);
        if (birthdayInfoOpt.isPresent()) {
            DatabaseHelper.CustomerBirthdayInfo birthdayInfo = birthdayInfoOpt.get();
            if (birthdayInfo.canUseBirthdayDiscount() && isTodayBirthday(birthdayInfo.getBirthdate())) {
                double discountAmount = calculateBirthdayDiscount();
                if (discountAmount > 0) {
                    app.setCurrentFixedDiscountAmount(discountAmount);
                    if (cartPanel != null) {
                        cartPanel.applyFixedDiscount(discountAmount);
                    }

                    app.getDatabaseHelper().setCanBirthdayUsed(username);
                    JOptionPane.showMessageDialog(this, "Happy Birthday! You have received a discount on one pizza and one drink.", "Birthday Discount", JOptionPane.INFORMATION_MESSAGE);
                    discountApplied = true;
                } else {
                    JOptionPane.showMessageDialog(this, "No pizza or drink in your order to apply birthday discount.", "Birthday Discount", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
        if (!discountApplied && discountCode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No discount applied.", "Discount", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public boolean isDiscount(String discount) {
        List<DiscountCode> discountCodes = app.getDatabaseHelper().getDiscountCodes();
        for (DiscountCode dc : discountCodes) {
            if (dc.getCode().equalsIgnoreCase(discount) && dc.isAvailable()) {
                double discountValue = dc.getValue();
                app.setCurrentDiscountValue(discountValue);
                if (cartPanel != null) {
                    cartPanel.applyPercentageDiscount(discountValue);
                }
                appliedDiscountCode = dc;
                return true;
            }
        }
        return false;
    }

    private boolean isTodayBirthday(Date birthdate) {
        Calendar today = Calendar.getInstance();
        Calendar birthday = Calendar.getInstance();
        birthday.setTime(birthdate);
        return today.get(Calendar.MONTH) == birthday.get(Calendar.MONTH) &&
                today.get(Calendar.DAY_OF_MONTH) == birthday.get(Calendar.DAY_OF_MONTH);
    }

    private double calculateBirthdayDiscount() {
        double discountAmount = 0.0;
        boolean pizzaFound = false;
        boolean drinkFound = false;
        for (CartItem item : app.getOrder()) {
            if (item.getItemType() == CartItem.ItemType.PIZZA && !pizzaFound) {
                double pizzaPrice = app.getDatabaseHelper().getPizzaPriceByName(item.getName());
                discountAmount += pizzaPrice;
                pizzaFound = true;
            } else if (item.getItemType() == CartItem.ItemType.DRINK && !drinkFound) {
                double drinkPrice = app.getDatabaseHelper().getDrinkPriceByName(item.getName());
                discountAmount += drinkPrice;
                drinkFound = true;
            }
            if (pizzaFound && drinkFound) {
                break;
            }
        }
        return discountAmount;
    }
}