package cli;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import picocli.CommandLine;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class CLIMain {

    public static void main(String[] argv) {
        String[] args;
        try {
            TerminalBuilder builder = TerminalBuilder.builder();
            Terminal terminal = builder.build();
            LineReader reader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .build();

            String userCommand;
            new CommandLine(new CLIOdms()).parseWithHandler(new CommandLine.RunLast(), System.err, "-h");
            userCommand = reader.readLine();
            System.out.println(userCommand);
            while (!userCommand.trim().equals("quit")) {
                args = userCommand.split(" ");
                new CommandLine(new CLIOdms()).parseWithHandler(new CommandLine.RunLast(), System.err, args);
                userCommand = reader.readLine(">>");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
