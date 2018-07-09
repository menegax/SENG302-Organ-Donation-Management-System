package cli;

import model.Patient;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import service.Database;

import java.io.InvalidObjectException;
import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

@SuppressWarnings("unused")
@Command(name = "remove", description = "used to remove existing patients")
class CLIPatientRemove implements Runnable {

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Displays this help message.")
    private boolean helpRequested = false;

    @Option(names = {"-n", "--nhi"}, required = true, description = "The NHI number of the patient.")
    private String nhi;

    Database database = Database.getDatabase();

    public void run() {
        Patient patient = database.getPatientByNhi(nhi);
        database.delete(patient);
    }

}
