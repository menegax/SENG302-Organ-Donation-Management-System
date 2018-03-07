package cli;

import picocli.CommandLine.Option;
import picocli.CommandLine.Command;
import service.Database;

@SuppressWarnings("unused")
@Command(name = "import")
public class CLIImport implements Runnable {

    @Option(names = {"-f", "--file"}, required = true, description = "the file name you wish to import") String fileName;

    public void run() {
        System.out.print("Importing...");
        Database.importDonors(fileName);
        System.out.println("Done.");
    }

}
