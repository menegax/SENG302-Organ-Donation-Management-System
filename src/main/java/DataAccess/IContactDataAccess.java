package DataAccess;

import model.Patient;

import java.util.List;

public interface IContactDataAccess {

    /**
     *
     * @param patient -
     * @return -
     */
    boolean update(Patient patient);

    /**
     *
     * @param nhi -
     * @return -
     */
    List<String> select(String nhi);

    /**
     *
     * @return -
     */
    boolean delete();
}
