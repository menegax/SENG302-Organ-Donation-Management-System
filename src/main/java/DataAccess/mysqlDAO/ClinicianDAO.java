package DataAccess.mysqlDAO;

import DataAccess.interfaces.IClinicianDataAccess;
import model.Clinician;
import model.Patient;

import java.util.List;

public class ClinicianDAO implements IClinicianDataAccess {

    @Override
    public int updatePatient(List<Clinician> clinician) {
        return 0;
    }

    @Override
    public boolean addPatient(Clinician clinician) {
        return false;
    }

    @Override
    public Patient getClinicianByStaffId(String id) {
        return null;
    }

    @Override
    public List<Patient> searchClinician(String searchTerm) {
        return null;
    }
}
