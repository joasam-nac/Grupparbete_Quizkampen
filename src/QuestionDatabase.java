import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class QuestionDatabase {

    private List<Questions> allQuestions = new ArrayList<>();

    public QuestionDatabase() {
        readQuestionsFromFile(Path.of("src/questions.txt"));
    }

    public void readQuestionsFromFile(Path path) {
        try (BufferedReader reader = Files.newBufferedReader(path)) {

        }
    }
}
