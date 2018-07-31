package DataAccess;

import java.sql.Connection;
import java.util.List;

/**
 * @param <T>
 */
public interface ILogDataAccess<T> {


    /**
     * @param records -
     * @param id      -
     * @return -
     */
    int update(List<T> records, String id);


    /**
     * @return -
     */
    List<T> selectAll(String id);

    List<T> selectAll(Connection connection, String id);
}
