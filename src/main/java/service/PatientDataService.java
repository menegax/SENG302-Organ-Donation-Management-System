package service;

import DataAccess.factories.DAOFactory;
import model.Patient;
import service.interfaces.IPatientDataService;
import utility.GlobalEnums.*;

import java.util.HashSet;


/**
 * Split this to Patient USE-CASES -> which call dao
 */
public class PatientDataService implements IPatientDataService {

    private DAOFactory mysqlFactory;
    private DAOFactory localDbFactory;

    public PatientDataService() {
        mysqlFactory = DAOFactory.getDAOFactory(FactoryType.MYSQL);
        localDbFactory = DAOFactory.getDAOFactory(FactoryType.LOCAL);
    }

    @Override
    public Patient getPatientByNhi(String nhi) {
        if (localDbFactory.getPatientDataAccess().getPatientByNhi(nhi) == null) { //not in local db
            return mysqlFactory.getPatientDataAccess().getPatientByNhi(nhi); //get from remote
        }
        return localDbFactory.getPatientDataAccess().getPatientByNhi(nhi);
    }

    @Override
    public void save(Patient patient) {
        localDbFactory.getPatientDataAccess().savePatients(new HashSet<Patient>(){{add(patient);}});
    }


}
