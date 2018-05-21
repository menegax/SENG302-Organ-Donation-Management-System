package cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import service.Database;

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
        database.importFromDiskPatients(fileName);
        userActions.log(Level.INFO, "Successfully imported", "Attempted to import patients");
    }

}
