package utility;

import java.io.IOException;
import java.util.logging.*;

public class ActionHistory {

    public static final Logger logger =
            Logger.getLogger(ActionHistory.class.getName());

    private static FileHandler fileTxt;
    private static SimpleFormatter formatterTxt;

    static public void saveToDisk() {
        // todo this currently only add a file handler to logger
        // todo needs to actually print the logs to a file not just open a file and be ready to write
        logger.setLevel(Level.INFO);
        try {
            fileTxt = new FileHandler("ActionHistory.txt");
        } catch (IOException e) {
            System.out.println("An error occurred writing logs to disk");
        }
        // create a TXT formatter
        formatterTxt = new SimpleFormatter();
        fileTxt.setFormatter(formatterTxt);
        logger.addHandler(fileTxt);
    }

//    static public void addSomeHandlerThing() {
//        Logger logger = Logger.getLogger("myLogger");
//
//        logger.addHandler(new ConsoleHandler());
//
//        logger.logrb(Level.SEVERE, "logging.LoggingExamples", "main",
//                "resources.myresources", "key1");
//    }
}
