package com.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CartPanel extends JDialog {
    private final PizzaDeliveryApp app;
    private final JTable cartTable;
    private final JLabel totalLabel;
    private final JLabel discountedTotalLabel;
    private double totalAmount;
    double discountValue = 0.0;
    private final HashMap<CartItem, Integer> orderMap;
    private DefaultTableModel tableModel;

    public CartPanel(PizzaDeliveryApp app) {
        super(app.getFrame(), "Your Cart", true);
        this.app = app;
        this.totalLabel = new JLabel("Total Amount: $0.00");
        this.discountedTotalLabel = new JLabel();
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
        discountedTotalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalsPanel.add(discountedTotalLabel, BorderLayout.SOUTH);
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
        tableModel.setRowCount(0);
        totalAmount = 0.0;
        orderMap.clear();

        List<CartItem> order = app.getOrder();
        for (CartItem item : order) {
            orderMap.put(item, orderMap.getOrDefault(item, 0) + item.getQuantity());
        }

        for (CartItem item : orderMap.keySet()) {
            int quantity = orderMap.get(item);
            double itemPrice = DatabaseHelper.getItemPriceByNameAndType(item.getName(), item.getItemType()).orElse(0.0);
            totalAmount += itemPrice * quantity;
            tableModel.addRow(new Object[]{item, quantity, "$" + String.format("%.2f", itemPrice * quantity), "Remove"});
        }

        // Retrieve the discount value from the PizzaDeliveryApp instance
        discountValue = app.getCurrentDiscountValue();
        if (discountValue > 0) {
            double discountedTotal = totalAmount - (totalAmount * discountValue);
            totalLabel.setText("Before Discount: $" + String.format("%.2f", totalAmount));
            discountedTotalLabel.setText("Total (after " + (discountValue * 100) + "% off): $" + String.format("%.2f", discountedTotal));
        } else {
            totalLabel.setText("Total Amount: $" + String.format("%.2f", totalAmount));
            discountedTotalLabel.setText(""); // Clear the discounted total label if no discount is applied
        }
    }
    public void applyDiscount(double discountValue) {
        app.setCurrentDiscountValue(discountValue); // Set the discount value in the app instance
        updateCartDisplay(); // Update the cart display to reflect the new discount
    }


    private void removeItem(CartItem item) {
        orderMap.remove(item);
        app.getOrder().removeIf(orderItem -> orderItem.equals(item));
        updateCartDisplay();
    }

    private void clearCart() {
        app.getOrder().clear();
        updateCartDisplay();
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
                removeItem(item);
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
}