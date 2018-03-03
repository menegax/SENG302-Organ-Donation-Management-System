package cli;

import picocli.CommandLine.Option;
import picocli.CommandLine.Command;

@SuppressWarnings("unused")
@Command(name = "donor", subcommands = {CLIDonorAdd.class, CLIDonorUpdate.class})
public class CLIDonor implements Runnable {

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Displays this help message.")
    private boolean helpRequested = false;

    public void run() {

    }

}


