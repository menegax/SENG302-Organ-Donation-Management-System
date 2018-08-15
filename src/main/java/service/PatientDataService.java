package service;

import data_access.factories.DAOFactory;
import data_access.interfaces.IPatientDataAccess;
import model.Patient;
import service.interfaces.IPatientDataService;
import utility.CachedThreadPool;
import utility.GlobalEnums.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;

import static utility.SystemLogger.systemLogger;

/**
 * Split this to Patient USE-CASES - which call dao
 */
public class PatientDataService implements IPatientDataService {

    private DAOFactory mysqlFactory = DAOFactory.getDAOFactory(FactoryType.MYSQL);
    private DAOFactory localDbFactory = DAOFactory.getDAOFactory(FactoryType.LOCAL);


    @Override
    public Patient getPatientByNhi(String nhi) {
        CachedThreadPool threadPool = CachedThreadPool.getCachedThreadPool();
        Callable<Patient> task = () -> {
            if (localDbFactory.getPatientDataAccess().getPatientByNhi(nhi) == null) { //not in local db
                return mysqlFactory.getPatientDataAccess().getPatientByNhi(nhi); //get from remote
            }
            return localDbFactory.getPatientDataAccess().getPatientByNhi(nhi);
        };
        Future<Patient> patientFuture = threadPool.getThreadService().submit(task);
        try {
            return patientFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            systemLogger.log(Level.SEVERE, "Could not get patient fromMYSQL DB", this);
        }
        return null;
    }

    @Override
    public void save(Patient patient) {
        IPatientDataAccess patientDataAccess = localDbFactory.getPatientDataAccess();
        patientDataAccess.savePatients(new HashSet<Patient>(){{add(patient);}});
    }

    @Override
    public void save(List<Patient> patients) {
        for (Patient patient : patients) {
            save(patient);
        }
    }

    @Override
    public List<Patient> getDeadPatients() {
        IPatientDataAccess patientDataAccessLocal = localDbFactory.getPatientDataAccess();
        IPatientDataAccess patientDataAccessSQL = mysqlFactory.getPatientDataAccess();
        List<Patient> allDeadPatients = new ArrayList<>();
        allDeadPatients.addAll(patientDataAccessLocal.getDeadPatients());
        allDeadPatients.addAll(patientDataAccessSQL.getDeadPatients());
        return allDeadPatients;
    }


}
