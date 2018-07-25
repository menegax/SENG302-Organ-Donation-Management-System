package cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import service.Database;
import utility.parsing.ParseCSV;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

@SuppressWarnings("unused")
@Command(name = "import", description = "Reads and loads patient data into the application")
public class CLIImport implements Runnable {

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Displays this help message and quits.")
    private boolean helpRequested = false;

    @Option(names = {"-f", "--file"}, required = true, description = "the file name you wish to import i.e. import -f=doc/examples/example_patient.json")
    private String fileName;

    Database database = Database.getDatabase();

    public void run() {
        if (fileName.endsWith(".csv")) {
            try {
            Reader reader = new FileReader(fileName);
            ParseCSV parseCSV = new ParseCSV();
            parseCSV.parse(reader);
            } catch (FileNotFoundException e) {
                userActions.log(Level.SEVERE, "File doesn't exist", "Attempted to import patients");
            }
            userActions.log(Level.INFO, "Successfully imported", "Attempted to import patients");
        } else if (fileName.endsWith(".json")) {
            Database.getDatabase().importFromDiskPatients(fileName);
        } else {
            userActions.log(Level.SEVERE, "Unsupported file typeim", "Attempted to import patients");
        }
    }

}
