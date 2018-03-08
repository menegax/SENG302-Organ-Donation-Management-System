package cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@SuppressWarnings("unused")
@Command(name = "odms", subcommands = {CLIDonor.class, CLISave.class, CLIImport.class}, //todo remove usage heading so only commands are listed i.e. no odms command is shown
        sortOptions = false,
        headerHeading = "+  @|yellow xxxxx|@   @|red xxxxxxx|@  @|green xx       xx|@    @|blue xxxxxxxxxx|@" + "\n" +
                        "| @|yellow x     x|@  @|red x     xx|@ @|green xxx     xxx|@    @|blue x|@" + "\n" +
                        "| @|yellow x     x|@  @|red x      x|@ @|green x xx    x xx|@   @|blue xx|@" + "\n" +
                        "| @|yellow x     x|@  @|red x      x|@ @|green x   xx xx  x|@    @|blue xxxxxxxx|@" + "\n" +
                        "| @|yellow x     x|@  @|red x     xx|@ @|green x    xxx   xx|@          @|blue xx|@" + "\n" +
                        "|  @|yellow xxxxx|@   @|red xxxxxxx|@  @|green x     xx    xx|@ @|blue xxxxxxxxxx|@" + "\n" +
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
