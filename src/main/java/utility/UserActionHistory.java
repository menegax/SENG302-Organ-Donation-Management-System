package utility;

import java.io.IOException;
import java.util.logging.*;

public class UserActionHistory {

    public static final Logger userActions = Logger.getLogger(UserActionHistory.class.getName());

    private static FileHandler fileTxt;
    private static SimpleFormatter formatterTxt;
    private static FormatterLog logFormat = new FormatterLog();

    static public void setup() {
        userActions.setUseParentHandlers(false); // disables default console userActions in parent

        // Console handler
        Handler console = new ConsoleHandler();
        console.setLevel(Level.INFO);
        console.setFormatter(logFormat);
        userActions.addHandler(console);

        // File handler
        try {
            Handler file = new FileHandler("UserActionHistory%u.%g.xml");
            System.out.println("File filter: " + console.getFilter());
            System.out.println("File formatter: " + console.getFormatter());
            System.out.println("File level: " + console.getLevel());
            userActions.addHandler(file);
        } catch (IOException e) {
            userActions.log(Level.SEVERE, "Unable to write log to file");
        }

    }
}
