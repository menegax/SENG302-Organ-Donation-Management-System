package data_access.interfaces;

import java.util.List;

public interface ILogDataAccess<T> {

    /**
     * Saves the given list of logs for a user
     *
     * @param records The list of log records for the user
     * @param id      The id of the user
     */
    void saveLogs(List<T> records, String id);

    /**
     * Fetches all logs for a given user
     *
     * @param id The id of the user
     * @return The list of log records for the user
     */
    List<T> getAllLogsByUserId(String id);

    /**
     * Removes all log records for a given user
     *
     * @param id The id of the user
     */
    void deleteLogsByUserId(String id);
}
