package server;

public class Question {
    String text;
    String correctAnswer;

    Question(String text, String correctAnswer) {
        this.text = text;
        this.correctAnswer = correctAnswer.toLowerCase();
    }

    String getText() {
        return text;
    }

    String getCorrectAnswer() {
        return  correctAnswer;
    }
}
