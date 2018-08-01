package DataAccess.localDAO;

import DataAccess.LocalDB;
import DataAccess.factories.LocalDatabaseFactory;
import DataAccess.interfaces.IPatientDataAccess;
import model.Patient;

import java.util.List;

public class PatientLocalDAO implements IPatientDataAccess {

    private LocalDB localDB;

    public PatientLocalDAO() {
        localDB = LocalDatabaseFactory.getLocalDbInstance();
    }

    @Override
    public int updatePatient(List<Patient> patient) {
        return 0;
    }

    @Override
    public boolean addPatient(Patient patient) {
        localDB.storePatient(patient); //todo:
        return true;
    }

    @Override
    public boolean addPatients(List<Patient> patient) {
        patient.forEach(x -> {
            localDB.storePatient(x);
        });
        return true; //TODO:
    }

    @Override
    public List<Patient> getPatients() {
        return null;
    }

    @Override
    public Patient getPatientByNhi(String nhi) {
        return null;
    }

    @Override
    public List<Patient> searchPatient(String searchTerm) {
        return null;
    }
}
