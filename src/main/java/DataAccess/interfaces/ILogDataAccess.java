package DataAccess.interfaces;

import java.sql.Connection;
import java.util.List;

/**
 * @param <T>
 */
public interface ILogDataAccess<T> {


    int updateLogs(List<T> records, String id);


    List<T> getAllLogsByUserId(String id);

    boolean deleteLogsByUserId(String id);

}
