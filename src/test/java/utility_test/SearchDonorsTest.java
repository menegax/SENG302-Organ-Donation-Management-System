package utility_test;

import static org.junit.Assert.*;
import static utility.UserActionHistory.userActions;

import java.io.IOException;
import java.io.InvalidObjectException;
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


    /**
     * Populate database with test donors and disables logging
     */
    @BeforeClass
    public static void setUp() {

        userActions.setLevel(Level.OFF);
        Database.resetDatabase();
    }


    //todo rework
    @Test
    public void testSearchByName() throws IOException {

        // Given donors in a db
        Donor d1 = new Donor("abc1234", "Pat", null, "Laff", LocalDate.now());
        Donor d2 = new Donor("def1234", "Patik", null, "Laffey", LocalDate.now());
        Donor d3 = new Donor("ghi1234", "George", null, "Romera", LocalDate.now());
        Database.addDonor(d3);
        Database.addDonor(d2);
        Database.addDonor(d1);

        // Given an index
        SearchDonors.createFullIndex();

        // When index searched for a single specific donor
        ArrayList<Donor> results = SearchDonors.searchByName("Pati Laffe");

        // Should return that donor and no other
        assertTrue(results.contains(d1));

    }


    /**
     *
     */
    @Test
    public void testSearchAfterUpdateDonor() throws IOException {

        // Given donor in db
        Donor donor1 = new Donor("abc4321", "Pat", null, "Laff", LocalDate.now());
        Database.addDonor(donor1);

        // given an index
        SearchDonors.createFullIndex();

        // When first name of donor changed
        Database.getDonorByNhi("abc4321")
                .setFirstName("Andrew");

        // Then searching by new first name returns correct results
        ArrayList<Donor> results = SearchDonors.searchByName("Andre Laf");
        assertTrue(results.contains(Database.getDonorByNhi("abc4321")));
    }


    /**
     * Reset the logging level
     */
    @AfterClass
    public static void tearDown() {

        userActions.setLevel(Level.INFO);
    }

}
