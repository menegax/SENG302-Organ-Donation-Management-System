package service.interfaces;

import model.Clinician;
import model.Patient;
import service.OrganWaitlist;
import utility.GlobalEnums;

import java.util.List;
import java.util.Map;

public interface IClinicianDataService {

    OrganWaitlist getOrganWaitList();

    void updateOrganWaitList(OrganWaitlist organRequests);

    Clinician getClinician(int staffId);

    int getPatientCount();

    int nextStaffId();

    List<Patient> searchPatients(String searchTerm, Map<GlobalEnums.FilterOption, String> filters, int numResults);

    /**
     * Will save to LOCAL ONLY
     * @param clinician The clinician to save
     */
    void save(Clinician clinician);

}
