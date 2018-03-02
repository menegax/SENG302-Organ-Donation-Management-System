package cli;
import picocli.CommandLine;
@CommandLine.Command(name = "odms", subcommands= {CLIDonor.class})
public class CLIODMS implements Runnable{

    @Override
    public void run() {

    }
}
