package data_access_test;

import data_access.DBHelper;
import data_access.factories.DAOFactory;
import model.Clinician;
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

public class MYSQLClinicianDAOTest {
    private static DAOFactory daoFactory;
    private static DBHelper dbHelper;
    private Clinician clinician;
    private Map<Integer, List<Clinician>> searchResults;

    @BeforeClass
    @SuppressWarnings("Duplicates")
    public static void setUp() {
        userActions.setLevel(OFF);
        SystemLogger.systemLogger.setLevel(OFF);
        daoFactory = DAOFactory.getDAOFactory(GlobalEnums.FactoryType.MYSQL);
        dbHelper = new DBHelper();
    }

    private void givenClinician() {
        clinician = new Clinician(5, "Michael", new ArrayList<>(), "Scott", GlobalEnums.Region.AUCKLAND);
    }

    private void whenClinicianSaved() {
        daoFactory.getClinicianDataAccess().saveClinician(new HashSet<Clinician>() {{
            add(clinician);
        }});
    }

    private void thenClinicianIsInDB() {
        Clinician parsed = daoFactory.getClinicianDataAccess().getClinicianByStaffId(5);
        assert parsed.getFirstName().equals("Michael");
        assert parsed.getRegion() == GlobalEnums.Region.AUCKLAND;
    }

    private void whenSearched(String searchTerm) {
        searchResults = daoFactory.getClinicianDataAccess().searchClinicians(searchTerm);
    }

    private void thenSearchHasResults() {
        boolean found = false;
        for (List<Clinician> cliList : searchResults.values()) {
            if (cliList.contains(clinician)) {
                found = true;
            }
        }
        assert found;
    }

    private void thenSearchHasNoResults() {
        boolean found = false;
        for (List<Clinician> cliList : searchResults.values()) {
            if (cliList.contains(clinician)) {
                found = true;
            }
        }
        assert !found;
    }

    @Test
    public void testClinicianSave() {
        givenClinician();
        whenClinicianSaved();
        thenClinicianIsInDB();
    }

    @Test
    public void testClinicianMatchingSearch() {
        givenClinician();
        whenClinicianSaved();
        whenSearched("5");
        thenSearchHasResults();
    }

    @Test
    public void testClinicianNonMatchingSearch() {
        givenClinician();
        whenClinicianSaved();
        whenSearched("michelangelo");
        thenSearchHasNoResults();
    }

    @AfterClass
    public static void reset() {
        dbHelper.reset();
    }
}
