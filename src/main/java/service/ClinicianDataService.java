package service;

import DataAccess.factories.DAOFactory;
import DataAccess.interfaces.IClinicianDataAccess;
import DataAccess.interfaces.IPatientDataAccess;
import model.Clinician;
import model.Patient;
import service.interfaces.IClinicianDataService;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import utility.CachedThreadPool;
import utility.GlobalEnums;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public Clinician getClinician(int staffId) {
        IClinicianDataAccess clinicianDataAccessLocal = localDbFactory.getClinicianDataAccess();
        if (clinicianDataAccessLocal.getClinicianByStaffId(staffId) == null) {
            return mysqlFactory.getClinicianDataAccess().getClinicianByStaffId(staffId);
        }
        return  clinicianDataAccessLocal.getClinicianByStaffId(staffId);
    }

    @Override
    public List<Patient> searchPatients(String searchTerm, Map<GlobalEnums.FilterOption, String> filters, int numResults) {
        List<Patient> results = new ArrayList<>();

        IPatientDataAccess dbPatientDataAccess = mysqlFactory.getPatientDataAccess();
        //IPatientDataAccess localPatientDataAccess = localDbFactory.getPatientDataAccess(); //todo uncomment once Searcher.search is done
        //Map<Integer, SortedSet<User>> localResults = localPatientDataAccess.searchPatients(searchTerm, filters, numResults);
        Map<Integer, List<Patient>> localResults = getMockedLocal();

        CachedThreadPool pool = CachedThreadPool.getCachedThreadPool();
        ExecutorService service = pool.getThreadService();
        Future task = service.submit(() -> {
            Map<Integer, List<Patient>> dbResults = dbPatientDataAccess.searchPatients(searchTerm, filters, numResults);
            //Remove users in dbResults that are already in localResults
            for (List<Patient> userList : localResults.values()) {
                for (Patient u : userList) {
                    for (List<Patient> userList_db : dbResults.values()) {
                        userList_db.remove(u);
                    }
                }
            }
            //Place the results in one final Map
            Map<Integer, List<Patient>> resultMap = new HashMap<>(localResults);
            for (Integer i : dbResults.keySet()) {
                for (Patient p : dbResults.get(i)) {
                    resultMap.get(i).add(p);
                }
            }
            //Select the top <numResults> patients
            int count = 0;
            for (List<Patient> userList : resultMap.values()) {
                for (Patient u : userList) {
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

    private Map<Integer, List<Patient>> getMockedLocal() { //temp <see this.searchPatients>
        Map<Integer, List<Patient>> map = new HashMap<>();
        List<Patient> s1 = new ArrayList<>();
        List<Patient> s2 = new ArrayList<>();
        List<Patient> s3 = new ArrayList<>();
        s1.add(new Patient("ABC1238", "joey", new ArrayList<>(), "Bloggs", LocalDate.now()));
        map.put(0, s1);
        map.put(1, s2);
        map.put(2, s3);
        return map;
    }

    @Override
    public void save(Clinician clinician) {
        throw new NotImplementedException();
    }

}
