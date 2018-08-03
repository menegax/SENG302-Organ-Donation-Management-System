package service.interfaces;

import model.Administrator;

public interface IAdministratorDataService {

    public void deletePatient(String nhi);

    public void importRecords();

    public void searchUsers();

    public Administrator getAdministratorByUsername(String username);

    public void save(Administrator administrator);
}
