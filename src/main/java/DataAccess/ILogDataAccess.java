package DataAccess;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @param <T>
 */
public interface ILogDataAccess<T> {


    /**
     *
     * @param records -
     * @param id -
     * @return -
     */
    public  int update(List<T> records, String id);


    /**
     *
     * @return -
     */
    public List<T> selectAll(String id);
}
