package service;
import DataAccess.factories.DAOFactory;
import DataAccess.interfaces.IPatientDataAccess;
import model.Patient;
import service.interfaces.IUserDataService;
import utility.GlobalEnums;

import java.util.HashSet;
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
        IPatientDataAccess patientDataAccess = mysqlFactory.getPatientDataAccess();
        patientDataAccess.savePatients(patients); //save to remote db
    }
}
