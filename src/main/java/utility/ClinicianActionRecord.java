package utility;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.logging.Level;

public class ClinicianActionRecord implements Serializable {

    private Timestamp timestamp;

    private Level level;

    private String action;

    private String message;

    private String targetNHI;

    /**
     * Clinician model for an action
     * @param timestamp - Timestamp of the log
     * @param level - severity of the log
     * @param action - action completed/attempted by the clinician
     * @param message - message to display in log
     * @param targetNHI - patient whom's actions were against
     */
    public ClinicianActionRecord(Timestamp timestamp, Level level, String action, String message, String targetNHI){
        this.timestamp = timestamp;
        this.level = level;
        this.action = action;
        this.message = message;
        this.targetNHI = targetNHI;
    }

    /**
     * Gets the timestamp of the log
     * @return Timestamp of log
     */
    public Timestamp getTimestamp() {
        return timestamp;
    }

    /**
     * Gets the level of the log
     * @return severity of the log
     */
    public Level getLevel() {
        return level;
    }

    /**
     * Gets the action from the log
     * @return action completed/attempted by the clinician
     */
    public String getAction() {
        return action;
    }

    /**
     * gets the message of the log
     * @return message to display in log
     */
    public String getMessage() {
        return message;
    }

    /**
     * gets the targetNHI of the log
     * @return patient whoms actions were against
     */
    public String getTarget() {
        return targetNHI;
    }
}
