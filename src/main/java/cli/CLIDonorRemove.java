package cli;

import model.Donor;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import service.Database;

import java.io.InvalidObjectException;
import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

@SuppressWarnings("unused")
@Command(name = "remove", description = "used to remove existing donors")
class CLIDonorRemove implements Runnable {

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Displays this help message.")
    private boolean helpRequested = false;

    @Option(names = {"-i", "--ird"}, required = true, description = "The IRD number of the donor.")
    private int ird;

    public void run() {
        try {
            Donor donor = Database.getDonorByIrd(ird);
            Database.removeDonor(ird);
            userActions.log(Level.INFO, "Successfully removed " + donor);
        } catch (InvalidObjectException e) {
            userActions.log(Level.SEVERE, e.getMessage());
        }
    }

}
