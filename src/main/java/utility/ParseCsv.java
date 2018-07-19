package utility;

import com.j256.simplecsv.processor.CsvProcessor;
import model.Patient;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public class ParseCsv {

    @SuppressWarnings("unchecked") // casting of type to class
    public List<Patient> parse(String filePath) {
        CsvProcessor<Patient> csvProcessor = new CsvProcessor<Patient>(Patient.class);
        File csvFile = new File(filePath);
        try {
            return csvProcessor.readAll(csvFile,null );
        } catch (IOException | ParseException e) {
            return null;
        }
    }

    public static void main(String[] argv) {
        ParseCsv parseCsv = new ParseCsv();
        System.out.println(parseCsv.parse("C:\\Users\\Hayden Taylor\\Downloads\\testCSV.csv"));
    }

}
