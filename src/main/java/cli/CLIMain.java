package cli;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import picocli.CommandLine;
import utility.SystemLogger;
import utility.UserActionHistory;
import java.io.IOException;
import java.util.logging.Level;

import static utility.SystemLogger.systemLogger;
import static utility.UserActionHistory.userActions;

@SuppressWarnings("ConstantConditions")
public class CLIMain {

    private static LineReader getLineReader(){
        try {
            TerminalBuilder builder = TerminalBuilder.builder();
            Terminal terminal = builder.build();
            return LineReaderBuilder.builder().terminal(terminal).build();
        } catch (IOException e) {
            systemLogger.log(Level.SEVERE, "Unable to start LineReader", "Attempted to begin CLI application");
            System.exit(1);
        }
        return null;
    }

    public static void main(String[] argv) {
        String[] args;

        UserActionHistory.setup();
        SystemLogger.setup();

        LineReader reader = getLineReader();
        String userCommand;
        new CommandLine(new CLIOdms()).parseWithHandler(new CommandLine.RunLast(), System.err, "-h");
        userCommand = reader.readLine();
        while (!userCommand.trim().equals("quit")) {
            args = userCommand.split(" ");
            new CommandLine(new CLIOdms()).parseWithHandler(new CommandLine.RunLast(), System.err, args);
            userCommand = reader.readLine();
        }
    }
}
