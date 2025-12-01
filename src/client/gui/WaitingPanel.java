package client.gui;

import javax.swing.*;
import java.awt.*;

public class WaitingPanel extends JPanel implements GuiConstants.ThemeChangeListener {
    private final JLabel messageLabel;
    private final JLabel spinnerLabel;
    private final JPanel centerPanel;

    public WaitingPanel() {
        GuiConstants.addThemeChangeListener(this);

        setLayout(new BorderLayout());

        centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        spinnerLabel = new JLabel("⏳", SwingConstants.CENTER);
        spinnerLabel.setFont(new Font("SansSerif", Font.PLAIN, 48));
        spinnerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        messageLabel = new JLabel("Väntar...", SwingConstants.CENTER);
        messageLabel.setFont(GuiConstants.TITLE_FONT);
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

        updateColors();
    }

    @Override
    public void onThemeChanged() {
        updateColors();
        revalidate();
        repaint();
    }

    private void updateColors() {
        setBackground(GuiConstants.getBackground());
        centerPanel.setBackground(GuiConstants.getBackground());
        messageLabel.setForeground(GuiConstants.getTextDark());
    }

    public void setMessage(String message) {
        messageLabel.setText(message);
    }
}