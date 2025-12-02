package server;

import java.io.FileInputStream;
import java.util.Properties;

public class GameConfig {
    private int questionsPerRound = 4;  // Default
    private int totalRounds = 3;        // Default
    private int answerTimeout = 15;     // Default

    public GameConfig() {
        try {
            Properties props = new Properties();
            props.load(new FileInputStream("game.properties"));

            questionsPerRound = Integer.parseInt(props.getProperty("questions.per.round"));
            totalRounds = Integer.parseInt(props.getProperty("total.rounds"));
            answerTimeout = Integer.parseInt(props.getProperty("answer.timeout"));

            System.out.println("Config laddad: " + questionsPerRound + " frågor, " +
                    totalRounds + " ronder, " + answerTimeout + "s timeout");

        } catch (Exception e) {
            System.out.println("Använder defaults: 4 frågor, 3 ronder, 15s timeout");
        }
    }

    public int getQuestionsPerRound() {
        return questionsPerRound;
    }

    public int getTotalRounds() {
        return totalRounds;
    }

    public int getAnswerTimeout() {
        return answerTimeout;
    }
}