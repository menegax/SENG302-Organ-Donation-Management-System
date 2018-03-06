package cli;

import picocli.CommandLine.Command;
import service.Database;

@SuppressWarnings("unused")
@Command(name = "save")
public class CLISave implements Runnable {

    public void run() {
        System.out.print("Saving...");
        Database.saveToDisk();
        System.out.println(" done.");
    }

}
