package data_access.interfaces;

import model.Patient;
import utility.GlobalEnums;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IPatientDataAccess {

    /**
     * Saves the given set of globalPatients to the local or external database
     *
     * @param patient The set of patient objects to save
     */
    void savePatients(Set<Patient> patient);

    /**
     * Adds a list of globalPatients to the local or external database
     * and adds them using a batch operation
     *
     * @param patient The list of globalPatients to add
     * @return The success status of the add
     */
    boolean addPatientsBatch(List<Patient> patient);

    /**
     * Returns all patient objects
     *
     * @return The set of globalPatients
     */
    Set<Patient> getPatients();

    /**
     * Gets the number of globalPatients present in the local or external database
     *
     * @return The number of globalPatients
     */
    int getPatientCount();

    /**
     * Gets the patient object that has the matching nhi
     *
     * @param nhi The nhi to match
     * @return The matching patient object
     */
    Patient getPatientByNhi(String nhi);

    /**
     * Deletes the given patient object
     *
     * @param patient The patient to delete
     * @return The success status of the deletion
     */
    boolean deletePatient(Patient patient);

    /**
     * Searches globalPatients using the given search term.
     * Patients match if their username matches the search term directly,
     * or if any of the names, or the full name, is within 2 changes of the search term
     * <p>
     * The Map returned contains a list for the search term closeness
     * (ie 0 characters difference, 1 character difference, 2 characters difference)
     *
     * @param searchTerm The search term to match on
     * @param filters    The filters map to filter results on
     * @param numResults The max number of results to fetch
     * @return A Map containing the matching globalPatients
     */
    Map<Integer, List<Patient>> searchPatients(String searchTerm, Map<GlobalEnums.FilterOption, String> filters, int numResults);

    /**
     * Deletes the patient from the local or external database that has the given nhi
     *
     * @param nhi The nhi of the patient to delete
     */
    void deletePatientByNhi(String nhi);


    /**
     * Gets all the dead globalPatients from the remote database
     * @return A list of dead donors
     */
    List<Patient> getDeadDonors();

}
