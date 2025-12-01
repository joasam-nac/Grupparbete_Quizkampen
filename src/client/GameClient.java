package client;

import shared.serverProtocol;
import shared.Question;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class GameClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private GameClientListener listener;
    private volatile boolean running;

    private int currentQuestionNumber = 0;
    private int currentScore = 0;
    private boolean isFirstPlayer = false;

    public void setListener(GameClientListener listener) {
        this.listener = listener;
    }

    public void connect(String host, int port, String playerName) {
        new Thread(() -> {
            try {
                socket = new Socket(host, port);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                out.println(playerName);
                running = true;

                notifyOnEDT(() -> listener.onConnected());
                notifyOnEDT(() -> listener.onWaitingForOpponent());

                listenForMessages();
            } catch (IOException e) {
                notifyOnEDT(() -> listener.onError("Kunde inte ansluta: " + e.getMessage()));
            }
        }).start();
    }

    private void listenForMessages() {
        try {
            String line;
            while (running && (line = in.readLine()) != null) {
                processMessage(line);
            }
        } catch (IOException e) {
            if (running) {
                notifyOnEDT(() -> listener.onDisconnected());
            }
        }
    }

    private void processMessage(String msg) {
        System.out.println("Received: " + msg);

        if (msg.startsWith(serverProtocol.THEMES_LIST)) {
            String themesStr = msg.substring(serverProtocol.THEMES_LIST.length());
            List<String> themes = Arrays.asList(themesStr.split(","));
            notifyOnEDT(() -> listener.onThemesReceived(themes));

        } else if (msg.startsWith(serverProtocol.OPPONENT_NAME)) {
            String name = msg.substring(serverProtocol.OPPONENT_NAME.length());
            notifyOnEDT(() -> listener.onOpponentNameReceived(name));

        } else if (msg.equals("YOUR_TURN_CHOOSE")) {
            currentQuestionNumber = 0;
            currentScore = 0;

            // person som väljer tema först är spelare 1
            if (!isFirstPlayer && currentQuestionNumber == 0) {
                isFirstPlayer = true;
            }
            notifyOnEDT(() -> listener.onYourTurnToChoose());

        } else if (msg.equals("OPPONENT_CHOOSING")) {
            notifyOnEDT(() -> listener.onOpponentChoosing());

        } else if (msg.startsWith(serverProtocol.THEME_CHOSEN)) {
            String theme = msg.substring(serverProtocol.THEME_CHOSEN.length());
            notifyOnEDT(() -> listener.onThemeChosen(theme));

        } else if (msg.startsWith(serverProtocol.QUESTION)) {
            String questionData = msg.substring(serverProtocol.QUESTION.length());
            Question q = Question.fromProtocolString(questionData);
            currentQuestionNumber++;
            notifyOnEDT(() -> listener.onQuestionReceived(q, currentQuestionNumber));

        } else if (msg.startsWith(serverProtocol.RESULT)) {
            String result = msg.substring(serverProtocol.RESULT.length());
            boolean correct = result.equals("CORRECT");
            if (correct) currentScore++;
            notifyOnEDT(() -> listener.onAnswerResult(correct, currentScore));

        } else if (msg.startsWith(serverProtocol.ROUND_SCORE)) {
            int[] scores = parseScores(msg.substring(serverProtocol.ROUND_SCORE.length()));
            notifyOnEDT(() -> listener.onRoundComplete(scores[0], scores[1]));

        } else if (msg.startsWith(serverProtocol.GAME_OVER)) {
            int[] scores = parseScores(msg.substring(serverProtocol.GAME_OVER.length()));
            notifyOnEDT(() -> listener.onGameOver(scores[0], scores[1]));

        } else if (msg.equals("NOT_YOUR_TURN")) {
            notifyOnEDT(() -> listener.onError("Det är inte din tur!"));

        } else if (msg.equals("WAIT_FOR_OPPONENT")) {
            notifyOnEDT(() -> listener.onWaitingForOpponent());
        }
    }

    private int[] parseScores(String scoreStr) {
        String[] parts = scoreStr.split(":");
        int first = Integer.parseInt(parts[0]);
        int second = Integer.parseInt(parts[1]);
        // Return [yourScore, opponentScore]
        if (isFirstPlayer) {
            return new int[]{first, second};
        } else {
            return new int[]{second, first};
        }
    }

    public void chooseTheme(String theme) {
        send(serverProtocol.CHOOSE_THEME + theme);
    }

    public void sendAnswer(int answerIndex) {
        send(serverProtocol.ANSWER + answerIndex);
    }

    private void send(String msg) {
        if (out != null) {
            out.println(msg);
            System.out.println("Sent: " + msg);
        }
    }

    public void disconnect() {
        running = false;
        try {
            if (socket != null) socket.close();
        } catch (IOException ignored) {}
    }

    private void notifyOnEDT(Runnable action) {
        SwingUtilities.invokeLater(action);
    }
}