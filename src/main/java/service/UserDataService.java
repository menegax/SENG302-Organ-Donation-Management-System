package service;
import DataAccess.factories.DAOFactory;
import DataAccess.interfaces.IAdministratorDataAccess;
import DataAccess.interfaces.IClinicianDataAccess;
import DataAccess.interfaces.IPatientDataAccess;
import model.Administrator;
import model.Clinician;
import model.Patient;
import service.interfaces.IUserDataService;
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
        patientDataAccess.savePatients(patients); //save to remote db
        IClinicianDataAccess clinicianDataService = mysqlFactory.getClinicianDataAccess();
        clinicianDataService.saveClinician(clinicians);
        IAdministratorDataAccess administratorDataAccess = mysqlFactory.getAdministratorDataAccess();
        administratorDataAccess.saveAdministrator(administrators);
    }
}
