package DataAccess.interfaces;

import model.Clinician;
import model.Patient;

import java.util.List;

public interface IClinicianDataAccess {

    public int updateClincian (List<Clinician> clinician);

    public boolean addClincian (Clinician clinician);

    public Patient getClinicianByStaffId (String id);

    List<Patient> searchClinician (String searchTerm);
}
