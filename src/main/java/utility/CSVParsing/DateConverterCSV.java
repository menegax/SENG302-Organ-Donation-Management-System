package utility.CSVParsing;

import com.univocity.parsers.conversions.Conversion;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateConverterCSV implements Conversion {

    @Override
    public Object execute(Object o) {
        if (o != null) {
            final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("M/d/yyyy");
            return LocalDate.parse(o.toString(), dtf);
        }
        return o;
    }

    @Override
    public Object revert(Object o) {
        return o.toString();
    }
}
