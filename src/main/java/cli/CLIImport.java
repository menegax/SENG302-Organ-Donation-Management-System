package cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import service.Database;

import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

@SuppressWarnings("unused")
@Command(name = "import", description = "Reads and loads data into the application")
public class CLIImport implements Runnable {

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Displays this help message and quits.")
    private boolean helpRequested = false;

    @Option(names = {"-f", "--file"}, required = true, description = "the file name you wish to import i.e. import -f=doc/examples/example_patient.json")
    private String fileName;

    public void run() {
        Database.importFromDisk(fileName);
        userActions.log(Level.INFO, "successfully imported", "attempting to import");
    }

}
