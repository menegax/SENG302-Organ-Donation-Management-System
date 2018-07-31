package DataAccess;

import model.Procedure;

import java.sql.Connection;
import java.util.List;

public interface IProcedureDataAccess {

    /**
     * @param nhi       -
     * @param procedure -
     * @return -
     */
    int update(String nhi, Procedure procedure);


    /**
     * @param nhi -
     * @return -
     */
    List<Procedure> select(String nhi);

    List<Procedure> select(Connection connection, String nhi);
}
