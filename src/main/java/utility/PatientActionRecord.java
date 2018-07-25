package utility;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.logging.Level;

public class PatientActionRecord implements Serializable {

    private Timestamp timestamp;

    private Level level;

    private String message;

    private String action;

    /**
     *
     * @param timestamp - Timestamp of the log
     * @param level - severity of the log
     * @param action - action completed/attempted by the patients
     * @param message - message to display in log
     */
    public PatientActionRecord(Timestamp timestamp, Level level, String action, String message){
        this.timestamp = timestamp;
        this.level = level;
        this.action = action;
        this.message = message;

    }

    /**
     * gets action
     * @return action completed/attempted by the patients
     */
    public String getAction() {
        return action;
    }


    /**
     * gets the message
     * @return message to display in log
     */
    public String getMessage() {
        return message;
    }


    /**
     * gets the level
     * @return severity of the log
     */

    public Level getLevel() {
        return level;
    }

    /**
     * Gets the timestamp
     * @return Timestamp of the log
     */
    public Timestamp getTimestamp() {
        return timestamp;
    }
}
