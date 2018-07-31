package DataAccess;

import java.util.List;

public class ClinicianLogDAO extends DataAccessBase implements ILogDataAccess<ClinicianLogDAO>{

    @Override
    public int update(List<ClinicianLogDAO> records, String id) {
        return 0;
    }

    @Override
    public List<ClinicianLogDAO> selectAll(String id) {
        return null;
    }
}
