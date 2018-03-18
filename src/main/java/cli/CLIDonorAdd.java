package cli;

import model.Donor;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import service.Database;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

@SuppressWarnings("unused")
@Command(name = "add", description = "used to add new donors")
class CLIDonorAdd implements Runnable {

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Displays this help message.")
    private boolean helpRequested = false;

    @Option(names = {"-n", "--nhi"}, required = true, description = "The NHI number of the donor.")
    private String nhi;

    @Option(names = {"-f", "--firstname"}, required = true, description = "The first name of the donor.")
    private String firstName;

    @Option(names = {"-m", "--middlenames"}, split = ",", description = "Comma-separated list of middle names of the donor.")
    private ArrayList<String> middleNames;

    @Option(names = {"-l", "--lastname"}, required = true, description = "The last name of the donor.")
    private String lastName;

    @Option(names = {"-b", "--dateofbirth"}, required = true, description = "The date of birth of the donor (yyyy-mm-dd).")
    private LocalDate birth;

    public void run() {
        Donor donor = new Donor(nhi, firstName, middleNames, lastName, birth);
        try {
            Database.addDonor(donor);
        } catch(IllegalArgumentException i){
            userActions.log(Level.SEVERE, i.getMessage());
        }
    }

}
