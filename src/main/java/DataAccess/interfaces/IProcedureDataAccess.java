package DataAccess.interfaces;

import model.Procedure;

import java.util.List;

public interface IProcedureDataAccess {

    int updateProcedure (String nhi, Procedure procedure);

    List<Procedure> getProceduresByNhi (String nhi);

    void deleteAllProceduresByNhi(String nhi);
}
