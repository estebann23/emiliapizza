package com.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class PizzaPanel extends JPanel {
    private final PizzaDeliveryApp app;
    private JButton cartButton;

    public PizzaPanel(PizzaDeliveryApp app) {
        this.app = app;
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(500, 600));

        cartButton = new JButton();

        JPanel topPanel = PanelHelper.createTopPanel("Pizza Menu", cartButton, app);
        add(topPanel, BorderLayout.NORTH);

        List<Pizza> pizzas = app.getDatabaseHelper().getPizzaDetails();

        JPanel pizzaListPanel = new JPanel(new GridLayout(0, 1, 15, 15));
        pizzaListPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        if (pizzas.isEmpty()) {
            pizzaListPanel.add(new JLabel("No pizzas available."));
        } else {
            for (Pizza pizza : pizzas) {
                JButton pizzaButton = new JButton(pizza.getName());
                pizzaButton.setPreferredSize(new Dimension(300, 50));
                pizzaButton.addActionListener(e -> app.navigateToPizzaDetails(pizza));
                pizzaListPanel.add(pizzaButton);
            }
        }

        JScrollPane scrollPane = new JScrollPane(pizzaListPanel);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());

        JButton nextButton = new JButton("Drinks");
        nextButton.addActionListener(e -> app.navigateTo(PanelNames.DRINKS_PANEL));

        bottomPanel.add(nextButton, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void updateCartButton() {
        cartButton.setText("Cart: " + app.getOrder().size() + " Items");
    }
}