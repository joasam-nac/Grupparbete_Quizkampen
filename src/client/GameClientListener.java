package client;

import shared.Question;
import java.util.List;

public interface GameClientListener {
    void onConnected();
    void onWaitingForOpponent();
    void onOpponentFound();
    void onThemesReceived(List<String> themes);
    void onYourTurnToChoose();
    void onOpponentChoosing();
    void onThemeChosen(String theme);
    void onQuestionReceived(Question question, int questionNumber);
    void onAnswerResult(boolean correct, int yourScore);
    void onRoundComplete(int yourScore, int opponentScore);
    void onGameOver(int yourScore, int opponentScore);
    void onError(String message);
    void onDisconnected();
    void onOpponentNameReceived(String name);
    void onTimeout(int currentScore);
}