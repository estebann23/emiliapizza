package com.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.Timer;

import static com.example.DatabaseHelper.getCurrentOrderId;

public class OrderStatusPanel extends JPanel {
    private final PizzaDeliveryApp app;
    private JLabel countdownLabel;
    private JLabel cancelCountdownLabel; // Label to show the remaining time for cancellation
    private int orderId;
    private JLabel assignedDriverLabel;
    private JButton cancelOrderButton;
    private Timer countdownTimer;
    private Timer statusUpdateTimer;
    private int countdownSeconds;
    private int currentStatusIndex = 0;
    private final String[] statuses = {"Order Received", "Being prepared", "Out for delivery", "Delivered"};

    public OrderStatusPanel(PizzaDeliveryApp app, String assignedDriver) {
        this.app = app;
        this.assignedDriverLabel = new JLabel("Assigned Delivery Driver: " + assignedDriver);
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel centerPanel = new JPanel(new GridLayout(5, 1)); // Adjusted to 5 rows to accommodate the new label

        JPanel statusProgressPanel = new StatusProgressPanel();
        centerPanel.add(statusProgressPanel);

        countdownLabel = new JLabel("Estimated Delivery Time: ");
        centerPanel.add(countdownLabel);

        cancelCountdownLabel = new JLabel("You have 5:00 minutes to cancel the order."); // Initial message
        centerPanel.add(cancelCountdownLabel); // Add the countdown label for the cancel button

        centerPanel.add(assignedDriverLabel);

        cancelOrderButton = new JButton("Cancel Order");
        cancelOrderButton.setEnabled(true);
        cancelOrderButton.addActionListener(e -> cancelOrder());
        centerPanel.add(cancelOrderButton);

        add(centerPanel, BorderLayout.CENTER);

        startCountdown(15 * 60);
        startCancelTimer();
    }

    public int getOrderId() {
        return orderId;
    }

    private void startCountdown(int totalSeconds) {
        countdownSeconds = totalSeconds;
        countdownLabel.setText("Estimated Delivery Time: " + timeFormat(countdownSeconds));

        countdownTimer = new Timer(1000, e -> {
            countdownSeconds--;
            if (countdownSeconds <= 0) {
                countdownTimer.stop();
                countdownLabel.setText("Your order has arrived!");
            } else {
                countdownLabel.setText("Estimated Delivery Time: " + timeFormat(countdownSeconds));
            }
        });
        countdownTimer.start();
    }

    private void startCancelTimer() {
        AtomicInteger cancelTimeLimit = new AtomicInteger(5 * 60); // 5 minutes in seconds
        cancelCountdownLabel.setText("You have " + timeFormat(cancelTimeLimit.get()) + " minutes to cancel the order.");

        Timer cancelTimer = new Timer(1000, e -> {
            int remainingTime = cancelTimeLimit.decrementAndGet();
            cancelCountdownLabel.setText("You have " + timeFormat(remainingTime) + " minutes to cancel the order.");

            if (remainingTime <= 0) {
                cancelOrderButton.setEnabled(false);
                cancelCountdownLabel.setText("Cancellation time expired.");
                ((Timer) e.getSource()).stop();
            }
        });
        cancelTimer.start();
    }

    private void cancelOrder() {
        // Show a confirmation message
        JOptionPane.showMessageDialog(this, "Your order has been canceled.", "Order Canceled", JOptionPane.INFORMATION_MESSAGE);

        // Update the order status in the database
        boolean isUpdated = DatabaseHelper.updateOrderStatusToCanceled(getCurrentOrderId());
        if (isUpdated) {
            JOptionPane.showMessageDialog(this, "Order status updated to 'Canceled'.", "Update Successful", JOptionPane.INFORMATION_MESSAGE);
            // Reset all panels and clear the cart
            app.resetAllPanels();
            app.navigateTo(PanelNames.PIZZAS_PANEL);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update order status.", "Update Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String timeFormat(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private class StatusProgressPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            int circleRadius = 10;
            int numSteps = statuses.length;
            int spacing = (width - 2 * circleRadius) / (numSteps - 1);

            g2d.setColor(Color.LIGHT_GRAY);
            g2d.setStroke(new BasicStroke(3));
            g2d.drawLine(circleRadius, height / 2, width - circleRadius, height / 2);

            for (int i = 0; i < numSteps; i++) {
                int x = circleRadius + i * spacing;
                int y = height / 2;

                if (i <= currentStatusIndex) {
                    g2d.setColor(Color.BLUE);
                } else {
                    g2d.setColor(Color.LIGHT_GRAY);
                }

                g2d.fillOval(x - circleRadius, y - circleRadius, 2 * circleRadius, 2 * circleRadius);

                g2d.setColor(Color.BLACK);
                String statusLabel = statuses[i];
                FontMetrics fm = g2d.getFontMetrics();
                int labelWidth = fm.stringWidth(statusLabel);
                g2d.drawString(statusLabel, x - labelWidth / 2, y + 3 * circleRadius);
            }
        }
    }
}