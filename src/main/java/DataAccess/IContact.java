package DataAccess;

import model.Disease;
import model.Patient;

import java.util.List;

public interface IContact {

    boolean update(Patient patient);

    List<Disease> select();

    boolean delete();
}
