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
    public AdministratorActionRecord(Timestamp timestamp, Level level, String action, String message, String target){
        this.timestamp = timestamp;
        this.level = level;
        this.action = action;
        this.message = message;
        this.target = target;
    }

    /**
     * gets the target of the log
     * @return user whoms actions were against
     */
    public String getTarget() {
        return target;
    }


    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTarget(String target) {
        this.target = target;
    }

}
