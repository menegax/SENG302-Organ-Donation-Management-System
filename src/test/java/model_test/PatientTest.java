package model_test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.logging.Level;

import model.Patient;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import service.Database;
import utility.GlobalEnums;
import utility.GlobalEnums.Organ;

import static utility.UserActionHistory.userActions;


import static org.junit.Assert.*;

public class PatientTest {

    private static Database database = Database.getDatabase();
    private static Patient testPatient; //Patient obj not within the database

    private static Patient testPatient1; //Patient obj not within the database

    /**
     * Populate database with test patients and disables logging
     */
    @BeforeClass
    public static void setUp() {

        userActions.setLevel(Level.OFF);

        testPatient = new Patient("ABC1234", "James", null, "Wallace",
                LocalDate.of(1970, 2, 12));

        database.addPatient(new Patient("XYZ9876", "Joe", new ArrayList<String>() {{
            add("Jane");
        }},
                "Bloggs", LocalDate.of(1994, 12, 12)));

        database.addPatient(new Patient("DEF4567", "Bob", null, "Bobby",
                LocalDate.of(1994, 12, 12)));

        testPatient1 = new Patient("JJJ1234", "Rex", null, "Petsberg",
                LocalDate.of(1977, 6, 16));
    }

    /**
     * Test patient constructor
     */
    @Test
    public void testPatientConstructor() {
        Patient patient = givenPatient();
        thenPatientHasAttributes(patient);
    }

    /**
     * Adds a few organs correctly to requirements
     */
    @Test
    public void testAddingOfOrgansToRequirements() {
        ArrayList<Organ> expected = new ArrayList<Organ>();
        expected.add(Organ.LIVER);
        expected.add(Organ.CORNEA);
        testPatient1.addRequired(Organ.LIVER);
        testPatient1.addRequired(Organ.CORNEA);
        assertEquals(expected, testPatient1.getRequiredOrgans());
    }

    /**
     * Removes a few organs correctly
     */
    @Test
    public void testRemovingOfOrgansFromRequirements() {
        ArrayList<Organ> expected = new ArrayList<Organ>();
        testPatient1.addRequired(Organ.LIVER);
        testPatient1.addRequired(Organ.CORNEA);
        testPatient1.removeRequired(Organ.LIVER);
        testPatient1.removeRequired(Organ.CORNEA);
        assertEquals(expected, testPatient1.getRequiredOrgans());
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
        testPatient.updateDonations(addDonations, null);
        ArrayList<Organ> expected = new ArrayList<Organ>() {{
            add(Organ.LIVER);
            add(Organ.LUNG);
        }};
        assertEquals(expected, testPatient.getDonations());
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
        testPatient.updateDonations(addDonations, null);
        ArrayList<Organ> expected = new ArrayList<Organ>() {{
            add(Organ.LIVER);
        }};
        assertEquals(expected, testPatient.getDonations());
    }

    /**
     * Remove a valid organ from donations,
     * expect donations to contain no organs
     */
    @Test
    public void testUpdateDonationsRmValid() {
        addDonationsToPatient();
        ArrayList<String> rmDonations = new ArrayList<String>() {{
            add("liver");
        }};
        testPatient.updateDonations(null, rmDonations);
        ArrayList<Organ> expected = new ArrayList<Organ>() {{
            add(Organ.LUNG);
        }};
        assertEquals(expected, testPatient.getDonations());
        resetDonationsPatient();
    }

    /**
     * Add a list containing at least one invalid organ
     * expect only liver to be in donations
     */
    @Test
    public void testUpdateDonationsRmInvalid() {
        testPatient.addDonation(Organ.LIVER);
        ArrayList<String> rmDonations = new ArrayList<String>() {{
            add("liver");
            add("test");
        }};
        testPatient.updateDonations(null, rmDonations);
        ArrayList<Organ> expected = new ArrayList<>();
        assertEquals(expected, testPatient.getDonations());
        resetDonationsPatient();
    }

    /**
     * Pass two null arrays into updateDonation,
     * expect donations to be empty
     */
    @Test
    public void testUpdateDonationsAddRmNull() {
        testPatient.updateDonations(null, null);
        ArrayList<Organ> expected = new ArrayList<>();
        assertEquals(expected, testPatient.getDonations());
    }


    /**
     * Check name concat method with multiple middle names
     */
    @Test
    public void testGetNameConcatenatedWithMiddles() {
        setPatientNamesMultipleMiddle();
        assertEquals("Joe Jane Jarred Bloggs", testPatient.getNameConcatenated());
    }


    /**
     * Check name concat method with no middle names
     */
    @Test
    public void testGetNameConcatenatedWithoutMiddles() {
        setPatientNamesNoMiddle();
        assertEquals("Joe Bloggs", testPatient.getNameConcatenated());
    }

    /**
     * Checks correct age of deceased patient
     */
    @Test
    public void testGetAge() {
        testPatient.setDeath(LocalDate.of(2005, 5, 12));
        assertEquals(35, testPatient.getAge());
    }

    /**
     * Checks correct age of deceased patient who is just about to have a birthday
     */
    @Test
    public void testGetAgeRightBeforeBirthday() {
        testPatient.setDeath(LocalDate.of(2005, 2, 11));
        assertEquals(34, testPatient.getAge());
    }

    /**
     * Checks correct BMI for patient
     */
    @Test
    public void testGetBmi() {
        testPatient.setWeight(70.0);
        testPatient.setHeight(1.80);
        assertEquals(21.6, testPatient.getBmi(), 0.2);
    }

    /**
     * Reset the logging level
     */
    @AfterClass
    public static void tearDown() {

        userActions.setLevel(Level.INFO);
    }

    /**
     * Create patient object
     */
    private Patient givenPatient() {
        return new Patient("AAA1111", "Bob", null, "Wallace",
                LocalDate.of(1995, 12, 31));
    }

    /**
     * Check the attributes have been set correctly upon patient obj creation
     */
    private void thenPatientHasAttributes(Patient patient) {
        assertTrue(patient.getCREATED() != null);
        assertEquals(patient.getFirstName(), "Bob");
        assertEquals(patient.getMiddleNames(), null);
        assertEquals(patient.getLastName(), "Wallace");
        assertEquals(patient.getBirth(), LocalDate.of(1995, 12, 31));
    }

    /**
     * Helper method for testUpdateDonationsRmValid to populate donations
     */
    private void addDonationsToPatient() {
        testPatient.addDonation(Organ.LIVER);
        testPatient.addDonation(Organ.LUNG);
    }

    /**
     * Helper method for testUpdateDonationsRmValid reset donations list
     */
    private void resetDonationsPatient() {
        testPatient.setDonations(new ArrayList<>()); //set to empty
    }

    /**
     * Helper method for setting patient names with multiple middle names
     */
    private void setPatientNamesMultipleMiddle() {
        testPatient.setFirstName("Joe");
        testPatient.setMiddleNames(new ArrayList<String>() {{
            add("Jane");
            add("Jarred");
        }});
        testPatient.setLastName("Bloggs");
    }

    /**
     * Helper method for setting patient names with no middle names
     */
    private void setPatientNamesNoMiddle() {
        testPatient.setFirstName("Joe");
        testPatient.setMiddleNames(new ArrayList<>());
        testPatient.setLastName("Bloggs");
    }

}