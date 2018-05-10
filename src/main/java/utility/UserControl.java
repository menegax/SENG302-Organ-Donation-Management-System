package utility;

import model.Patient;

import java.util.HashMap;
import java.util.Map;

public class UserControl {

    private static final Map<String, Object> cache = new HashMap<>();

    /**
     * Adds entries to the map
     * @param key - key to identify the value by
     * @param value - value to store
     */
    private void add(String key, Object value) {
        if (key != null && value != null){
            cache.put(key, value);
        }
    }

    /**
     * Remove an entry from the map
     * @param key - key value to be removed
     */
    private void remove(String key) {
        if (cache.get(key) != null) {
            cache.remove(key);
        }
    }

    /**
     * Returns the object at the given key
     * @param key - key to identify the object value by
     * @return - object at the given key
     */
    private Object get(String key) {
        return cache.get(key);
    }

    /**
     * Clears the map of all entries
     */
    private void clear() {
        cache.clear();
    }


    /**
     * Adds a user to the cache
     * @param user - user to be added
     */
    public void addLoggedInUserToCache(Object user) {
        add("user_logged_in", user);
    }

    /**
     * Removes the logged in user from the cache
     */
    public void rmLoggedInUserCache() {
        remove("user_logged_in");
    }

    /**
     *  Gets the logged in user
     * @return - user object
     */
    public Object getLoggedInUser() {
        return get("user_logged_in");
    }

    /**
     *  Gets the target patient that is currently being viewed
     * @return - Patient that is being viewed
     */
    public Patient getTargetPatient() {
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
    public void setTargetPatient(Patient patient) {
        add("target_patient", patient);
    }

    /**
     * Clears cache, removes all key value pairs
     */
    public void clearCahce(){
        clear();
    }


}
