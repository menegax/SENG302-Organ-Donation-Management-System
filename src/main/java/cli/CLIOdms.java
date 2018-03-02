package cli;
import picocli.CommandLine;
@CommandLine.Command(name = "odms", subcommands= {CLIDonor.class})
public class CLIOdms implements Runnable{

    @Override
    public void run() {

    }
}
