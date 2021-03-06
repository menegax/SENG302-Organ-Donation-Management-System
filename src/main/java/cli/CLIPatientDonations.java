package cli;

import model.Patient;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import service.PatientDataService;
import service.interfaces.IPatientDataService;
import utility.GlobalEnums.Organ;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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

    private IPatientDataService patientDataService = new PatientDataService();

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
        Set<Organ> donations = patient.getDonations().keySet();
        userActions.log(Level.INFO, donations.toString(), "attempted to display patient donations");
    }

    public void run() {
        Patient patient = patientDataService.getPatientByNhi(searchNhi);
        if (patient != null) {
            if (donationsRequested) {
                displayPatientDonations(patient);
            } else {
                Map<Organ, String> newDonationMap = new HashMap<>();
                newDonations.forEach(s -> newDonationMap.put(Organ.getEnumFromString(s), null));
                Map<Organ, String> rmDonationMap = new HashMap<>();
                rmDonations.forEach(s -> rmDonationMap.put(Organ.getEnumFromString(s), null));
                patient.updateDonations(newDonationMap, rmDonationMap);
                patientDataService.save(patient);
            }
        } else {
            userActions.log(Level.SEVERE, "Patient " + searchNhi + " not found.", "attempted to view or update patient donations");
        }
    }

}
