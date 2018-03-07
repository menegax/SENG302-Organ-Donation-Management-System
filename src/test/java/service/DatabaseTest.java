package service;

import model.Donor;
import org.junit.Before;
import org.junit.Test;

import java.io.InvalidObjectException;
import java.time.LocalDate;
import java.util.HashSet;

import static org.junit.Assert.*;

public class DatabaseTest {

    private static Donor bob;


    @Before
    public void restoreDatabase(){
        Database.resetDatabase();
        setup();
    }
    /**
     * Setup before all unit tests run
     */
    public static void setup() {
        bob = new Donor(1381421476, "Bob", null,
                "Wallace", LocalDate.of(1995, 12, 31));
        Database.addDonor(bob);

    }

    /**
     * Try find a donor who doesn't exist
     * @throws InvalidObjectException
     */
    @Test(expected = InvalidObjectException.class)
    public void getDonorByIncorrectIrd() throws  InvalidObjectException{
        Database.getDonorByIrd(1);
    }

    /**
     * Try find a donor who does exist
     */
    @Test
    public void getDonorByCorrectIrd(){
        Donor testDonor = null;
        try {
            testDonor = Database.getDonorByIrd(1381421476);
        } catch (InvalidObjectException e) {
            e.printStackTrace();
        }
        assertEquals(testDonor,bob);
    }


    /**
     * Try adding donors to database
     */
    @Test
    public void testAddDonor(){
        Database.addDonor(bob);
        assertEquals(new HashSet<Donor>() { {add(bob);add(bob);} }, Database.getDonors());
    }

    /**
     * Try removing donors from the database
     */
    @Test
    public void removeDonor(){
        try {
            Database.removeDonor(bob.getIrdNumber());
        } catch (InvalidObjectException e) {
            e.printStackTrace();
        }
        assertEquals(new HashSet<Donor>(), Database.getDonors());
    }


}
