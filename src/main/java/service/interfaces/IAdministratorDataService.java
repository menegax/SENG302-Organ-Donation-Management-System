package service.interfaces;

import model.Administrator;
import model.User;

import java.util.List;

public interface IAdministratorDataService {

    void deletePatient(String nhi);

    void importRecords();

    List<User> searchUsers(String searchTerm);

    Administrator getAdministratorByUsername(String username);

    void save(Administrator administrator);
}
