public class Questions {

    private String question;
    private String[] options;
    private String correctAnswer;
    private Category category;

    public Questions(String question, String[] options, String correctAnswer, Category category) {
        this.question = question;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.category = category;
    }


}
