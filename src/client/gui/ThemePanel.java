package client.gui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ThemePanel extends JPanel implements GuiConstants.ThemeChangeListener {
    private final JPanel buttonsPanel;
    private final JLabel title;
    private final List<JButton> themeButtons = new ArrayList<>();
    private final Consumer<String> onThemeSelected;

    public ThemePanel(Consumer<String> onThemeSelected) {
        this.onThemeSelected = onThemeSelected;
        GuiConstants.addThemeChangeListener(this);

        setLayout(new BorderLayout());

        title = new JLabel("Välj tema", SwingConstants.CENTER);
        title.setFont(GuiConstants.TITLE_FONT);
        title.setBorder(BorderFactory.createEmptyBorder(40, 0, 40, 0));
        add(title, BorderLayout.NORTH);

        // knappar
        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        add(buttonsPanel, BorderLayout.CENTER);
        updateColors();
    }

    @Override
    public void onThemeChanged() {
        updateColors();
        themeButtons.forEach(btn -> btn.setBackground(GuiConstants.getPrimary()));
        revalidate();
        repaint();
    }

    private void updateColors() {
        setBackground(GuiConstants.getBackground());
        buttonsPanel.setBackground(GuiConstants.getBackground());
        title.setForeground(GuiConstants.getPrimary());
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
        btn.setBackground(GuiConstants.getPrimary());
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