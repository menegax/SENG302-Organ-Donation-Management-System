package DataAccess.localDAO;

import DataAccess.factories.LocalDatabaseFactory;
import DataAccess.interfaces.IPatientDataAccess;
import model.Patient;
import model.User;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import utility.GlobalEnums.FilterOption;
import utility.GlobalEnums.UserTypes;
import utility.Searcher;

import java.util.*;


public class PatientLocalDAO implements IPatientDataAccess {

    private LocalDB localDB;

    public PatientLocalDAO() {
        localDB = LocalDatabaseFactory.getLocalDbInstance();
    }


    @Override
    public int savePatients(Set<Patient> patient) {
        patient.forEach(x -> {
            localDB.storePatient(x);
            Searcher.getSearcher().updateIndex(x);
        });
        return 0; //TODO:
    }

    @Override
    public boolean addPatientsBatch(List<Patient> patient) {
        return false;
    }

    @Override
    public Set<Patient> getPatients() {
        return localDB.getPatients();
    }

    @Override
    public int getPatientCount() {
        return localDB.getPatients().size();
    }

    @Override
    public Patient getPatientByNhi(String nhi) {
        return localDB.getPatientByNHI(nhi);
    }

    @Override
    public boolean deletePatient(Patient patient) {
        return false;
    }

    @Override
    public Map<Integer, List<Patient>> searchPatients(String searchTerm, Map<FilterOption, String> filters, int numResults) {
        Map<Integer, List<User>> searchResults = Searcher.getSearcher().search(searchTerm, new UserTypes[]{UserTypes.PATIENT}, numResults, filters);
        Map<Integer, List<Patient>> results = new HashMap<>();
        results.put(0, new ArrayList<>());
        results.put(1, new ArrayList<>());
        results.put(2, new ArrayList<>());
        for (Integer i : searchResults.keySet()) {
            for (User u : searchResults.get(i)) {
                if (u instanceof Patient) {
                    results.get(i).add((Patient) u);
                }
            }
        }
        return results;
    }

    @Override
    public void deletePatientByNhi(String nhi) {
        throw new NotImplementedException();
    }

}
