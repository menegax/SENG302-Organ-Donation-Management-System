package cli;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import service.Database;

import java.io.InvalidObjectException;
import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

@SuppressWarnings("unused")
@Command(name = "view", description = "used to view patient attributes")
public class CLIPatientView implements Runnable {

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Displays this help message and quits.")
    private boolean helpRequested = false;

    @Option(names = {"-n", "--nhi"}, description = "Search patient by the NHI number of the patient.")
    private String searchNhi;

    @Option(names = {"-a", "--all"}, description = "View all patients")
    private boolean searchAll;

    public void run() {
        if (searchNhi != null) {
            try {
                userActions.log(Level.INFO, Database.getPatientByNhi(searchNhi).toString(), "attempted to view a particular patient");
            } catch (InvalidObjectException e) {
                userActions.log(Level.SEVERE, e.getMessage(), "attempted to view a particular patient");
            }
        }
        if (searchAll) {
            if (Database.getPatients().size() == 0) {
                userActions.log(Level.INFO, "No patient in the database", "attempted to view all patients");
            } else {
                userActions.log(Level.WARNING, Database.getPatients().toString(), "attempted to view all patients");
            }
        }
        if (searchNhi == null && !searchAll) {
            userActions.log(Level.INFO, "use patient view -h for help", "patient view command invoked");
        }
    }

}
