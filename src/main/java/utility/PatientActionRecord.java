package utility;

import java.sql.Timestamp;
import java.util.logging.Level;

public class PatientActionRecord {

    private Timestamp timestamp;

    private Level level;

    private String message;

    private String action;

    PatientActionRecord(Timestamp timestamp, Level level, String action, String message){
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
