package question;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuestionDatabase {
    private List<Question> allQuestions;

    // Konstruktor - läser in alla frågor från filen när databasen skapas
    public QuestionDatabase(Path path) {
        this.allQuestions = readQuestions(path);
    }

    // Läser frågor från textfilen
    private List<Question> readQuestions(Path path) {
        List<Question> questionList = new ArrayList<>();

        if (!Files.exists(path)) {
            System.err.println("Filen finns inte: " + path);
            return questionList;
        }

        try (BufferedReader br = Files.newBufferedReader(path)) {
            String line;

            // Läs rad för rad
            while ((line = br.readLine()) != null) {
                line = line.trim();

                // Hoppa över tomma rader
                if (line.isEmpty()) {
                    continue;
                }

                String[] parts = line.split(";");
                if (parts.length != 7) {
                    System.out.println("Fel format på raden: " + line);
                    continue;
                }

                try {
                    // Skapa Question-objekt från delarna
                    String questionText = parts[0];
                    String[] options = {parts[1], parts[2], parts[3], parts[4]};
                    String correctAnswer = parts[5];
                    Category category = Category.valueOf(parts[6].toUpperCase());

                    Question question = new Question(questionText, options, correctAnswer, category);
                    questionList.add(question);
                } catch (IllegalArgumentException e) {
                    System.err.println("Ogiltig kategori på raden: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Fel vid inläsning av fil: " + e.getMessage());
        }

        return questionList;
    }

    // Returnerar alla frågor från en specifik kategori
    public List<Question> getQuestionsByCategory(Category category) {
        List<Question> categoryQuestions = new ArrayList<>();
        for (Question q : allQuestions) {
            if (q.getCategory() == category) {
                categoryQuestions.add(q);
            }
        }
        return categoryQuestions;
    }

    // Returnerar alla tillgängliga kategorier
    public List<Category> getAllCategories() {
        return Arrays.asList(Category.values());
    }

    // Returnerar alla frågor
    public List<Question> getAllQuestions() {
        return new ArrayList<>(allQuestions);
    }
}