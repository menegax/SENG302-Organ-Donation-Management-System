package cli;
import jline.console.ConsoleReader;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import picocli.CommandLine;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Scanner;

public class CLIMain {

    public static void main(String[] argv) {
        String[] args;

        // setup keyboard listener
        /**try {
            GlobalScreen.registerNativeHook();
        }
        catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());

            System.exit(1);
        }

        GlobalScreen.addNativeKeyListener(new CLIKeyboardListener());**/
        try {
            ConsoleReader consoleReader = new ConsoleReader();
            consoleReader.addTriggeredAction('a', new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("a press");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        // listen for input
        Scanner inputScanner = new Scanner(System.in);
        String userCommand;
        new CommandLine(new CLIOdms()).parseWithHandler(new CommandLine.RunLast(), System.err, "-h");
        userCommand = inputScanner.nextLine();
        while (!userCommand.trim().equals("quit")) {
            args = userCommand.split(" ");
            new CommandLine(new CLIOdms()).parseWithHandler(new CommandLine.RunLast(), System.err, args);
            userCommand = inputScanner.nextLine();
        }
    }

}
