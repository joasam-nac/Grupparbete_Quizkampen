package client.gui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GuiConstants {
    // färger (remove final to allow changes)
    private static Color PRIMARY = new Color(52, 73, 94);
    private static Color SECONDARY = new Color(241, 196, 15);
    private static Color CORRECT = new Color(46, 204, 113);
    private static Color WRONG = new Color(231, 76, 60);
    private static Color BACKGROUND = new Color(236, 240, 241);
    private static Color TEXT_DARK = new Color(44, 62, 80);

    // typsnitt
    public static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 24);
    public static final Font BUTTON_FONT = new Font("SansSerif", Font.BOLD, 16);
    public static final Font TEXT_FONT = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font QUESTION_FONT = new Font("SansSerif", Font.BOLD, 18);
    public static final Font TIMER_FONT = new Font("Arial", Font.BOLD, 20);

    // storlek
    public static final int WINDOW_WIDTH = 400;
    public static final int WINDOW_HEIGHT = 600;
    public static final Dimension BUTTON_SIZE = new Dimension(300, 50);

    // listener för temaändringar
    private static final List<ThemeChangeListener> listeners = new ArrayList<>();

    // getters
    public static Color getPrimary() { return PRIMARY; }
    public static Color getSecondary() { return SECONDARY; }
    public static Color getCorrect() { return CORRECT; }
    public static Color getWrong() { return WRONG; }
    public static Color getBackground() { return BACKGROUND; }
    public static Color getTextDark() { return TEXT_DARK; }

    // sätt nytt tema
    public static void setTheme(Color primary, Color secondary, Color background, Color textDark) {
        PRIMARY = primary;
        SECONDARY = secondary;
        BACKGROUND = background;
        TEXT_DARK = textDark;
        notifyListeners();
    }

    // fördefinierade teman
    public static void applyDefaultTheme() {
        setTheme(
                new Color(52, 73, 94),
                new Color(241, 196, 15),
                new Color(236, 240, 241),
                new Color(44, 62, 80)
        );
    }

    public static void applyDarkTheme() {
        setTheme(
                new Color(45, 52, 54),
                new Color(99, 205, 218),
                new Color(32, 35, 41),
                new Color(220, 221, 225)
        );
    }

    public static void applyGreenTheme() {
        setTheme(
                new Color(27, 94, 32),
                new Color(255, 193, 7),
                new Color(232, 245, 233),
                new Color(33, 33, 33)
        );
    }

    // listener-hantering
    public static void addThemeChangeListener(ThemeChangeListener listener) {
        listeners.add(listener);
    }

    private static void notifyListeners() {
        listeners.forEach(ThemeChangeListener::onThemeChanged);
    }

    public interface ThemeChangeListener {
        void onThemeChanged();
    }
}