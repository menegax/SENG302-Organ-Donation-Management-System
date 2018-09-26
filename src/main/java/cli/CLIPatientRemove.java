package cli;

import data_access.factories.DAOFactory;
import data_access.interfaces.IPatientDataAccess;
import model.Patient;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import service.PatientDataService;
import service.interfaces.IPatientDataService;
import utility.GlobalEnums;

@SuppressWarnings("unused")
@Command(name = "remove", description = "used to remove existing patients")
class CLIPatientRemove implements Runnable {

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Displays this help message.")
    private boolean helpRequested = false;

    @Option(names = {"-n", "--nhi"}, required = true, description = "The NHI number of the patient.")
    private String nhi;

    private IPatientDataService patientDataService = new PatientDataService();
    private IPatientDataAccess patientDataAccess = DAOFactory.getDAOFactory(GlobalEnums.FactoryType.LOCAL).getPatientDataAccess();

    public void run() {
        Patient patient = patientDataService.getPatientByNhi(nhi);
        patientDataAccess.deletePatient(patient);
    }

}
