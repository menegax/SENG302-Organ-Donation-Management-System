package utility_test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.*;
import static utility.UserActionHistory.userActions;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.logging.Level;

import model.Patient;
import model.User;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import service.Database;
import utility.GlobalEnums;
import utility.GlobalEnums.*;
import utility.GlobalEnums.UserTypes;
import utility.Searcher;

public class SearcherTest {

	private static Patient d1;
	private static Patient d2;
	private static Patient d3;
	private static Patient d4;
	private static Searcher searcher;


    private Map<FilterOption, String> filter = new HashMap<>();

    /**
     * Populate database with test patients and disables logging
     */
    @BeforeClass
    public static void setUp() {
        Database.resetDatabase();
        userActions.setLevel(Level.ALL);

        // Given patients in a db
        d1 = new Patient("abc1234", "Pat", new ArrayList<String>(), "Laff", LocalDate.now());
        d2 = new Patient("def1234", "Patik", new ArrayList<String>(), "Laffey", LocalDate.now());
        d3 = new Patient("ghi1234", "George", new ArrayList<String>(), "Romera", LocalDate.now());
        d4 = new Patient("jkl1234", "George", new ArrayList<String>(), "Bobington", LocalDate.now());
        Database.addPatient(d4);
        Database.addPatient(d3);
        Database.addPatient(d2);
        Database.addPatient(d1);

        searcher = Searcher.getSearcher();

        searcher.clearIndex();

        // Given an index
        searcher.createFullIndex();
    }

	/**
	 * Tests alphabetical ordering on equally close names from search after a patients name has been chnaged.
	 */
	@Test
	public void testEqualSearchOrderingAfterNameUpdate() {

	    Database.resetDatabase();

        d1 = new Patient("abc1234", "Pat", new ArrayList<String>(), "Laff", LocalDate.now());
        d2 = new Patient("def1234", "Patik", new ArrayList<String>(), "Laffey", LocalDate.now());
        d3 = new Patient("ghi1234", "George", new ArrayList<String>(), "Romera", LocalDate.now());
        d4 = new Patient("jkl1234", "George", new ArrayList<String>(), "Bobington", LocalDate.now());

        Database.addPatient(d4);
        Database.addPatient(d3);
        Database.addPatient(d2);
        Database.addPatient(d1);

		// Change last name of George Romero to come before George Bobington
		d3.setLastName("Addington");

		// For a name search of George
    	List<User> results = searcher.search("George", new UserTypes[] {UserTypes.PATIENT}, 30, null);

    	// Get indices of the two Georges
    	int d3index = results.indexOf(d3);
    	int d4index = results.indexOf(d4);

    	// Ensure both Georges are in the results
    	assertTrue(d3index != -1);
    	assertTrue(d4index != -1);

    	// Ensure George Addington comes before George Bobington
    	assertTrue(d4index > d3index);

	}

    /**
     * Tests the alphabetical ordering of patients that are equal close to the search result.
     */
    @Test
    public void testEqualSearchOrdering(){

    	// For a name search of George
    	List<User> results = searcher.search("George", new UserTypes[] {UserTypes.PATIENT}, 30, null);

    	// Get indices of the two Georges
    	int d3index = results.indexOf(d3);
    	int d4index = results.indexOf(d4);

    	// Ensure both Georges are in the results
    	assertTrue(d3index != -1);
    	assertTrue(d4index != -1);

    	// Ensure George Bobington comes before George Romero
    	assertTrue(d4index < d3index);
    }

    /**
     * Tests that the search will not return more than 30 results.
     */
    @Test
    public void testNumberOfResults(){

    	Database.resetDatabase();
    	searcher.clearIndex();

    	// Create more than 36 patients
    	String[] firstNames = {"A", "B", "C", "D", "E", "F"};
    	String[] lastNames = {"Z", "Y", "X", "W", "V", "U"};
    	String[] nhi = {"AZA1111", "AZA1112", "AZA1113", "AZA1114", "AZA1115", "AZA1116",
    			"AZA1117", "AZA1118", "AZA1119", "AZA1120", "AZA1121", "AZA1122",
    			"AZA1123", "AZA1124", "AZA1125", "AZA1126", "AZA1127", "AZA1128",
    			"AZA1129", "AZA1130", "AZA1131", "AZA1132", "AZA1133", "AZA1134",
    			"AZA1135", "AZA1136", "AZA1137", "AZA1138", "AZA1139", "AZA1140",
    			"AZA1141", "AZA1142", "AZA1143", "AZA1144", "AZA1145", "AZA1146"};
    	int count = 0;
    	for (String lName : lastNames) {
    		for (String fName : firstNames) {
    			Database.addPatient(new Patient(nhi[count], fName, new ArrayList<String>(), lName, LocalDate.of(1990, 2, 3)));
    			count += 1;
    		}
    	}
    	searcher.createFullIndex();

    	// For a number of patients more than 30
    	assertTrue(Database.getPatients().size() > 30);

        // Search to match all 36 added patients.
        List<User> results = searcher.search("A B C D E F Z Y X W V U", new UserTypes[] {UserTypes.PATIENT}, 30, null);
        for (User result: results) {
        	System.out.println(result);
        }

        // The returned result should be exactly 30
        assertEquals(30, results.size());
    }

