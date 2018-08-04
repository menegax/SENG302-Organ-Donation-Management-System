package DataAccess.localDAO;

import DataAccess.LocalDB;
import DataAccess.factories.LocalDatabaseFactory;
import DataAccess.interfaces.IPatientDataAccess;
import model.Patient;
import model.User;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import utility.GlobalEnums.FilterOption;
import utility.GlobalEnums.UserTypes;
import utility.Searcher;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;


public class PatientLocalDAO implements IPatientDataAccess {

    private LocalDB localDB;

    public PatientLocalDAO() {
        localDB = LocalDatabaseFactory.getLocalDbInstance();
    }


    @Override
    public int savePatients(Set<Patient> patient) {
        patient.forEach(x -> {
            localDB.storePatient(x);
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
    public Patient getPatientByNhi(String nhi) {
        return localDB.getPatientByNHI(nhi);
    }

    @Override
    public boolean deletePatient(Patient patient) {
        return false;
    }

    @Override
    public Map<Integer, SortedSet<User>> searchPatients(String searchTerm, Map<FilterOption, String> filters, int numResults) {
        return Searcher.getSearcher().search(searchTerm, new UserTypes[]{UserTypes.PATIENT}, numResults, filters);
    }

    @Override
    public void deletePatientByNhi(String nhi) {
        throw new NotImplementedException();
    }

}
