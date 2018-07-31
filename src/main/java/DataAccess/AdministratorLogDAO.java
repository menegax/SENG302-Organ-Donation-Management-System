package DataAccess;

import utility.AdministratorActionRecord;

import java.util.List;

public class AdministratorLogDAO extends DataAccessBase implements ILogDataAccess<AdministratorActionRecord>{

    @Override
    public int update(List records, String id) {
        return 0;
    }

    @Override
    public List selectAll(String id) {
        return null;
    }
}
