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
    private String opponentName = "Motståndaren";

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
        questionPanel = new QuestionPanel(this::onAnswerSelected, this::onGiveUpClicked);
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

    private void onConnectClicked(String host, int port) {
        String playerName = connectPanel.getPlayerName();

        if (playerName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Varning! Ange ett namn.");
            return;
        }

        connectPanel.setConnecting(true);
        client.connect(host, port, playerName);
    }

    private void onThemeSelected(String theme) {
        client.chooseTheme(theme);
        waitingPanel.setMessage("Laddar frågor...");
        showPanel(WAITING);
    }

    private void onAnswerSelected(int answerIndex) {
        questionPanel.stopTimer();
        questionPanel.setButtonsEnabled(false);
        client.sendAnswer(answerIndex);
    }

    private void onContinueClicked() {
        questionPanel.reset();
        waitingPanel.setMessage("Väntar...");
        showPanel(WAITING);
    }

    private void onGiveUpClicked() {
        questionPanel.stopTimer();
        questionPanel.setButtonsEnabled(false);
        client.giveUp();   // skickar GIVE_UP till servern
    }
    // för GameClientListener
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
        waitingPanel.setMessage("Motståndare hittad! Startar spel...");
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
        waitingPanel.setMessage(opponentName + " väljer tema...");
        showPanel(WAITING);
    }

    @Override
    public void onThemeChosen(String theme) {
        questionPanel.setTheme(theme);
    }

    @Override
    public void onQuestionReceived(Question question, int questionNumber, int questionsPerRound) {
        questionPanel.showQuestion(question, questionNumber, questionsPerRound);
        showPanel(QUESTION);
    }

    @Override
    public void onAnswerResult(boolean correct, int yourScore) {
        questionPanel.stopTimer();
        questionPanel.showResult(correct);
        // server skickar nästa svar eller nästa tema
    }

    @Override
    public void onTimeout(int currentScore) {
        questionPanel.stopTimer();
        questionPanel.showTimeoutResult();

        // visar timeout
        String originalTitle = getTitle();
        setTitle("⏱️ Tiden är ute!");

        Timer titleTimer = new Timer(2000, _ -> setTitle(originalTitle));
        titleTimer.setRepeats(false);
        titleTimer.start();

        // server skickar själv efter nästa fråga eller nytt tema
    }

    @Override
    public void onScoreUpdate(int yourScore, int opponentScore) {
        questionPanel.updateScores(yourScore, opponentScore);
    }

    @Override
    public void onRoundComplete(int yourScore, int opponentScore) {
        questionPanel.stopTimer();
        resultPanel.showRoundResult(yourScore, opponentScore, opponentName);
        showPanel(RESULT);
    }

    @Override
    public void onGameOver(int yourScore, int opponentScore) {
        questionPanel.stopTimer();
        resultPanel.showGameOver(yourScore, opponentScore, opponentName);
        showPanel(RESULT);
    }

    @Override
    public void onError(String message) {
        JOptionPane.showMessageDialog(this, message, "Fel", JOptionPane.ERROR_MESSAGE);
        connectPanel.setConnecting(false);
    }

    @Override
    public void onDisconnected() {
        questionPanel.stopTimer();
        JOptionPane.showMessageDialog(
                this,
                "Tappade anslutningen till servern",
                "Frånkopplad",
                JOptionPane.ERROR_MESSAGE
        );
        questionPanel.reset();
        showPanel(CONNECT);
    }

    @Override
    public void onOpponentNameReceived(String name) {
        this.opponentName = name;
        waitingPanel.setMessage("Spelar mot " + opponentName + "!");
    }

    static void main() {
        SwingUtilities.invokeLater(() -> new MainGui().setVisible(true));
    }
}