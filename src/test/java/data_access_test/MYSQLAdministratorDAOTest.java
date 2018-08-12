package data_access_test;

import data_access.DBHelper;
import data_access.factories.DAOFactory;
import model.Administrator;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import utility.GlobalEnums;
import utility.SystemLogger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static java.util.logging.Level.OFF;
import static utility.UserActionHistory.userActions;

public class MYSQLAdministratorDAOTest {
    private static DAOFactory daoFactory;
    private static DBHelper dbHelper;
    private Administrator administrator;
    private Map<Integer, List<Administrator>> searchResults;

    @BeforeClass
    @SuppressWarnings("Duplicates")
    public static void setUp() {
        userActions.setLevel(OFF);
        SystemLogger.systemLogger.setLevel(OFF);
        daoFactory = DAOFactory.getDAOFactory(GlobalEnums.FactoryType.MYSQL);
        dbHelper = new DBHelper();
    }

    private void givenAdministrator() {
        administrator = new Administrator("kingadmin", "Issac", new ArrayList<>(), "Newton", "hunter2");
    }

    private void whenAdministratorSaved() {
        daoFactory.getAdministratorDataAccess().saveAdministrator(new HashSet<Administrator>() {{
            add(administrator);
        }});
    }

    private void thenAdministratorIsInDB() {
        Administrator parsed = daoFactory.getAdministratorDataAccess().getAdministratorByUsername("kingadmin");
        assert parsed.getFirstName().equals("Issac");
    }

    private void whenSearched(String searchTerm) {
        searchResults = daoFactory.getAdministratorDataAccess().searchAdministrators(searchTerm);
    }

    private void thenSearchHasResults() {
        boolean found = false;
        for (List<Administrator> adList : searchResults.values()) {
            if (adList.contains(administrator)) {
                found = true;
            }
        }
        assert found;
    }

    private void thenSearchHasNoResults() {
        boolean found = false;
        for (List<Administrator> adList : searchResults.values()) {
            if (adList.contains(administrator)) {
                found = true;
            }
        }
        assert !found;
    }

    @Test
    public void testCAdministratorSave() {
        givenAdministrator();
        whenAdministratorSaved();
        thenAdministratorIsInDB();
    }

    @Test
    public void testAdminMatchingSearch() {
        givenAdministrator();
        whenAdministratorSaved();
        whenSearched("kingadmin");
        thenSearchHasResults();
    }

    @Test
    public void testClinicianNonMatchingSearch() {
        givenAdministrator();
        whenAdministratorSaved();
        whenSearched("frederick");
        thenSearchHasNoResults();
    }

    @AfterClass
    public static void reset() {
        dbHelper.reset();
    }
}
