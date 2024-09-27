package com.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

public class DrinksPanel extends JPanel {
    private final PizzaDeliveryApp app;

    public DrinksPanel(PizzaDeliveryApp app) {
        this.app = app;
        initialize();
    }

    private void initialize() {
        ArrayList<String> drinksNames = DatabaseHelper.getDrinksNames();

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel header = new JLabel("Select the drink(s) you want");
        header.setHorizontalAlignment(JLabel.CENTER);
        add(header, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(0, 1));
        for (String drink : drinksNames) {
            JCheckBox checkBox = new JCheckBox(drink);
            centerPanel.add(checkBox);
        }
        add(centerPanel, BorderLayout.CENTER);

        JButton nextButton = new JButton("Next");
        add(nextButton, BorderLayout.SOUTH);

        nextButton.addActionListener(e -> {
            for (Component comp : centerPanel.getComponents()) {
                if (comp instanceof JCheckBox && ((JCheckBox) comp).isSelected()) {
                    app.getOrder().add(((JCheckBox) comp).getText());
                }
            }
            app.navigateTo(PanelNames.DESSERTS_PANEL);
        });
    }
}