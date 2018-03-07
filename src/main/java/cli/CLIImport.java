package cli;

import picocli.CommandLine.Option;
import picocli.CommandLine.Command;
import service.Database;

@SuppressWarnings("unused")
@Command(name = "import")
public class CLIImport implements Runnable {

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Displays this help message and quits.")
    private boolean helpRequested = false;

    @Option(names = {"-f", "--file"}, required = true, description = "the file name you wish to import i.e. import -f=doc/examples/example_donor.json")
    private String fileName;

    public void run() {
        System.out.print("Importing...");
        Database.importFromDisk(fileName);
        System.out.println("Done.");
    }

}
