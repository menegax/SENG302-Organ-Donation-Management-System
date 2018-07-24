package utility.parsing;

import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import model.Patient;
import service.Database;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.*;

public class ParseCSV {

    public enum Result {
        SUCCESS,
        FAIL
    }

    public Map<Result, List> parse(Reader reader) {
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
      //  results.get(Result.SUCCESS).forEach(x -> Database.addPatient((Patient) x));
        return results;
    }
}
