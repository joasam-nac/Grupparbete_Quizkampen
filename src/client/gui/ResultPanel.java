package client.gui;

import javax.swing.*;
import java.awt.*;

public class ResultPanel extends JPanel implements GuiConstants.ThemeChangeListener {
    private final JLabel titleLabel;
    private final JLabel scoreLabel;
    private final JLabel messageLabel;
    private final JButton continueButton;
    private final JPanel centerPanel;

    private boolean isGameOver = false;
    private int currentYourScore = 0;
    private int currentOpponentScore = 0;

    public ResultPanel(Runnable onContinue) {
        GuiConstants.addThemeChangeListener(this);

        setLayout(new BorderLayout());

        centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        titleLabel = new JLabel("Resultat", SwingConstants.CENTER);
        titleLabel.setFont(GuiConstants.TITLE_FONT);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        scoreLabel = new JLabel("0 - 0", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("SansSerif", Font.BOLD, 48));
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setFont(GuiConstants.TEXT_FONT);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        continueButton = new JButton("Fortsätt");
        continueButton.setFont(GuiConstants.BUTTON_FONT);
        continueButton.setForeground(Color.WHITE);
        continueButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        continueButton.setMaximumSize(GuiConstants.BUTTON_SIZE);
        continueButton.addActionListener(_ -> {
            if (!isGameOver) {
                onContinue.run();
            } else {
                System.exit(0); // komma tillbaka till anslutningsskärmen
            }
        });

        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(titleLabel);
        centerPanel.add(Box.createVerticalStrut(30));
        centerPanel.add(scoreLabel);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(messageLabel);
        centerPanel.add(Box.createVerticalStrut(40));
        centerPanel.add(continueButton);
        centerPanel.add(Box.createVerticalGlue());

        add(centerPanel, BorderLayout.CENTER);
        updateColors();
    }

    @Override
    public void onThemeChanged() {
        updateColors();
        updateScoreColor();
        revalidate();
        repaint();
    }

    private void updateColors() {
        setBackground(GuiConstants.getBackground());
        centerPanel.setBackground(GuiConstants.getBackground());
        titleLabel.setForeground(GuiConstants.getPrimary());
        continueButton.setBackground(GuiConstants.getPrimary());

        if (!isGameOver) {
            scoreLabel.setForeground(GuiConstants.getTextDark());
        }
    }

    private void updateScoreColor() {
        if (isGameOver) {
            if (currentYourScore > currentOpponentScore) {
                scoreLabel.setForeground(GuiConstants.getCorrect());
            } else if (currentYourScore < currentOpponentScore) {
                scoreLabel.setForeground(GuiConstants.getWrong());
            } else {
                scoreLabel.setForeground(GuiConstants.getSecondary());
            }
        }
    }

    public void showRoundResult(int yourScore, int opponentScore, String opponentName) {
        isGameOver = false;
        currentYourScore = yourScore;
        currentOpponentScore = opponentScore;

        titleLabel.setText("Rundan klar!");
        scoreLabel.setText(yourScore + " - " + opponentScore);
        scoreLabel.setForeground(GuiConstants.getTextDark());

        if (yourScore > opponentScore) {
            messageLabel.setText("Du vann rundan! 🎉");
        } else if (yourScore < opponentScore) {
            messageLabel.setText(opponentName + " vann rundan");
        } else {
            messageLabel.setText("Oavgjort!");
        }

        continueButton.setText("Fortsätt");
    }

    public void showGameOver(int yourScore, int opponentScore, String opponentName) {
        isGameOver = true;
        currentYourScore = yourScore;
        currentOpponentScore = opponentScore;

        titleLabel.setText("Spelet är slut!");
        scoreLabel.setText(yourScore + " - " + opponentScore);

        if (yourScore > opponentScore) {
            messageLabel.setText("Grattis, du vann!");
            scoreLabel.setForeground(GuiConstants.getCorrect());
        } else if (yourScore < opponentScore) {
            messageLabel.setText("Tyvärr, " + opponentName + " vann.");
            scoreLabel.setForeground(GuiConstants.getWrong());
        } else {
            messageLabel.setText("Oavgjort!");
            scoreLabel.setForeground(GuiConstants.getSecondary());
        }

        continueButton.setText("Avsluta");
    }
}