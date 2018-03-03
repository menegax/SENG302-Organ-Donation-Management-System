package cli;

import picocli.CommandLine.Option;
import picocli.CommandLine.Command;
import utility.GlobalEnums.Organ;

import java.util.ArrayList;

@SuppressWarnings("unused")
@Command(name = "donations")
public class CLIDonorUpdateDonations implements Runnable {

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Displays this help message.")
    private boolean helpRequested = false;

    @Option(names = {"-i", "--ird"}, required = true, description = "Search donor by the IRD number of the donor.")
    private int searchIrd;

    @Option(names = {"-l", "--list"}, description = "Lists current organ donations.")
    private boolean donationsRequested = false;

    @Option(names = "--add", description = "Takes a list of organs to add to donations") //todo add list of available organs
    private ArrayList<Organ> newDonations;

    @Option(names = "-rm", description = "Takes a list of organs to remove from donations") //todo add list of available organs
    private ArrayList<Organ> rmDonations;

    public void run() {
        // todo add the meat here bro

    }

}
