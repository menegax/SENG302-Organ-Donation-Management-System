package cli;
import data_access.factories.DAOFactory;
import data_access.interfaces.IPatientDataAccess;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import service.PatientDataService;
import service.interfaces.IPatientDataService;
import utility.GlobalEnums;

import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

@SuppressWarnings("unused")
@Command(name = "view", description = "used to view patient attributes")
public class CLIPatientView implements Runnable {

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Displays this help message and quits.")
    private boolean helpRequested = false;

    @Option(names = {"-n", "--nhi"}, description = "Search patient by the NHI number of the patient.")
    private String searchNhi;

    @Option(names = {"-a", "--all"}, description = "View all patients")
    private boolean searchAll;

    private IPatientDataService patientDataService = new PatientDataService();
    private IPatientDataAccess patientDataAccess = DAOFactory.getDAOFactory(GlobalEnums.FactoryType.LOCAL).getPatientDataAccess();

    public void run() {
        if (searchNhi != null) {
            userActions.log(Level.INFO, patientDataService.getPatientByNhi(searchNhi).toString(), "attempted to view a particular patient");
        }
        if (searchAll) {
            if (patientDataAccess.getPatients().size() == 0) {
                userActions.log(Level.INFO, "No patient in the database", "attempted to view all patients");
            } else {
                userActions.log(Level.WARNING, patientDataAccess.getPatients().toString(), "attempted to view all patients");
            }
        }
        if (searchNhi == null && !searchAll) {
            userActions.log(Level.INFO, "use patient view -h for help", "patient view command invoked");
        }
    }

}
