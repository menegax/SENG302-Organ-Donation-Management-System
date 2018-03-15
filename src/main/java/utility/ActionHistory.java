package utility;

import java.io.IOException;
import java.util.logging.*;

public class ActionHistory {

    public static final Logger logger = Logger.getLogger(ActionHistory.class.getName());

    private static FileHandler fileTxt;
    private static SimpleFormatter formatterTxt;
    private static FormatterLog logFormat = new FormatterLog();

    static public void setup() {
        logger.setUseParentHandlers(false); // disables default console logger in parent

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

        logger.addHandler(console);
        System.out.println("Console level after change: " + console.getLevel());

        // todo set file handler settings
        try {
            Handler file = new FileHandler("ActionHistory.xml"); //todo add %u or %g modifiers to file names
            System.out.println("File filter: " + console.getFilter());
            System.out.println("File formatter: " + console.getFormatter());
            System.out.println("File level: " + console.getLevel());
            logger.addHandler(file);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Unable to write log to file");
        }

    }

    @Deprecated
    static public void saveToDisk() {
        // todo this currently only add a file handler to logger
        // todo needs to actually print the logs to a file not just open a file and be ready to write
        logger.setLevel(Level.ALL);
        try {
            fileTxt = new FileHandler("ActionHistory.txt"); //todo add %u or %g modifiers to file names
        } catch (IOException e) {
            System.out.println("An error occurred writing logs to disk");
        }
        // create a TXT formatter
        formatterTxt = new SimpleFormatter();
        fileTxt.setFormatter(formatterTxt);
        logger.addHandler(fileTxt);
    }

    public static void consoleDebugVerbosity() {

    }
}
