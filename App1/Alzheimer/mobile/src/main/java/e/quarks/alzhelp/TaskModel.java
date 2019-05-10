package e.quarks.alzhelp;

import java.util.UUID;

public class TaskModel {

    private String title;
    private String dateLimit;
    private String timeLimit;
    private String description;
    private String id;

    public TaskModel() {
    }

    public TaskModel(String title, String description) {
        this.title = title;
        this.description = description;
        this.id = UUID.randomUUID().toString();
    }

    public TaskModel(String title, String dateLimit, String timeLimit, String description) {
        this.title = title;
        this.description = description;
        this.dateLimit = dateLimit;
        this.timeLimit = timeLimit;
        this.id = UUID.randomUUID().toString();
    }

    public TaskModel copyData(TaskModel taskM){
        this.title = taskM.getTitle();
        this.dateLimit = taskM.getDateLimit();
        this.description = taskM.getDescription();
        return this;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDateLimit() {
        return dateLimit;
    }

    public void setDateLimit(String dateLimit) {
        this.dateLimit = dateLimit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(String timeLimit) {
        this.timeLimit = timeLimit;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
