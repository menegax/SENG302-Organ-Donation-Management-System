package utility_test;

import static junit.framework.TestCase.assertTrue;
import static utility.UserActionHistory.userActions;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.logging.Level;

import model.Patient;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import org.junit.Test;
import service.Database;
import utility.SearchPatients;

public class SearchPatientsTest {

	private static Patient d1;
	private static Patient d2;
	private static Patient d3;
	private static Patient d4;
	
    /**
     * Populate database with test patients and disables logging
     */
    @BeforeClass
    public static void setUp() {

        userActions.setLevel(Level.OFF);
        Database.resetDatabase();
        
        // Given patients in a db
        d1 = new Patient("abc1234", "Pat", null, "Laff", LocalDate.now());
        d2 = new Patient("def1234", "Patik", null, "Laffey", LocalDate.now());
        d3 = new Patient("ghi1234", "George", null, "Romera", LocalDate.now());
        d4 = new Patient("jkl1234", "George", null, "Bobington", LocalDate.now());
        Database.addPatient(d4);
        Database.addPatient(d3);
        Database.addPatient(d2);
        Database.addPatient(d1);

        // Given an index
        SearchPatients.createFullIndex();
    }

    /**
     * Ensures the search results are changed after a patient's attributes are changed
     *
     * @throws IOException when the patient's nhi cannot be found in the database
     */
    @Test
    public void testSearchAfterUpdatePatient() throws IOException {

            // When first name of patient changed
            Database.getPatientByNhi("abc1234").setFirstName("Andrew");

            // Then searching by new first name returns correct results
            ArrayList<Patient> results = SearchPatients.searchByName("Ande Lafey");

            assertTrue(results.contains(Database.getPatientByNhi("abc1234")));
        }


    /**
     * Reset the logging level
     */
    @AfterClass
    public static void tearDown() {

        userActions.setLevel(Level.INFO);
    }

}
