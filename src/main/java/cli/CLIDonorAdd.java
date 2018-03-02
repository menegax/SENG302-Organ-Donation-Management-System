package cli;

import model_test.Donor;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import service.Database;

import java.time.LocalDate;
import java.util.ArrayList;

@Command(name = "add", description="used to add new donors")
class CLIDonorAdd implements Runnable {
    @Option(names = {"-f", "--firstname"}, required = true, description = "The first name of the donor.")
    private String firstName;

    @Option(names = {"-m", "--middlenames"}, split = ",", required = false, description = "Comma-separated list of middle names of the donor.")
    private ArrayList<String> middleNames;

    @Option(names = {"-l", "--lastname"}, required = true, description = "The last name of the donor.")
    private String lastName;

    @Option(names = {"-d", "--dateofbirth"}, required = true, description = "The date of birth of the donor (yyyy-mm-dd).")
    private LocalDate birth;

    public void run() {
        // Donor newDonor = new Donor(firstName, middleNames, lastName, );
        System.out.println("firstName: " + firstName + " " +
                "middleNames: " + middleNames + " " +
                "lastName: " + lastName + " " +
                "birthDate: " + birth);

        Donor newDonor = new Donor(firstName, middleNames, lastName, birth);
        System.out.println(newDonor + "will be added to database");

        Database.addDonor(newDonor);

        System.out.println(Database.getDonors());

    }

}
