package service;

import data_access.factories.DAOFactory;
import data_access.interfaces.IClinicianDataAccess;
import data_access.interfaces.IPatientDataAccess;
import model.Clinician;
import model.Patient;
import service.interfaces.IClinicianDataService;
import utility.CachedThreadPool;
import utility.GlobalEnums;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Level;

import static utility.SystemLogger.systemLogger;

public class ClinicianDataService implements IClinicianDataService {

    private DAOFactory mysqlFactory;
    private DAOFactory localDbFactory;

    public ClinicianDataService() {
        mysqlFactory = DAOFactory.getDAOFactory(GlobalEnums.FactoryType.MYSQL);
        localDbFactory = DAOFactory.getDAOFactory(GlobalEnums.FactoryType.LOCAL);
    }

    public OrganWaitlist getOrganWaitList() {
        if (localDbFactory.getTransplantWaitingListDataAccess().getWaitingList() == null) {
            this.updateOrganWaitList(mysqlFactory.getTransplantWaitingListDataAccess().getWaitingList());
            return mysqlFactory.getTransplantWaitingListDataAccess().getWaitingList();
        }
        return localDbFactory.getTransplantWaitingListDataAccess().getWaitingList();
    }

    @Override
    public void updateOrganWaitList(OrganWaitlist organRequests) {
        localDbFactory.getTransplantWaitingListDataAccess().updateWaitingList(organRequests);
    }

    @Override
    public Clinician getClinician(int staffId) {
        IClinicianDataAccess clinicianDataAccessLocal = localDbFactory.getClinicianDataAccess();
        if (clinicianDataAccessLocal.getClinicianByStaffId(staffId) == null) {
            return mysqlFactory.getClinicianDataAccess().getClinicianByStaffId(staffId);
        }
        return clinicianDataAccessLocal.getClinicianByStaffId(staffId);
    }

    @Override
    public int getPatientCount() {
        return mysqlFactory.getPatientDataAccess().getPatientCount();
    }

    @Override
    public int nextStaffId() {
        return Integer.max(mysqlFactory.getClinicianDataAccess().nextStaffID(), localDbFactory.getClinicianDataAccess().nextStaffID());
    }

    @Override
    public List<Patient> searchPatients(String searchTerm, Map<GlobalEnums.FilterOption, String> filters, int numResults) {
        List<Patient> results = new ArrayList<>();

        IPatientDataAccess dbPatientDataAccess = mysqlFactory.getPatientDataAccess();
        IPatientDataAccess localPatientDataAccess = localDbFactory.getPatientDataAccess();
        Map<Integer, List<Patient>> localResults = localPatientDataAccess.searchPatients(searchTerm, filters, numResults);

        CachedThreadPool pool = CachedThreadPool.getCachedThreadPool();
        ExecutorService service = pool.getThreadService();
        Future task = service.submit(() -> {
            Map<Integer, List<Patient>> dbResults = dbPatientDataAccess.searchPatients(searchTerm, filters, numResults);
            Set<Patient> localPatients = localPatientDataAccess.getPatients();
            //Remove users in dbResults that are already in local storage
            for (List<Patient> userList : dbResults.values()) {
                userList.removeAll(localPatients);
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
            systemLogger.log(Level.WARNING, "Unable to get task");
        }
        return null;
    }

    @Override
    public void save(Clinician clinician) {
        localDbFactory.getClinicianDataAccess().saveClinician(new HashSet<Clinician>() {{
            add(clinician);
        }});
    }

}
