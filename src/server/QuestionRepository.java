package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import shared.Question;

public class QuestionRepository {
    private final Map<String, List<Question>> themeQuestions = new HashMap<>();

    public QuestionRepository(String filePath) {
        loadFromFile(filePath);
    }

    private void loadFromFile(String filePath) {
        try (BufferedReader reader = Files.newBufferedReader(Path.of(filePath))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();

                if (line.isEmpty()) {
                    continue;
                }

                Question question = parseQuestionLine(line, lineNumber);
                if (question != null) {
                    String theme = parseTheme(line);
                    themeQuestions.computeIfAbsent(theme, _ -> new ArrayList<>()).add(question);
                }
            }

            System.out.println("Laddade in " + themeQuestions.size() + " st teman från " + filePath);
            themeQuestions.forEach((name, questions) ->
                    System.out.println("  - " + name + ": " + questions.size() + " frågor"));

        } catch (IOException e) {
            System.err.println("Kunde inte ladda in frågor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Question parseQuestionLine(String line, int lineNumber) {
        try {
            String[] parts = line.split(";");
            if (parts.length != 7) {
                System.err.println("Rad " + lineNumber + ": fel formatering i textfilen: " + line);
                return null;
            }

            String text = parts[0];
            List<String> alternatives = List.of(parts[1], parts[2], parts[3], parts[4]);
            String correctAnswer = parts[5];

            // hittar svaret på raden
            int correctIndex = alternatives.indexOf(correctAnswer);
            if (correctIndex == -1) {
                System.err.println("Rad " + lineNumber + ": hittade inget svar: " + correctAnswer);
                return null;
            }

            return new Question(text, alternatives, correctIndex);
        } catch (Exception e) {
            System.err.println("Rad " + lineNumber + ": fel: " + e.getMessage());
            return null;
        }
    }

    private String parseTheme(String line) {
        String[] parts = line.split(";");
        return parts[6].trim().toUpperCase();
    }

    public Set<String> getThemeNames() {
        return themeQuestions.keySet();
    }

    public List<Question> getQuestions(String themeName, int count) {
        List<Question> questions = themeQuestions.get(themeName.toUpperCase());
        if (questions == null) {
            return List.of();
        }

        if (questions.size() <= count) {
            return new ArrayList<>(questions);
        }

        // blandar
        List<Question> shuffled = new ArrayList<>(questions);
        Collections.shuffle(shuffled);
        return shuffled.subList(0, count);
    }

    public boolean isValidTheme(String name) {
        return themeQuestions.containsKey(name.toUpperCase());
    }
}