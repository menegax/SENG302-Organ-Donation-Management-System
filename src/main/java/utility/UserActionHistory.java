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

        // todo set console handler settings
        Handler console = new ConsoleHandler();

        // set filter
        System.out.println("Console filter: " + console.getFilter());

        // set formatter
        console.setFormatter(logFormat);
        System.out.println("Console formatter: " + console.getFormatter());

        // set level
        console.setLevel(Level.INFO);
        System.out.println("Console level: " + console.getLevel());

        userActions.addHandler(console);
        System.out.println("Console level after change: " + console.getLevel());

        // todo set file handler settings
        try {
            Handler file = new FileHandler("UserActionHistory%u.%g.xml"); //todo add %u or %g modifiers to file names
            System.out.println("File filter: " + console.getFilter());
            System.out.println("File formatter: " + console.getFormatter());
            System.out.println("File level: " + console.getLevel());
            userActions.addHandler(file);
        } catch (IOException e) {
            userActions.log(Level.SEVERE, "Unable to write log to file");
        }

    }
}
