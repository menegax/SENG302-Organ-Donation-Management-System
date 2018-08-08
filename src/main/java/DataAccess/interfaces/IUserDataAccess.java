package DataAccess.interfaces;

import model.User;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IUserDataAccess {

    /**
     * Adds a user to the local database. It is added to the appropriate set (patients, clinician, administrators)
     * based on the type of the user
     *
     * @param user The user to add
     */
    void addUser(User user);

    /**
     * Deletes the given user from its associated set in the database (patients, clinicians, administrators)
     * and adds the user to the deleted users set
     *
     * @param user The user to delete
     */
    void deleteUser(User user);

    /**
     * Returns all users in the local database
     *
     * @return the set of all users
     */
    Set<User> getUsers();

    /**
     * Returns a set of users that have been deleted in the local database.
     *
     * @return the deleted users set
     */
    Set<User> getDeletedUsers();

    /**
     * Searches the users in the local database using the given search term and returns matching results.
     * If the searchTerm is the empty string, all results are matched,
     * else a match is determined by if the identifier, any of the names, or the full name
     * is within 2 characters difference (Using the levenshtein distance) from the search term
     *
     * @param searchTerm The search term to match users on
     * @return The map of all matching users
     */
    Map<Integer, List<User>> searchUsers(String searchTerm);
}
