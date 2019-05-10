package e.quarks.alzhelp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.UUID;

public class HelpModel implements Parcelable{

    private String idUser;
    private String date;
    private String msg;
    private String email;
    private String geo;

    public static final Parcelable.Creator<HelpModel> CREATOR =
            new Parcelable.Creator<HelpModel>()
            {
                @Override
                public HelpModel createFromParcel(Parcel parcel)
                {
                    return new HelpModel(parcel);
                }

                @Override
                public HelpModel[] newArray(int size)
                {
                    return new HelpModel[size];
                }
            };

    public HelpModel(Parcel parcel)
    {
        //seguir el mismo orden que el usado en el método writeToParcel
        idUser = parcel.readString();
        date = parcel.readString();
        msg = parcel.readString();
        email = parcel.readString();
        geo = parcel.readString();

    }

    public HelpModel() {
    }

    public HelpModel(String idUser, String msg) {
        this.idUser = idUser;
        this.msg = msg;
        this.date = new Date().toString();
    }

    public HelpModel(String email, String idUser, String geo, String msg) {
        this.idUser = idUser;
        this.msg = msg;
        this.date = new Date().toString();
        this.email = email;
        this.geo = geo;
    }


    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getGeo() {
        return geo;
    }

    public void setGeo(String geo) {
        this.geo = geo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(idUser);
        dest.writeSerializable(date);
        dest.writeString(msg);
        dest.writeSerializable(email);
        dest.writeSerializable(geo);
    }
}