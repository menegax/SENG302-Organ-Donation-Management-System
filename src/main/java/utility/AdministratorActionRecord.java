package utility;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.logging.Level;

public class AdministratorActionRecord implements Serializable {

    private Timestamp timestamp;

    private Level level;

    private String action;

    private String message;

    private String target;

    /**
     * Clinician model for an action
     * @param timestamp - Timestamp of the log
     * @param level - severity of the log
     * @param action - action completed/attempted by the clinician
     * @param message - message to display in log
     * @param target - user whom's actions were against
     */
    AdministratorActionRecord(Timestamp timestamp, Level level, String action, String message, String target){
        this.timestamp = timestamp;
        this.level = level;
        this.action = action;
        this.message = message;
        this.target = target;
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
     * gets the target of the log
     * @return user whoms actions were against
     */
    public String getTarget() {
        return target;
    }
}
