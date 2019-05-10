package e.quarks.alzhelp;

import java.util.Date;

public class ResultModel {

    private String type;
    private String date;
    private float correct;
    private float incorrect;

    public ResultModel (){
        this.correct = 0;
        this.incorrect = 0;
    }

    public ResultModel (String type){
        this.correct = 0;
        this.incorrect = 0;
        this.type = type;
        this.date = new Date().toString();
    }

    ResultModel(String type, String date, float correct, float incorrect){
        this.type = type;
        this.date = date;
        this.correct = correct;
        this.incorrect = incorrect;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getCorrect() {
        return correct;
    }

    public void setCorrect(float correct) {
        this.correct = correct;
    }

    public float getIncorrect() {
        return incorrect;
    }

    public void setIncorrect(float incorrect) {
        this.incorrect = incorrect;
    }

    public void incrementCorrect(){
        this.correct = this.correct+1;
    }

    public void incrementIncorrect(){
        this.incorrect = this.incorrect+1;
    }
}
