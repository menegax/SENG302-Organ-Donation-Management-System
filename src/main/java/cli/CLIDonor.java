package cli;
import picocli.CommandLine.Command;

@SuppressWarnings("unused")
@Command(name = "donor", subcommands = {CLIDonorAdd.class, CLIDonorUpdate.class, CLIDonorView.class, CLIDonorDonations.class})
public class CLIDonor implements Runnable {

    public void run() {

    }

}


