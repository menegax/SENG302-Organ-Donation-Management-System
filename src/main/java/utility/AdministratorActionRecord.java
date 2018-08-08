package utility;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.logging.Level;

public class AdministratorActionRecord extends UserActionRecord implements Serializable {

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
        super(timestamp, level, action, message);
        this.target = target;
    }

    /**
     * gets the target of the log
     * @return user whoms actions were against
     */
    public String getTarget() {
        return target;
    }
}
