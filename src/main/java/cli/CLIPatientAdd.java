package cli;

import model.Patient;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import service.Database;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

@SuppressWarnings("unused")
@Command(name = "add", description = "used to add new patients")
class CLIPatientAdd implements Runnable {

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Displays this help message.")
    private boolean helpRequested = false;

    @Option(names = {"-n", "--nhi"}, required = true, description = "The NHI number of the patient.")
    private String nhi;

    @Option(names = {"-f", "--firstname"}, required = true, description = "The first name of the patient.")
    private String firstName;

    @Option(names = {"-m", "--middlenames"}, split = ",", description = "Comma-separated list of middle names of the patient.")
    private ArrayList<String> middleNames;

    @Option(names = {"-l", "--lastname"}, required = true, description = "The last name of the patient.")
    private String lastName;

    @Option(names = {"-b", "--dateofbirth"}, required = true, description = "The date of birth of the patient (yyyy-mm-dd).")
    private LocalDate birth;

    Database database = Database.getDatabase();

    public void run() {
        Patient patient = new Patient(nhi, firstName, middleNames, lastName, birth);
        try {
            database.addPatient(patient);
        } catch(IllegalArgumentException i){
            userActions.log(Level.SEVERE, i.getMessage(), "attempted to add patient");
        }
    }

}
