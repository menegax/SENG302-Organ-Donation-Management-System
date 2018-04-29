package cli;

import model.Donor;
import model.Human;
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

    @Option(names = {"-n", "--nhi"}, required = true, description = "The NHI number of the donor.")
    private String nhi;

    public void run() {
        try {
            Human donor = Database.getDonorByNhi(nhi);
            Database.removeDonor(nhi);
            userActions.log(Level.INFO, "Successfully removed donor " + donor.getNhiNumber(), "attempted to remove donor");
        } catch (InvalidObjectException e) {
            userActions.log(Level.SEVERE, e.getMessage(), "attempted to remove donor");
        }
    }

}
