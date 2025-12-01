package server;

import shared.Question;
import java.util.List;

public class SessionState {
    public int getScoreFor(boolean isFirst) {
        return isFirst ? firstPlayerRoundScore : secondPlayerRoundScore;
    }

    public enum Phase {
        FIRST_CHOOSING_THEME,
        SECOND_CHOOSING_THEME,
        BOTH_ANSWERING,
        GAME_FINISHED
    }

    // Läs från config
    private final int questionsPerRound;
    private final int totalRounds;

    private Phase phase = Phase.FIRST_CHOOSING_THEME;
    private String currentTheme;
    private List<Question> currentQuestions;
    private int roundsPlayed;

    private int firstPlayerQuestionIndex;
    private int secondPlayerQuestionIndex;
    private boolean firstPlayerFinishedRound;
    private boolean secondPlayerFinishedRound;

    private int firstPlayerScore;
    private int secondPlayerScore;
    private int firstPlayerRoundScore;
    private int secondPlayerRoundScore;

    // Konstruktor som tar emot config
    public SessionState(GameConfig config) {
        this.questionsPerRound = config.getQuestionsPerRound();
        this.totalRounds = config.getTotalRounds();
    }

    // Getters
    public Phase getPhase() { return phase; }
    public String getCurrentTheme() { return currentTheme; }
    public int getRoundsPlayed() { return roundsPlayed; }
    public int getFirstPlayerScore() { return firstPlayerScore; }
    public int getSecondPlayerScore() { return secondPlayerScore; }
    public int getQuestionsPerRound() { return questionsPerRound; }
    public int getTotalRounds() { return totalRounds; }

    public Question getCurrentQuestionFor(boolean isFirstPlayer) {
        int index = isFirstPlayer ? firstPlayerQuestionIndex : secondPlayerQuestionIndex;
        if (currentQuestions == null || index >= currentQuestions.size()) {
            return null;
        }
        return currentQuestions.get(index);
    }

    public int getQuestionNumberFor(boolean isFirstPlayer) {
        return (isFirstPlayer ? firstPlayerQuestionIndex : secondPlayerQuestionIndex) + 1;
    }

    public boolean hasMoreQuestionsFor(boolean isFirstPlayer) {
        int index = isFirstPlayer ? firstPlayerQuestionIndex : secondPlayerQuestionIndex;
        return index < currentQuestions.size();
    }

    public boolean hasPlayerFinished(boolean isFirstPlayer) {
        return isFirstPlayer ? firstPlayerFinishedRound : secondPlayerFinishedRound;
    }

    public boolean bothPlayersFinished() {
        return firstPlayerFinishedRound && secondPlayerFinishedRound;
    }

    public void startRound(String theme, List<Question> questions) {
        this.currentTheme = theme;
        this.currentQuestions = questions;
        this.firstPlayerQuestionIndex = 0;
        this.secondPlayerQuestionIndex = 0;
        this.firstPlayerFinishedRound = false;
        this.secondPlayerFinishedRound = false;
        this.firstPlayerRoundScore = 0;
        this.secondPlayerRoundScore = 0;
    }

    public void recordAnswer(boolean isFirstPlayer, boolean correct) {
        if (correct) {
            if (isFirstPlayer) {
                firstPlayerRoundScore++;
                firstPlayerScore++;
            } else {
                secondPlayerRoundScore++;
                secondPlayerScore++;
            }
        }
    }

    public void nextQuestionFor(boolean isFirstPlayer) {
        if (isFirstPlayer) {
            firstPlayerQuestionIndex++;
            if (firstPlayerQuestionIndex >= currentQuestions.size()) {
                firstPlayerFinishedRound = true;
            }
        } else {
            secondPlayerQuestionIndex++;
            if (secondPlayerQuestionIndex >= currentQuestions.size()) {
                secondPlayerFinishedRound = true;
            }
        }
    }

    public void setPhase(Phase phase) {
        this.phase = phase;
    }

    public void completeRound() {
        roundsPlayed++;
    }

    public boolean isGameFinished() {
        return roundsPlayed >= totalRounds;
    }

    public String getRoundScoreString() {
        return firstPlayerRoundScore + ":" + secondPlayerRoundScore;
    }

    public String getTotalScoreString() {
        return firstPlayerScore + ":" + secondPlayerScore;
    }

    public int getFirstPlayerRoundScore() { return firstPlayerRoundScore; }
    public int getSecondPlayerRoundScore() { return secondPlayerRoundScore; }
}