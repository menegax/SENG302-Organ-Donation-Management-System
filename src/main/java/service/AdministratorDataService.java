package service;

import DataAccess.factories.DAOFactory;
import DataAccess.interfaces.IAdministratorDataAccess;
import DataAccess.interfaces.IClinicianDataAccess;
import DataAccess.interfaces.IPatientDataAccess;
import DataAccess.interfaces.IUserDataAccess;
import model.Administrator;
import model.Clinician;
import model.Patient;
import model.User;
import service.interfaces.IAdministratorDataService;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import utility.CachedThreadPool;
import utility.GlobalEnums;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class AdministratorDataService implements IAdministratorDataService {

    private DAOFactory mysqlFactory;
    private DAOFactory localDbFactory;
    private CachedThreadPool cachedThreadPool;

    public AdministratorDataService() {
        cachedThreadPool = CachedThreadPool.getCachedThreadPool();
        mysqlFactory = DAOFactory.getDAOFactory(GlobalEnums.FactoryType.MYSQL);
        localDbFactory = DAOFactory.getDAOFactory(GlobalEnums.FactoryType.LOCAL);
    }

    @Override
    public void deletePatient(String nhi) {
        IPatientDataAccess patientDataAccess = mysqlFactory.getPatientDataAccess(); //todo:
        patientDataAccess.deletePatientByNhi(nhi);
    }

    @Override
    public void importRecords() {
        cachedThreadPool.getThreadService().submit(() -> {
            //List<Patient> patients = parseCSV.parse(filepath); //todo:
            IPatientDataAccess patientDataAccess = mysqlFactory.getPatientDataAccess();
            patientDataAccess.addPatientsBatch(new ArrayList<>());
        });
    }

    @Override
    public List<User> searchUsers(String searchTerm) {
        final int numResults = 30;
        List<User> results = new ArrayList<>();

        IUserDataAccess localUserDataAccess = localDbFactory.getUserDataAccess();
        Map<Integer, List<User>> localResults = localUserDataAccess.searchUsers(searchTerm);

        CachedThreadPool pool = CachedThreadPool.getCachedThreadPool();
        ExecutorService service = pool.getThreadService();
        Future task = service.submit(() -> {
            Map<Integer, List<User>> dbResults = collectDBResults(searchTerm);
            Set<User> localUsers = localUserDataAccess.getUsers();
            //Remove users in dbResults that are already in local storage
            for (List<User> userList : dbResults.values()) {
                userList.removeAll(localUsers);
            }
            //Place the results in one final Map
            Map<Integer, List<User>> resultMap = new HashMap<>(localResults);
            for (Integer i : dbResults.keySet()) {
                for (User u : dbResults.get(i)) {
                    resultMap.get(i).add(u);
                }
            }
            //Select the top 30 users
            int count = 0;
            for (List<User> userList : resultMap.values()) {
                for (User u : userList) {
                    if (count < numResults) {
                        results.add(u);
                        count++;
                    }
                }
            }
        });
        try {
            task.get();
            return results;
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    private Map<Integer, List<User>> collectDBResults(String searchTerm) {
        IPatientDataAccess patientDataAccess = mysqlFactory.getPatientDataAccess();
        IClinicianDataAccess clinicianDataAccess = mysqlFactory.getClinicianDataAccess();
        IAdministratorDataAccess administratorDataAccess = mysqlFactory.getAdministratorDataAccess();
        Map<Integer, List<Patient>> patients = patientDataAccess.searchPatients(searchTerm, null, 30);
        Map<Integer, List<Clinician>> clinicians = clinicianDataAccess.searchClinicians(searchTerm);
        Map<Integer, List<Administrator>> admins = administratorDataAccess.searchAdministrators(searchTerm);
        Map<Integer, List<User>> results = new HashMap<>();
        results.put(0, new ArrayList<>());
        results.put(1, new ArrayList<>());
        results.put(2, new ArrayList<>());
        for (Integer i : patients.keySet()) {
            results.get(i).addAll(patients.get(i));
        }
        for (Integer i : clinicians.keySet()) {
            results.get(i).addAll(clinicians.get(i));
        }
        for (Integer i : admins.keySet()) {
            results.get(i).addAll(admins.get(i));
        }
        return results;
    }

    @Override
    public Administrator getAdministratorByUsername(String username) {
        IAdministratorDataAccess administratorDataAccess = localDbFactory.getAdministratorDataAccess();
        Administrator administrator = administratorDataAccess.getAdministratorByUsername(username);
        if (administrator == null) {
            return mysqlFactory.getAdministratorDataAccess().getAdministratorByUsername(username);
        }
        return administrator;
    }

    @Override
    public void save(Administrator administrator) {
        IAdministratorDataAccess dataAccess = localDbFactory.getAdministratorDataAccess();
        dataAccess.addAdministrator(administrator);
    }
}
