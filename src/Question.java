public class Question {

    private String question;
    private String[] options;
    private String correctAnswer;
    private Category category;

    public Question(String question, String[] options, String correctAnswer, Category category) {
        this.question = question;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.category = category;
    }

    public String getQuestion() {
        return question;
    }

    public String[] getOptions() {
        return options;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public Category getCategory() {
        return category;
    }

    public boolean isCorrectAnswer(String answer) {
        return answer.equalsIgnoreCase(correctAnswer);
    }
}