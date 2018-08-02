package DataAccess.interfaces;

import model.Administrator;

import java.util.List;

public interface IAdministratorDataAccess {

    public int updateAdministrator (List<Administrator> administrators);

    public boolean addAdministrator (Administrator administrator);

    public boolean deleteAdministrator (Administrator administrator);

    public Administrator getAdministratorByUsername (String username);

    List<Administrator> searchAdministrator (String searchTerm);
}
