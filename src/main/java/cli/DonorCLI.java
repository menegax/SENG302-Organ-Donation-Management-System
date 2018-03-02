package cli;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Command;

import java.util.Scanner;


@Command(name = "donor", subcommands = DonorAdd.class)
public class DonorCLI implements Runnable {
    @Option(names = { "-h", "--help" }, usageHelp = true, description = "Displays this help message and quits.")
    private boolean helpRequested = false;

    public void run() {

    }

}


