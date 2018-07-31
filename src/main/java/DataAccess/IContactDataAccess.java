package DataAccess;

import model.Patient;

import java.util.List;

public interface IContactDataAccess {

    boolean update(Patient patient);

    List<String> select(String nhi);

    boolean delete();
}