    /**
     * Tests that a blank search will not return more than 30 results.
     */
    @Test
    public void testNumberOfResultsBlankSearch() {

    	// Create more than 36 patients
    	String[] firstNames = {"A", "B", "C", "D", "E", "F"};
    	String[] lastNames = {"Z", "Y", "X", "W", "V", "U"};
    	String[] nhi = {"AZA1111", "AZA1112", "AZA1113", "AZA1114", "AZA1115", "AZA1116",
    			"AZA1117", "AZA1118", "AZA1119", "AZA1120", "AZA1121", "AZA1122",
    			"AZA1123", "AZA1124", "AZA1125", "AZA1126", "AZA1127", "AZA1128",
    			"AZA1129", "AZA1130", "AZA1131", "AZA1132", "AZA1133", "AZA1134",
    			"AZA1135", "AZA1136", "AZA1137", "AZA1138", "AZA1139", "AZA1140",
    			"AZA1141", "AZA1142", "AZA1143", "AZA1144", "AZA1145", "AZA1146"};
    	int count = 0;
    	for (String lName : lastNames) {
    		for (String fName : firstNames) {
    			Database.addPatient(new Patient(nhi[count], fName, new ArrayList<String>(), lName, LocalDate.of(1990, 2, 3)));
    			count += 1;
    		}
    	}

    	// For a number of patients more than 30
    	assertTrue(Database.getPatients().size() > 30);

        // Blank search to return maximum number of results. EG every patient.
        List<User> results = searcher.search("", new UserTypes[] {UserTypes.PATIENT}, 30, null);

        // The returned result should be less than or equal to 30
        assertTrue(results.size() <= 30);
    }

    /**
     * Tests a simple name search case.
     * @throws IOException -
     */
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

        searcher.clearIndex();

        // Given an index
        searcher.createFullIndex();

        // When index searched for a single specific patient
        List<User> results = searcher.search("Pat Bobinton", new UserTypes[] {UserTypes.PATIENT}, 30, null);

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
     * Tests a name search for after a patient's name has been updated.
     */
    @Test
    public void testSearchAfterNameUpdate() throws IOException {
        Database.resetDatabase();
        d1 = new Patient("abc1234", "Pat", null, "Lafey", LocalDate.now());
        Database.addPatient(d1);
        // When first name of patient changed
        Database.getPatientByNhi("abc1234").setFirstName("Andrew");

        // Then searching by new first name returns correct results
        List<User> results = searcher.search("Ande Lafey", new UserTypes[] {UserTypes.PATIENT}, 30, null);

        assertTrue(results.contains(Database.getPatientByNhi("abc1234")));
    }

    /**
     * Tests a name search for after a patient's NHI has been updated.
     * @throws IOException -
     */
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

        searcher.clearIndex();

        // Given an index
        searcher.createFullIndex();
    	String name = Database.getPatientByNhi("def1234").getFirstName();
    	Database.getPatientByNhi("def1234").setNhiNumber("def5678");

    	List<User> results = searcher.search(name, new UserTypes[] {UserTypes.PATIENT}, 30, null);

