package client.gui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import shared.Question;

public class QuestionPanel extends JPanel implements GuiConstants.ThemeChangeListener {
    private final JLabel themeLabel;
    private final JLabel questionNumberLabel;
    private final JLabel questionTextLabel;
    private final JLabel timerLabel;
    private final JProgressBar timerBar;
    private final JLabel scoreLabel;
    private final List<JButton> answerButtons = new ArrayList<>();
    private final Consumer<Integer> onAnswerSelected;

    private final JPanel timerPanel;
    private final JPanel scorePanel;
    private final JPanel topPanel;
    private final JPanel headerPanel;
    private final JPanel answersPanel;

    private Timer countdownTimer;
    private int timeRemaining;
    private static final int TOTAL_TIME = 15;

    private int selectedAnswer = -1;
    private Question currentQuestion;

    public QuestionPanel(Consumer<Integer> onAnswerSelected) {
        this.onAnswerSelected = onAnswerSelected;
        GuiConstants.addThemeChangeListener(this);

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        timerPanel = new JPanel(new BorderLayout(5, 5));
        timerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        scoreLabel = new JLabel("Du: 0 | Motståndare: 0", SwingConstants.CENTER);
        scoreLabel.setFont(GuiConstants.TEXT_FONT);

        timerLabel = new JLabel("Tid: 15s", SwingConstants.CENTER);
        timerLabel.setFont(GuiConstants.TITLE_FONT);

        timerBar = new JProgressBar(0, TOTAL_TIME);
        timerBar.setValue(TOTAL_TIME);
        timerBar.setStringPainted(false);
        timerBar.setPreferredSize(new Dimension(0, 25));
        timerBar.setBackground(Color.LIGHT_GRAY);

        timerPanel.add(timerLabel, BorderLayout.NORTH);
        timerPanel.add(timerBar, BorderLayout.CENTER);

        scorePanel = new JPanel();
        scorePanel.add(scoreLabel);
        timerPanel.add(scorePanel, BorderLayout.SOUTH);

        topPanel = new JPanel(new BorderLayout());

        themeLabel = new JLabel("Tema");
        themeLabel.setFont(GuiConstants.TEXT_FONT);

        questionNumberLabel = new JLabel("Fråga 1");
        questionNumberLabel.setFont(GuiConstants.TEXT_FONT);
        questionNumberLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        topPanel.add(themeLabel, BorderLayout.WEST);
        topPanel.add(questionNumberLabel, BorderLayout.EAST);

        headerPanel = new JPanel(new BorderLayout(0, 10));
        headerPanel.add(timerPanel, BorderLayout.NORTH);
        headerPanel.add(topPanel, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);

        questionTextLabel = new JLabel("", SwingConstants.CENTER);
        questionTextLabel.setFont(GuiConstants.QUESTION_FONT);
        questionTextLabel.setBorder(BorderFactory.createEmptyBorder(30, 10, 30, 10));
        add(questionTextLabel, BorderLayout.CENTER);

        answersPanel = new JPanel();
        answersPanel.setLayout(new GridLayout(4, 1, 10, 10));

        for (int i = 0; i < 4; i++) {
            JButton btn = createAnswerButton(i);
            answerButtons.add(btn);
            answersPanel.add(btn);
        }

        add(answersPanel, BorderLayout.SOUTH);
        updateColors();
    }

    @Override
    public void onThemeChanged() {
        updateColors();
        revalidate();
        repaint();
    }

    private void updateColors() {
        setBackground(GuiConstants.getBackground());
        timerPanel.setBackground(GuiConstants.getBackground());
        scorePanel.setBackground(GuiConstants.getBackground());
        topPanel.setBackground(GuiConstants.getBackground());
        headerPanel.setBackground(GuiConstants.getBackground());
        answersPanel.setBackground(GuiConstants.getBackground());

        scoreLabel.setForeground(GuiConstants.getTextDark());
        themeLabel.setForeground(GuiConstants.getPrimary());
        questionTextLabel.setForeground(GuiConstants.getTextDark());

        updateTimerDisplay();
    }

    private JButton createAnswerButton(int index) {
        JButton btn = new JButton();
        btn.setFont(GuiConstants.BUTTON_FONT);
        btn.setBackground(Color.WHITE);
        btn.setForeground(GuiConstants.getTextDark());
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

    public void showQuestion(Question question, int questionNumber, int totalQuestionsPerRound) {
        this.currentQuestion = question;
        this.selectedAnswer = -1;

        questionNumberLabel.setText("Fråga " + questionNumber + " av " + totalQuestionsPerRound);
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
                btn.setBackground(GuiConstants.getCorrect());
            } else if (i == selectedAnswer && !correct) {
                btn.setBackground(GuiConstants.getWrong());
            }
            btn.setEnabled(false);
        }
    }

    public void showTimeoutResult() {
        stopTimer();

        for (int i = 0; i < 4; i++) {
            JButton btn = answerButtons.get(i);

            if (i == currentQuestion.correctIndex()) {
                btn.setBackground(GuiConstants.getCorrect());
            } else {
                btn.setBackground(new Color(255, 200, 200));
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

        Color color;
        if (timeRemaining <= 5) {
            color = GuiConstants.getWrong();
        } else if (timeRemaining <= 10) {
            color = new Color(255, 152, 0);
        } else {
            color = GuiConstants.getPrimary();
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
        timerLabel.setForeground(GuiConstants.getPrimary());
        timerBar.setForeground(GuiConstants.getPrimary());

        answerButtons.forEach(btn -> {
            btn.setText("");
            btn.setBackground(Color.WHITE);
            btn.setEnabled(false);
        });
    }

    public void updateScores(int yourScore, int opponentScore) {
        scoreLabel.setText("Du: " + yourScore + " | Motståndare: " + opponentScore);
    }
}