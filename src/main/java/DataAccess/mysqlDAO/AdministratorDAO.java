package DataAccess.mysqlDAO;

import DataAccess.interfaces.IAdministratorDataAccess;
import model.Administrator;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;
import java.util.Set;

public class AdministratorDAO implements IAdministratorDataAccess {


    @Override
    public int saveAdministrator(Set<Administrator> administrators) {
        return 0;
    }

    @Override
    public boolean addAdministrator(Administrator clinician) {
        return false;
    }

    @Override
    public boolean deleteAdministrator(Administrator administrator) { return false; }

    @Override
    public Administrator getAdministratorByUsername(String username) {
        return null;
    }

    @Override
    public List<Administrator> searchAdministrator(String searchTerm) {
        return null;
    }

    @Override
    public Set<Administrator> getAdministrators() {
        throw new NotImplementedException();
    }
}
