package utility;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class UserActionRecord {

    private String timestamp;
    private String level;
    private String message;
    private String action;

    public static ObservableList<UserActionRecord> logHistory = FXCollections.observableArrayList();

    UserActionRecord(String timestamp, String level, String message, String action){
        this.timestamp = timestamp;
        this.level = level;
        this.message = message;
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
