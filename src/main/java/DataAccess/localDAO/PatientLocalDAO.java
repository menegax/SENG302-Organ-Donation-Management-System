package DataAccess.localDAO;

import DataAccess.interfaces.IPatientDataAccess;
import model.Patient;

import java.util.List;

public class PatientLocalDAO implements IPatientDataAccess {
    
    @Override
    public int update(List<Patient> patient) {
        return 0;
    }

    @Override
    public boolean insert(Patient patient) {
        return false;
    }

    @Override
    public boolean insert(List<Patient> patient) {
        return false;
    }

    @Override
    public List<Patient> select() {
        return null;
    }

    @Override
    public Patient selectOne(String nhi) {
        return null;
    }

    @Override
    public List<Patient> selectFiltered(String searchTerm) {
        return null;
    }
}
