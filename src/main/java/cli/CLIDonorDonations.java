package cli;

import model.Donor;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import service.Database;
import utility.GlobalEnums.Organ;

import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

@SuppressWarnings({"unused"})
@Command(name = "donations", description = "used to update the donations on a particular donor")
public class CLIDonorDonations implements Runnable {

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Displays this help message and quits.")
    private boolean helpRequested = false;

    @Option(names = {"-n", "--nhi"}, required = true, description = "SearchDonors donor by the NHI number.")
    private String searchNhi;

    @Option(names = {"-l", "--list"}, description = "Lists current organ donations.")
    private boolean donationsRequested;

    @Option(names = "--add", split = ",", description = "Takes a comma-separated list of organs to add to donations.\n" +
            "LIVER\n" +
            "KIDNEY\n" +
            "PANCREAS\n" +
            "HEART\n" +
            "LUNG\n" +
            "INTESTINE\n" +
            "CORNEA\n" +
            "MIDDLEEAR\n" +
            "SKIN\n" +
            "BONE\n" +
            "BONE_MARROW\n" +
            "CONNECTIVETISSUE")
    private ArrayList<String> newDonations;

    @Option(names = {"-rm", "--remove"}, split = ",", description = "Takes a comma-separated list of organs to remove from donations.")
    private ArrayList<String> rmDonations;

    private void displayDonorDonations(Donor donor) {
        ArrayList<Organ> donations = donor.getDonations();
        if (donations == null) {
            userActions.log(Level.WARNING, "No donations registered for donor: " + donor.getNameConcatenated(), "attempted to display donor donations");
        }
        else {
            userActions.log(Level.INFO, donations.toString(), "attempted to display donor donations");
        }
    }

    public void run() {
        try {
            Donor donor = Database.getDonorByNhi(searchNhi);
            if (donationsRequested) {
                displayDonorDonations(donor);
            }
            else {
               donor.updateDonations(newDonations, rmDonations);
            }
        } catch (InvalidObjectException e) {
            userActions.log(Level.SEVERE, e.getMessage(), "attempted to view or update donor donations");
        }
    }

}
