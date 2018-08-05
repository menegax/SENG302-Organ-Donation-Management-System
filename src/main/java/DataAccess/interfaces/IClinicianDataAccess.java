package DataAccess.interfaces;

import model.Clinician;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IClinicianDataAccess {

    void saveClinician(Set<Clinician> clinician);

    boolean addClinician(Clinician clinician);

    Clinician getClinicianByStaffId(int id);

    boolean deleteClinician(Clinician clinician);

    int nextStaffID();

    Map<Integer, List<Clinician>> searchClinicians(String searchTerm);

    Set<Clinician> getClinicians();

}
