package e.quarks.alzhelp;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class User {
    private String uid;
    private String name;
    private String surname;
    private String email;
    private String phoneNumber;
    private String password;
    private String typeUser;
    private String pinUser;
    private String carer; //cuidador


    public User() {
    }

    public User(String name, String surname, String email, String typeUser) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.typeUser = typeUser;
        Random rand = new Random();
        this.pinUser = String.valueOf(rand.nextInt(999)+1);
        if(typeUser.equals("A")){
            this.carer = "Default";
        }
    }

    public User(String name, String surname, String email, String typeUser, String phoneNumber) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.typeUser = typeUser;
        this.phoneNumber = phoneNumber;
        Random rand = new Random();
        this.pinUser = String.valueOf(rand.nextInt(999)+1);
        if(typeUser.equals("A")){
            this.carer = "Default";
        }
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getTypeUser() {
        return typeUser;
    }

    public void setTypeUser(String typeUser) {
        this.typeUser = typeUser;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPinUser() {
        return pinUser;
    }

    public void setPinUser(String pinUser) {
        this.pinUser = pinUser;
    }

    public String getCarer() {
        return carer;
    }

    public void setCarer(String carer) {
        this.carer = carer;
    }

    public String getFullName(){
        return this.name + " " + this.surname;
    }


}
