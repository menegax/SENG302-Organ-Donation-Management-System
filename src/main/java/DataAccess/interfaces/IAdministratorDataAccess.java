package DataAccess.interfaces;

import model.Administrator;

import java.util.List;

public interface IAdministratorDataAccess {

    public int updateAdministrator (List<Administrator> clinician);

    public boolean addAdministrator (Administrator clinician);

    public Administrator getAdministrator (String username, String password);

    List<Administrator> searchAdministrator (String searchTerm);
}
