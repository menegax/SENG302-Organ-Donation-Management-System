package utility;



import java.io.Serializable;
import java.sql.Timestamp;
import java.util.logging.Level;

public class PatientActionRecord extends UserActionRecord implements Serializable {


    /**
     * @param timestamp - Timestamp of the log
     * @param level     - severity of the log
     * @param action    - action completed/attempted by the patients
     * @param message   - message to display in log
     */
    public PatientActionRecord(Timestamp timestamp, Level level, String action, String message) {
        super(timestamp, level, action, message);
    }

}