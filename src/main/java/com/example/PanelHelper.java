package com.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PanelHelper {
    public static JPanel createTopPanel(String titleText, JButton cartButton, PizzaDeliveryApp app) {
        JPanel topPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel(titleText, JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(new EmptyBorder(10, 10, 10, 10));

        topPanel.add(titleLabel, BorderLayout.CENTER);

        cartButton.setText("Cart");
        cartButton.addActionListener(e -> app.showCart());
        topPanel.add(cartButton, BorderLayout.EAST);

        return topPanel;
    }
}