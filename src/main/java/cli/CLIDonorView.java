package cli;

import model.Donor;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import service.Database;
import utility.GlobalEnums;

import java.io.InvalidObjectException;

@SuppressWarnings("unused")
@Command(name = "view", description = "used to view donor attributes")
public class CLIDonorView implements Runnable {


    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Displays this help message.")
    private boolean helpRequested = false;

    @Option(names = {"-s", "--search"}, required = true, description = "Search donor by the IRD number of the donor.")
    private int searchIrd;


    public void run() {
        System.out.println("*** Results of Search ***");
        try {
            System.out.println(Database.getDonorByIrd(searchIrd));
        } catch (InvalidObjectException e) {
            System.out.println(e.getMessage());
        }
    }

}
