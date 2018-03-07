package cli;

import model.Donor;
import picocli.CommandLine.Option;
import picocli.CommandLine.Command;
import service.Database;
import utility.GlobalEnums.Organ;

import java.io.InvalidObjectException;
import java.util.ArrayList;

@SuppressWarnings({"unused"})
@Command(name = "donations", description = "used to update the donations on a particular donor")
public class CLIDonorDonations implements Runnable {

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Displays this help message and quits.")
    private boolean helpRequested = false;

    @Option(names = {"-i", "--ird"}, required = true, description = "Search donor by the IRD number.")
    private int searchIrd;

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
        if (donations == null)
            System.out.println("No donations registered for donor: " + donor.getNameConcatenated());
        else System.out.println(donations);
    }

    public void run() {
        try {
            Donor donor = Database.getDonorByIrd(searchIrd);
            if (donationsRequested) {
                displayDonorDonations(donor);
            }
            else {
               donor.updateDonations(donor, newDonations, rmDonations);
            }
        } catch (InvalidObjectException e) {
            System.out.println(e.getMessage());
        }
    }

}
