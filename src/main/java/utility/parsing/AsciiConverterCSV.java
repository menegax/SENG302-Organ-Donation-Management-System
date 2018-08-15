package utility.parsing;

import com.univocity.parsers.conversions.Conversion;
import utility.UserActionHistory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;

public class AsciiConverterCSV implements Conversion {

    @Override
    public Object execute(Object o) {
        String input = (String) o;
        try {
            return URLDecoder.decode(input, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            UserActionHistory.userActions.log(Level.SEVERE, "Error decoding parser output", "attempted to decode parser output");
            return null;
        }
    }

    @Override
    public Object revert(Object o) {
        return o.toString();
    }
}
