package cli;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import service.Database;

import java.io.InvalidObjectException;
import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

@SuppressWarnings("unused")
@Command(name = "view", description = "used to view donor attributes")
public class CLIDonorView implements Runnable {

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Displays this help message and quits.")
    private boolean helpRequested = false;

    @Option(names = {"-i", "--ird"}, description = "Search donor by the IRD number of the donor.")
    private int searchIrd;

    @Option(names = {"-a", "--all"}, description = "View all donors")
    private boolean searchAll;

    public void run() {
        if (searchIrd != 0) {
            try {
                userActions.log(Level.INFO, Database.getDonorByIrd(searchIrd).toString());
            } catch (InvalidObjectException e) {
                userActions.log(Level.SEVERE, e.getMessage());
            }
        }
        if (searchAll) {
            if (Database.getDonors().size() == 0) {
                userActions.log(Level.INFO, "No donors in the database");
            } else {
                userActions.log(Level.WARNING, Database.getDonors().toString());
            }
        }
        if (searchIrd == 0 && !searchAll) {
            userActions.log(Level.INFO, "donor view command invoked. Use donor view -h for help.");
        }
    }

}
