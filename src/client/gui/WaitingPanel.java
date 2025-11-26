package client.gui;

import javax.swing.*;
import java.awt.*;

public class WaitingPanel extends JPanel {
    private final JLabel messageLabel;
    private final JLabel spinnerLabel;

    public WaitingPanel() {
        setLayout(new BorderLayout());
        setBackground(GuiConstants.BACKGROUND);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(GuiConstants.BACKGROUND);

        spinnerLabel = new JLabel("⏳", SwingConstants.CENTER);
        spinnerLabel.setFont(new Font("SansSerif", Font.PLAIN, 48));
        spinnerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        messageLabel = new JLabel("Väntar...", SwingConstants.CENTER);
        messageLabel.setFont(GuiConstants.TITLE_FONT);
        messageLabel.setForeground(GuiConstants.TEXT_DARK);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(spinnerLabel);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(messageLabel);
        centerPanel.add(Box.createVerticalGlue());

        add(centerPanel, BorderLayout.CENTER);

        // häftig animation
        Timer timer = new Timer(500, _ -> {
            String current = spinnerLabel.getText();
            spinnerLabel.setText(current.equals("⏳") ? "⌛" : "⏳");
        });
        timer.start();
    }

    public void setMessage(String message) {
        messageLabel.setText(message);
    }
}