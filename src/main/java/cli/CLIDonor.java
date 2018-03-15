package cli;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

@SuppressWarnings("unused")
@Command(name = "donor", subcommands = {CLIDonorAdd.class, CLIDonorRemove.class, CLIDonorUpdate.class, CLIDonorView.class, CLIDonorDonations.class}, description = "Manages donor objects in the database")
public class CLIDonor implements Runnable {

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Displays this help message and quits.")
    private boolean helpRequested;

    public void run() {
        userActions.log(Level.FINE, "donor command invoked. Use donor -h for help.");
    }

}
