package e.quarks.whoismore;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.UUID;

public class Match implements Parcelable {
    private String uid;
    private String name;
    private String pass;
    private String date;
    private Status state;
    private String currentQuestion;


    public static final Parcelable.Creator<Match> CREATOR =
            new Parcelable.Creator<Match>()
            {
                @Override
                public Match createFromParcel(Parcel parcel)
                {
                    return new Match(parcel);
                }

                @Override
                public Match[] newArray(int size)
                {
                    return new Match[size];
                }
            };

    public Match(Parcel parcel)
    {
        //seguir el mismo orden que el usado en el método writeToParcel
        name = parcel.readString();
        pass = parcel.readString();
        state = (Status) parcel.readSerializable();
        date = parcel.readString();
        uid = parcel.readString();
        currentQuestion = parcel.readString();
    }

    public Match() {
    }



    public Match(String name, String pass, Status state) {
        this.name = name;
        this.pass = pass;
        this.uid = UUID.randomUUID().toString();
        this.state = state;
        this.date = new Date().toString();
        this.currentQuestion = "Default";
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Status getState() {
        return state;
    }

    public void setState(Status state) {
        this.state = state;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(pass);
        dest.writeSerializable(state);
        dest.writeString(date);
        dest.writeString(uid);
    }

    public String getCurrentQuestion() {
        return currentQuestion;
    }

    public void setCurrentQuestion(String currentQuestion) {
        this.currentQuestion = currentQuestion;
    }
}
