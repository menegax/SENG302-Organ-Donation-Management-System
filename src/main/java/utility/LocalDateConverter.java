package utility;

import com.j256.simplecsv.converter.Converter;
import com.j256.simplecsv.processor.ColumnInfo;
import com.j256.simplecsv.processor.ParseError;

import java.text.ParseException;
import java.time.LocalDate;

public class LocalDateConverter implements Converter<LocalDate, String> {

    @Override
    public String configure(String s, long l, ColumnInfo<LocalDate> columnInfo) {
        return null;
    }

    @Override
    public boolean isNeedsQuotes(String s) {
        return false;
    }

    @Override
    public boolean isAlwaysTrimInput() {
        return false;
    }

    @Override
    public String javaToString(ColumnInfo<LocalDate> columnInfo, LocalDate localDate) {
        return null;
    }

    @Override
    public LocalDate stringToJava(String s, int i, int i1, ColumnInfo<LocalDate> columnInfo, String s1, ParseError parseError) throws ParseException {
        return null;
    }

}
