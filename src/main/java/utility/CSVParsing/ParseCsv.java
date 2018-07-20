package utility.CSVParsing;

import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import model.Patient;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ParseCsv {

    public enum Result {
        SUCCESS,
        FAIL
    }

    public static Map parse(FileReader reader) {
        BeanListProcessor<Patient> rowProcessor = new BeanListProcessor<>(Patient.class);
        CsvParserSettings settings = new CsvParserSettings();
        settings.getFormat().setLineSeparator("\n");
        settings.setProcessor(rowProcessor);
        settings.setHeaderExtractionEnabled(true);

        List<List<String>> errors = new ArrayList<>();
        settings.setProcessorErrorHandler((e , objects, context) ->{
            List<String> rowError = new ArrayList<>();
            Arrays.asList(objects).forEach((obj) -> {
                if (obj != null) {
                    rowError.add(obj.toString());
                }
            });
            errors.add(rowError);
        });

        CsvParser parser = new CsvParser(settings);
        parser.parse(reader);
        List<Patient> pats = rowProcessor.getBeans();

        Map<Result, List> results = new HashMap<>();
        results.put(Result.SUCCESS, pats);
        results.put(Result.FAIL, errors);
        return results;
    }

    public static void main(String[] argv) {
        try {
            FileReader reader = new FileReader("C:\\Users\\Hayden Taylor\\Downloads\\testCSV.csv");
            parse(reader);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
