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

    private String message;

    private String action;

    public UserActionRecord(Timestamp timestamp, Level level, String action, String message){
        this.timestamp = timestamp;
        this.level = level;
        this.action = action;
        this.message = message;

    }


    public String getAction() {
        return action;
    }


    public String getMessage() {
        return message;
    }


    public Level getLevel() {
        return level;
    }


    public Timestamp getTimestamp() {
        return timestamp;
    }
}
