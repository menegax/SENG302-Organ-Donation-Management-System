package cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import service.Database;

import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

@SuppressWarnings("unused")
@Command(name = "save", description = "Saves data to disk. Changes will not be saved without this command.")
public class CLISave implements Runnable {

    @CommandLine.Option(names = {"-h", "--help"}, usageHelp = true, description = "Displays this help message and quits.")
    private boolean helpRequested = false;

    public void run() {
        Database.saveToDisk();
        userActions.log(Level.INFO, "successfully saved", "attempted to save data");
    }

}
