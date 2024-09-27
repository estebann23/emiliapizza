package com.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import org.mindrot.jbcrypt.BCrypt;

public class PizzaDeliveryApp {
    static final String DB_URL = "jdbc:mysql://localhost:3306/PIZZARE"; // Database URL
    static final String USER = "root"; // MySQL user
    static final String PASS = "02072005"; // MySQL password

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
        frame.setSize(500, 400); // Larger size for better view

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        order = new ArrayList<>();

        // Add different panels
        mainPanel.add(createLoginPanel(), "LoginPanel");
        mainPanel.add(createPizzasPanel(), "PizzasPanel");
        mainPanel.add(createDrinksPanel(), "DrinksPanel");
        mainPanel.add(createDessertsPanel(), "DessertsPanel");
        mainPanel.add(createAccountCreationPanel(), "CreateAccountPanel");

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel header = createHeaderLabel("Welcome to Emilia Pizza!");
        panel.add(header, BorderLayout.NORTH);

        JPanel loginPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        loginPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        loginPanel.add(new JLabel("Username:"));
        loginPanel.add(usernameField);
        loginPanel.add(new JLabel("Password:"));
        loginPanel.add(passwordField);

        panel.add(loginPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton loginButton = createButton("Log In", Color.GREEN);
        JButton createAccountButton = createButton("Create Account", Color.RED);

        buttonPanel.add(loginButton);
        buttonPanel.add(createAccountButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Action listeners
        loginButton.addActionListener(e -> handleLogin(usernameField, passwordField));
        createAccountButton.addActionListener(e -> cardLayout.show(mainPanel, "CreateAccountPanel"));

        return panel;
    }

    private JPanel createAccountCreationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel header = createHeaderLabel("Create New Account");
        panel.add(header, BorderLayout.NORTH);

        JPanel createAccountPanel = new JPanel(new GridLayout(9, 2, 10, 10));
        createAccountPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        // Create input fields for account creation
        JTextField nameField = new JTextField();
        JTextField genderField = new JTextField();
        JTextField birthdateField = new JTextField("YYYY-MM-DD");
        JTextField emailAddressField = new JTextField();
        JTextField phoneNumberField = new JTextField();
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        // Add labels and text fields
        createAccountPanel.add(new JLabel("Name:"));
        createAccountPanel.add(nameField);
        createAccountPanel.add(new JLabel("Gender:"));
        createAccountPanel.add(genderField);
        createAccountPanel.add(new JLabel("Birthdate:"));
        createAccountPanel.add(birthdateField);
        createAccountPanel.add(new JLabel("Email Address:"));
        createAccountPanel.add(emailAddressField);
        createAccountPanel.add(new JLabel("Phone Number:"));
        createAccountPanel.add(phoneNumberField);
        createAccountPanel.add(new JLabel("Username:"));
        createAccountPanel.add(usernameField);
        createAccountPanel.add(new JLabel("Password:"));
        createAccountPanel.add(passwordField);

        panel.add(createAccountPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton createButton = createButton("Create Account", Color.GREEN);
        JButton backButton = createButton("Back to Login", Color.RED);

        buttonPanel.add(createButton);
        buttonPanel.add(backButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Action listeners
        createButton.addActionListener(e -> handleCreateAccount(nameField, genderField, birthdateField, emailAddressField,phoneNumberField, usernameField, passwordField));
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "LoginPanel"));

        return panel;
    }

    private JPanel createPizzasPanel() {
        ArrayList<String> pizzaNames = getPizzaNames();
        return createSelectionPanel("Select the pizza(s) you want", pizzaNames, "DrinksPanel");
    }

    private JPanel createDrinksPanel() {
        ArrayList<String> drinksNames = getDrinksNames();
        return createSelectionPanel("Select the drink(s) you want", drinksNames, "DessertsPanel");
    }

    private JPanel createDessertsPanel() {
        ArrayList<String> dessertNames = getDessertNames();
        return createSelectionPanel("Select the dessert(s) you want", dessertNames, "LoginPanel");
    }

