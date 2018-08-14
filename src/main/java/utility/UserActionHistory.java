package utility;

import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;

import controller.UserControl;
import model.Administrator;
import model.Clinician;
import model.Patient;
import model.User;
import org.apache.commons.lang3.StringUtils;

import java.sql.Timestamp;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class UserActionHistory {

    /**
     * This log contains user actions and their results in parameters param and message respectively.
     */
    public static final Logger userActions = Logger.getLogger(UserActionHistory.class.getName());

    private static UserControl userControl = UserControl.getUserControl();

    /**
     * Sets up custom logger class.
     * Disables parent inheritance and adds custom console and file handlers.
     */
    public static void setup() {

        userActions.setUseParentHandlers(false); // disables default console userActions in parent

        Handler userHandler = new Handler() {
            public void publish(LogRecord logRecord) {
                Timestamp currentTimeStamp = new Timestamp(System.currentTimeMillis());

                // get logged in patient if it exists
                Object loggedInUser = userControl.getLoggedInUser();

                // Ensure all params (resulted, attempted actions) are there
                if (logRecord.getParameters() == null) {
                    SystemLogger.systemLogger.log(SEVERE, "Failed to log user action to user object. Ensure both resulted and attempted actions are logged");
                }

                // PATIENT RECORDS
                else if (loggedInUser instanceof Patient) {

                    // Add a patient record if a patient is logged in
                    ((Patient) loggedInUser).getUserActionsList()
                            .add(new PatientActionRecord(currentTimeStamp,
                                    logRecord.getLevel(),
                                    StringUtils.capitalize(logRecord.getParameters()[0].toString()),
                                    StringUtils.capitalize(logRecord.getMessage())));

                }

                // CLINICIAN RECORDS
                else if (loggedInUser instanceof Clinician) {

                    //Add a clinician record if a clinician is logged in
                    String nhiParam = "";
                    // Ensure all params (resulted, attempted actions) are there
                    if (logRecord.getParameters() == null) {
                        SystemLogger.systemLogger.log(SEVERE, "Failed to log user action to admin object. Ensure both resulted and attempted actions are logged");
                    }

                    //If there are more than 1 parameter, in which case the target nhi is provided as the second parameter
                    if (logRecord.getParameters().length >= 2) {
                        nhiParam = logRecord.getParameters()[1].toString()
                                .toUpperCase();

                        ((Clinician) loggedInUser).getClinicianActionsList()
                                .add(new ClinicianActionRecord(currentTimeStamp,
                                        logRecord.getLevel(),
                                        StringUtils.capitalize(logRecord.getParameters()[0].toString()),
                                        StringUtils.capitalize(logRecord.getMessage()),
                                        nhiParam));
                    } else {
                        ((Clinician) loggedInUser).getClinicianActionsList()
                                .add(new ClinicianActionRecord(currentTimeStamp,
                                        logRecord.getLevel(),
                                        StringUtils.capitalize(logRecord.getParameters()[0].toString()),
                                        StringUtils.capitalize(logRecord.getMessage()),
                                        nhiParam));
                    }
                }

                // ADMINISTRATOR RECORDS
                else if (loggedInUser instanceof Administrator) {

                    //Add an administrator record if a administrator is logged in
                    String targetParam = "";

                    //If there are more than 1 parameter, in which case the target ID is provided as the second parameter
                    if (logRecord.getParameters().length >= 2) {
                        targetParam = logRecord.getParameters()[1].toString()
                                .toUpperCase();

                        ((Administrator) loggedInUser).getAdminActionsList()
                                .add(new AdministratorActionRecord(currentTimeStamp,
                                        logRecord.getLevel(),
                                        StringUtils.capitalize(logRecord.getParameters()[0].toString()),
                                        StringUtils.capitalize(logRecord.getMessage()),
                                        targetParam));
                    } else {
                        ((Administrator) loggedInUser).getAdminActionsList()
                                .add(new AdministratorActionRecord(currentTimeStamp,
                                        logRecord.getLevel(),
                                        StringUtils.capitalize(logRecord.getParameters()[0].toString()),
                                        StringUtils.capitalize(logRecord.getMessage()),
                                        targetParam));
                    }
                }

                // Show resulting action in GUI status bar
                StatusObservable.getInstance().setStatus(logRecord.getMessage());
            }

            @Override
            public void flush() {
            }


            @Override
            public void close() throws SecurityException {
            }
        };
        userActions.addHandler(userHandler);

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
