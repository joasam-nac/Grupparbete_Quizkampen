package client.gui;

import client.GameClient;
import client.GameClientListener;
import shared.Question;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainGui extends JFrame implements GameClientListener {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainPanel = new JPanel(cardLayout);

    private final GameClient client = new GameClient();

    private final ConnectPanel connectPanel;
    private final WaitingPanel waitingPanel;
    private final ThemePanel themePanel;
    private final QuestionPanel questionPanel;
    private final ResultPanel resultPanel;

    // namn för CardLayout
    private static final String CONNECT = "connect";
    private static final String WAITING = "waiting";
    private static final String THEME = "theme";
    private static final String QUESTION = "question";
    private static final String RESULT = "result";

    public MainGui() {
        super("Quizkampen");

        client.setListener(this);

        // skapar paneler
        connectPanel = new ConnectPanel(this::onConnectClicked);
        waitingPanel = new WaitingPanel();
        themePanel = new ThemePanel(this::onThemeSelected);
        questionPanel = new QuestionPanel(this::onAnswerSelected);
        resultPanel = new ResultPanel(this::onContinueClicked);

        // lägger till paneler
        mainPanel.add(connectPanel, CONNECT);
        mainPanel.add(waitingPanel, WAITING);
        mainPanel.add(themePanel, THEME);
        mainPanel.add(questionPanel, QUESTION);
        mainPanel.add(resultPanel, RESULT);

        add(mainPanel);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(GuiConstants.WINDOW_WIDTH, GuiConstants.WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);

        showPanel(CONNECT);
    }

    private void showPanel(String name) {
        cardLayout.show(mainPanel, name);
    }

    // Button callbacks
    private void onConnectClicked(String host, int port) {
        connectPanel.setConnecting(true);
        client.connect(host, port);
    }

    private void onThemeSelected(String theme) {
        client.chooseTheme(theme);
        waitingPanel.setMessage("Laddar frågor...");
        showPanel(WAITING);
    }

    private void onAnswerSelected(int answerIndex) {
        questionPanel.setButtonsEnabled(false);
        client.sendAnswer(answerIndex);
    }

    private void onContinueClicked() {
        // Will receive next state from server
        waitingPanel.setMessage("Väntar...");
        showPanel(WAITING);
    }

    // GameClientListener implementation
    @Override
    public void onConnected() {
        connectPanel.setConnecting(false);
    }

    @Override
    public void onWaitingForOpponent() {
        waitingPanel.setMessage("Väntar på motståndare...");
        showPanel(WAITING);
    }

    @Override
    public void onOpponentFound() {
        waitingPanel.setMessage("Motståndare hittad!");
    }

    @Override
    public void onThemesReceived(List<String> themes) {
        themePanel.setThemes(themes);
    }

    @Override
    public void onYourTurnToChoose() {
        themePanel.setEnabled(true);
        showPanel(THEME);
    }

    @Override
    public void onOpponentChoosing() {
        waitingPanel.setMessage("Motståndaren väljer tema...");
        showPanel(WAITING);
    }

    @Override
    public void onThemeChosen(String theme) {
        questionPanel.setTheme(theme);
    }

    @Override
    public void onQuestionReceived(Question question, int questionNumber) {
        questionPanel.showQuestion(question, questionNumber);
        showPanel(QUESTION);
    }

    @Override
    public void onAnswerResult(boolean correct, int yourScore) {
        questionPanel.showResult(correct);

        // Brief delay then auto-continue or wait for next question
        Timer timer = new Timer(1500, _ -> {
            // Server will send next question or round complete
        });
        timer.setRepeats(false);
        timer.start();
    }

    @Override
    public void onRoundComplete(int yourScore, int opponentScore) {
        resultPanel.showRoundResult(yourScore, opponentScore);
        showPanel(RESULT);
    }

    @Override
    public void onGameOver(int yourScore, int opponentScore) {
        resultPanel.showGameOver(yourScore, opponentScore);
        showPanel(RESULT);
    }

    @Override
    public void onError(String message) {
        JOptionPane.showMessageDialog(this, message, "Fel", JOptionPane.ERROR_MESSAGE);
        connectPanel.setConnecting(false);
    }

    @Override
    public void onDisconnected() {
        JOptionPane.showMessageDialog(this, "Tappade anslutningen", "Fel",
                JOptionPane.ERROR_MESSAGE);
        showPanel(CONNECT);
    }

    static void main() {
        SwingUtilities.invokeLater(() -> new MainGui().setVisible(true));
    }
}