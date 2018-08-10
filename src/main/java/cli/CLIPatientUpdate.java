package cli;

import model.Patient;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import service.PatientDataService;
import service.interfaces.IPatientDataService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

@SuppressWarnings("unused")
@Command(name = "update", description = "used to update patient attributes")
public class CLIPatientUpdate implements Runnable {

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Displays this help message and quits.")
    private boolean helpRequested = false;

    @Option(names = {"-s", "--search"}, required = true, description = "Search patient by the NHI number of the patient.")
    private String searchNhi;

    @Option(names = {"-f", "--firstname"}, description = "The first name of the patient.")
    private String firstName;

    @Option(names = {"-m", "--middlenames"}, split = ",", description = "Comma-separated list of middle names of the patient.")
    private ArrayList<String> middleNames;

    @Option(names = {"-l", "--lastname"}, description = "The last name of the patient.")
    private String lastName;

    @Option(names = {"-p", "--preferredname"}, description = "The preferred name of the patient.")
    private String preferredName;

    @Option(names = {"-b", "--dateofbirth"}, description = "The date of birth of the patient (yyyy-mm-dd).")
    private LocalDate birth;

    @Option(names = {"-d", "--dateofdeath"}, description = "The date of death of the patient (yyyy-mm-dd).")
    private LocalDateTime death;

    @Option(names = {"--birthGender"}, description = "The birth gender of the patient. Choose one from: \n" +
            "FEMALE, MALE")
    private String birthGender;

    @Option(names = {"--preferredGender"}, description = "The preferred gender of the patient. Choose one from: \n" +
            "MAN, WOMAN, NON-BINARY")
    private String preferredGender;

    @Option(names = {"--height"}, description = "The height of the patient (cm).")
    private double height;

    @Option(names = {"--weight"}, description = "The weight of the patient (kg).")
    private double weight;

    @Option(names = {"--street1"}, description = "The street1 field for the address of the patient.")
    private String street1;

    @Option(names = {"--street2"}, description = "The street2 field for the address of the patient.")
    private String street2;

    @Option(names = {"--suburb"}, description = "The suburb field for the address of the patient.")
    private String suburb;

    @Option(names = {"--region"}, description = "The region of the patient. Choose one from: \n" +
            "NORTHLAND, AUCKLAND, WAIKATO, BAYOFPLENTY,\n" +
            "GISBORNE, HAWKESBAY, TARANAKI, MANAWATU,\n" +
            "WELLINGTON, TASMAN, NELSON, MARLBOROUGH,\n" +
            "WESTCOAST, CANTERBURY, OTAGO, SOUTHLAND")
    private String region;


    @Option(names = {"--zip"}, description = "The zip field for the address of the patient.")
    private int zip;

    @Option(names = {"--nhi"}, description = "The NHI number of the patient.")
    private String nhi;

    @Option(names = {"--bloodgroup"}, description = "The blood group of the patient. Choose one from:\n" +
            "A_POSITIVE, A_NEGATIVE, B_POSITIVE, B_NEGATIVE,\n" +
            "AB_POSITIVE, AB_NEGATIVE, O_POSITIVE, O_NEGATIVE")
    private String bloodGroup;

    private IPatientDataService patientDataService = new PatientDataService();

    public void run() {
        Patient patient = patientDataService.getPatientByNhi(searchNhi);
        if (patient != null) {
            patient.updateAttributes(firstName, lastName, middleNames, preferredName, birth, death, street1,
                    street2, suburb, region, birthGender, preferredGender, bloodGroup, height, weight, nhi);
            patientDataService.save(patient);
        } else {
            userActions.log(Level.SEVERE, "Patient " + searchNhi + " not found.", "attempted to update patient attributes");
        }
    }

}
