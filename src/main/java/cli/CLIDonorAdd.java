package cli;

import model.Donor;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import service.Database;

import java.time.LocalDate;
import java.util.ArrayList;

@Command(name = "add", description = "used to add new donors")
class CLIDonorAdd implements Runnable {

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Displays this help message.")
    private boolean helpRequested = false;

    @Option(names = {"-f", "--firstname"}, required = true, description = "The first name of the donor.")
    private String firstName;

    @Option(names = {"-m", "--middlenames"}, split = ",", required = false, description = "Comma-separated list of middle names of the donor.")
    private ArrayList<String> middleNames;

    @Option(names = {"-l", "--lastname"}, required = true, description = "The last name of the donor.")
    private String lastName;

    @Option(names = {"-d", "--dateofbirth"}, required = true, description = "The date of birth of the donor (yyyy-mm-dd).")
    private LocalDate birth;

    public void run() {

        Database.addDonor(new Donor(firstName, middleNames, lastName, birth));

    }

}
