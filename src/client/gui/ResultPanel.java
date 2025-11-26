package client.gui;

import javax.swing.*;
import java.awt.*;

public class ResultPanel extends JPanel {
    private final JLabel titleLabel;
    private final JLabel scoreLabel;
    private final JLabel messageLabel;
    private final JButton continueButton;

    private boolean isGameOver = false;

    public ResultPanel(Runnable onContinue) {
        setLayout(new BorderLayout());
        setBackground(GuiConstants.BACKGROUND);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(GuiConstants.BACKGROUND);

        titleLabel = new JLabel("Resultat", SwingConstants.CENTER);
        titleLabel.setFont(GuiConstants.TITLE_FONT);
        titleLabel.setForeground(GuiConstants.PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        scoreLabel = new JLabel("0 - 0", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("SansSerif", Font.BOLD, 48));
        scoreLabel.setForeground(GuiConstants.TEXT_DARK);
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setFont(GuiConstants.TEXT_FONT);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        continueButton = new JButton("Fortsätt");
        continueButton.setFont(GuiConstants.BUTTON_FONT);
        continueButton.setBackground(GuiConstants.PRIMARY);
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
    }

    public void showRoundResult(int yourScore, int opponentScore) {
        isGameOver = false;
        titleLabel.setText("Rundan klar!");
        scoreLabel.setText(yourScore + " - " + opponentScore);

        if (yourScore > opponentScore) {
            messageLabel.setText("Du vann rundan! 🎉");
        } else if (yourScore < opponentScore) {
            messageLabel.setText("Motståndaren vann rundan");
        } else {
            messageLabel.setText("Oavgjort!");
        }

        continueButton.setText("Fortsätt");
    }

    public void showGameOver(int yourScore, int opponentScore) {
        isGameOver = true;
        titleLabel.setText("Spelet är slut!");
        scoreLabel.setText(yourScore + " - " + opponentScore);

        if (yourScore > opponentScore) {
            messageLabel.setText("Grattis, du vann!");
            scoreLabel.setForeground(GuiConstants.CORRECT);
        } else if (yourScore < opponentScore) {
            messageLabel.setText("Tyvärr, du förlorade");
            scoreLabel.setForeground(GuiConstants.WRONG);
        } else {
            messageLabel.setText("Oavgjort!");
            scoreLabel.setForeground(GuiConstants.SECONDARY);
        }

        continueButton.setText("Avsluta");
    }
}