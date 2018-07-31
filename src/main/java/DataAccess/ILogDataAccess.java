package DataAccess;

import java.util.ArrayList;

public interface ILogDataAccess {


    /**
     *
     * @param records -
     * @param id -
     * @param <T> -
     * @return -
     */
    public <T> int update(ArrayList<T> records, String id);


    /**
     *
     * @param <T> -
     * @return -
     */
    public <T> ArrayList<T> selectAll();
}
