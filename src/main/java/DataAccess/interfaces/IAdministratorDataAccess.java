package DataAccess.interfaces;

import model.Administrator;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IAdministratorDataAccess {

    void saveAdministrator(Set<Administrator> administrators);

    void addAdministrator(Administrator administrator);

    boolean deleteAdministrator(Administrator administrator);

    Administrator getAdministratorByUsername(String username);

    Map<Integer, List<Administrator>> searchAdministrators(String searchTerm);

    Set<Administrator> getAdministrators();
}
