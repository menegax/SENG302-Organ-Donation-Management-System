package utility;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

public class FilterUserOutput implements Filter {

    public boolean isLoggable(LogRecord record) {
        return true; // all record are loggable
    }

}