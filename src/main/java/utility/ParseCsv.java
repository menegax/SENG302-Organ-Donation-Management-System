
package utility;
import model.CSVUser;
import org.csveed.api.CsvClient;
import org.csveed.api.CsvClientImpl;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;


public class ParseCsv {
    private static final String SAMPLE_CSV_FILE_PATH = "";

    public static void main(String[] args) throws IOException {
        Reader reader = new FileReader(SAMPLE_CSV_FILE_PATH);
        CsvClient<CSVUser> csvClient = new CsvClientImpl<CSVUser>(reader, CSVUser.class);
        //System.out.println(csvClient.readRows());
        final List<CSVUser> beans = csvClient.readBeans();

        for (CSVUser bean : beans) {
            System.out.println(bean.getFirstname());
        }
    }
}