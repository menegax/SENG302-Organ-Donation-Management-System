package cli;

import model.Donor;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import service.Database;

import java.io.InvalidObjectException;

@SuppressWarnings("unused")
@Command(name = "remove", description = "used to remove existing donors")
class CLIDonorRemove implements Runnable {

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Displays this help message.")
    private boolean helpRequested = false;

    @Option(names = {"-n", "--nhi"}, required = true, description = "The NHI number of the donor.")
    private String nhi;

    public void run() {
        try {
            Donor donor = Database.getDonorByNhi(nhi);
            Database.removeDonor(nhi);
            System.out.println("Successfully removed " + donor);
        } catch (InvalidObjectException e) {
            System.out.println(e.getMessage());
        }
    }

}
