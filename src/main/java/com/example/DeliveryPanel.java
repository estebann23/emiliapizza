package com.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

public class DeliveryPanel extends JPanel {
    private final PizzaDeliveryApp app;

    public DeliveryPanel(PizzaDeliveryApp app) {
        this.app = app;
        initialize();
    }

    private void initialize() {
        ArrayList<String> orderList = app.getOrder();
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Create and add the header label
        JLabel header = new JLabel("Here is an overview of your order");
        header.setFont(new Font("Serif", Font.BOLD, 24));
        header.setHorizontalAlignment(JLabel.CENTER);
        add(header, BorderLayout.NORTH);

        //listener to the "Create new order" button
        JButton finishButton = new JButton("Create new order");
        add(finishButton, BorderLayout.SOUTH);
        finishButton.addActionListener(e -> {
            if (app.getOrder().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select at least one item", "No Selection", JOptionPane.WARNING_MESSAGE);
            } else {
                app.navigateTo(PanelNames.PIZZAS_PANEL);
            }
        });
    }
}