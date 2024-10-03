package com.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PanelHelper {
    public static JPanel createTopPanel(String titleText, JButton cartButton, PizzaDeliveryApp app) {
        JPanel topPanel = new JPanel(new BorderLayout());

        // Title Label with consistent font and style
        JLabel titleLabel = new JLabel(titleText, JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Add title and cart button to the panel
        topPanel.add(titleLabel, BorderLayout.CENTER);

        // Set up the cart button
        cartButton.setText("Cart");
        cartButton.addActionListener(e -> app.showCart());
        topPanel.add(cartButton, BorderLayout.EAST);

        return topPanel;
    }
}