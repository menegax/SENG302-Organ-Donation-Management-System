package utility;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class SystemLogger {


        /**
         * This log contains and handles system logs
         */
        public static final Logger systemLogger = Logger.getLogger(utility.SystemLogger.class.getName());


        /**
         * Adds appropriate handlers to the logger
         */
        static public void setup() {

            systemLogger.setUseParentHandlers(false); // disables default console handler in parent
            systemLogger.setLevel(Level.FINE); // DO NOT CHANGE. See below to turn off logging.


            // Console handler
            Handler console = new ConsoleHandler();
            console.setLevel(Level.ALL); // TURN ON TO 'ALL' TO LOG ALL LEVELS TO CONSOLE
            console.setFormatter(new SimpleFormatter(){
                @Override
                public String format(LogRecord record){
                    return "== SYSTEM DEBUG == " + record.getLevel() + ": " + StringUtils.capitalize(record.getMessage()) + "\n";

                }
            });
            systemLogger.addHandler(console);

        }
    }
