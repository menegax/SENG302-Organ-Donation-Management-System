package DataAccess.interfaces;

import model.Administrator;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IAdministratorDataAccess {

    /**
     * Saves the administrators changes. When the save is performed locally, the administrators
     * are either added or updated in the local database set.
     *
     * @param administrators The set of administrators to save
     */
    void saveAdministrator(Set<Administrator> administrators);

    /**
     * Adds the given administrator to the local or external database
     *
     * @param administrator The administrator to save
     */
    void addAdministrator(Administrator administrator);

    /**
     * Deletes the given administrator from either the local or external database
     *
     * @param administrator The administrator to delete
     * @return A boolean representing the success status of the deletion
     */
    boolean deleteAdministrator(Administrator administrator);

    /**
     * Returns the administrator that has the given username
     *
     * @param username The username to match
     * @return The administrator object
     */
    Administrator getAdministratorByUsername(String username);

    /**
     * Searches administrators using the given search term.
     * Administrators match if their username matches the search term directly,
     * or if any of the names, or the full name, is within 2 changes of the search term
     * <p>
     * The Map returned contains a list for the search term closeness
     * (ie 0 characters difference, 1 character difference, 2 characters difference)
     *
     * @param searchTerm The search term to match on
     * @return A Map containing the matching administrators
     */
    Map<Integer, List<Administrator>> searchAdministrators(String searchTerm);

    /**
     * Returns the set of administrators from the local or external database
     *
     * @return The set of administrators
     */
    Set<Administrator> getAdministrators();
}
