package com.example;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

public class OrderStatusPanel extends JPanel {
    private final PizzaDeliveryApp app;
    private JLabel countdownLabel;
    private JLabel cancelCountdownLabel;
    private int countdownSeconds;
    private JLabel assignedDriverLabel;
    private JButton cancelOrderButton;
    private Timer countdownTimer;
    private Timer statusUpdateTimer;
    private int currentStatusIndex = 0;
    private final String[] statuses = {"Order Received", "Being prepared", "Out for delivery", "Delivered"};

    public OrderStatusPanel(PizzaDeliveryApp app, String assignedDriver, int countdownTime) {
        this.app = app;
        this.assignedDriverLabel = new JLabel("Assigned Delivery Driver: " + assignedDriver);
        this.countdownSeconds = countdownTime;
        initialize();
        startCountdown(countdownTime);
        startStatusUpdate();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel centerPanel = new JPanel(new GridLayout(5, 1));

        JPanel statusProgressPanel = new StatusProgressPanel();
        centerPanel.add(statusProgressPanel);

        countdownLabel = new JLabel("Estimated Delivery Time: ");
        centerPanel.add(countdownLabel);

        cancelCountdownLabel = new JLabel("You have 5:00 minutes to cancel the order.");
        centerPanel.add(cancelCountdownLabel);

        centerPanel.add(assignedDriverLabel);

        cancelOrderButton = new JButton("Cancel Order");
        cancelOrderButton.setEnabled(true);
        cancelOrderButton.addActionListener(e -> cancelOrder());
        centerPanel.add(cancelOrderButton);

        add(centerPanel, BorderLayout.CENTER);
        startCancelTimer();
    }

    private void startCountdown(int totalSeconds) {
        countdownSeconds = totalSeconds;
        updateCountdownLabel();

        countdownTimer = new Timer(1000, e -> {
            if (countdownSeconds > 0) {
                countdownSeconds--;
                updateCountdownLabel();
            } else {
                countdownTimer.stop();
                countdownLabel.setText("Your order has arrived!");
            }
        });
        countdownTimer.start();
    }

    private void updateCountdownLabel() {
        countdownLabel.setText("Estimated Delivery Time: " + timeFormat(countdownSeconds));
    }

    private void startCancelTimer() {
        AtomicInteger cancelTimeLimit = new AtomicInteger(5 * 60);
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

    private void startStatusUpdate() {
        statusUpdateTimer = new Timer(300000, e -> {
            if (currentStatusIndex < statuses.length - 1) {
                currentStatusIndex++;
                repaint();
            } else {
                statusUpdateTimer.stop();
            }
        });
        statusUpdateTimer.start();
    }

    private void cancelOrder() {
        boolean isUpdated = DatabaseHelper.updateOrderStatusToCanceled(DatabaseHelper.getCurrentOrderId());
        if (isUpdated) {
            int confirmation = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to cancel the order?",
                    "Confirm Cancellation", JOptionPane.YES_NO_OPTION);

            if (confirmation == JOptionPane.NO_OPTION) {
                return;
            }

            String driverName = assignedDriverLabel.getText().replace("Assigned Delivery Driver: ", "");
            DatabaseHelper.markDriverAvailable(driverName);
            JOptionPane.showMessageDialog(this, "Your order has been canceled.", "Order Canceled", JOptionPane.INFORMATION_MESSAGE);
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
            int circleRadius = 10;
            int spacing = (width - 2 * circleRadius) / (statuses.length - 1);
            int y = getHeight() / 2;

            g2d.setColor(Color.LIGHT_GRAY);
            g2d.setStroke(new BasicStroke(3));
            g2d.drawLine(circleRadius, y, width - circleRadius, y);

            for (int i = 0; i < statuses.length; i++) {
                int x = circleRadius + i * spacing;

                g2d.setColor(i <= currentStatusIndex ? Color.BLUE : Color.LIGHT_GRAY);
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