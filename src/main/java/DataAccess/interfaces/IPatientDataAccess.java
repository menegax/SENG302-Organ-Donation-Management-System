package DataAccess.interfaces;

import model.Patient;
import model.User;
import utility.GlobalEnums;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

public interface IPatientDataAccess {

    /**
     * Saves the given set of patients to the local or external database
     * @param patient The set of patient objects to save
     * @return The success code of the save
     */
    int savePatients(Set<Patient> patient);

    /**
     * Adds a list of patients to the local or external database
     * and adds them using a batch operation
     * @param patient The list of patients to add
     * @return The success status of the add
     */
    boolean addPatientsBatch(List<Patient> patient);

    /**
     * Returns all patient objects
     * @return The set of patients
     */
    Set<Patient> getPatients();

    /**
     * Gets the number of patients present in the local or external database
     * @return The number of patients
     */
    int getPatientCount();

    /**
     * Gets the patient object that has the matching nhi
     * @param nhi The nhi to match
     * @return The matching patient object
     */
    Patient getPatientByNhi(String nhi);

    /**
     * Deletes the given patient object
     * @param patient The patient to delete
     * @return The success status of the deletion
     */
    boolean deletePatient(Patient patient);

    /**
     * Searches patients using the given search term.
     * Patients match if their username matches the search term directly,
     * or if any of the names, or the full name, is within 2 changes of the search term
     * <p>
     * The Map returned contains a list for the search term closeness
     * (ie 0 characters difference, 1 character difference, 2 characters difference)
     *
     * @param searchTerm The search term to match on
     * @return A Map containing the matching patients
     */
    Map<Integer, List<Patient>> searchPatients(String searchTerm, Map<GlobalEnums.FilterOption, String> filters, int numResults);

    /**
     * Deletes the patient from the local or external database that has the given nhi
     * @param nhi The nhi of the patient to delete
     */
    void deletePatientByNhi(String nhi);

}
