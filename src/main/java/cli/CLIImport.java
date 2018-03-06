package cli;

import picocli.CommandLine.Command;
import service.Database;

@SuppressWarnings("unused")
@Command(name = "import")
public class CLIImport implements Runnable {

    public void run() {
        //todo
        System.out.println("import command invoked");
    }

}
