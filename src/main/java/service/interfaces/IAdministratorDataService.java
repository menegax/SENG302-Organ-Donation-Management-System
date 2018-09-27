package service.interfaces;

import model.Administrator;
import model.User;

import java.util.List;

public interface IAdministratorDataService {

    /**
     * Delete patient from local storage then mysql
     * @param nhi - nhi of patient to delete
     */
    void deletePatient(String nhi);

    /**
     * Delete user from lcoal then mysql
     * @param user - user to delete
     */
    void deleteUser(User user);


    /**
     * Import records into the system
     * @param filePath The filepath for the import csv
     */
    void importRecords(String filePath);

    /**
     * Search users in the db
     * @param searchTerm - search query
     * @return - list of users
     */
    List<User> searchUsers(String searchTerm);

    /**
     * Gets an admin first from local db, otherwise will look in mysql
     * @param username - username of admin
     * @return - return administrator
     */
    Administrator getAdministratorByUsername(String username);

    void save(Administrator administrator);
}
