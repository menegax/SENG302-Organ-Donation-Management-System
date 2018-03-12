package cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import service.Database;

@SuppressWarnings("unused")
@Command(name = "save", description = "Saves data. Data on disk will not be altered without using this command.")
public class CLISave implements Runnable {

    @CommandLine.Option(names = {"-h", "--help"}, usageHelp = true, description = "Displays this help message and quits.")
    private boolean helpRequested = false;

    public void run() {
        System.out.print("Saving...");
        Database.saveToDisk();
        System.out.println(" done.");
    }

}
