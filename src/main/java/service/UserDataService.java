package service;
import DataAccess.factories.DAOFactory;
import DataAccess.interfaces.IAdministratorDataAccess;
import DataAccess.interfaces.IClinicianDataAccess;
import DataAccess.interfaces.IPatientDataAccess;
import DataAccess.localDAO.LocalDB;
import model.Administrator;
import model.Clinician;
import model.Patient;
import service.interfaces.IUserDataService;
import utility.CachedThreadPool;
import utility.GlobalEnums;

import java.util.ArrayList;
import java.util.Set;

import static java.util.logging.Level.INFO;
import static utility.SystemLogger.systemLogger;

public class UserDataService implements IUserDataService {

    private DAOFactory mysqlFactory;
    private DAOFactory localDatabase;


    public UserDataService() {
        mysqlFactory = DAOFactory.getDAOFactory(GlobalEnums.FactoryType.MYSQL);
        localDatabase = DAOFactory.getDAOFactory(GlobalEnums.FactoryType.LOCAL);
    }

    @Override
    public void save() {
        Set<Patient> patients = localDatabase.getPatientDataAccess().getPatients();
        Set<Clinician> clinicians = localDatabase.getClinicianDataAccess().getClinicians();
        Set<Administrator> administrators = localDatabase.getAdministratorDataAccess().getAdministrators();
        IPatientDataAccess patientDataAccess = mysqlFactory.getPatientDataAccess();
        IClinicianDataAccess clinicianDataService = mysqlFactory.getClinicianDataAccess();
        IAdministratorDataAccess administratorDataAccess = mysqlFactory.getAdministratorDataAccess();


        //Thread management
        CachedThreadPool threadPool = CachedThreadPool.getCachedThreadPool();
        threadPool.getThreadService().submit(() -> {
            clinicianDataService.saveClinician(clinicians);
            patientDataAccess.savePatients(patients); //save to remote db
            administratorDataAccess.saveAdministrator(administrators);
        });
    }

    @Override
    public void clear() {
        LocalDB.getInstance().clear();
        prepareApplication();
    }

    public void prepareApplication() {
        AdministratorDataService administratorDataService = new AdministratorDataService();
        ClinicianDataService clinicianDataService = new ClinicianDataService();
        if (clinicianDataService.getClinician(0) == null) {
            systemLogger.log(INFO, "Default clinician not in database. Adding default clinician to database.");
            clinicianDataService.save(new Clinician(0, "Rob", new ArrayList<>(), "Burns", GlobalEnums.Region.CANTERBURY));
        }
        if (administratorDataService.getAdministratorByUsername("admin") == null) {
            systemLogger.log(INFO, "Default admin not in database. Adding default admin to database.");
            administratorDataService.save(new Administrator("admin", "John", new ArrayList<>(), "Smith", "password"));
        }
    }


}
