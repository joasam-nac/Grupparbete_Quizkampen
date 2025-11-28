package client.gui;

import javax.swing.*;
import java.awt.*;
import java.util.function.BiConsumer;

public class ConnectPanel extends JPanel {
    private final JTextField hostField = new JTextField("localhost", 15);
    private final JTextField portField = new JTextField("5000", 5);
    private final JTextField nameField = new JTextField("Spelare", 15);
    private final JButton connectButton = new JButton("Anslut");
    private final JLabel statusLabel = new JLabel(" ");

    public ConnectPanel(BiConsumer<String, Integer> onConnect) {
        setLayout(new BorderLayout());
        setBackground(GuiConstants.BACKGROUND);

        // titel
        JLabel title = new JLabel("Quizkampen", SwingConstants.CENTER);
        title.setFont(GuiConstants.TITLE_FONT);
        title.setForeground(GuiConstants.PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 0));
        add(title, BorderLayout.NORTH);

        // formulär
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(GuiConstants.BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Namn:"), gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Server:"), gbc);
        gbc.gridx = 1;
        formPanel.add(hostField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Port:"), gbc);
        gbc.gridx = 1;
        formPanel.add(portField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        connectButton.setFont(GuiConstants.BUTTON_FONT);
        connectButton.setBackground(GuiConstants.PRIMARY);
        connectButton.setForeground(Color.WHITE);
        connectButton.setPreferredSize(GuiConstants.BUTTON_SIZE);
        formPanel.add(connectButton, gbc);

        gbc.gridy = 4;
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        formPanel.add(statusLabel, gbc);

        add(formPanel, BorderLayout.CENTER);

        // knapp
        connectButton.addActionListener(_ -> {
            try {
                int port = Integer.parseInt(portField.getText().trim());
                onConnect.accept(hostField.getText().trim(), port);
            } catch (NumberFormatException ex) {
                statusLabel.setText("Ogiltig port");
            }
        });
    }

    public String getPlayerName() {
        return nameField.getText().trim();
    }

    public void setConnecting(boolean connecting) {
        connectButton.setEnabled(!connecting);
        nameField.setEnabled(!connecting);
        statusLabel.setText(connecting ? "Ansluter..." : " ");
    }
}