package utility;

import org.apache.commons.lang3.StringUtils;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class FormatterLog extends Formatter {

    @Override
    public String format(LogRecord record) {
        return record.getLevel() + ": " + StringUtils.capitalize(record.getMessage()) + "\n";
    }

}
