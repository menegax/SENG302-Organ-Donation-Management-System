package utility_test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static utility.UserActionHistory.userActions;

import java.io.IOException;
import java.io.InvalidObjectException;
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

        SearchPatients.clearIndex();
        
        // Given an index
        SearchPatients.createFullIndex();
    }

//    /**
//     *
//     */
//    @Test
//    public void testSearchAfterUpdatePatient() throws IOException {
//
//        // When first name of patient changed
//        Database.getPatientByNhi("abc1234").setFirstName("Andrew");
//
//        // Then searching by new first name returns correct results
//        ArrayList<Patient> results = SearchPatients.searchByName("Ande Lafey");
//
//        assertTrue(results.contains(Database.getPatientByNhi("abc1234")));
//    }
    
    
    @Test
    public void testSearchByName() throws IOException {
    	//Bug with setup() means it has to be copied here or wont work
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

        SearchPatients.clearIndex();

        // Given an index
        SearchPatients.createFullIndex();

        // When index searched for a single specific patient
        ArrayList<Patient> results = SearchPatients.searchByName("Pat Bobinton");

        // Should contain Pat Laff
        assertTrue(results.contains(Database.getPatientByNhi("abc1234")));
        // Should contain Patik Laffey
        assertTrue(results.contains(Database.getPatientByNhi("def1234")));
        // Shouldn't contain George Romera
        assertFalse(results.contains(Database.getPatientByNhi("ghi1234")));
        // Should contain George Bobington
        assertTrue(results.contains(Database.getPatientByNhi("jkl1234")));
    }


    /**
     *
     */
    @Test
    public void testSearchAfterNameUpdate() throws IOException {

        // When first name of patient changed
        Database.getPatientByNhi("abc1234").setFirstName("Andrew");

        // Then searching by new first name returns correct results
        ArrayList<Patient> results = SearchPatients.searchByName("Ande Lafey");
 
        assertTrue(results.contains(Database.getPatientByNhi("abc1234")));
    }


    @Test
    public void testSearchAfterNhiUpdate() throws IOException {
    	//Bug with setup() means it has to be copied here or wont work
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

        SearchPatients.clearIndex();

        // Given an index
        SearchPatients.createFullIndex();
    	String name = Database.getPatientByNhi("def1234").getFirstName();
    	Database.getPatientByNhi("def1234").setNhiNumber("def5678");

    	ArrayList<Patient> results = SearchPatients.searchByName(name);

    	assertTrue(results.contains(Database.getPatientByNhi("def5678")));
    }

    @Test
    public void testSearchUnusualNameResults() throws InvalidObjectException {
        Database.resetDatabase();

        // Given patients in a db
        d1 = new Patient("abc9876", "Joe", null, "Plaffer", LocalDate.now());
        d2 = new Patient("def9876", "Johnothan", null, "zzne", LocalDate.now());
        d3 = new Patient("ghi9876", "John", null, "Romera", LocalDate.now());
        d4 = new Patient("jkl9876", "Samantha", null, "Fon", LocalDate.now());
        Database.addPatient(d4);
        Database.addPatient(d3);
        Database.addPatient(d2);
        Database.addPatient(d1);
        /* if comment out addPatient d3, d1, or both, it passes*/

        SearchPatients.clearIndex();

        // Given an index
        SearchPatients.createFullIndex();

        // When searching patients
    	ArrayList<Patient> results = SearchPatients.searchByName("Jone");

    	// Should contain Joe Plaffer, remove 'n' for Joe
    	assertTrue(results.contains(d1));

    	// Should contain Johnothan Doe, remove 'n' and replace 'J' with 'D' for Doe
    	assertTrue(results.contains(d2));

    	// Should contain John Romera, remove 'e' and insert 'h' before 'n' for John
    	assertTrue(results.contains(d3));

    	// Should contain Samantha Fon, remove 'e' and replace 'J' with 'F' for Fon
    	assertTrue(results.contains(d4));
    }

    /**
     * Reset the logging level
     */
    @AfterClass
    public static void tearDown() {

        userActions.setLevel(Level.INFO);
    }

}
