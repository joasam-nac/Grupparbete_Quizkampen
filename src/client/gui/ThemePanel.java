package client.gui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ThemePanel extends JPanel {
    private final JPanel buttonsPanel;
    private final List<JButton> themeButtons = new ArrayList<>();
    private final Consumer<String> onThemeSelected;

    public ThemePanel(Consumer<String> onThemeSelected) {
        this.onThemeSelected = onThemeSelected;

        setLayout(new BorderLayout());
        setBackground(GuiConstants.BACKGROUND);

        JLabel title = new JLabel("Välj tema", SwingConstants.CENTER);
        title.setFont(GuiConstants.TITLE_FONT);
        title.setForeground(GuiConstants.PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(40, 0, 40, 0));
        add(title, BorderLayout.NORTH);

        // knappar
        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setBackground(GuiConstants.BACKGROUND);
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        add(buttonsPanel, BorderLayout.CENTER);
    }

    public void setThemes(List<String> themes) {
        buttonsPanel.removeAll();
        themeButtons.clear();

        for (String theme : themes) {
            JButton btn = createThemeButton(theme);
            themeButtons.add(btn);
            buttonsPanel.add(btn);
            buttonsPanel.add(Box.createVerticalStrut(15));
        }

        buttonsPanel.revalidate();
        buttonsPanel.repaint();
    }

    private JButton createThemeButton(String theme) {
        JButton btn = new JButton(theme);
        btn.setFont(GuiConstants.BUTTON_FONT);
        btn.setBackground(GuiConstants.PRIMARY);
        btn.setForeground(Color.WHITE);
        btn.setMaximumSize(GuiConstants.BUTTON_SIZE);
        btn.setPreferredSize(GuiConstants.BUTTON_SIZE);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFocusPainted(false);

        btn.addActionListener(_ -> onThemeSelected.accept(theme));

        return btn;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        themeButtons.forEach(b -> b.setEnabled(enabled));
    }
}