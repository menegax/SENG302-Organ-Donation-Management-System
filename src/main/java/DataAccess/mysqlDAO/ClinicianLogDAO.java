package DataAccess.mysqlDAO;

import DataAccess.interfaces.ILogDataAccess;

import java.util.List;

public class ClinicianLogDAO implements ILogDataAccess<ClinicianLogDAO> {

    @Override
    public int updateLogs(List<ClinicianLogDAO> records, String id) {
        return 0;
    }

    @Override
    public List<ClinicianLogDAO> getAllLogsByUserId(String id) {
        return null;
    }
}
