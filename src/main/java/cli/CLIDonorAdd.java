package cli;
import model.Donor;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import service.Database;
import java.time.LocalDate;
import java.util.ArrayList;

@SuppressWarnings("unused")
@Command(name = "add", description = "used to add new donors")
class CLIDonorAdd implements Runnable {

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Displays this help message.")
    private boolean helpRequested = false;

    @Option(names = {"-ird", "--ird"}, required = true, description = "The IRD number of the donor.")
    private int ird;

    @Option(names = {"-f", "--firstname"}, required = true, description = "The first name of the donor.")
    private String firstName;

    @Option(names = {"-m", "--middlenames"}, split = ",", description = "Comma-separated list of middle names of the donor.")
    private ArrayList<String> middleNames;

    @Option(names = {"-l", "--lastname"}, required = true, description = "The last name of the donor.")
    private String lastName;

    @Option(names = {"-b", "--dateofbirth"}, required = true, description = "The date of birth of the donor (yyyy-mm-dd).")
    private LocalDate birth;

    public void run() {
        try{
            Database.addDonor(new Donor(ird,firstName, middleNames, lastName, birth));
            System.out.println("Successfully added new donor " + firstName + " " +
                    (middleNames == null ? "": middleNames.toString() + "") + lastName);
        }catch (IllegalArgumentException e){
            System.out.println(e.getMessage());
        }
    }

}
