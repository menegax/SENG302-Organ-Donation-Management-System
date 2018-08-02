package DataAccess.localDAO;

import DataAccess.LocalDB;
import DataAccess.interfaces.IAdministratorDataAccess;
import model.Administrator;

import java.util.List;

public class AdministratorLocalDAO implements IAdministratorDataAccess {
    @Override
    public int updateAdministrator(List<Administrator> clinician) {
        return 0;
    }

    @Override
    public boolean addAdministrator(Administrator clinician) {
        return false;
    }

    @Override
    public boolean deleteAdministrator(Administrator administrator){ return false; }

    @Override
    public Administrator getAdministratorByUsername(String username) {
        LocalDB localDB = LocalDB.getInstance();
        return localDB.getAdministratorByUsername(username);
    }

    @Override
    public List<Administrator> searchAdministrator(String searchTerm) {
        return null;
    }
}
