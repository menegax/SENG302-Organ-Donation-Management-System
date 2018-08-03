package service.interfaces;

import model.Clinician;
import model.Patient;
import service.OrganWaitlist;
import utility.GlobalEnums;

import java.util.List;
import java.util.Map;

public interface IClinicianDataService {

    public OrganWaitlist getOrganWaitList();

    public Clinician getClinician(String staffId);

    public List<Patient> searchPatient(String searchTerm, Map<GlobalEnums.FilterOption, String> filters, int numResults);

    /**
     * Will save to LOCAL ONLY
     */
    public void save(Clinician clinician);
}
