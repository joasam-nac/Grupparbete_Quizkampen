package client.gui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import shared.Question;

public class QuestionPanel extends JPanel {
    private final JLabel themeLabel;
    private final JLabel questionNumberLabel;
    private final JLabel questionTextLabel;
    private final List<JButton> answerButtons = new ArrayList<>();
    private final Consumer<Integer> onAnswerSelected;

    private int selectedAnswer = -1;
    private Question currentQuestion;

    public QuestionPanel(Consumer<Integer> onAnswerSelected) {
        this.onAnswerSelected = onAnswerSelected;

        setLayout(new BorderLayout(10, 10));
        setBackground(GuiConstants.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // tema och nummer på fråga
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(GuiConstants.BACKGROUND);

        themeLabel = new JLabel("Tema");
        themeLabel.setFont(GuiConstants.TEXT_FONT);
        themeLabel.setForeground(GuiConstants.PRIMARY);

        questionNumberLabel = new JLabel("Fråga 1/4");
        questionNumberLabel.setFont(GuiConstants.TEXT_FONT);
        questionNumberLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        topPanel.add(themeLabel, BorderLayout.WEST);
        topPanel.add(questionNumberLabel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // text för fråga
        questionTextLabel = new JLabel("", SwingConstants.CENTER);
        questionTextLabel.setFont(GuiConstants.QUESTION_FONT);
        questionTextLabel.setForeground(GuiConstants.TEXT_DARK);
        questionTextLabel.setBorder(BorderFactory.createEmptyBorder(30, 10, 30, 10));
        add(questionTextLabel, BorderLayout.CENTER);

        // svarsalternativ
        JPanel answersPanel = new JPanel();
        answersPanel.setLayout(new GridLayout(4, 1, 10, 10));
        answersPanel.setBackground(GuiConstants.BACKGROUND);

        for (int i = 0; i < 4; i++) {
            JButton btn = createAnswerButton(i);
            answerButtons.add(btn);
            answersPanel.add(btn);
        }

        add(answersPanel, BorderLayout.SOUTH);
    }

    private JButton createAnswerButton(int index) {
        JButton btn = new JButton();
        btn.setFont(GuiConstants.BUTTON_FONT);
        btn.setBackground(Color.WHITE);
        btn.setForeground(GuiConstants.TEXT_DARK);
        btn.setPreferredSize(new Dimension(300, 45));
        btn.setFocusPainted(false);

        btn.addActionListener(_ -> {
            selectedAnswer = index;
            onAnswerSelected.accept(index);
        });

        return btn;
    }

    public void setTheme(String theme) {
        themeLabel.setText("Tema: " + theme);
    }

    public void showQuestion(Question question, int questionNumber) {
        this.currentQuestion = question;
        this.selectedAnswer = -1;

        questionNumberLabel.setText("Fråga " + questionNumber + "/4");
        questionTextLabel.setText("<html><center>" + question.text() + "</center></html>");

        List<String> alts = question.alternatives();
        for (int i = 0; i < 4; i++) {
            JButton btn = answerButtons.get(i);
            btn.setText(alts.get(i));
            btn.setBackground(Color.WHITE);
            btn.setEnabled(true);
        }
    }

    public void showResult(boolean correct) {
        for (int i = 0; i < 4; i++) {
            JButton btn = answerButtons.get(i);

            if (i == currentQuestion.correctIndex()) {
                btn.setBackground(GuiConstants.CORRECT);
            } else if (i == selectedAnswer && !correct) {
                btn.setBackground(GuiConstants.WRONG);
            }
            btn.setEnabled(false);
        }
    }

    public void setButtonsEnabled(boolean enabled) {
        answerButtons.forEach(b -> b.setEnabled(enabled));
    }
}