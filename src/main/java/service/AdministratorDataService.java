package service;

import controller.ScreenControl;
import data_access.factories.DAOFactory;
import data_access.interfaces.IAdministratorDataAccess;
import data_access.interfaces.IClinicianDataAccess;
import data_access.interfaces.IPatientDataAccess;
import data_access.interfaces.IUserDataAccess;
import data_access.localDAO.LocalDB;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Administrator;
import model.Clinician;
import model.Patient;
import model.User;
import service.interfaces.IAdministratorDataService;
import utility.CachedThreadPool;
import utility.GlobalEnums;
import utility.ImportObservable;
import utility.SystemLogger;
import utility.parsing.ParseCSV;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Level;

import static utility.SystemLogger.systemLogger;

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
        IPatientDataAccess patientDataAccess = mysqlFactory.getPatientDataAccess();
        patientDataAccess.deletePatientByNhi(nhi);
    }

    @Override
    public void deleteUser(User user) {
        localDbFactory.getUserDataAccess().deleteUser(user);
    }

    @Override
    public void importRecords(String filepath) {
        cachedThreadPool.getThreadService().submit(() -> {
            ParseCSV parseCSV = new ParseCSV();
            try {
                Map<ParseCSV.Result, List> patients = parseCSV.parse(new FileReader(filepath));
                IPatientDataAccess patientDataAccess = mysqlFactory.getPatientDataAccess();
                ImportObservable importObservable = ImportObservable.getInstance();
                importObservable.setTotal(patients.get(ParseCSV.Result.SUCCESS).size());
                importObservable.setCompleted(0);
                long startTime = System.nanoTime();
                patientDataAccess.addPatientsBatch(patients.get(ParseCSV.Result.SUCCESS));
                importObservable.setFinished();
                long endTime = System.nanoTime();
                long duration = (endTime - startTime);
                LocalDB db = LocalDB.getInstance();
                Set<Patient> imported = new HashSet<Patient>(patients.get(ParseCSV.Result.SUCCESS));
                db.setImported(imported);
                Platform.runLater(this::showImportResults);
                systemLogger.log(Level.INFO, "Time to complete import: " + duration /1000000000 + " seconds", this );
            } catch (FileNotFoundException e) {
                systemLogger.log(Level.SEVERE, "Unable to find file", this);
            }
        });
    }

    private void showImportResults() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scene/adminImportResults.fxml"));
            Parent parent = fxmlLoader.load();
            Scene scene = new Scene(parent, 900, 600);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            SystemLogger.systemLogger.log(Level.SEVERE, "Couldn't open import results popup");
        }
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
            Set<User> deletedUsers = localUserDataAccess.getDeletedUsers();
            //Remove users in dbResults that are already in local storage
            for (List<User> userList : dbResults.values()) {
                userList.removeAll(localUsers);
                userList.removeAll(deletedUsers);
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
        dataAccess.saveAdministrator(new HashSet<Administrator>() {{
            add(administrator);
        }});
    }
}
