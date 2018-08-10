package cli;

import model.Patient;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import service.PatientDataService;
import service.interfaces.IPatientDataService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

@SuppressWarnings("unused")
@Command(name = "odms", subcommands = {CLIPatient.class, CLISave.class, CLIImport.class},
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

    @Option(names = {"-d", "--dev"}, hidden = true, description = "Auto adds a patient for your convenience.")
    private boolean devMode;

    private IPatientDataService patientDataService = new PatientDataService();

    @Override
    public void run() {
        if (devMode) {
            prepTheApp();
        }
    }

    private void prepTheApp() {
        try {
            patientDataService.save(new Patient("aaa1111", "David", new ArrayList<String>() {{
                add("John");
            }}, "Dennison", LocalDate.of(1994, 12, 12)));

            patientDataService.save(new Patient("bbb2222", "Peggy", new ArrayList<String>() {{
                add("Jane");
            }}, "Peterson", LocalDate.of(1994, 12, 12)));
        } catch (IllegalArgumentException i) {
            userActions.log(Level.WARNING, i.getMessage(), "attempted to prep the app using --dev mode");
        }

    }
}
