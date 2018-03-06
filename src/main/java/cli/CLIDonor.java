package cli;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@SuppressWarnings("unused")
@Command(name = "donor", subcommands = {CLIDonorAdd.class, CLIDonorUpdate.class, CLIDonorView.class, CLIDonorDonations.class})
public class CLIDonor implements Runnable {

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Displays this help message and quits.")
    private boolean helpRequested = false;

    public void run() {
        // todo make it display usage message when invoked without a subcommand
    }

}
