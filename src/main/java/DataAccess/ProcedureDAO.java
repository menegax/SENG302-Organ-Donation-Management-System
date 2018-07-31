package DataAccess;

import model.Procedure;

import java.util.List;

public class ProcedureDAO extends DataAccessBase implements IProcedureDataAccess{
    @Override
    public int update(String nhi, Procedure procedure) {
        return 0;
    }

    @Override
    public List<Procedure> select(String nhi) {
        return null;
    }
}
