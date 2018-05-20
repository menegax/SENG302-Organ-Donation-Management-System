package utility;

import controller.UserControl;
import model.Patient;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.logging.*;

public class UserActionHistory {

    /**
     * This log contains user actions and their results in parameters param and message respectively.
     */
    public static final Logger userActions = Logger.getLogger(UserActionHistory.class.getName());

    private static FormatterLog logFormat = new FormatterLog();


    /**
     * Sets up custom logger class.
     * Disables parent inheritance and adds custom console and file handlers.
     */
    static public void setup() {

        userActions.setUseParentHandlers(false); // disables default console userActions in parent

        // todo patient handler
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
        console.setLevel(Level.INFO);
        console.setFormatter(new SimpleFormatter(){
            @Override
            public String format(LogRecord record){
                return record.getLevel() + ": " + StringUtils.capitalize(record.getMessage()) + "\n";

            }
        });

        userActions.addHandler(console);

        // todo remove maybe
        // File handler
        try {
            Handler file = new FileHandler("UserActionHistory%u.xml", true);
            userActions.addHandler(file);
        }
        catch (IOException e) {
            userActions.log(Level.SEVERE, "Unable to write log to file");
        }

    }
}
