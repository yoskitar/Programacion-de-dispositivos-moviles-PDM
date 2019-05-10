package e.quarks.whoismore;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.UUID;

class Question {
    //private String uid;
    private String question;
    private String date;

    public Question() {
    }

    public Question(String question) {
        this.question = question;
        this.date = new Date().toString();
        //this.uid = UUID.randomUUID().toString();
    }

   /* public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
*/

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
