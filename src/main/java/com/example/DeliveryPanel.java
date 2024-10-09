package com.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

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
    private CartPanel cartPanel;  // Used to show the cart and calculate totals
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

        // Simulate the delivery driver assignment
        String deliveryDriver = DatabaseHelper.getDeliveryDriver(postcode);
        if (deliveryDriver != null) {
            // Create a new order in the database and get the generated Order_ID
            int customerId = app.getCustomerIdByUsername(app.getCurrentUsername());
            double totalAmount = app.getOrder().stream().mapToDouble(item -> {
                switch (item.getItemType()) {
                    case PIZZA: return DatabaseHelper.getPizzaPriceByName(item.getName()) * item.getQuantity();
                    case DESSERT: return DatabaseHelper.getDessertPriceByName(item.getName()) * item.getQuantity();
                    case DRINK: return DatabaseHelper.getDrinkPriceByName(item.getName()) * item.getQuantity();
                    default: return 0.0;
                }
            }).sum();

            int orderId = DatabaseHelper.createNewOrder(customerId, totalAmount, deliveryDriver);

            // Insert order items (pizzas, drinks, desserts) into the orderitems table
            for (CartItem item : app.getOrder()) {
                DatabaseHelper.insertOrderItem(orderId, item);
            }

            // Navigate to OrderStatusPanel and pass the assigned driver
            app.navigateToOrderStatusPanel(deliveryDriver);
        } else {
            JOptionPane.showMessageDialog(this, "No delivery driver found for this postal code. Enter a valid postcode.", "Error", JOptionPane.ERROR_MESSAGE);
        }
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
        List<DiscountCode> discountCodes = DatabaseHelper.getDiscountCodes();
        for (DiscountCode dc : discountCodes) {
            if (dc.getCode().equalsIgnoreCase(discount)) {
                double discountValue = dc.getValue();
                cartPanel = new CartPanel(app);
                cartPanel.applyDiscount(discountValue);
                return true;
            }
        }
        return false;
    }
}