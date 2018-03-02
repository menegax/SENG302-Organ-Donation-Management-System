package cli;

import picocli.CommandLine;

import java.util.Scanner;

public class CLIMain {

    public static void main(String[] argv) {
        String[] args;
        System.out.println("+  xxxxx   xxxxxxx  xx       xx    xxxxxxxxxx" + "\n" +
                           "| x     x  x     xx xxx     xxx    x" + "\n" +
                           "| x     x  x      x x xx    x xx   xx" + "\n" +
                           "| x     x  x      x x   xx xx  x    xxxxxxxx" + "\n" +
                           "| x     x  x     xx x    xxx   xx          xx" + "\n" +
                           "|  xxxxx   xxxxxxx  x     xx    xx xxxxxxxxxx" + "\n" +
                           "+----------------------------------------------+" + "\n"
        );

        // listen for input
        Scanner inputScanner = new Scanner(System.in);
        String userCommand;
        new CommandLine(new CLIOdms()).parseWithHandler(new CommandLine.RunLast(), System.err, "-h");
        userCommand = inputScanner.nextLine();
        while(!userCommand.trim().equals("quit")){
            args = userCommand.split(" ");
            new CommandLine(new CLIOdms()).parseWithHandler(new CommandLine.RunLast(), System.err, args);
            userCommand = inputScanner.nextLine();
        }
    }

}
