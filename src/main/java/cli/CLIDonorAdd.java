package cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.Arrays;

@Command(name = "add", description="used to add new donors")
class CLIDonorAdd implements Runnable {
    @Option(names = {"-f", "--firstname"}, required = true, description = "The first name of the donor.")
    private String firstName;

    @Option(names = {"-m", "--middlenames"}, required = false, description = "The middle names of the donor.")
    private String[] middleNames;

    @Option(names = {"-l", "--lastname"}, required = true, description = "The last name of the donor.")
    private String lastName;

    public void run() {
        //        Donor newDonor = new Donor(firstName, middleNames, lastName, );
        System.out.println("firstName: " + firstName + " " +
                "middleNames: " + Arrays.toString(middleNames) + " " +
                "lastName: " + lastName);
    }

}
