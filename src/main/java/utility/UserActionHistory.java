package utility;

import java.io.IOException;
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

        // Console handler
        Handler console = new ConsoleHandler();
        console.setLevel(Level.INFO);
        console.setFormatter(logFormat);
        userActions.addHandler(console);

        // File handler
        try {
            Handler file = new FileHandler("UserActionHistory%u.%u.xml", true);
            userActions.addHandler(file);
        } catch (IOException e) {
            userActions.log(Level.SEVERE, "Unable to write log to file");
        }

    }
}
