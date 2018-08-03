package DataAccess.localDAO;

import DataAccess.LocalDB;
import DataAccess.factories.LocalDatabaseFactory;
import DataAccess.interfaces.IPatientDataAccess;
import model.Patient;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import utility.GlobalEnums;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
    public Set<Patient> getPatients() { return localDB.getPatients(); }

    @Override
    public Patient getPatientByNhi(String nhi) {
        return localDB.getPatientByNHI(nhi);
    }

    @Override
    public boolean deletePatient(Patient patient) {
        return false;
    }

    @Override
    public List<Patient> searchPatient(String searchTerm, Map<GlobalEnums.FilterOption, String> filters, int numResults) {
        return null;
    }

    @Override
    public void deletePatientByNhi(String nhi) { throw new NotImplementedException(); }

}
