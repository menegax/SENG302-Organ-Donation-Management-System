package utility;


import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.conversions.Conversion;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import model.Patient;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ParseCsv {

    public static void parse(FileReader reader) {
        BeanListProcessor<Patient> rowProcessor = new BeanListProcessor<>(Patient.class);
        //rowProcessor.convertFields(Conversions.toDate("MM/dd/yyyy")).set("date_of_birth");
        rowProcessor.convertFields(new Conversion() {
            @Override
            public Object execute(Object o) {
                final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("M/d/yyyy");
                return LocalDate.parse(o.toString(), dtf);
            }

            @Override
            public Object revert(Object o) {
                return o.toString();
            }
        }).set("date_of_birth");

        CsvParserSettings settings = new CsvParserSettings();
        settings.getFormat().setLineSeparator("\n");
        settings.setProcessor(rowProcessor);
        settings.setHeaderExtractionEnabled(true);

        CsvParser parser = new CsvParser(settings);

        parser.parse(reader);
        List<Patient> pats = rowProcessor.getBeans();
        for (Patient pat : pats) {
            System.out.println(pat.getNhiNumber() + " - " + pat.getBirth());
        }
    }

    public static void main(String[] argv) {
        try {
            FileReader reader = new FileReader("test.csv");
            parse(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
