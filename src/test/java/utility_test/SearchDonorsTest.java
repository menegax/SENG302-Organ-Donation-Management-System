package utility_test;

import static org.junit.Assert.*;
import static utility.UserActionHistory.userActions;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.logging.Level;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import model.Donor;
import service.Database;
import utility.SearchDonors;

public class SearchDonorsTest {

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

	private static Donor d1;
	private static Donor d2;
	private static Donor d3;
	private static Donor d4;
	
    /**
     * Populate database with test donors and disables logging
     */
    @BeforeClass
    public static void setUp() {

        userActions.setLevel(Level.OFF);
        Database.resetDatabase();
        
        // Given donors in a db
        d1 = new Donor("abc1234", "Pat", null, "Laff", LocalDate.now());
        d2 = new Donor("def1234", "Patik", null, "Laffey", LocalDate.now());
        d3 = new Donor("ghi1234", "George", null, "Romera", LocalDate.now());
        d4 = new Donor("jkl1234", "George", null, "Bobington", LocalDate.now());
        Database.addDonor(d4);
        Database.addDonor(d3);
        Database.addDonor(d2);
        Database.addDonor(d1);

        SearchDonors.clearIndex();

        // Given an index
        SearchDonors.createFullIndex();
    }


    @Test
    public void testSearchByName() throws IOException {
    	//Bug with setup() means it has to be copied here or wont work
        Database.resetDatabase();

        // Given donors in a db
        d1 = new Donor("abc1234", "Pat", null, "Laff", LocalDate.now());
        d2 = new Donor("def1234", "Patik", null, "Laffey", LocalDate.now());
        d3 = new Donor("ghi1234", "George", null, "Romera", LocalDate.now());
        d4 = new Donor("jkl1234", "George", null, "Bobington", LocalDate.now());
        Database.addDonor(d4);
        Database.addDonor(d3);
        Database.addDonor(d2);
        Database.addDonor(d1);

        SearchDonors.clearIndex();

        // Given an index
        SearchDonors.createFullIndex();

        // When index searched for a single specific donor
        ArrayList<Donor> results = SearchDonors.searchByName("Pat Bobinton");

        // Should contain Pat Laff
        assertTrue(results.contains(Database.getDonorByNhi("abc1234")));
        // Should contain Patik Laffey
        assertTrue(results.contains(Database.getDonorByNhi("def1234")));
        // Shouldn't contain George Romera
        assertFalse(results.contains(Database.getDonorByNhi("ghi1234")));
        // Should contain George Bobington
        assertTrue(results.contains(Database.getDonorByNhi("jkl1234")));
    }


    /**
     *
     */
    @Test
    public void testSearchAfterNameUpdate() throws IOException {

        // When first name of donor changed
        Database.getDonorByNhi("abc1234").setFirstName("Andrew");

        // Then searching by new first name returns correct results
        ArrayList<Donor> results = SearchDonors.searchByName("Ande Lafey");
 
        assertTrue(results.contains(Database.getDonorByNhi("abc1234")));
    }


    @Test
    public void testSearchAfterNhiUpdate() throws IOException {
    	//Bug with setup() means it has to be copied here or wont work
        Database.resetDatabase();

        // Given donors in a db
        d1 = new Donor("abc1234", "Pat", null, "Laff", LocalDate.now());
        d2 = new Donor("def1234", "Patik", null, "Laffey", LocalDate.now());
        d3 = new Donor("ghi1234", "George", null, "Romera", LocalDate.now());
        d4 = new Donor("jkl1234", "George", null, "Bobington", LocalDate.now());
        Database.addDonor(d4);
        Database.addDonor(d3);
        Database.addDonor(d2);
        Database.addDonor(d1);

        SearchDonors.clearIndex();

        // Given an index
        SearchDonors.createFullIndex();
    	String name = Database.getDonorByNhi("def1234").getFirstName();
    	Database.getDonorByNhi("def1234").setNhiNumber("def5678");

    	ArrayList<Donor> results = SearchDonors.searchByName(name);

    	assertTrue(results.contains(Database.getDonorByNhi("def5678")));
    }

    @Test
    public void testSearchUnusualNameResults() throws InvalidObjectException {
        Database.resetDatabase();

        // Given donors in a db
        d1 = new Donor("abc9876", "Joe", null, "Plaffer", LocalDate.now());
        d2 = new Donor("def9876", "Johnothan", null, "zzne", LocalDate.now());
        d3 = new Donor("ghi9876", "John", null, "Romera", LocalDate.now());
        d4 = new Donor("jkl9876", "Samantha", null, "Fon", LocalDate.now());
        Database.addDonor(d4);
        Database.addDonor(d3);
        Database.addDonor(d2);
        Database.addDonor(d1);
        /* if comment out addDonor d3, d1, or both, it passes*/

        SearchDonors.clearIndex();

        // Given an index
        SearchDonors.createFullIndex();

        // When searching donors
    	ArrayList<Donor> results = SearchDonors.searchByName("Jone");

    	// Should contain Joe Plaffer, remove 'n' for Joe
    	assertTrue(results.contains(d1));

    	// Should contain Johnothan Doe, remove 'n' and replace 'J' with 'D' for Doe
//    	assertTrue(results.contains(d2)); //todo re-implement

    	// Should contain John Romera, remove 'e' and insert 'h' before 'n' for John
    	assertTrue(results.contains(d3));

    	// Should contain Samantha Fon, remove 'e' and replace 'J' with 'F' for Fon
    	assertTrue(results.contains(d4));
    }

    @Test
    public void testSearchUnusualNameResults2() throws InvalidObjectException {
        Database.resetDatabase();

        // Given donors in a db
        d1 = new Donor("abc9876", "Joe", null, "Plaffer", LocalDate.now());
        d2 = new Donor("def9876", "Jane", null, "Gregor", LocalDate.now());
        d3 = new Donor("ghi9876", "John", null, "Romera", LocalDate.now());
        d4 = new Donor("jkl9876", "Samantha", null, "Done", LocalDate.now());
        Database.addDonor(d4);
        Database.addDonor(d3);
        Database.addDonor(d2);
        Database.addDonor(d1);

        SearchDonors.clearIndex();

        // Given an index
        SearchDonors.createFullIndex();

    	ArrayList<Donor> results = SearchDonors.searchByName("Joe");

    	// Should contain Joe Plaffer
    	assertTrue(results.contains(Database.getDonorByNhi("abc9876")));

    	// Should contain Jane Gregor, insert 'n' before 'e' and replace 'o' with 'a' for Jane
    	assertTrue(results.contains(Database.getDonorByNhi("def9876")));

    	// Should contain John Romera, replace 'e' with 'n' and insert 'h' before 'n' for John
    	assertTrue(results.contains(Database.getDonorByNhi("ghi9876")));

    	// Should contain Samantha Done, insert 'n' before 'e' and replace 'J' with 'D' for Done
    	assertTrue(results.contains(Database.getDonorByNhi("jkl9876")));
    }

    /**
     * Reset the logging level
     */
    @AfterClass
    public static void tearDown() {
    	Database.resetDatabase();
    	SearchDonors.clearIndex();
        userActions.setLevel(Level.INFO);
    }

}
