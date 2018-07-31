package DataAccess;

import java.util.ArrayList;

public class AdministratorLogDAO extends DataAccessBase implements ILogDataAccess{
    @Override
    public <T> int update(ArrayList<T> records, String id) {
        return 0;
    }

    @Override
    public <T> ArrayList<T> selectAll() {
        return null;
    }
}
