package utility;

import model.Patient;
import service.Cache;

public class CacheHelper {

    /**
     * Adds a user to the cache
     *
     * @param user - user to be added
     */
    public void addLoggedInUserToCache(Object user) {
        Cache.add("user_logged_in", user);
    }


    /**
     * Removes the logged in user from the cache
     */
    public void rmLoggedInUserCache() {
        Cache.remove("user_logged_in");
    }


    /**
     * Gets the logged in user
     *
     * @return - user object
     */
    public Object getLoggedInUser() {
        return Cache.get("user_logged_in");
    }


    /**
     * Gets the target patient that is currently being viewed
     *
     * @return - Patient that is being viewed
     */
    public Patient getTargetPatient() {
        Object value = Cache.get("target_patient");
        if (value instanceof Patient) {
            return (Patient) value;
        }
        return null;
    }


    /**
     * Sets the patient to be viewed
     *
     * @param patient - Patient object to view
     */
    public void setTargetPatient(Patient patient) {
        Cache.add("target_patient", patient);
    }


    /**
     * Clears cache, removes all key value pairs
     */
    public void clearCache() {
        Cache.clear();
    }

}

