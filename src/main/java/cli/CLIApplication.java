package cli;
import picocli.CommandLine;
import java.util.Scanner;

public class CLIApplication {

    private Scanner scannerInput;
    private String userCommand;

    public CLIApplication(){
        scannerInput = new Scanner(System.in);
        userCommand = "";
    }

    public void awaitUserCommand(){
        String[] args;
        while(true){
            userCommand = scannerInput.nextLine();
            args = userCommand.split(" " );
            CommandLine.run(new DonorCLI(), System.out, args);
        }
    }

    public static void main(String[] argv){
        CLIApplication app = new CLIApplication();
        app.awaitUserCommand();
    }
}
