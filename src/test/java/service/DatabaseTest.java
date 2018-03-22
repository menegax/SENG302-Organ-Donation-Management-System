package service;

import model.Donor;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.io.InvalidObjectException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;

import static org.junit.Assert.*;
import static utility.UserActionHistory.userActions;

public class DatabaseTest {

    private static Donor bob;

    /**
     * Restores database before each test and disables console logging
     */
    @Before
    public void restoreDatabase(){
        Database.resetDatabase();

        userActions.setLevel(Level.OFF);

        bob = new Donor("ZZZ1234", "Bob", null,
                "Wallace", LocalDate.of(1995, 12, 31));
        Database.addDonor(bob);
    }

    /**
     * Try find a donor who doesn't exist
     * @throws InvalidObjectException when donor object cannot be found
     */
    @Test(expected = InvalidObjectException.class)
    public void getDonorByIncorrectNhi() throws  InvalidObjectException{
        Database.getDonorByNhi("Z");
    }

    /**
     * Try find a donor who does exist
     */
    @Test
    public void getDonorByCorrectNhi(){
        Donor testDonor = null;
        try {
            testDonor = Database.getDonorByNhi("ZZZ1234");
        } catch (InvalidObjectException e) {
            e.printStackTrace();
        }
        assertEquals(testDonor,bob);
    }


    /**
     * Try adding donors to database
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddDonor(){
        Database.addDonor(bob);
    }

    /**
     * Try removing donors from the database
     */
    @Test
    public void removeDonor(){
        try {
            Database.removeDonor(bob.getNhiNumber());
        } catch (InvalidObjectException e) {
            e.printStackTrace();
        }
        assertEquals(new HashSet<Donor>(), Database.getDonors());
    }

    /**
     * Reset the logging level
     */
    @AfterClass
    public static void tearDown() {

        userActions.setLevel(Level.INFO);
    }


}
