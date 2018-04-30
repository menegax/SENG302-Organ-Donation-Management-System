package cli;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

@SuppressWarnings("unused")
@Command(name = "donor", subcommands = {CLIPatientAdd.class, CLIPatientRemove.class, CLIPatientUpdate.class, CLIPatientView.class, CLIPatientDonations.class}, description = "Manages donor objects in the database")
public class CLIPatient implements Runnable {

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Displays this help message and quits.")
    private boolean helpRequested;

    public void run() {
        userActions.log(Level.FINE, "use donor -h for help", "donor command invoked");
    }

}
