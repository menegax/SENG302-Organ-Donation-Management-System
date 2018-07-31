package DataAccess;

import model.Patient;

import java.sql.Connection;
import java.util.List;

public interface IContactDataAccess {

    boolean update(Patient patient);

    List<String> select(String nhi);

    List<String> select(Connection connection, String nhi);

    boolean delete();
}
