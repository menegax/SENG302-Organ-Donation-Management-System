package utility;

import controller.ScreenControl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.UUID;
import java.util.logging.Level;

public class UserActionRecord {

    private Timestamp timestamp;

    private Level level;

    private UUID uuid;

    private String message;

    private String action;

    public static ObservableList<UserActionRecord> logHistory = FXCollections.observableArrayList(); //todo move to db class


    public UserActionRecord(Timestamp timestamp, Level level, UUID uuid, String action, String message){
        this.timestamp = timestamp;
        this.level = level;
        this.uuid = uuid;
        this.action = action;
        this.message = message;

    }


    public Timestamp getTimestamp() {
        return timestamp;
    }


    public String getAction() {
        return action;
    }


    public String getMessage() {
        return message;
    }


    public UUID getUuid() {
        return uuid;
    }


    public Level getLevel() {
        return level;
    }
}
