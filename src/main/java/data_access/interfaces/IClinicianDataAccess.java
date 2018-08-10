package data_access.interfaces;

import model.Clinician;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IClinicianDataAccess {

    /**
     * Saves the clinician changes. When the save is performed locally, the clinicians
     * are either added or updated in the local database set.
     *
     * @param clinician The set of clinicians to save
     */
    void saveClinician(Set<Clinician> clinician);

    /**
     * Adds the given clinician to the local or external database
     *
     * @param clinician The clinician to save
     * @return The success status
     */
    boolean addClinician(Clinician clinician);

    /**
     * Returns the clinician that has the given staff id
     *
     * @param id The staff id to match
     * @return The clinician object
     */
    Clinician getClinicianByStaffId(int id);

    /**
     * Deletes the given clinician from either the local or external database
     *
     * @param clinician The clinician to delete
     * @return A boolean representing the success status of the deletion
     */
    boolean deleteClinician(Clinician clinician);

    /**
     * Returns the next due staff id from the local or external database. This is given by the max
     * staff id found in the data (local or external), and adding 1
     *
     * @return The next staff ID
     */
    int nextStaffID();

    /**
     * Searches clinicians using the given search term.
     * Clinicians match if their username matches the search term directly,
     * or if any of the names, or the full name, is within 2 changes of the search term
     * <p>
     * The Map returned contains a list for the search term closeness
     * (ie 0 characters difference, 1 character difference, 2 characters difference)
     *
     * @param searchTerm The search term to match on
     * @return A Map containing the matching clinicians
     */
    Map<Integer, List<Clinician>> searchClinicians(String searchTerm);

    /**
     * Returns the set of clinicians from the local or external database
     *
     * @return The set of clinicians
     */
    Set<Clinician> getClinicians();

}
