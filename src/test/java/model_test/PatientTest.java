package model_test;

import static java.util.logging.Level.OFF;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static utility.SystemLogger.systemLogger;
import static utility.UserActionHistory.userActions;

import com.google.maps.model.LatLng;
import model.Disease;
import model.Patient;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import service.PatientDataService;
import service.interfaces.IPatientDataService;
import utility.GlobalEnums;
import utility.GlobalEnums.Organ;
import utility.SystemLogger;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.zip.DataFormatException;

import static java.util.logging.Level.OFF;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static utility.SystemLogger.systemLogger;
import static utility.UserActionHistory.userActions;

public class PatientTest implements Serializable {

    private static Patient testPatient; //Patient obj not within the database

    private static Patient testPatient1; //Patient obj not within the database

    private static final IPatientDataService patientDataService = new PatientDataService();

    @BeforeClass
    public static void setUpBeforeClass() {
        userActions.setLevel(OFF);
        SystemLogger.systemLogger.setLevel(OFF);
    }

    @Before
    public void setUp() {
        userActions.setLevel(Level.OFF);
        systemLogger.setLevel(Level.OFF);
        userActions.setLevel(Level.OFF);

        testPatient = new Patient("ABC1234", "James", new ArrayList<>(), "Wallace",
                LocalDate.of(1970, 2, 12));

        patientDataService.save(new Patient("XYZ9876", "Joe", new ArrayList<String>() {{
            add("Jane");
        }},
                "Bloggs", LocalDate.of(1994, 12, 12)));

        patientDataService.save(new Patient("DEF4567", "Bob", new ArrayList<>(), "Bobby",
                LocalDate.of(1994, 12, 12)));

        testPatient1 = new Patient("JJJ1234", "Rex", new ArrayList<>(), "Petsberg",
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
        Set<Organ> expected = new TreeSet<>();
        expected.add(Organ.LIVER);
        expected.add(Organ.CORNEA);
        testPatient1.addRequired(Organ.LIVER);
        testPatient1.addRequired(Organ.CORNEA);
        assertEquals(expected, testPatient1.getRequiredOrgans().keySet());
    }

    /**
     * Removes a few organs correctly
     */
    @Test
    public void testRemovingOfOrgansFromRequirements() {
        Set<Organ> expected = new TreeSet<>();
        testPatient1.addRequired(Organ.LIVER);
        testPatient1.addRequired(Organ.CORNEA);
        testPatient1.removeRequired(Organ.LIVER);
        testPatient1.removeRequired(Organ.CORNEA);
        assertEquals(expected, testPatient1.getRequiredOrgans().keySet());
    }

    @Test
    public void testHasUnassignedRequiredOrgans() {
        testPatient1.addRequired(Organ.LIVER);
        testPatient1.addRequired(Organ.CORNEA);
        assertTrue(testPatient1.hasUnassignedRequiredOrgans());

        testPatient1.getRequiredOrgans().get(Organ.LIVER).setDonorNhi("ZZZ9999");
        testPatient1.getRequiredOrgans().get(Organ.CORNEA).setDonorNhi("ZZZ9999");
        assertFalse(testPatient1.hasUnassignedRequiredOrgans());
    }

    /**
     * Add a list of valid organs,
     * expect all items passed to be added to donations
     */
    @Test
    public void testUpdateDonationsMultiCorrectAdd() {
        Map<Organ, String> addDonations = new HashMap<Organ, String>() {{
            put(Organ.LIVER, null);
            put(Organ.LUNG, null);
        }};
        testPatient.updateDonations(addDonations, null);
        Map<Organ, String> expected = new HashMap<Organ, String>() {{
            put(Organ.LIVER, null);
            put(Organ.LUNG, null);
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
        Map<Organ, String> rmDonations = new HashMap<Organ, String>() {{
            put(Organ.LIVER, null);
        }};
        testPatient.updateDonations(null, rmDonations);
        Map<Organ, String> expected = new HashMap<Organ, String>() {{
            put(Organ.LUNG, null);
        }};
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
        Map<Organ, String> expected = new HashMap<>();
        assertEquals(expected, testPatient.getDonations());
    }


    /**
     * Check name concat method with multiple middle names
     */
    @Test
    public void testGetNameConcatenatedWithMiddles() {
        setPatientNamesMultipleMiddle();
        assertEquals("James Jane Jarred Bloggs", testPatient.getNameConcatenated());
    }


    /**
     * Check name concat method with no middle names
     */
    @Test
    public void testGetNameConcatenatedWithoutMiddles() {
        setPatientNamesNoMiddle();
        assertEquals("James Bloggs", testPatient.getNameConcatenated());
    }

    /**
     * Checks correct age of deceased patient
     */
    @Test
    public void testGetAge() {
        testPatient.setDeathDate(LocalDateTime.of(2005, 5, 12, 0, 0));
        assertEquals(35, testPatient.getAge());
    }

    /**
     * Checks correct age of deceased patient who is just about to have a birthday
     */
    @Test
    public void testGetAgeRightBeforeBirthday() {
        testPatient.setDeathDate(LocalDateTime.of(2005, 2, 11, 0, 0));
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
     * Checks that the patient can successfully sort its current and past disease lists
     */
    @Test
    public void testDiseaseSorting() {
        // Define diseases
        Disease noneToCured = new Disease("test1", null);
        Disease chronicToCured = new Disease("test2", GlobalEnums.DiseaseState.CHRONIC);
        Disease curedToNone = new Disease("test3", GlobalEnums.DiseaseState.CURED);
        Disease curedToChronic = new Disease("test4", GlobalEnums.DiseaseState.CHRONIC);
        Disease noneToChronic = new Disease("test5", null);
        Disease none = new Disease("test6", null);
        Disease chronic = new Disease("test7", GlobalEnums.DiseaseState.CHRONIC);
        Disease cured = new Disease("test8", GlobalEnums.DiseaseState.CURED);

        // Add diseases to patient
        testPatient.setCurrentDiseases(new ArrayList<Disease>() {{
            add(noneToCured);
            add(chronicToCured);
            add(noneToChronic);
            add(none);
            add(chronic);
        }});
        testPatient.setPastDiseases(new ArrayList<Disease>() {{
            add(curedToNone);
            add(curedToChronic);
            add(cured);
        }});

        // Edit disease tags
        noneToCured.setDiseaseState(GlobalEnums.DiseaseState.CURED);
        chronicToCured.setDiseaseState(GlobalEnums.DiseaseState.CURED);
        curedToNone.setDiseaseState(null);
        curedToChronic.setDiseaseState(GlobalEnums.DiseaseState.CHRONIC);
        noneToChronic.setDiseaseState(GlobalEnums.DiseaseState.CHRONIC);

        // Check sort outcome
        testPatient.sortDiseases();
        List<Disease> currentDiseasesExpected = new ArrayList<Disease>() {{
            add(curedToNone);
            add(curedToChronic);
            add(noneToChronic);
            add(none);
            add(chronic);
        }};
        List<Disease> pastDiseasesExpected = new ArrayList<Disease>() {{
            add(noneToCured);
            add(chronicToCured);
            add(cured);
        }};
        checkSameDiseases(currentDiseasesExpected, testPatient.getCurrentDiseases());
        checkSameDiseases(pastDiseasesExpected, testPatient.getPastDiseases());
    }

    /**
     * Tests the setAttributes method
     */
    @Test
    public void testSetAttributes() {
        Patient beforePatient = givenPatient();
        Patient afterPatient = givenPatient();
        afterPatient.setFirstName("Different");
        afterPatient.setMiddleNames(new ArrayList<String>() {{
            add("Middle");
            add("Name");
        }});
        beforePatient.setAttributes(afterPatient);
        assertEquals("Different", beforePatient.getFirstName());
        assertEquals("Name", beforePatient.getMiddleNames().get(1));

        // checks deep copy has occurred
        beforePatient.getMiddleNames().remove(0);
        assertEquals("Middle", afterPatient.getMiddleNames().get(0));
    }

    /**
     * Ensures the current location Lat and Lng is reset whenever part of the patient's address is reset
     *
     * Due to the getCurrentLocation checking for null, this test can only test certain parts of the address (e.g. not street)
     */
    @Test
    public void resetCurrentLocation() throws DataFormatException {
        testPatient.setCurrentLocation(new LatLng(-43.525650, 172.639847)); // set to UC
        testPatient.setStreetNumber("10");
        assertNull(testPatient.getCurrentLocationForTestingOnly());

        testPatient.setCurrentLocation(new LatLng(-43.525650, 172.639847)); // set to UC
        testPatient.setStreetName("Ilam");
        assertNull(testPatient.getCurrentLocationForTestingOnly());

        testPatient.setCurrentLocation(new LatLng(-43.525650, 172.639847)); // set to UC
        testPatient.setSuburb("Avonside");
        assertNull(testPatient.getCurrentLocationForTestingOnly());

        testPatient.setCurrentLocation(new LatLng(-43.525650, 172.639847)); // set to UC
        testPatient.setCity("Christchurch");
        assertNull(testPatient.getCurrentLocationForTestingOnly());

        testPatient.setCurrentLocation(new LatLng(-43.525650, 172.639847)); // set to UC
        testPatient.setRegion(GlobalEnums.Region.CANTERBURY);
        assertNull(testPatient.getCurrentLocationForTestingOnly());

        testPatient.setCurrentLocation(new LatLng(-43.525650, 172.639847)); // set to UC
        testPatient.setDeathStreet("10 Ilam");
        assertNull(testPatient.getCurrentLocationForTestingOnly());

        testPatient.setCurrentLocation(new LatLng(-43.525650, 172.639847)); // set to UC
        testPatient.setDeathCity("Chch");
        assertNull(testPatient.getCurrentLocationForTestingOnly());

        testPatient.setCurrentLocation(new LatLng(-43.525650, 172.639847)); // set to UC
        testPatient.setDeathRegion(GlobalEnums.Region.CANTERBURY);
        assertNull(testPatient.getCurrentLocationForTestingOnly());
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
        assertNotNull(patient.getCREATED());
        assertEquals(patient.getFirstName(), "Bob");
        assertNull(patient.getMiddleNames());
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
        testPatient.setDonations(new HashMap<>()); //set to empty
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


    /**
     * Runs asserts that the same diseases occur in two lists irrespective of order
     *
     * @param expectedDiseases the list of expected diseases
     * @param actualDiseases   the list of actual diseases
     */
    private void checkSameDiseases(List<Disease> expectedDiseases, List<Disease> actualDiseases) {
        assertEquals(expectedDiseases.size(), actualDiseases.size());
        expectedDiseases.sort(Comparator.comparing(Disease::getDiseaseName));
        actualDiseases.sort(Comparator.comparing(Disease::getDiseaseName));
        for (int i = 0; i < expectedDiseases.size(); i++) {
            assertEquals(expectedDiseases.get(i), actualDiseases.get(i));
        }
    }
}