package controller;

import model.Administrator;
import model.Clinician;
import static java.util.logging.Level.INFO;
import static utility.SystemLogger.systemLogger;

import model.Clinician;
import model.Patient;
import model.User;

import java.util.HashMap;
import java.util.Map;

public class UserControl {


    private static final Map<String, User> users = new HashMap<>();


    /**
     * Adds entries to the map
     * @param key - key to identify the value by
     * @param value - value to store
     */
    private void add(String key, User value) {
        if (key != null && value != null){
            users.put(key, value);
        }
    }

    /**
     * Remove an entry from the map
     * @param key - key value to be removed
     */
    private void remove(String key) {
        if (users.get(key) != null) {
            users.remove(key);
        }
    }

    /**
     * Returns the object at the given key
     * @param key - key to identify the object value by
     * @return - object at the given key
     */
    private User get(String key) {
        return users.get(key);
    }

    /**
     * Adds a user to the cache
     * @param user - user to be added
     */
    public void addLoggedInUserToCache(User user) {
        add("user_logged_in", user);
    }


    /**
     *  Gets the logged in user
     * @return - user object
     */
    public User getLoggedInUser() {
        return get("user_logged_in");
    }

    public boolean isUserLoggedIn() {
        return !users.isEmpty();
    }

    /**
     *  Gets the target user that is currently being viewed
     * @return - User that is being viewed
     */
    public User getTargetUser() {
        Object value = get("target_user");
        if (value instanceof Patient) {
            return (Patient) value;
        } else if (value instanceof Clinician) {
            return (Clinician) value;
        } else if (value instanceof Administrator) {
            return (Administrator) value;
        }
        return null;
    }

    /**
     * Sets the user to be viewed
     * @param user - Patient object to view
     */
    public void setTargetUser(User user) {
        add("target_user", user);
    }

    /**
     * Clears cache, removes all key value pairs
     */
    void clearCache(){
        clear();
    }

    /**
     * Clears the map of all entries
     */
    private void clear() {
        users.clear();
    }

    /**
     * Removes the logged in user from the cache
     */
    public void rmLoggedInUserCache() {
//        remove("user_logged_in");
        users.clear();
        systemLogger.log(INFO, "All users have been logged out");
    }

}
