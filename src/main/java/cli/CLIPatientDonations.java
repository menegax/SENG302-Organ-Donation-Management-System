package cli;

import model.Patient;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import service.Database;
import utility.GlobalEnums.Organ;

import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

@SuppressWarnings({"unused"})
@Command(name = "donations", description = "used to update the donations on a particular patient")
public class CLIPatientDonations implements Runnable {

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Displays this help message and quits.")
    private boolean helpRequested = false;

    @Option(names = {"-n", "--nhi"}, required = true, description = "SearchPatients patient by the NHI number.")
    private String searchNhi;

    @Option(names = {"-l", "--list"}, description = "Lists current organ donations.")
    private boolean donationsRequested;

    Database database = Database.getDatabase();

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
            "BONEMARROW\n" +
            "CONNECTIVETISSUE")
    private ArrayList<String> newDonations;

    @Option(names = {"-rm", "--remove"}, split = ",", description = "Takes a comma-separated list of organs to remove from donations.")
    private ArrayList<String> rmDonations;

    private void displayPatientDonations(Patient patient) {
        ArrayList<Organ> donations = patient.getDonations();
        if (donations == null) {
            userActions.log(Level.WARNING, "No donations registered for patient: " + patient.getNameConcatenated(), "attempted to display patient donations");
        }
        else {
            userActions.log(Level.INFO, donations.toString(), "attempted to display patient donations");
        }
    }

    public void run() {
        Patient patient = database.getPatientByNhi(searchNhi);
        if (patient != null) {
            if (donationsRequested) {
                displayPatientDonations(patient);
            }
            else {
               patient.updateDonations(newDonations, rmDonations);
            }
        } else {
            userActions.log(Level.SEVERE, "Patient " + searchNhi + " not found.", "attempted to view or update patient donations");
        }
    }

}
