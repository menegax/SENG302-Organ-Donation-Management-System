package utility_test;

import static junit.framework.TestCase.assertTrue;
import static utility.UserActionHistory.userActions;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.logging.Level;

import controller.Main;
import model.Patient;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import org.junit.Test;
import service.Database;
import utility.SearchPatients;

public class SearchPatientsTest {

    /**
     * Populate database with test patients and disables logging
     */
    @BeforeClass
    public static void setUp() {

        userActions.setLevel(Level.OFF);
        Database.resetDatabase();

        // Given patients in a db
        Database.addPatient(new Patient("abc1234", "Pat", null, "Laff", LocalDate.now()));
        Database.addPatient(new Patient("def1234", "Patik", null, "Laffey", LocalDate.now()));
        Database.addPatient(new Patient("ghi1234", "George", null, "Romera", LocalDate.now()));
        Database.addPatient(new Patient("jkl1234", "George", null, "Bobington", LocalDate.now()));

        // Given an index
        SearchPatients.createFullIndex(); //Todo shouldn't be necessary
    }


    /**
     * Ensures the search results are changed after a patient's attributes are changed
     *
     * @exception IOException when the patient's nhi cannot be found in the database
     */
    @Test
    public void testSearchAfterUpdatePatient() throws IOException {

        // Given

        // When first name of patient changed
        Database.getPatientByNhi("abc1234")
                .setFirstName("Andrew");

        // Then searching by new first name returns correct results
        ArrayList<Patient> results = SearchPatients.searchByName("Ande Lafey");
        assertTrue(results.contains(Database.getPatientByNhi("abc1234")));
    }


}
