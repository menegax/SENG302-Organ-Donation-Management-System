import cli.CLIMain;
import cli.CLIOdms;
import controller.Main;
import org.jline.reader.LineReader;
import picocli.CommandLine;
import utility.UserActionHistory;

import java.util.Arrays;
import java.util.List;

public class App {

    public static void main(String[] argv) {
        String[] args;

        List<String> argArrayList = Arrays.asList(argv);


        if (argArrayList.contains("cli")) {
            CLIMain.main(null);
        } else {
            Main.main(null);
        }
    }
}
