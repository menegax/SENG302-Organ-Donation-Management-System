package utility;

import static java.util.logging.Level.INFO;

import controller.UserControl;
import model.Patient;
import org.apache.commons.lang3.StringUtils;

import java.sql.Timestamp;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class UserActionHistory {

    /**
     * This log contains user actions and their results in parameters param and message respectively.
     */
    public static final Logger userActions = Logger.getLogger(UserActionHistory.class.getName());


    /**
     * Sets up custom logger class.
     * Disables parent inheritance and adds custom console and file handlers.
     */
    static public void setup() {

        userActions.setUseParentHandlers(false); // disables default console userActions in parent

        Handler patientHandler = new Handler() {
            public void publish(LogRecord logRecord) {
                Timestamp currentTimeStamp = new Timestamp(System.currentTimeMillis());

                // get logged in patient if it exists
                Patient loggedInPatient =
                        new UserControl().getLoggedInUser() instanceof Patient ? ((Patient) new UserControl().getLoggedInUser()) : null;

                // if it exists log the record to it
                if (loggedInPatient != null ) {
                    loggedInPatient.getUserActionsList()
                            .add(new UserActionRecord(currentTimeStamp,
                                    logRecord.getLevel(),
                                    StringUtils.capitalize(logRecord.getParameters()[0].toString()), //capitalise the action
                                    StringUtils.capitalize(logRecord.getMessage())));
                }

            }

            @Override
            public void flush() {
            }


            @Override
            public void close() throws SecurityException {
            }
        };
        userActions.addHandler(patientHandler);

        // Console handler
        Handler console = new ConsoleHandler();
        console.setLevel(INFO);
        console.setFormatter(new SimpleFormatter(){
            @Override
            public String format(LogRecord record){
                return record.getLevel() + ": " + StringUtils.capitalize(record.getMessage()) + "\n";

            }
        });
        userActions.addHandler(console);


    }
}
