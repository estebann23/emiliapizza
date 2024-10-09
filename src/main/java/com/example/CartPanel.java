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
    private final JLabel discountedtotalLabel;
    private double totalAmount;
    private double discountValue = 0.0; // Discount percentage
    private final HashMap<CartItem, Integer> orderMap;
    private DefaultTableModel tableModel;

    public CartPanel(PizzaDeliveryApp app) {
        super(app.getFrame(), "Your Cart", true);
        this.app = app;
        this.totalLabel = new JLabel("Total Amount: $0.00");
        this.discountedtotalLabel = new JLabel();
        this.orderMap = new HashMap<>();
        this.cartTable = new JTable();
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout(10, 10));
        setPreferredSize(new Dimension(500, 400));
        setResizable(false);

        tableModel = new DefaultTableModel(new Object[]{"Item", "Quantity", "Price", "Actions"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1 || column == 3;
            }

            @Override
            public void setValueAt(Object aValue, int row, int column) {
                if (column == 1) {
                    try {
                        int newQuantity = Integer.parseInt(aValue.toString());
                        CartItem item = (CartItem) getValueAt(row, 0);
                        if (newQuantity <= 0) {
                            removeItem(item);
                        } else {
                            item.setQuantity(newQuantity);
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

        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel totalsPanel = new JPanel(new BorderLayout(10, 0));
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        discountedtotalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalsPanel.add(discountedtotalLabel, BorderLayout.SOUTH);
        totalsPanel.add(totalLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));

        JButton clearCartButton = new JButton("Clear Cart");
        clearCartButton.addActionListener(e -> clearCart());

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> this.dispose());

        buttonPanel.add(backButton);
        buttonPanel.add(clearCartButton);

        bottomPanel.add(buttonPanel, BorderLayout.WEST);
        bottomPanel.add(totalsPanel, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);

        updateCartDisplay();
        pack();
        setLocationRelativeTo(app.getFrame());
    }

    public void updateCartDisplay() {
        tableModel.setRowCount(0); // Clear existing rows
        totalAmount = 0.0;
        orderMap.clear();

        ArrayList<CartItem> order = app.getOrder();  // Adjust this to CartItem
        for (CartItem item : order) {
            orderMap.put(item, orderMap.getOrDefault(item, 0) + item.getQuantity());
        }

        for (CartItem item : orderMap.keySet()) {
            int quantity = orderMap.get(item);
            double itemPrice = 0.0;

            switch (item.getItemType()) {
                case PIZZA:
                    itemPrice = DatabaseHelper.getPizzaPriceByName(item.getName());
                    break;
                case DRINK:
                    itemPrice = DatabaseHelper.getDrinkPriceByName(item.getName());
                    break;
                case DESSERT:
                    itemPrice = DatabaseHelper.getDessertPriceByName(item.getName());
                    break;
            }

            totalAmount += itemPrice * quantity;
            tableModel.addRow(new Object[]{item, quantity, "$" + String.format("%.2f", itemPrice * quantity), "Remove"});
        }

        if (discountValue > 0) {
            double discountedTotal = totalAmount - (totalAmount * discountValue);
            totalLabel.setText("Before korting: " + String.format("%.2f", totalAmount));
            discountedtotalLabel.setText("Total (after " + (discountValue * 100) + "% off): $" + String.format("%.2f", discountedTotal));
        } else {
            totalLabel.setText("Total Amount: $" + String.format("%.2f", totalAmount));
        }
    }

    public void applyDiscount(double discountValue) {
        this.discountValue = discountValue;
        updateCartDisplay();
    }

    private void decreaseQuantity(CartItem item) {
        if (orderMap.containsKey(item)) {
            int quantity = orderMap.get(item);
            if (quantity > 1) {
                item.setQuantity(quantity - 1);
                app.getOrder().remove(item); // Update the order list
            } else {
                removeItem(item); // If quantity is 1, remove the item
            }
            updateCartDisplay();
        }
    }

    private void removeItem(CartItem item) {
        orderMap.remove(item); // Remove item from the map
        app.getOrder().removeIf(orderItem -> orderItem.equals(item)); // Remove all instances of the item
        updateCartDisplay();
    }

    private void clearCart() {
        app.getOrder().clear();
        updateCartDisplay();
    }

    public double getCartTotal() {
        return totalAmount;
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "Remove" : value.toString());
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }



        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = (value == null) ? "Remove" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                int selectedRow = cartTable.getSelectedRow();
                CartItem item = (CartItem) tableModel.getValueAt(selectedRow, 0);
                decreaseQuantity(item);  // Call the decreaseQuantity method
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