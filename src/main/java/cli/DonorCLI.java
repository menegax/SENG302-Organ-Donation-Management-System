package cli;

import model.Donor;
import cli.DonorAdd;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Command;


@Command(name = "donor", subcommands = DonorAdd.class)
public class DonorCLI implements Runnable {
    @Option(names = { "-h", "--help" }, usageHelp = true, description = "Displays this help message and quits.")
    private boolean helpRequested = false;

    public void run() {

    }

    public static void main(String[] args) {
        CommandLine.run(new DonorCLI(), System.out, args);
    }

}


