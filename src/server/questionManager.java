package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;

public class questionManager {
    private final Map<String, List<Question>> questions = new HashMap<>();
    private final int amountOfQuestions;

    public questionManager(int amountOfQuestions) {
        this.amountOfQuestions = amountOfQuestions;

        questions.put("geografi", Arrays.asList(
                new Question("Vilken av dessa städer är norr om Stockholm\n" +
                        "Malmö, Göteborg, Uppsala", "Uppsala"),
                new Question("Vad heter Turkiets huvudstad?", "Ankara"),
                new Question("Vilket av dessa länder gränsar Frankrike?\n" +
                        "Surinam, Portugal, Österrike, USA", "Surinam")
        ));

        questions.put("aritmetik", Arrays.asList(
                new Question("Vad är 1+2?", "3"),
                new Question("Vad är 3*4", "12"),
                new Question("Vad är 3! (fakultet)?", "6"),
                new Question("12-3*4", "0")
        ));
    }

    public String getQuestionThemes() {
        return String.join(", ", questions.keySet());
    }

    public int runQuestionGame(String question, BufferedReader in, BufferedWriter out) throws IOException {
        List<Question> list = questions.get(question.toLowerCase());
        if (list == null) {
            out.write("Inte ett tema!");
            out.newLine();
            out.flush();
            return 0;
        }

        int score = 0;
        int count = Math.min(amountOfQuestions, list.size());

        for (int i = 0; i < count; i++) {
            Question q = list.get(i);

            out.write("Fråga: " + q.getText());
            out.newLine();
            out.flush();

            String answer = in.readLine().trim().toLowerCase();

            if (answer.equals(q.getCorrectAnswer())) {
                score++;
                out.write("Rätt svar!\n");
            } else {
                out.write("Fel svar! Rätt svar är " + q.getCorrectAnswer());
            }
            out.newLine();
            out.flush();
        }
        return score;
    }


}
