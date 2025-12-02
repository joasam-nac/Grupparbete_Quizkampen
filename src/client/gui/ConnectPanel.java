package client.gui;

import javax.swing.*;
import java.awt.*;
import java.util.function.BiConsumer;

public class ConnectPanel extends JPanel implements GuiConstants.ThemeChangeListener {
    private final JTextField hostField = new JTextField("localhost", 15);
    private final JTextField portField = new JTextField("5074", 5);
    private final JTextField nameField = new JTextField("Spelare", 15);
    private final JButton connectButton = new JButton("Anslut");
    private final JButton colorThemeButton = new JButton("🎨 Färgtema");
    private final JLabel statusLabel = new JLabel(" ");
    private final JLabel title;
    private final JPanel formPanel;

    public ConnectPanel(BiConsumer<String, Integer> onConnect) {
        GuiConstants.addThemeChangeListener(this);

        setLayout(new BorderLayout());

        // titel
        title = new JLabel("Quizkampen", SwingConstants.CENTER);
        title.setFont(GuiConstants.TITLE_FONT);
        title.setBorder(BorderFactory.createEmptyBorder(50, 0, 30, 0));
        add(title, BorderLayout.NORTH);

        // formulär
        formPanel = new JPanel(new GridBagLayout());
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
        connectButton.setForeground(Color.WHITE);
        connectButton.setPreferredSize(GuiConstants.BUTTON_SIZE);
        formPanel.add(connectButton, gbc);

        gbc.gridy = 4;
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        formPanel.add(statusLabel, gbc);

        add(formPanel, BorderLayout.CENTER);

        // färgtema-knapp
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        colorThemeButton.setFont(GuiConstants.BUTTON_FONT);
        colorThemeButton.setFocusPainted(false);
        colorThemeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        colorThemeButton.addActionListener(e -> showColorThemeDialog());
        bottomPanel.add(colorThemeButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // anslut-knapp
        connectButton.addActionListener(_ -> {
            try {
                int port = Integer.parseInt(portField.getText().trim());
                onConnect.accept(hostField.getText().trim(), port);
            } catch (NumberFormatException ex) {
                statusLabel.setText("Ogiltig port");
            }
        });

        updateColors();
    }

    private void showColorThemeDialog() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Välj färgtema", true);
        dialog.setLayout(new GridLayout(4, 1, 10, 10));
        dialog.setSize(300, 250);
        dialog.setLocationRelativeTo(this);

        JButton defaultBtn = createThemeOptionButton(
                "Standard",
                new Color(52, 73, 94),
                new Color(236, 240, 241),
                () -> {
                    GuiConstants.applyDefaultTheme();
                    dialog.dispose();
                }
        );

        JButton darkBtn = createThemeOptionButton(
                "Mörkt tema",
                new Color(45, 52, 54),
                new Color(32, 35, 41),
                () -> {
                    GuiConstants.applyDarkTheme();
                    dialog.dispose();
                }
        );

        JButton greenBtn = createThemeOptionButton(
                "Grönt tema",
                new Color(27, 94, 32),
                new Color(232, 245, 233),
                () -> {
                    GuiConstants.applyGreenTheme();
                    dialog.dispose();
                }
        );

        dialog.add(new JLabel("Välj färgtema:", SwingConstants.CENTER));
        dialog.add(defaultBtn);
        dialog.add(darkBtn);
        dialog.add(greenBtn);

        dialog.setVisible(true);
    }

    private JButton createThemeOptionButton(String name, Color primary, Color background, Runnable action) {
        JButton btn = new JButton();
        btn.setLayout(new BorderLayout(10, 0));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        JPanel colorPreview = new JPanel(new GridLayout(1, 2, 5, 0));
        colorPreview.setOpaque(false);

        JPanel primarySwatch = new JPanel();
        primarySwatch.setBackground(primary);
        primarySwatch.setPreferredSize(new Dimension(30, 30));
        primarySwatch.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        JPanel backgroundSwatch = new JPanel();
        backgroundSwatch.setBackground(background);
        backgroundSwatch.setPreferredSize(new Dimension(30, 30));
        backgroundSwatch.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        colorPreview.add(primarySwatch);
        colorPreview.add(backgroundSwatch);

        btn.add(nameLabel, BorderLayout.WEST);
        btn.add(colorPreview, BorderLayout.EAST);

        btn.addActionListener(e -> action.run());

        return btn;
    }

    @Override
    public void onThemeChanged() {
        updateColors();
        revalidate();
        repaint();
    }

    private void updateColors() {
        setBackground(GuiConstants.getBackground());
        formPanel.setBackground(GuiConstants.getBackground());
        title.setForeground(GuiConstants.getPrimary());
        connectButton.setBackground(GuiConstants.getPrimary());
        colorThemeButton.setBackground(GuiConstants.getBackground());
        colorThemeButton.setForeground(GuiConstants.getTextDark());
    }

    public String getPlayerName() {
        return nameField.getText().trim();
    }

    public void setConnecting(boolean connecting) {
        connectButton.setEnabled(!connecting);
        nameField.setEnabled(!connecting);
        hostField.setEnabled(!connecting);
        portField.setEnabled(!connecting);
        colorThemeButton.setEnabled(!connecting);
        statusLabel.setText(connecting ? "Ansluter..." : " ");
    }
}