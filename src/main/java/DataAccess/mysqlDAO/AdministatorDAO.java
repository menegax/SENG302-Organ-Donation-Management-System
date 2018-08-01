package DataAccess.mysqlDAO;

import DataAccess.interfaces.IAdministratorDataAccess;
import model.Administrator;

import java.util.List;

public class AdministatorDAO implements IAdministratorDataAccess {

    @Override
    public int updateAdministrator(List<Administrator> clinician) {
        return 0;
    }

    @Override
    public boolean addAdministrator(Administrator clinician) {
        return false;
    }

    @Override
    public Administrator getAdministrator(String username, String password) {
        return null;
    }

    @Override
    public List<Administrator> searchAdministrator(String searchTerm) {
        return null;
    }
}
