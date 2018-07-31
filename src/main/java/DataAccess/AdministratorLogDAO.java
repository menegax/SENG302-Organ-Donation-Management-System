package DataAccess;

import utility.AdministratorActionRecord;

import java.sql.Connection;
import java.util.List;

public class AdministratorLogDAO extends DataAccessBase implements ILogDataAccess<AdministratorActionRecord> {

    @Override
    public int update(List records, String id) {
        return 0;
    }

    @Override
    public List selectAll(String id) {
        return null;
    }

    @Override
    public List<AdministratorActionRecord> selectAll(Connection connection, String id) {
        return null;
    }
}
