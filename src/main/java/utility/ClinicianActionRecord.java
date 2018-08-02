package utility;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.logging.Level;

public class ClinicianActionRecord extends UserActionRecord implements Serializable {

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
        super(timestamp,level,action,message);
        this.targetNHI = targetNHI;
    }

    /**
     * gets the targetNHI of the log
     * @return patient whoms actions were against
     */
    public String getTarget() {
        return targetNHI;
    }
}
