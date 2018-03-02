package cli;
import picocli.CommandLine.Option;
import picocli.CommandLine.Command;


@Command(name = "donor", subcommands = CLIDonorAdd.class)
public class CLIDonor implements Runnable {
    @Option(names = { "-h", "--help" }, usageHelp = true, description = "Displays this help message and quits.")
    private boolean helpRequested = false;

    public void run() {

    }

}


