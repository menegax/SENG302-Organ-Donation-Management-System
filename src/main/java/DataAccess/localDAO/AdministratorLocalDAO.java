package DataAccess.localDAO;

import DataAccess.factories.LocalDatabaseFactory;
import DataAccess.interfaces.IAdministratorDataAccess;
import model.Administrator;

import java.util.List;
import java.util.Set;

public class AdministratorLocalDAO implements IAdministratorDataAccess {
    private LocalDB localDB;

    public AdministratorLocalDAO() {
        localDB = LocalDatabaseFactory.getLocalDbInstance();
    }


    @Override
    public void saveAdministrator(Set<Administrator> administrators) {
        administrators.forEach(x -> localDB.storeAdministrator(x));
    }

    @Override
    public boolean addAdministrator(Administrator clinician) {
        return false;
    }

    @Override
    public boolean deleteAdministrator(Administrator administrator){ return false; }

    @Override
    public Administrator getAdministratorByUsername(String username) {
        return localDB.getAdministratorByUsername(username);
    }

    @Override
    public List<Administrator> searchAdministrator(String searchTerm) {
        return null;
    }

    @Override
    public Set<Administrator> getAdministrators() {
        return localDB.getAdministrators();
    }
}
