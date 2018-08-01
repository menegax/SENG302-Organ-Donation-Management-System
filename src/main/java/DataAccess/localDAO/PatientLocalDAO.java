package DataAccess.localDAO;

import DataAccess.interfaces.IPatientDataAccess;
import model.Patient;

import java.util.List;

public class PatientLocalDAO implements IPatientDataAccess {

    @Override
    public int updatePatient(List<Patient> patient) {
        return 0;
    }

    @Override
    public boolean addPatient(Patient patient) {
        return false;
    }

    @Override
    public boolean addPatients(List<Patient> patient) {
        return false;
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
