public class Question {

    private String question;
    private String[] option;
    private String correctAnswer;
    private Category category;

    public Question(String question, String[] option, String correctAnswer, Category category) {
        this.question = question;
        this.option = option;
        this.correctAnswer = correctAnswer;
        this.category = category;
    }

    public String getQuestion() {
        return question;
    }

    public String[] getOption() {
        return option;
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
