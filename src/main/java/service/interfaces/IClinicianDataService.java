package service.interfaces;

import model.Clinician;
import model.Patient;
import service.OrganWaitlist;
import utility.GlobalEnums;

import java.util.List;
import java.util.Map;

public interface IClinicianDataService {

    /**
     * Gets the organ wait list first from local then db
     * @return - Organwaitlist
     */
    OrganWaitlist getOrganWaitList();

    /**
     * Updates the organ wait list
     * @param organRequests - organ wait list to replace with
     */
    void updateOrganWaitList(OrganWaitlist organRequests);

    /**
     * Gets the clinician from local db first then looks in remote
     * @param staffId - staff id of clinician to get
     * @return clinician
     */
    Clinician getClinician(int staffId);

    /**
     * Gets the number of globalPatients
     * @return - int of patient count
     */
    int getPatientCount();

    /**
     * Gets the next staff id for a clinician
     * @return - next staff id for a new clinician
     */
    int nextStaffId();

    /**
     * Search globalPatients in the db and local storage
     * @param searchTerm - search query
     * @param filters - filters applied to search query
     * @param numResults - number of results to return
     * @return - list of globalPatients
     */
    List<Patient> searchPatients(String searchTerm, Map<GlobalEnums.FilterOption, String> filters, int numResults);

    /**
     * Will save to LOCAL ONLY
     * @param clinician The clinician to save
     */
    void save(Clinician clinician);

}
