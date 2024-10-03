package com.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

public class CartPanel extends JDialog {
    private final PizzaDeliveryApp app;
    private final JTable cartTable;
    private final JLabel totalLabel;
    private double totalAmount;
    private final HashMap<String, Integer> orderMap;
    private DefaultTableModel tableModel;

    public CartPanel(PizzaDeliveryApp app) {
        super(app.getFrame(), "Your Cart", true);
        this.app = app;
        this.totalLabel = new JLabel("Total Amount: $0.00");
        this.orderMap = new HashMap<>();
        this.cartTable = new JTable();
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout(10, 10));
        setPreferredSize(new Dimension(500, 400));
        setResizable(false);

        // Create and configure the table model with editable quantity column
        tableModel = new DefaultTableModel(new Object[]{"Item", "Quantity", "Price", "Actions"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1 || column == 3; // Allow editing the Quantity and Actions columns
            }

            @Override
            public void setValueAt(Object aValue, int row, int column) {
                if (column == 1) {
                    try {
                        int newQuantity = Integer.parseInt(aValue.toString());
                        if (newQuantity <= 0) {
                            String item = (String) getValueAt(row, 0);
                            removeItem(item);
                        } else {
                            String item = (String) getValueAt(row, 0);
                            int oldQuantity = orderMap.get(item);
                            int diff = newQuantity - oldQuantity;
                            if (diff > 0) {
                                for (int i = 0; i < diff; i++) {
                                    app.getOrder().add(item);
                                }
                            } else if (diff < 0) {
                                for (int i = 0; i < -diff; i++) {
                                    app.getOrder().remove(item);
                                }
                            }
                            orderMap.put(item, newQuantity);
                            updateCartDisplay();
                        }
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(CartPanel.this, "Please enter a valid number", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        };

        cartTable.setModel(tableModel);
        cartTable.setFont(new Font("Arial", Font.PLAIN, 14));
        cartTable.setRowHeight(30);

        cartTable.getColumn("Actions").setCellRenderer(new ButtonRenderer());
        cartTable.getColumn("Actions").setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(cartTable);
        scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel for total and action buttons
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Total Label
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        bottomPanel.add(totalLabel, BorderLayout.EAST);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));

        JButton clearCartButton = new JButton("Clear Cart");
        clearCartButton.addActionListener(e -> clearCart());

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> this.dispose());

        buttonPanel.add(backButton);
        buttonPanel.add(clearCartButton);

        bottomPanel.add(buttonPanel, BorderLayout.WEST);

        add(bottomPanel, BorderLayout.SOUTH);

        // Update the cart display initially
        updateCartDisplay();
        pack();
        setLocationRelativeTo(app.getFrame());
    }

    public void updateCartDisplay() {
        tableModel.setRowCount(0); // Clear existing rows
        totalAmount = 0.0;
        orderMap.clear();

        ArrayList<String> order = app.getOrder();
        for (String item : order) {
            orderMap.put(item, orderMap.getOrDefault(item, 0) + 1);
        }

        for (String item : orderMap.keySet()) {
            int quantity = orderMap.get(item);
            double itemPrice = 0.0;

            // Check if the item is a pizza, drink, or dessert
            if (DatabaseHelper.getPizzaNames().contains(item)) {
                itemPrice = DatabaseHelper.getPizzaPriceByName(item);
            } else if (DatabaseHelper.getDrinksNames().contains(item)) {
                itemPrice = DatabaseHelper.getDrinkPriceByName(item);
            } else if (DatabaseHelper.getDessertNames().contains(item)) {  // Handle desserts
                itemPrice = DatabaseHelper.getDessertPriceByName(item);
            }

            totalAmount += itemPrice * quantity;

            // Add row to the table
            tableModel.addRow(new Object[]{item, quantity, "$" + String.format("%.2f", itemPrice * quantity), "Remove"});
        }

        totalLabel.setText("Total Amount: $" + String.format("%.2f", totalAmount));
    }

    private void decreaseQuantity(String item) {
        if (orderMap.containsKey(item)) {
            int quantity = orderMap.get(item);
            if (quantity > 1) {
                orderMap.put(item, quantity - 1);
                app.getOrder().remove(item); // Remove one instance of the item from the order list
            } else {
                removeItem(item); // If quantity is 1, remove the item
            }
            updateCartDisplay(); // Refresh cart display after updating quantity
        }
    }

    private void removeItem(String item) {
        orderMap.remove(item); // Remove item from the map
        app.getOrder().removeIf(orderItem -> orderItem.equals(item)); // Remove all instances of the item from the order list
        updateCartDisplay(); // Refresh cart display after item removal
    }

    private void clearCart() {
        app.getOrder().clear();
        updateCartDisplay();
    }

    // Custom TableCellRenderer for displaying a button in a table cell
    class ButtonRenderer extends JButton implements TableCellRenderer {

        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "Remove" : value.toString());
            return this;
        }
    }

    // Custom TableCellEditor for making a button clickable in a table cell
    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            label = (value == null) ? "Remove" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                int selectedRow = cartTable.getSelectedRow();
                String item = (String) tableModel.getValueAt(selectedRow, 0);
                decreaseQuantity(item); // Call the decreaseQuantity method when the button is pressed
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }
}