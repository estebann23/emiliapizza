package com.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

public class PizzaDeliveryApp {
    private JFrame frame;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private ArrayList<String> order;

    public PizzaDeliveryApp() {
        initializeUI();
    }

    private void initializeUI() {
        frame = new JFrame("Emilia Pizza Delivery");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        order = new ArrayList<>();

        // Adding different panels
        mainPanel.add(new LoginPanel(this), PanelNames.LOGIN_PANEL);
        mainPanel.add(new PizzaPanel(this), PanelNames.PIZZAS_PANEL);
        mainPanel.add(new DrinksPanel(this), PanelNames.DRINKS_PANEL);
        mainPanel.add(new DessertsPanel(this), PanelNames.DESSERTS_PANEL);
        mainPanel.add(new AccountCreationPanel(this), PanelNames.CREATE_ACCOUNT_PANEL);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    public void navigateTo(String panelName) {
        cardLayout.show(mainPanel, panelName);
    }

    public ArrayList<String> getOrder() {
        return order;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PizzaDeliveryApp::new);
    }
}