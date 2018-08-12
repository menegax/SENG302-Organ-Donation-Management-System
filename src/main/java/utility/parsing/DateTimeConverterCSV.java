package utility.parsing;

import com.univocity.parsers.conversions.Conversion;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeConverterCSV implements Conversion {
    @Override
    public Object execute(Object o) {
        if (o != null) {
            final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("M/d/yyyy");
            return LocalDate.parse(o.toString(), dtf).atStartOfDay();
        }
        return null;
    }

    @Override
    public Object revert(Object o) {
        return o.toString();
    }
}
