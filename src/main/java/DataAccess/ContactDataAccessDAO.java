package DataAccess;

import model.Disease;
import model.Patient;

import java.util.List;

public class ContactDataAccessDAO extends DataAccessBase implements IContactDataAccess {

    @Override
    public boolean update(Patient patient) {
        return false;
    }

    @Override
    public List<Disease> select() {
        return null;
    }

    @Override
    public boolean delete() {
        return false;
    }
}
