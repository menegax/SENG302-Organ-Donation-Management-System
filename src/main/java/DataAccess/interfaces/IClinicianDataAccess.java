package DataAccess.interfaces;

import model.Clinician;

import java.util.List;
import java.util.Set;

public interface IClinicianDataAccess {

    public int saveClinician(Set<Clinician> clinician);

    public boolean addClinician (Clinician clinician);

    public Clinician getClinicianByStaffId (int id);

    public boolean deleteClinician(Clinician clinician);

    public int nextStaffID();

    public List<Clinician> searchClinician (String searchTerm);

    public Set<Clinician> getClinicians();

}
