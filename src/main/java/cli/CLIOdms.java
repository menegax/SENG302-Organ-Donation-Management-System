package cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@SuppressWarnings("unused")
@Command(name = "odms", subcommands = {CLIDonor.class, CLISave.class, CLIImport.class}, //todo remove usage heading so only commands are listed i.e. no odms command is shown
        sortOptions = false,
        headerHeading = "+  @|(247,159,121)xxxxx|@   @|(247,208,138)xxxxxxx|@  @|(227,240,155)xx       xx|@    @|(135,182,167)xxxxxxxxxx|@" + "\n" +
                        "| @|(247,159,121)x     x|@  @|(247,208,138)x     xx|@ @|(227,240,155)xxx     xxx|@    @|(135,182,167)x|@" + "\n" +
                        "| @|(247,159,121)x     x|@  @|(247,208,138)x      x|@ @|(227,240,155)x xx    x xx|@   @|(135,182,167)xx|@" + "\n" +
                        "| @|(247,159,121)x     x|@  @|(247,208,138)x      x|@ @|(227,240,155)x   xx xx  x|@    @|(135,182,167)xxxxxxxx|@" + "\n" +
                        "| @|(247,159,121)x     x|@  @|(247,208,138)x     xx|@ @|(227,240,155)x    xxx   xx|@          @|(135,182,167)xx|@" + "\n" +
                        "|  @|(247,159,121)xxxxx|@   @|(247,208,138)xxxxxxx|@  @|(227,240,155)x     xx    xx|@ @|(135,182,167)xxxxxxxxxx|@" + "\n" +
                        "+----------------------------------------------+" + "\n",
        synopsisHeading = "%n",
        descriptionHeading = "%n@|bold,underline Description|@:%n%n",
        parameterListHeading = "%n@|bold,underline Parameters|@:%n",
        optionListHeading = "%n@|bold,underline Options|@:%n",
        header = "The Organ Donation Management System",
        description = "The best way to manage some organs!")
public class CLIOdms implements Runnable {

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Displays this help message and quits.")
    private boolean helpRequested = false;

    @Override
    public void run() {
        System.out.print("odms parent command invoked. Use -h to view usage information for subcommands.");
    }
}
