package controller;

import static java.util.logging.Level.INFO;
import static utility.SystemLogger.systemLogger;

import model.User;
import utility.undoRedo.UndoableWrapper;

import java.util.HashMap;
import java.util.Map;

public class UserControl {

    private final Map<UndoableWrapper, User> users = new HashMap<>();

    private User loggedInUser;

    private static UserControl userControl;


    private UserControl() {
    }


    /**
     * Getter to enable this to be a singleton
     *
     * @return the single UserControl object
     */
    public static UserControl getUserControl() {
        if (userControl == null) {
            userControl = new UserControl();
        }
        return userControl;
    }


    /**
     * Adds entries to the map
     *
     * @param key   - key to identify the value by
     * @param value - value to store
     */
    private void add(UndoableWrapper key, User value) {
        if (key != null && value != null) {
            users.put(key, value);
        }
    }


    /**
     * Remove an entry from the map
     *
     * @param key - key value to be removed
     */
    private void remove(UndoableWrapper key) {
        if (users.get(key) != null) {
            users.remove(key);
        }
    }


    /**
     * Returns the object at the given key
     *
     * @param key - key to identify the object value by
     * @return - object at the given key
     */
    private User get(UndoableWrapper key) {
        return users.get(key);
    }


    /**
     * Adds a user to the cache
     *
     * @param user - user to be added
     */
    void addLoggedInUserToCache(User user) {
        loggedInUser = user;
    }


    /**
     * Gets the logged in user
     *
     * @return - user object
     */
    public User getLoggedInUser() {
        return loggedInUser;
    }


    public boolean isUserLoggedIn() {
        return loggedInUser != null;
    }


    /**
     * Gets the target user that is currently being viewed
     * @param undoableWrapper
     * @return
     */
    public User getTargetUser(UndoableWrapper undoableWrapper) {
        return get(undoableWrapper);
    }


    /**
     * Sets the user to be viewed
     *
     * @param user - Patient object to view
     */
    void setTargetUser(User user, UndoableWrapper undoableWrapper) {
        add(undoableWrapper, user);
    }


    /**
     * Clears the target user record
     */
    void clearTargetUser(UndoableWrapper undoableWrapper) {
        remove(undoableWrapper);
    }


    /**
     * Clears cache, removes all key value pairs
     */
    void clearCache() {
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
        loggedInUser = null;
        systemLogger.log(INFO, "All users have been logged out");
    }

}
