package DataAccess.mysqlDAO;

import DataAccess.interfaces.ILogDataAccess;
import utility.AdministratorActionRecord;


import java.util.List;

public class AdministratorLogDAO  implements ILogDataAccess<AdministratorActionRecord> {

    @Override
    public int updateLogs(List<AdministratorActionRecord> records, String id) {
        return 0;
    }

    @Override
    public List<AdministratorActionRecord> getAllLogsByUserId(String id) {
        return null;
    }

    @Override
    public boolean deleteLogsByUserId(String id) {
        return false;
    }
}