    private JPanel createSelectionPanel(String title, ArrayList<String> items, String nextPanel) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel header = createHeaderLabel(title);
        panel.add(header, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(0, 1));
        centerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        for (String item : items) {
            JCheckBox checkBox = new JCheckBox(item);
            centerPanel.add(checkBox);
        }

        panel.add(centerPanel, BorderLayout.CENTER);

        JButton nextButton = createButton("Next", Color.GREEN);
        panel.add(nextButton, BorderLayout.SOUTH);

        nextButton.addActionListener(e -> {
            for (Component comp : centerPanel.getComponents()) {
                if (comp instanceof JCheckBox && ((JCheckBox) comp).isSelected()) {
                    order.add(((JCheckBox) comp).getText());
                }
            }
            cardLayout.show(mainPanel, nextPanel);
        });

        return panel;
    }

    private void handleLogin(JTextField usernameField, JPasswordField passwordField) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (isValidLogin(username, password)) {
            cardLayout.show(mainPanel, "PizzasPanel");
        } else {
            JOptionPane.showMessageDialog(frame, "Invalid username or password", "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleCreateAccount(JTextField nameField, JTextField genderField, JTextField birthdateField, JTextField emailAddressField, JTextField phoneNumberField,
                                     JTextField usernameField, JPasswordField passwordField) {
        String name = nameField.getText();
        String gender = genderField.getText();
        String birthdate = birthdateField.getText();
        String emailAddress = emailAddressField.getText();
        String phoneNumber = phoneNumberField.getText();
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (validateFields(name, gender, birthdate, emailAddress, phoneNumber, username, password)) {
            if (createNewAccount(name, gender, birthdate, emailAddress, phoneNumber, username, password)) {
                JOptionPane.showMessageDialog(frame, "Account created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                cardLayout.show(mainPanel, "LoginPanel");
            } else {
                JOptionPane.showMessageDialog(frame, "Error creating account", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validateFields(String... fields) {
        for (String field : fields) {
            if (field == null || field.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    // Method to insert new account into the Customer table in the database
    private boolean createNewAccount(String name, String gender, String birthdate,
                                     String emailAddress, String phoneNumber,
                                     String username, String password) {
        String insertSQL = "INSERT INTO Customers (Name, Gender, Birthdate, Email_Address, Phone_Number, Username, Password) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            pstmt.setString(1, name);
            pstmt.setString(2, gender);
            pstmt.setString(3, birthdate);
            pstmt.setString(4, emailAddress);
            pstmt.setString(5, phoneNumber);
            pstmt.setString(6, username);
            pstmt.setString(7, hashedPassword);  // Store the hashed password

            pstmt.executeUpdate();
            return true;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // Mock authentication for login
    private boolean isValidLogin(String username, String password) {
        String query = "SELECT Password FROM Customers WHERE Username = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedHashedPassword = rs.getString("Password");

                // Check if the entered password matches the stored hashed password
                if (BCrypt.checkpw(password, storedHashedPassword)) {
                    return true;
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    private JLabel createHeaderLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Serif", Font.BOLD, 24));
        label.setForeground(new Color(184, 54, 54));
        label.setHorizontalAlignment(JLabel.CENTER);
        return label;
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        return button;
    }

    private ArrayList<String> getPizzaNames() {
        ArrayList<String> pizzaNames = new ArrayList<>();
        pizzaNames.add("Margherita");
        pizzaNames.add("Pepperoni");
        pizzaNames.add("Hawaiian");
        return pizzaNames;
    }

    private ArrayList<String> getDrinksNames() {
        ArrayList<String> drinksNames = new ArrayList<>();
        drinksNames.add("Coca Cola");
        drinksNames.add("Sprite");
        drinksNames.add("Water");
        return drinksNames;
    }

    private ArrayList<String> getDessertNames() {
        ArrayList<String> dessertNames = new ArrayList<>();
        dessertNames.add("Chocolate Cake");
        dessertNames.add("Ice Cream");
        dessertNames.add("Apple Pie");
        return dessertNames;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PizzaDeliveryApp::new);
    }
}
