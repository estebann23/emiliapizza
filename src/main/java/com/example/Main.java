package com.example;
import java.sql.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;


    public class Main {
        static final String DB_URL = "jdbc:mysql://localhost:3306/emiliadb";
        static final String USER = "root";
        static final String PASS = "mysql2311";

        private JFrame frame;
        private JPanel mainPanel;
        private CardLayout cardLayout;
        private ArrayList<String> order;

        public Main() {
        order = new ArrayList<>();
        frame =new JFrame("Emilia Pizza Delivery");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400,300);

        cardLayout =new CardLayout();
        mainPanel =new JPanel(cardLayout);

        mainPanel.add(LoginPanel(), "LoginPanel");
        mainPanel.add(PizzasPanel(), "PizzasPanel");
        mainPanel.add(DessertsPanel(), "DessertsPanel");
        mainPanel.add(DrinksPanel(), "DrinksPanel");

        frame.add(mainPanel);
        frame.setVisible(true);

    }

        private JPanel LoginPanel() {
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());

            JPanel introPanel = new JPanel(new GridLayout(4,1,0,4));
            introPanel.add(new JLabel("Welcome to Emilia Pizza."));
            introPanel.add(new JLabel("Enter your log in details."));
            introPanel.add(new JLabel("Click 'Create account' if don't have one."));
            introPanel.add(new JLabel(" "));
            panel.add(introPanel, BorderLayout.NORTH);

            JPanel loginPanel = new JPanel(new GridLayout(5, 10,20,4));
            loginPanel.add(new JLabel("Username:"));
            JTextField usernameField = new JTextField();
            loginPanel.add(usernameField);
            loginPanel.add(new JLabel("Password:"));
            JPasswordField passwordField = new JPasswordField();
            loginPanel.add(passwordField);
            panel.add(loginPanel, BorderLayout.CENTER);

            JButton loginButton = new JButton("Log In");
            panel.add(loginButton, BorderLayout.SOUTH);

            loginButton.addActionListener(e -> {
                String username = usernameField.getText();
                cardLayout.show(mainPanel, "PizzasPanel");
            });
            return panel;
        }

        private JPanel PizzasPanel() {
            ArrayList <String> pizzaNames = getPizzaNames();
            return selectionpanelCreator("Select the pizza(s) you want", pizzaNames, "DrinksPanel");
        }

        private JPanel DrinksPanel() {
            ArrayList <String> drinksNames = getDrinksNames();
            return selectionpanelCreator("Select the drink(s) you want", drinksNames, "DessertsPanel");
        }
        private JPanel DessertsPanel() {
            ArrayList <String> dessertNames = getDessertNames();
            return selectionpanelCreator("Select the dessert(s) you want", dessertNames, "LoginPanel");
        }
        private JPanel selectionpanelCreator(String title, ArrayList<String> tableNames, String nextPanel) {
            JPanel panel = new JPanel(new BorderLayout());

            panel.add(new JLabel(title), BorderLayout.NORTH);

            JPanel centerPanel = new JPanel();
            for (String tableName : tableNames) {
                centerPanel.add(new JCheckBox(tableName));
            }
            panel.add(centerPanel, BorderLayout.CENTER);

            JButton nextButton = new JButton("Next");
            panel.add(nextButton, BorderLayout.SOUTH);

            nextButton.addActionListener(e -> {
                for (Component comp : centerPanel.getComponents()) {
                    if (comp instanceof JCheckBox) {
                        JCheckBox checkBox = (JCheckBox) comp;
                        if (checkBox.isSelected()) {
                            order.add(checkBox.getText());
                        }
                    }
                }
                cardLayout.show(mainPanel, nextPanel);
            });

            return panel;
        }

        private ArrayList<String> getPizzaNames() {
            ArrayList<String> pizzaNames = new ArrayList<>();
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                 Statement stmt = conn.createStatement()) {

                String sql = "SELECT pizza_name FROM Pizzas";
                ResultSet rs = stmt.executeQuery(sql);

                while (rs.next()) {
                    pizzaNames.add(rs.getString("pizza_name"));
                }

                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return pizzaNames;
        }

        private ArrayList<String> getDrinksNames() {
            ArrayList<String> drinksNames = new ArrayList<>();
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                 Statement stmt = conn.createStatement()) {

                String sql = "SELECT drink_name FROM Drinks";
                ResultSet rs = stmt.executeQuery(sql);

                while (rs.next()) {
                    drinksNames.add(rs.getString("drink_name"));
                }

                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return drinksNames;
        }
        private ArrayList<String> getDessertNames() {
            ArrayList<String> dessertNames = new ArrayList<>();
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                 Statement stmt = conn.createStatement()) {

                String sql = "SELECT dessert_name FROM Desserts";
                ResultSet rs = stmt.executeQuery(sql);

                while (rs.next()) {
                    dessertNames.add(rs.getString("dessert_name"));
                }

                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return dessertNames;
        }

        public static void main(String[] args) {
            SwingUtilities.invokeLater(Main::new);
        }
    }