package e.oscarjimfer.pruebatfg;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class ServiceModel {
    private String _id;
    private String serviceName;
    private String description;
    private String photo;
    private Bitmap imgService;
    private int duration;
    private String createdAt;
    private String dateInit;
    private String typeService;
    private ArrayList<String> microServices;

    public ServiceModel(){}

    public ServiceModel(String _id, String serviceName, String description, String photo, int duration, String createdAt, String dateInit) {
        this._id = _id;
        this.serviceName = serviceName;
        this.description = description;
        this.photo = photo;
        this.duration = duration;
        this.createdAt = createdAt;
        this.dateInit = dateInit;
    }

    public ServiceModel(String _id, String serviceName, String description, String photo, int duration, String createdAt, String dateInit, String typeService, ArrayList<String> microServices) {
        this._id = _id;
        this.serviceName = serviceName;
        this.description = description;
        this.photo = photo;
        this.duration = duration;
        this.createdAt = createdAt;
        this.dateInit = dateInit;
        this.typeService = typeService;
        this.microServices = microServices;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getTypeService() {
        return typeService;
    }

    public void setTypeService(String typeService) {
        this.typeService = typeService;
    }

    public ArrayList<String> getMicroServices() {
        return microServices;
    }

    public void setMicroServices(ArrayList<String> microServices) {
        this.microServices = microServices;
    }

    public String getDateInit() {
        return dateInit;
    }

    public void setDateInit(String dateInit) {
        this.dateInit = dateInit;
    }

    public Bitmap getImgService() {
        return imgService;
    }

    public void setImgService(Bitmap imgService) {
        this.imgService = imgService;
    }
}
