package e.quarks.alzhelp;

public class ObjectModel {

    private String nameObject;
    private int photo;


    public ObjectModel(){

    }

    public ObjectModel(String nameObject, int photo) {
        this.nameObject = nameObject;
        this.photo = photo;
    }

    public String getNameObject() {
        return nameObject;
    }

    public void setNameObject(String nameObject) {
        this.nameObject = nameObject;
    }

    public int getPhoto() {
        return photo;
    }

    public void setPhoto(int photo) {
        this.photo = photo;
    }
}
