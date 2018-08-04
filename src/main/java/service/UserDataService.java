package service;
import DataAccess.factories.DAOFactory;
import DataAccess.interfaces.IAdministratorDataAccess;
import DataAccess.interfaces.IClinicianDataAccess;
import DataAccess.interfaces.IPatientDataAccess;
import model.Administrator;
import model.Clinician;
import model.Patient;
import service.interfaces.IUserDataService;
import utility.CachedThreadPool;
import utility.GlobalEnums;

import java.util.Set;

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

        CachedThreadPool threadPool = CachedThreadPool.getCachedThreadPool();
        threadPool.getThreadService().submit(() -> {
            clinicianDataService.saveClinician(clinicians);
            patientDataAccess.savePatients(patients); //save to remote db
            administratorDataAccess.saveAdministrator(administrators);
        });

    }
}
