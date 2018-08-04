package DataAccess.interfaces;

import model.Administrator;

import java.util.List;
import java.util.Set;

public interface IAdministratorDataAccess {

    public void saveAdministrator (Set<Administrator> administrators);

    public boolean addAdministrator (Administrator administrator);

    public boolean deleteAdministrator (Administrator administrator);

    public Administrator getAdministratorByUsername (String username);

    public List<Administrator> searchAdministrator (String searchTerm);

    public Set<Administrator> getAdministrators();
}
