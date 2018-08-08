package service;

import DataAccess.factories.DAOFactory;
import DataAccess.interfaces.IPatientDataAccess;
import model.Patient;
import service.interfaces.IPatientDataService;
import utility.CachedThreadPool;
import utility.GlobalEnums.*;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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
            e.printStackTrace();
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
            save(patient); //TODO:
        }
    }


}
