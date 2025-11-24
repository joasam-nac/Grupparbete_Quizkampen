import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class QuestionDatabase {

    public List<Question> readQuestions(Path path) {
        List<Question> questionList = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(path)) {
            String line;

            while ((line = br.readLine()) != null) {
                line = line.trim();

                String[] parts = line.split(";");
                if (parts.length != 7) {
                    System.out.println("Fel format på raden: " + line);
                    continue;
                }

                String questionText = parts[0];
                String[] options = {parts[1], parts[2], parts[3], parts[4]};
                String correctAnswer = parts[5];
                Category category = Category.valueOf(parts[6]);

                Question question = new Question(questionText, options, correctAnswer, category);
                questionList.add(question);
            }
        } catch (IOException e) {
            System.err.println("Fel vid inläsning av fil: " + e.getMessage());
            return null;
        }

        return questionList;
    }
}
