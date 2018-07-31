package DataAccess;

import model.Medication;
import model.Procedure;

import java.util.List;

public interface IProcedureDataAccess {

    /**
     *
     * @param nhi -
     * @param procedure -
     * @return -
     */
    int update(String nhi, Procedure procedure);


    /**
     *
     * @param nhi -
     * @return -
     */
    List<Procedure> select(String nhi);
}
