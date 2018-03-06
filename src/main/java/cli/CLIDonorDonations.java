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

    private void displayInformationMessages(ArrayList<String> messages) {
        for (String message : messages)
            System.out.println( message);
    }

    private void displayDonorDonations(Donor donor) {
        ArrayList<Organ> donations = donor.getDonations();
        if (donations == null)
            System.out.println("No donations registered for donor: " + donor.getNameConcatenated());
        else System.out.println(donations);
    }

    private ArrayList<String> updateDonations(Donor d) {
        ArrayList<String> informationMessages = new ArrayList<>();
        if (newDonations != null) {
            for (String organ : newDonations) {
                Organ organEnum = (Organ) Organ.getEnumFromString(organ); //null if invalid
                if (organEnum == null) {
                    informationMessages.add("Error: Invalid organ " + organ + "given, hence was not added.");
                }
                else {
                    informationMessages.add(d.addDonation(organEnum));
                }
            }
        }
        if (rmDonations != null) {
            for (String organ : rmDonations) {
                Organ organEnum = (Organ) Organ.getEnumFromString(organ);
                if (organEnum == null) {
                    informationMessages.add("Invalid organ " + organ + " given, hence was not added.");
                }
                else {
                    informationMessages.add(d.removeDonation(organEnum));
                }
            }
        }
        return informationMessages;
    }

    public void run() {
        try {
            Donor donor = Database.getDonorByIrd(searchIrd);
            if (donationsRequested) {
                displayDonorDonations(donor);
            }
            else {
                displayInformationMessages(updateDonations(donor));
            }
        } catch (InvalidObjectException e) {
            System.out.println(e.getMessage());
        }
    }

}
