package DataAccess;

import java.sql.Connection;
import java.util.List;

public class ClinicianLogDAO extends DataAccessBase implements ILogDataAccess<ClinicianLogDAO> {

    @Override
    public int update(List<ClinicianLogDAO> records, String id) {
        return 0;
    }

    @Override
    public List<ClinicianLogDAO> selectAll(String id) {
        return null;
    }

    @Override
    public List<ClinicianLogDAO> selectAll(Connection connection, String id) {
        return null;
    }
}
