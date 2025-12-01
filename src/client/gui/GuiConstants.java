package client.gui;

import java.awt.*;

public class GuiConstants {
    // färger
    public static final Color PRIMARY = new Color(52, 73, 94);
    public static final Color SECONDARY = new Color(241, 196, 15);
    public static final Color CORRECT = new Color(46, 204, 113);
    public static final Color WRONG = new Color(231, 76, 60);
    public static final Color BACKGROUND = new Color(236, 240, 241);
    public static final Color TEXT_DARK = new Color(44, 62, 80);

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

    /*
    Att göra:
    Implementera getters och setters för att kunna ändra färgtema
     */
}