package DataAccess.interfaces;

import model.Administrator;
import model.Patient;

import java.util.List;

public interface IAdministratorDataAccess {

    public int updateAdministrator (List<Administrator> clinician);

    public boolean addAdministrator (Administrator clinician);

    public Patient getAdministrator (String username, String password);

    List<Patient> searchAdministrator (String searchTerm);
}