    	assertTrue(results.contains(Database.getPatientByNhi("def5678")));
    }

    /**
     * Tests weird edge cases for name search.
     */
    @Test
    public void testSearchUnusualNameResults() {
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

        searcher.clearIndex();

        // Given an index
        searcher.createFullIndex();

        // When searching patients
    	List<User> results = searcher.search("Jone", new UserTypes[] {UserTypes.PATIENT}, 30, null);

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
     * Check region filter works as intended
     * @throws InvalidObjectException - patient not in db
     */
    @Test
    public void testFilterRegionWithNameSearch() throws InvalidObjectException {
        Database.resetDatabase();
        searcher.clearIndex();
        filter.clear();

        //filter region
        filter.put(FilterOption.REGION,  Region.CANTERBURY.toString());

        //setup with region of null
        d1 = new Patient("abc1230", "Bob", null, "Bobby", LocalDate.of(1997, 8, 19));
        d2 = new Patient("abc1231", "aoc", null, "Bobby", LocalDate.of(2001, 9, 20));
        d3 = new Patient("abc1232", "aoc", null, "Bobby", LocalDate.of(2001, 9, 20));
        d1.setRegion(Region.CANTERBURY);

        Database.addPatient(d1);
        Database.addPatient(d2);
        Database.addPatient(d3);

        searcher.createFullIndex();

        //search with no name
        List<User> results = Searcher.getSearcher().search("",new UserTypes[] {UserTypes.PATIENT}, 30, filter);

        //only 1 should result
        Assert.assertEquals(1, results.size());

        //check that all have correct region
        for (User patient : results) {
            Assert.assertEquals(Region.CANTERBURY, ((Patient)patient).getRegion());
        }

        //update region
        Database.getPatientByNhi("abc1231").setRegion(Region.CANTERBURY);


        results = Searcher.getSearcher().search("", new UserTypes[] {UserTypes.PATIENT}, 30, filter);

        //2 results with region CANTERBURY
        Assert.assertEquals(2, results.size());

        hasRegions(results);

        results = Searcher.getSearcher().search("aoc", new UserTypes[] {UserTypes.PATIENT}, 30, filter);

        //1 results with region CANTERBURY and search
        Assert.assertEquals(2, results.size());

        hasRegions(results);

        //reset filter
        filter.replace(FilterOption.REGION,  filter.get(FilterOption.REGION), GlobalEnums.NONE_ID);
        results = Searcher.getSearcher().search("aoc", new UserTypes[] {UserTypes.PATIENT}, 30, filter);

        Assert.assertEquals(3, results.size());

    }


    /**
     * Check organ filtering
     * @throws InvalidObjectException - patient not in db
     */
    @Test
    public void testFilterOrganWithNameSearch() throws InvalidObjectException {
        addPatientsToDB();

        //filter region
        filter.put(FilterOption.DONATIONS, Organ.BONEMARROW.toString());

        Database.getPatientByNhi("abc1230").setDonations(new ArrayList<Organ>(){{add(Organ.BONEMARROW);}});
        List<User>  results = Searcher.getSearcher().search("", new UserTypes[] {UserTypes.PATIENT}, 30, filter);
        Assert.assertEquals(1, results.size());

        hasOrgans(results, Organ.BONEMARROW);

        //d2 - bone marrow
        Database.getPatientByNhi("abc1231").setDonations(new ArrayList<Organ>(){{add(Organ.BONEMARROW);}});

        results = Searcher.getSearcher().search("", new UserTypes[] {UserTypes.PATIENT}, 30, filter);
        Assert.assertEquals(2, results.size());

        hasOrgans(results, Organ.BONEMARROW);

        //d3 - kidney
        Database.getPatientByNhi("abc1232").setDonations(new ArrayList<Organ>(){{add(Organ.KIDNEY);}});

        results = Searcher.getSearcher().search("", new UserTypes[] {UserTypes.PATIENT}, 30, filter);
        Assert.assertEquals(2, results.size());

        hasOrgans(results, Organ.BONEMARROW);

        //d3 - heart
        Database.getPatientByNhi("abc1232").setDonations(new ArrayList<Organ>(){{add(Organ.HEART);}});

        // update filter
        filter.put(FilterOption.DONATIONS, Organ.HEART.toString());
        results = Searcher.getSearcher().search("", new UserTypes[] {UserTypes.PATIENT}, 30, filter);
        Assert.assertEquals(1, results.size());

        hasOrgans(results, Organ.HEART);
    }


    /**
     * Check birth gender filtering
     @throws InvalidObjectException - patient not in db
     */
    @Test
    public void testFilterBirthGender() throws InvalidObjectException {
        addPatientsToDB();

        //filter region
        filter.put(FilterOption.BIRTHGENDER, BirthGender.FEMALE.toString());

        //d1 - only one female
        Database.getPatientByNhi("abc1230").setBirthGender(BirthGender.FEMALE);
        List<User>  results = Searcher.getSearcher().search("", new UserTypes[] {UserTypes.PATIENT}, 30, filter);
        Assert.assertEquals(1, results.size());
        hasBirthGender(results, BirthGender.FEMALE);

        //d2 - 2 females
        Database.getPatientByNhi("abc1231").setBirthGender(BirthGender.FEMALE);
        results = Searcher.getSearcher().search("", new UserTypes[] {UserTypes.PATIENT}, 30, filter);
        Assert.assertEquals(2, results.size());
        hasBirthGender(results, BirthGender.FEMALE);

        //d2 - 1 male
        filter.replace(FilterOption.BIRTHGENDER, filter.get(FilterOption.BIRTHGENDER), BirthGender.MALE.toString());
        Database.getPatientByNhi("abc1232").setBirthGender(BirthGender.MALE);
        results = Searcher.getSearcher().search("", new UserTypes[] {UserTypes.PATIENT}, 30, filter);
        Assert.assertEquals(1, results.size());
        hasBirthGender(results, BirthGender.MALE);
    }


    /**
     * Check age filtering on bounds
     */
    @Test
    public void testFilterAge(){
        addPatientsToDB();

        //from 10 -100
        filter.put(FilterOption.AGELOWER, "10");
        filter.put(FilterOption.AGEUPPER, "100");
        List<User> results = Searcher.getSearcher().search("", new UserTypes[] {UserTypes.PATIENT}, 30, filter);
        Assert.assertEquals(4, results.size());
        hasAge(results, 10, 100);

        //from 11 - 100
        filter.put(FilterOption.AGELOWER, "11");
        results = Searcher.getSearcher().search("", new UserTypes[] {UserTypes.PATIENT}, 30, filter);
        Assert.assertEquals(3, results.size());
        hasAge(results, 11, 100);

        //from 11 - 99
        filter.put(FilterOption.AGEUPPER, "99");
        results = Searcher.getSearcher().search("", new UserTypes[] {UserTypes.PATIENT}, 30, filter);
        Assert.assertEquals(2, results.size());
        hasAge(results, 11, 99);


        //from 11 - 59
        filter.put(FilterOption.AGEUPPER, "59");
        results = Searcher.getSearcher().search("", new UserTypes[] {UserTypes.PATIENT}, 30, filter);
        Assert.assertEquals(1, results.size());
        hasAge(results, 11, 59);

        //from 11 - 12
        filter.put(FilterOption.AGEUPPER, "12");
        results = Searcher.getSearcher().search("", new UserTypes[] {UserTypes.PATIENT}, 30, filter);
        Assert.assertEquals(0, results.size());
        hasAge(results, 11, 12);
    }


    /**
     * Check the status filtering
     * @throws InvalidObjectException -
     */
    @Test
    public void testIsDonorReceiver() throws InvalidObjectException {
        addPatientsToDB();
        filter.put(FilterOption.DONOR, "true");

        List<User>  results = Searcher.getSearcher().search("", new UserTypes[] {UserTypes.PATIENT}, 30, filter);
        Assert.assertEquals(0, results.size());
        areDonors(results);

        //one donor
        Database.getPatientByNhi("abc1230").setDonations(new ArrayList<Organ>(){{add(Organ.KIDNEY);}});
        results = Searcher.getSearcher().search("", new UserTypes[] {UserTypes.PATIENT}, 30, filter);
        Assert.assertEquals(1, results.size());
        areDonors(results);

        //one reciever
        filter.clear();
        filter.put(FilterOption.RECIEVER, "true");
        Database.getPatientByNhi("abc1231").setRequiredOrgans(new ArrayList<Organ>(){{add(Organ.KIDNEY);}});
        results = Searcher.getSearcher().search("", new UserTypes[] {UserTypes.PATIENT}, 30, filter);
        Assert.assertEquals(1, results.size());
        areRecievers(results);


        //both donor and reciever
        filter.put(FilterOption.DONOR, "true");
        Database.getPatientByNhi("abc1230").setRequiredOrgans(new ArrayList<Organ>(){{add(Organ.KIDNEY);}});
        results = Searcher.getSearcher().search("", new UserTypes[] {UserTypes.PATIENT}, 30, filter);
        Assert.assertEquals(1, results.size());
        areDonorsAndRecievers(results);
    }


    @Test
    public void testAllFilterCombo() throws InvalidObjectException {
        addPatientsToDB();

        //set regions
        Database.getPatientByNhi("abc1230").setRegion(Region.CANTERBURY);
        Database.getPatientByNhi("abc1231").setRegion(Region.CANTERBURY);
        Database.getPatientByNhi("abc1232").setRegion(Region.AUCKLAND);
        Database.getPatientByNhi("abc1233").setRegion(Region.MANAWATU);

        //set donations, set requireds
        Database.getPatientByNhi("abc1230").setDonations(new ArrayList<Organ>(){{add(Organ.KIDNEY);}});
        Database.getPatientByNhi("abc1231").setRequiredOrgans(new ArrayList<Organ>(){{add(Organ.LIVER);}});

        //set genders
        Database.getPatientByNhi("abc1230").setBirthGender(BirthGender.FEMALE);
        Database.getPatientByNhi("abc1231").setBirthGender(BirthGender.MALE);
        Database.getPatientByNhi("abc1232").setBirthGender(BirthGender.MALE);
        Database.getPatientByNhi("abc1233").setBirthGender(BirthGender.FEMALE);

        filter.put(FilterOption.REGION, Region.CANTERBURY.getValue());
        filter.put(FilterOption.DONATIONS, Organ.KIDNEY.toString());
        filter.put(FilterOption.BIRTHGENDER, BirthGender.FEMALE.getValue());
        filter.put(FilterOption.DONOR, "true");

        List<User>  results = Searcher.getSearcher().search("a", new UserTypes[] {UserTypes.PATIENT}, 30, filter);
        assertEquals(1, results.size());
    }

    /**
     * Adds a set of patients to the db, also clears and resets
     */
    private void addPatientsToDB() {
    	searcher.clearIndex();
        Database.resetDatabase();
        filter.clear();
        d1 = new Patient("abc1230", "a", null, "Bobby", LocalDate.of(LocalDate.now().getYear() - 10,
                1, 1));
        d2 = new Patient("abc1231", "b", null, "Bobby", LocalDate.of(LocalDate.now().getYear() - 100,
                1, 1));
        d3 = new Patient("abc1232", "c", null, "Bobby", LocalDate.of(LocalDate.now().getYear() - 60,
                1, 1));
        d4 = new Patient("abc1233", "d", null, "Bobby", LocalDate.of(LocalDate.now().getYear() - 19,
                1, 1));

        Database.addPatient(d1);
        Database.addPatient(d2);
        Database.addPatient(d3);
        Database.addPatient(d4);
        searcher.createFullIndex();
    }


    /**
     * Checks the results have the correct status
     * @param res - results to check
     */
    private void areDonorsAndRecievers(List<User> res) {
        for (User patient : res) {
            Assert.assertTrue(((Patient)patient).getRequiredOrgans().size() > 0);
            Assert.assertTrue(((Patient)patient).getDonations().size() > 0);
        }
    }

    /**
     * Checks the results have the correct status
     * @param res - results to check
     */
    private void areRecievers(List<User> res) {
        for (User patient : res) {
            Assert.assertTrue(((Patient)patient).getRequiredOrgans().size() > 0);
        }
    }

    /**
     * Checks the results have the correct status
     * @param res - results to check
     */
    private void areDonors(List<User> res) {
        for (User patient : res) {
            Assert.assertTrue(((Patient)patient).getDonations().size() > 0);
        }
    }

    /**
     * Check results have the correct age
     * @param res - results to check
     * @param greaterThan - age the patient should be older than or equal
     * @param lessThan - age the patient should be younger than or equal
     */
    private void hasAge(List<User> res, int greaterThan, int lessThan) {
        for (User patient : res) {
            Assert.assertTrue(((Patient)patient).getAge() >= greaterThan);
            Assert.assertTrue(((Patient)patient).getAge() <= lessThan);
        }
    }
    /**
     *
     * @param res - results to check
     * @param gender -
     */
    private void hasBirthGender(List<User> res, BirthGender gender) {
        for (User patient : res) {
            Assert.assertEquals(gender, ((Patient)patient).getBirthGender());

        }
    }

    /**
     *
     * @param res - results to check
     * @param organ -
     */
    private void hasOrgans(List<User> res, Organ organ) {
        for (User patient : res) {
            Assert.assertTrue(((Patient)patient).getDonations().contains(organ));
        }
    }


    /**
     * check that all have correct region
     * @param res - results of search to check
     */
    private void hasRegions(List<User> res) {
        for (User patient : res) {
            Assert.assertEquals(Region.CANTERBURY, ((Patient) patient).getRegion());
        }
    }

    /**
     * Reset the logging level
     */
    @AfterClass
    public static void tearDown() {

        userActions.setLevel(Level.INFO);
    }

}