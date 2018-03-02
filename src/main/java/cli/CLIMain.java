package cli;

import picocli.CommandLine;

import java.util.Scanner;

public class CLIMain {

    public static void main(String[] argv) {
        String[] args;
        System.out.println("WELCOME TO ODMS");

        // listen for input
        Scanner inputScanner = new Scanner(System.in);
        String userCommand = "";
        userCommand = inputScanner.nextLine();
        while(!userCommand.trim().equals("quit")){
            args = userCommand.split(" ");
            new CommandLine(new CLIOdms()).parseWithHandler(new CommandLine.RunLast(), System.err, args);
            userCommand = inputScanner.nextLine();
        }
    }

}
