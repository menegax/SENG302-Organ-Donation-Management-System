package cli;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import service.Database;

import java.io.InvalidObjectException;

@SuppressWarnings("unused")
@Command(name = "view", description = "used to view donor attributes")
public class CLIDonorView implements Runnable {

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Displays this help message and quits.")
    private boolean helpRequested = false;

    @Option(names = {"-n", "--nhi"}, description = "Search donor by the NHI number of the donor.")
    private String searchNhi;

    @Option(names = {"-a", "--all"}, description = "View all donors")
    private boolean searchAll;

    public void run() {
        if (!searchNhi.equals("")) {
            try {
                System.out.println(Database.getDonorByNhi(searchNhi));
            } catch (InvalidObjectException e) {
                System.out.println(e.getMessage());
            }
        }
        if (searchAll) {
            System.out.println((Database.getDonors().size() == 0 ? "No donors in the database" : Database.getDonors()));
        }
        if (searchNhi.equals("") && !searchAll) {
            System.out.println("donor view command invoked. Use donor view -h for help.");
        }
    }

}
