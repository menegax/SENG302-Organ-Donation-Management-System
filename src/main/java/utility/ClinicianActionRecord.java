package utility;

import java.sql.Timestamp;
import java.util.logging.Level;

public class ClinicianActionRecord {

    private Timestamp timestamp;

    private Level level;

    private String action;

    private String message;

    private String targetNHI;

    ClinicianActionRecord(Timestamp timestamp, Level level, String action, String message, String targetNHI){
        this.timestamp = timestamp;
        this.level = level;
        this.action = action;
        this.message = message;
        this.targetNHI = targetNHI;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public Level getLevel() {
        return level;
    }

    public String getAction() {
        return action;
    }

    public String getMessage() {
        return message;
    }

    public String getTarget() {
        return targetNHI;
    }
}
