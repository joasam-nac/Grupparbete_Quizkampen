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
    private final JLabel timerLabel;
    private final JProgressBar timerBar;
    private final List<JButton> answerButtons = new ArrayList<>();
    private final Consumer<Integer> onAnswerSelected;

    private Timer countdownTimer;
    private int timeRemaining;
    private static final int TOTAL_TIME = 15;

    private int selectedAnswer = -1;
    private Question currentQuestion;

    public QuestionPanel(Consumer<Integer> onAnswerSelected) {
        this.onAnswerSelected = onAnswerSelected;

        setLayout(new BorderLayout(10, 10));
        setBackground(GuiConstants.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Timer panel
        JPanel timerPanel = new JPanel(new BorderLayout(5, 5));
        timerPanel.setBackground(GuiConstants.BACKGROUND);
        timerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        timerLabel = new JLabel("Tid: 15s", SwingConstants.CENTER);
        timerLabel.setFont(GuiConstants.TITLE_FONT);
        timerLabel.setForeground(GuiConstants.PRIMARY);

        timerBar = new JProgressBar(0, TOTAL_TIME);
        timerBar.setValue(TOTAL_TIME);
        timerBar.setStringPainted(false);
        timerBar.setPreferredSize(new Dimension(0, 25));
        timerBar.setForeground(GuiConstants.PRIMARY);
        timerBar.setBackground(Color.LIGHT_GRAY);

        timerPanel.add(timerLabel, BorderLayout.NORTH);
        timerPanel.add(timerBar, BorderLayout.CENTER);

        // Tema och nummer på fråga
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

        // Kombinera timer och top panel
        JPanel headerPanel = new JPanel(new BorderLayout(0, 10));
        headerPanel.setBackground(GuiConstants.BACKGROUND);
        headerPanel.add(timerPanel, BorderLayout.NORTH);
        headerPanel.add(topPanel, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);

        // Text för fråga
        questionTextLabel = new JLabel("", SwingConstants.CENTER);
        questionTextLabel.setFont(GuiConstants.QUESTION_FONT);
        questionTextLabel.setForeground(GuiConstants.TEXT_DARK);
        questionTextLabel.setBorder(BorderFactory.createEmptyBorder(30, 10, 30, 10));
        add(questionTextLabel, BorderLayout.CENTER);

        // Svarsalternativ
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
            stopTimer();
            setButtonsEnabled(false);
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

        startTimer();
    }

    public void showResult(boolean correct) {
        stopTimer();

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

    public void showTimeoutResult() {
        stopTimer();

        // visar rätt svar i grönt, allt annat i grått
        for (int i = 0; i < 4; i++) {
            JButton btn = answerButtons.get(i);

            if (i == currentQuestion.correctIndex()) {
                btn.setBackground(GuiConstants.CORRECT);
            } else {
                btn.setBackground(new Color(255, 200, 200)); // Light red tint
            }
            btn.setEnabled(false);
        }
    }

    private void startTimer() {
        stopTimer();
        timeRemaining = TOTAL_TIME;
        updateTimerDisplay();

        countdownTimer = new Timer(1000, _ -> {
            timeRemaining--;
            updateTimerDisplay();

            if (timeRemaining <= 0) {
                stopTimer();
                // server hanterar timeout
            }
        });
        countdownTimer.start();
    }

    public void stopTimer() {
        if (countdownTimer != null) {
            countdownTimer.stop();
            countdownTimer = null;
        }
    }

    private void updateTimerDisplay() {
        timerLabel.setText("Tid: " + timeRemaining + "s");
        timerBar.setValue(timeRemaining);

        // Ändra färg baserat på tid kvar
        Color color;
        if (timeRemaining <= 5) {
            color = GuiConstants.WRONG; // Röd
        } else if (timeRemaining <= 10) {
            color = new Color(255, 152, 0); // Orange
        } else {
            color = GuiConstants.PRIMARY; // Grön
        }

        timerLabel.setForeground(color);
        timerBar.setForeground(color);
    }

    public void setButtonsEnabled(boolean enabled) {
        answerButtons.forEach(b -> b.setEnabled(enabled));
    }

    public void reset() {
        stopTimer();
        selectedAnswer = -1;
        currentQuestion = null;
        questionTextLabel.setText("");
        timerLabel.setText("Tid: 15s");
        timerBar.setValue(TOTAL_TIME);
        timerLabel.setForeground(GuiConstants.PRIMARY);
        timerBar.setForeground(GuiConstants.PRIMARY);

        answerButtons.forEach(btn -> {
            btn.setText("");
            btn.setBackground(Color.WHITE);
            btn.setEnabled(false);
        });
    }
}