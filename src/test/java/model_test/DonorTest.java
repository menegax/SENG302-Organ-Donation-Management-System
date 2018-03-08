package model_test;

import java.time.LocalDate;
import java.util.ArrayList;

import model.Donor;
import org.junit.BeforeClass;
import org.junit.Test;
import service.Database;
import utility.GlobalEnums.Organ;

import static org.junit.Assert.*;

public class DonorTest {

    private static Donor testDonor; //Donor obj not within the database

    /**
     * Populate database with test donors
     */
    @BeforeClass
    public static void setUp() {
        testDonor = new Donor(98765, "James", null, "Wallace",
                LocalDate.of(1970, 2, 12));

        Database.addDonor(new Donor(1234567, "Joe", new ArrayList<String>() {{
            add("Jane");
        }},
                "Bloggs", LocalDate.of(1994, 12, 12)));

        Database.addDonor(new Donor(7654321, "Bob", null, "Bobby",
                LocalDate.of(1994, 12, 12)));
    }

    /**
     * Test donor constructor
     */
    @Test
    public void testDonorConstructor() {
        Donor donor = givenDonor();
        thenDonorHasAttributes(donor);
    }

    /**
     * Try add a donor with the a conflicting IRD number,
     * expect IllegalArgument exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDonorAddIrdCollision() {
        new Donor(1234567, "Joe", new ArrayList<String>() {{
            add("Jane");
        }},
                "Bloggs", LocalDate.of(1994, 12, 12));
    }


    /**
     * Add a list of valid organs,
     * expect all items passed to be added to donations
     */
    @Test
    public void testUpdateDonationsMultiCorrectAdd() {
        ArrayList<String> addDonations = new ArrayList<String>() {{
            add("liver");
            add("lung");
        }};
        testDonor.updateDonations(addDonations, null);
        ArrayList<Organ> expected = new ArrayList<Organ>() {{
            add(Organ.LIVER);
            add(Organ.LUNG);
        }};
        assertEquals(expected, testDonor.getDonations());
    }


    /**
     * Add a list containing at least one invalid organ,
     * expect only liver to be added to donations
     */
    @Test
    public void testUpdateDonationsAddContainInvalid() {
        ArrayList<String> addDonations = new ArrayList<String>() {{
            add("liver");
            add("test");
        }};
        testDonor.updateDonations(addDonations, null);
        ArrayList<Organ> expected = new ArrayList<Organ>() {{
            add(Organ.LIVER);
        }};
        assertEquals(expected, testDonor.getDonations());
    }

    /**
     * Remove a valid organ from donations,
     * expect donations to contain no organs
     */
    @Test
    public void testUpdateDonationsRmValid() {
        addDonationsToDonor();
        ArrayList<String> rmDonations = new ArrayList<String>() {{
            add("liver");
        }};
        testDonor.updateDonations(null, rmDonations);
        ArrayList<Organ> expected = new ArrayList<Organ>() {{
            add(Organ.LUNG);
        }};
        assertEquals(expected, testDonor.getDonations());
        resetDonationsDonor();
    }

    /**
     * Add a list containing at least one invalid organ
     * expect only liver to be in donations
     */
    @Test
    public void testUpdateDonationsRmInvalid() {
        testDonor.addDonation(Organ.LIVER);
        ArrayList<String> rmDonations = new ArrayList<String>() {{
            add("liver");
            add("test");
        }};
        testDonor.updateDonations(null, rmDonations);
        ArrayList<Organ> expected = new ArrayList<>();
        assertEquals(expected, testDonor.getDonations());
        resetDonationsDonor();
    }

    /**
     * Pass two null arrays into updateDonation,
     * expect donations to be empty
     */
    @Test
    public void testUpdateDonationsAddRmNull() {
        testDonor.updateDonations(null, null);
        ArrayList<Organ> expected = new ArrayList<>();
        assertEquals(expected, testDonor.getDonations());
    }


    /**
     * Check name concat method with multiple middle names
     */
    @Test
    public void testGetNameConcatenatedWithMiddles() {
        setDonorNamesMultipleMiddle();
        assertEquals("Joe Jane Jarred Bloggs", testDonor.getNameConcatenated());
    }


    /**
     * Check name concat method with no middle names
     */
    @Test
    public void testGetNameConcatenatedWithoutMiddles() {
        setDonorNamesNoMiddle();
        assertEquals("Joe Bloggs", testDonor.getNameConcatenated());
    }

    /**
     * Create donor object
     */
    private Donor givenDonor() {
        return new Donor(12, "Bob", null, "Wallace",
                LocalDate.of(1995, 12, 31));
    }

    /**
     * Check the attributes have been set correctly upon donor obj creation
     */
    private void thenDonorHasAttributes(Donor donor) {
        assertTrue(donor.getCREATED() != null);
        assertEquals(donor.getFirstName(), "Bob");
        assertEquals(donor.getMiddleNames(), null);
        assertEquals(donor.getLastName(), "Wallace");
        assertEquals(donor.getBirth(), LocalDate.of(1995, 12, 31));
    }

    /**
     * Helper method for testUpdateDonationsRmValid to populate donations
     */
    private void addDonationsToDonor() {
        testDonor.addDonation(Organ.LIVER);
        testDonor.addDonation(Organ.LUNG);
    }

    /**
     * Helper method for testUpdateDonationsRmValid reset donations list
     */
    private void resetDonationsDonor() {
        testDonor.setDonations(new ArrayList<>()); //set to empty
    }

    /**
     * Helper method for setting donor names with multiple middle names
     */
    private void setDonorNamesMultipleMiddle() {
        testDonor.setFirstName("Joe");
        testDonor.setMiddleNames(new ArrayList<String>() {{
            add("Jane");
            add("Jarred");
        }});
        testDonor.setLastName("Bloggs");
    }

    /**
     * Helper method for setting donor names with no middle names
     */
    private void setDonorNamesNoMiddle() {
        testDonor.setFirstName("Joe");
        testDonor.setMiddleNames(new ArrayList<>());
        testDonor.setLastName("Bloggs");
    }

}