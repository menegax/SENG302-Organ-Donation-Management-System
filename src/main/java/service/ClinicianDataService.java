package service;

import DataAccess.factories.DAOFactory;
import DataAccess.interfaces.IClinicianDataAccess;
import DataAccess.interfaces.IPatientDataAccess;
import model.Clinician;
import model.Patient;
import model.User;
import service.interfaces.IClinicianDataService;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import utility.CachedThreadPool;
import utility.GlobalEnums;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class ClinicianDataService implements IClinicianDataService {

    private DAOFactory mysqlFactory;
    private DAOFactory localDbFactory;

    public ClinicianDataService() {
        mysqlFactory = DAOFactory.getDAOFactory(GlobalEnums.FactoryType.MYSQL);
        localDbFactory = DAOFactory.getDAOFactory(GlobalEnums.FactoryType.LOCAL);
    }

    public OrganWaitlist getOrganWaitList() {
        if (localDbFactory.getTransplantWaitingListDataAccess().getWaitingList() == null) {
            return mysqlFactory.getTransplantWaitingListDataAccess().getWaitingList();
        }
        return  localDbFactory.getTransplantWaitingListDataAccess().getWaitingList();
    }

    @Override
    public Clinician getClinician(String staffId) {
        IClinicianDataAccess clinicianDataAccessLocal = localDbFactory.getClinicianDataAccess();
        if (clinicianDataAccessLocal.getClinicianByStaffId(Integer.parseInt(staffId)) == null) {
            return mysqlFactory.getClinicianDataAccess().getClinicianByStaffId(Integer.parseInt(staffId));
        }
        return  clinicianDataAccessLocal.getClinicianByStaffId(Integer.parseInt(staffId));
    }

    @Override
    public List<Patient> searchPatients(String searchTerm, Map<GlobalEnums.FilterOption, String> filters, int numResults) {
        List<Patient> results = new ArrayList<>();

        IPatientDataAccess dbPatientDataAccess = mysqlFactory.getPatientDataAccess();
        IPatientDataAccess localPatientDataAccess = localDbFactory.getPatientDataAccess();
        Map<Integer, SortedSet<User>> localResults = localPatientDataAccess.searchPatients(searchTerm, filters, numResults);

        CachedThreadPool pool = CachedThreadPool.getCachedThreadPool();
        ExecutorService service = pool.getThreadService();
        Future task = service.submit(() -> {
            Map<Integer, SortedSet<User>> dbResults = dbPatientDataAccess.searchPatients(searchTerm, filters, numResults);
            //Remove users in dbResults that are already in localResults
            for (SortedSet<User> userSet : localResults.values()) {
                for (User u : userSet) {
                    for (SortedSet<User> userSet_db : dbResults.values()) {
                        userSet_db.remove(u);
                    }
                }
            }
            //Place the results in one final Map
            Map<Integer, SortedSet<User>> resultMap = new HashMap<>();
            resultMap.putAll(localResults);
            resultMap.putAll(dbResults);
            //Select the top <numResults> patients
            int count = 0;
            for (SortedSet<User> userSet : resultMap.values()) {
                for (User u : userSet) {
                    if (u instanceof Patient && count < numResults) {
                        results.add((Patient) u);
                        count++;
                    }
                }
            }
        });
        try {
            task.get();
            DAOFactory daoFactory = DAOFactory.getDAOFactory(GlobalEnums.FactoryType.LOCAL);
            for (Patient patient : daoFactory.getPatientDataAccess().getPatients()) {
                System.out.println(patient);
            }
            return results;
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    @Override
    public void save(Clinician clinician) {
        throw new NotImplementedException();
    }

}
