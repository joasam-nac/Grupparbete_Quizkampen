package shared;

import java.io.Serializable;
import java.util.List;

public record Question(
        String text,
        List<String> alternatives,
        int correctIndex
) implements Serializable {

    public boolean isCorrect(int answerIndex) {
        return answerIndex == correctIndex;
    }

    public String getCorrectAnswer() {
        return alternatives.get(correctIndex);
    }

    public String toProtocolString() {
        return text + ";" + String.join(";", alternatives) + ";" + correctIndex;
    }

    public static Question fromProtocolString(String s) {
        String[] parts = s.split(";");
        String text = parts[0];
        List<String> alts = List.of(parts[1], parts[2], parts[3], parts[4]);
        int correct = Integer.parseInt(parts[5]);
        return new Question(text, alts, correct);
    }
}