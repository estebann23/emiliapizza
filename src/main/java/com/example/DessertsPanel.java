package com.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

public class DessertsPanel extends JPanel {
    private final PizzaDeliveryApp app;

    public DessertsPanel(PizzaDeliveryApp app) {
        this.app = app;
        initialize();
    }

    private void initialize() {
        ArrayList<String> dessertNames = DatabaseHelper.getDessertNames();

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel header = new JLabel("Select the dessert(s) you want");
        header.setHorizontalAlignment(JLabel.CENTER);
        add(header, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(0, 1));
        for (String dessert : dessertNames) {
            JCheckBox checkBox = new JCheckBox(dessert);
            centerPanel.add(checkBox);
        }
        add(centerPanel, BorderLayout.CENTER);

        JButton finishButton = new JButton("Finish");
        add(finishButton, BorderLayout.SOUTH);

        finishButton.addActionListener(e -> {
            for (Component comp : centerPanel.getComponents()) {
                if (comp instanceof JCheckBox && ((JCheckBox) comp).isSelected()) {
                    app.getOrder().add(((JCheckBox) comp).getText());
                }
            }
            JOptionPane.showMessageDialog(this, "Order Complete!\n" + String.join(", ", app.getOrder()), "Order Summary", JOptionPane.INFORMATION_MESSAGE);
            app.navigateTo(PanelNames.LOGIN_PANEL);
        });
    }
}