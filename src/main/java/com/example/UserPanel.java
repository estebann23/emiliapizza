package com.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;

public class UserPanel extends JDialog{
    private final PizzaDeliveryApp app;
    private final JLabel pizzasOrdered;

    public UserPanel(PizzaDeliveryApp app) {
        super(app.getFrame(), "User information", true);
        this.app = app;
        this.pizzasOrdered = new JLabel();
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout(10, 10));
        setPreferredSize(new Dimension(500, 400));
        setResizable(false);

        // Bottom panel: For amount of pizzas ordered
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        showTotalPizzasOrdered();
        bottomPanel.add(pizzasOrdered);
        add(bottomPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(app.getFrame());
    }

    public void showTotalPizzasOrdered() {
        String username = app.getCurrentUsername();
        if (username != null) {
            int totalPizzas = DatabaseHelper.getTotalPizzasOrderedByCustomer(username);
            pizzasOrdered.setText("Total pizzas ordered by " + username + ": " + totalPizzas);
        } else {
            JOptionPane.showMessageDialog(this, "Error, username not fetched", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
