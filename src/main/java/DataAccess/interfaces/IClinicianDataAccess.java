package DataAccess.interfaces;

import model.Clinician;

import java.util.List;

public interface IClinicianDataAccess {

    public int updateClinician (List<Clinician> clinician);

    public boolean addClinician (Clinician clinician);

    public Clinician getClinicianByStaffId (int id);

    public boolean deleteClinician(Clinician clinician);

    public int nextStaffID();

    List<Clinician> searchClinician (String searchTerm);
}
