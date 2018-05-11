package utility_test;

import static org.junit.Assert.*;
import static utility.UserActionHistory.userActions;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.logging.Level;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import model.Patient;
import service.Database;
import utility.SearchPatients;

public class SearchPatientTest {

    //	@Test
    //	public void testCreateFullIndex() {
    //		fail("Not yet implemented");
    //	}
    //
    //	@Test
    //	public void testAddIndex() {
    //		fail("Not yet implemented");
    //	}
    //
    //	@Test
    //	public void testCloseIndex() {
    //		fail("Not yet implemented");
    //	}

	private static Patient d1;
	private static Patient d2;
	private static Patient d3;
	private static Patient d4;
	
    /**
     * Populate database with test donors and disables logging
     */
    @BeforeClass
    public static void setUp() {

        userActions.setLevel(Level.OFF);
        Database.resetDatabase();
        
        // Given donors in a db
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

//    /**
//     *
//     */
//    @Test
//    public void testSearchAfterUpdatePatient() throws IOException {
//
//        // When first name of donor changed
//        Database.getPatientByNhi("abc1234").setFirstName("Andrew");
//
//        // Then searching by new first name returns correct results
//        ArrayList<Patient> results = SearchPatients.searchByName("Ande Lafey");
//
//        assertTrue(results.contains(Database.getPatientByNhi("abc1234")));
//    }


    /**
     * Reset the logging level
     */
    @AfterClass
    public static void tearDown() {

        userActions.setLevel(Level.INFO);
    }

}
