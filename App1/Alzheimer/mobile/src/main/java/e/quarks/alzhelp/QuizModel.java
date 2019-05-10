package e.quarks.alzhelp;

public class QuizModel {

    String quiz;
    String idQuiz;
    String answer;

    public QuizModel() {
    }

    public QuizModel(String quiz, String answer, String idQuiz){
        this.quiz = quiz;
        this.answer = answer;
        this.idQuiz = idQuiz;
    }

    public String getQuiz() {
        return quiz;
    }

    public void setQuiz(String quiz) {
        this.quiz = quiz;
    }

    public String getIdQuiz() {
        return idQuiz;
    }

    public void setIdQuiz(String idQuiz) {
        this.idQuiz = idQuiz;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }


}
