package com.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

public class PizzaPanel extends JPanel {
    private final PizzaDeliveryApp app;

    public PizzaPanel(PizzaDeliveryApp app) {
        this.app = app;
        initialize();
    }

    private void initialize() {
        // Get the list of pizza names from the database
        ArrayList<String> pizzaNames = DatabaseHelper.getPizzaNames();

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Create and add the header label
        JLabel header = new JLabel("Select the pizza(s) you want");
        header.setHorizontalAlignment(JLabel.CENTER);
        add(header, BorderLayout.NORTH);

        // Create a panel to hold the checkboxes for pizza selection
        JPanel centerPanel = new JPanel(new GridLayout(0, 1));
        for (String pizza : pizzaNames) {
            JCheckBox checkBox = new JCheckBox(pizza);
            centerPanel.add(checkBox);
        }
        add(centerPanel, BorderLayout.CENTER);

        // Create and add the "Next" button
        JButton nextButton = new JButton("Next");
        add(nextButton, BorderLayout.SOUTH);

        // Add action listener to the "Next" button
        nextButton.addActionListener(e -> {
            // Loop through the checkboxes and add selected pizzas to the order
            for (Component comp : centerPanel.getComponents()) {
                if (comp instanceof JCheckBox && ((JCheckBox) comp).isSelected()) {
                    app.getOrder().add(((JCheckBox) comp).getText());
                }
            }
            // Navigate to the next panel (DrinksPanel)
            app.navigateTo(PanelNames.DRINKS_PANEL);
        });
    }
}