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

        // Given an index
        SearchDonors.createFullIndex();
    }

    /**
     *
     */
    @Test
    public void testSearchAfterUpdateDonor() throws IOException {

        // When first name of donor changed
        Database.getDonorByNhi("abc1234").setFirstName("Andrew");

        // Then searching by new first name returns correct results
        ArrayList<Donor> results = SearchDonors.searchByName("Ande Lafey");
 
        assertTrue(results.contains(Database.getDonorByNhi("abc1234")));
    }


    /**
     * Reset the logging level
     */
    @AfterClass
    public static void tearDown() {

        userActions.setLevel(Level.INFO);
    }

}
