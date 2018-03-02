package cli;

import picocli.CommandLine;

@CommandLine.Command(name = "odms", subcommands = {CLIDonor.class})
public class CLIOdms implements Runnable {

    @CommandLine.Option(names = {"-h", "--help"}, usageHelp = true, description = "Displays this help message and quits.")
    private boolean helpRequested = false;

    @Override
    public void run() {

    }
}
