package data_access.localDAO;

import data_access.factories.LocalDatabaseFactory;
import data_access.interfaces.IAdministratorDataAccess;
import model.Administrator;
import utility.Searcher;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class AdministratorLocalDAO implements IAdministratorDataAccess {
    private LocalDB localDB;

    public AdministratorLocalDAO() {
        localDB = LocalDatabaseFactory.getLocalDbInstance();
    }


    @Override
    public void saveAdministrator(Set<Administrator> administrators) {
        administrators.forEach(x -> {
            localDB.storeAdministrator(x);
            Searcher.getSearcher().updateIndex(x);
        });
    }

    @Override
    public void addAdministrator(Administrator administrator) {
        localDB.storeAdministrator(administrator);
    }

    @Override
    public boolean deleteAdministrator(Administrator administrator){
        Searcher.getSearcher().removeIndex(administrator);
        return localDB.deleteUser(administrator);
    }

    @Override
    public Administrator getAdministratorByUsername(String username) {
        return localDB.getAdministratorByUsername(username);
    }

    @Override
    public Map<Integer, List<Administrator>> searchAdministrators(String searchTerm) {
        return null;
    }

    @Override
    public Set<Administrator> getAdministrators() {
        return localDB.getAdministrators();
    }
}
