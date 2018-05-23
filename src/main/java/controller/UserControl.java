package controller;

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
    void addLoggedInUserToCache(User user) {
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
     *  Gets the target patient that is currently being viewed
     * @return - Patient that is being viewed
     */
    Patient getTargetPatient() {
        Object value = get("target_patient");
        if (value instanceof Patient) {
            return (Patient) value;
        }
        return null;
    }

    /**
     * Sets the patient to be viewed
     * @param patient - Patient object to view
     */
    void setTargetPatient(Patient patient) {
        add("target_patient", patient);
    }

    /**
     * Clears cache, removes all key value pairs
     */
    void clearCache(){
        clear();
    } //Todo fix typo

    /**
     * Clears the map of all entries
     */
    private void clear() {
        users.clear();
    }

    /**
     * Removes the logged in user from the cache
     */
    void rmLoggedInUserCache() {
        remove("user_logged_in");
    }

}
