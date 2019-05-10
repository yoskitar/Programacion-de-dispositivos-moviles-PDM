package e.quarks.whoismore;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;

import java.util.Random;
import java.util.UUID;

public class User implements Parcelable{
    private String uid;
    private String name;
    private boolean state;
    private boolean host;

    public static final Parcelable.Creator<User> CREATOR =
            new Parcelable.Creator<User>()
            {
                @Override
                public User createFromParcel(Parcel parcel)
                {
                    return new User(parcel);
                }

                @Override
                public User[] newArray(int size)
                {
                    return new User[size];
                }
            };

    public User(Parcel parcel)
    {
        //seguir el mismo orden que el usado en el método writeToParcel
        name = parcel.readString();
        state = (Boolean) parcel.readSerializable();
        uid = parcel.readString();
        host = (Boolean) parcel.readSerializable();

    }

    public User() {
    }

    public User(String name, boolean state) {
        this.name = name;
        this.uid = UUID.randomUUID().toString();
        this.state = state;
        this.host = false;
    }

    public User(String name, boolean state, boolean host) {
        this.name = name;
        this.uid = UUID.randomUUID().toString();
        this.state = state;
        this.host = host;
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

    public boolean getState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }


    public boolean isHost() {
        return host;
    }

    public void setHost(boolean host) {
        this.host = host;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeSerializable(state);
        dest.writeString(uid);
        dest.writeSerializable(host);
    }

}
