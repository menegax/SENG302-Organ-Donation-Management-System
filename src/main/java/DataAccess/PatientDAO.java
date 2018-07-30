package DataAccess;

public class PatientDAO extends DataAccessBase implements {



    @Override
    protected <T> int update(T object) {

    }

    @Override
    protected <T> boolean insert(T object) {
        return false;
    }

    @Override
    protected <T> T select() {
        return null;
    }
}
