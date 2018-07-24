package utility_test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static utility.UserActionHistory.userActions;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import model.Patient;
import model.User;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import service.Database;
import utility.GlobalEnums.UserTypes;
import utility.Searcher;

public class SearcherTest {

	private static Searcher searcher;
	private static Database database;
	
    /**
     * Populate database with test patients and disables logging
     */
    @BeforeClass
    public static void setUp() {
    	database = Database.getDatabase();
        userActions.setLevel(Level.OFF);
        
        searcher = Searcher.getSearcher();
    }

    @Before
    public void beforeTest() {
        // Given patients in a db
        Patient d1 = new Patient("abc1234", "Pat", new ArrayList<String>(), "Laff", LocalDate.now());
        Patient d2 = new Patient("def1234", "Patik", new ArrayList<String>(), "Laffey", LocalDate.now());
        Patient d3 = new Patient("ghi1234", "George", new ArrayList<String>(), "Romera", LocalDate.now());
        Patient d4 = new Patient("jkl1234", "George", new ArrayList<String>(), "Bobington", LocalDate.now());
        database.update(d4);
        database.update(d3);
        database.update(d2);
        database.update(d1);
        refreshDatabase();
        refreshIndex();
    }
    
	/**
	 * Tests alphabetical ordering on equally close names from search after a patients name has been chnaged.
	 */
	@Test
	public void testEqualSearchOrderingAfterNameUpdate() throws IOException {
		
		// Change last name of George Romero to come before George Bobington
		database.getPatientByNhi("GHI1234").setLastName("Addington");
		
		refreshIndex();
		
		// For a name search of George
    	List<User> results = searcher.search("George", new UserTypes[] {UserTypes.PATIENT}, 30);

    	// Get indices of the two Georges
    	int d3index = results.indexOf(database.getPatientByNhi("GHI1234")); 
    	int d4index = results.indexOf(database.getPatientByNhi("JKL1234"));

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
    	beforeTest();
    	
    	refreshIndex();
    	
    	// For a name search of George
    	List<User> results = searcher.search("George", new UserTypes[] {UserTypes.PATIENT}, 30);
    	
    	// Get indices of the two Georges
    	int d3index = results.indexOf(database.getPatientByNhi("GHI1234")); 
    	int d4index = results.indexOf(database.getPatientByNhi("JKL1234"));
    	
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
    			database.update((new Patient(nhi[count], fName, new ArrayList<String>(), lName, LocalDate.of(1990, 2, 3))));
    			count += 1;
    		}
    	}
    	
    	// For a number of patients more than 30
    	assertTrue(database.getPatients().size() > 30);
    	
        // Search to match all 36 added patients.
        List<User> results = searcher.search("A B C D E F Z Y X W V U", new UserTypes[] {UserTypes.PATIENT}, 30);

        // The returned result should be exactly 30
        assertTrue(results.size() == 30);
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
    			database.update(new Patient(nhi[count], fName, new ArrayList<String>(), lName, LocalDate.of(1990, 2, 3)));
    			count += 1;
    		}
    	}
    	
    	// For a number of patients more than 30
    	assertTrue(database.getPatients().size() > 30);
    	
        // Blank search to return maximum number of results. EG every patient.
        List<User> results = searcher.search("", new UserTypes[] {UserTypes.PATIENT}, 30);

        // The returned result should be less than or equal to 30
        assertTrue(results.size() <= 30);
    }
    
    /**
     * Tests a simple name search case.
     * @throws IOException -
     */
    @Test
    public void testSearchByName() throws IOException {
    	beforeTest();
    	
    	refreshIndex();

        // When index searched for a single specific patient
        List<User> results = searcher.search("Pat Bobinton", new UserTypes[] {UserTypes.PATIENT}, 30);

        // Should contain Pat Laff
        assertTrue(results.contains(database.getPatientByNhi("ABC1234")));
        // Should contain Patik Laffey
        assertTrue(results.contains(database.getPatientByNhi("def1234")));
        // Shouldn't contain George Romera
        assertFalse(results.contains(database.getPatientByNhi("ghi1234")));
        // Should contain George Bobington
        assertTrue(results.contains(database.getPatientByNhi("jkl1234")));
    }


    /**
     * Tests a name search for after a patient's name has been updated.
     */
    @Test
    public void testSearchAfterNameUpdate() throws IOException {

        // When first name of patient changed
        database.getPatientByNhi("ABC1234").setFirstName("Andrew");

        // Then searching by new first name returns correct results
        List<User> results = searcher.search("Ande Lafey", new UserTypes[] {UserTypes.PATIENT}, 30);
 
        assertTrue(results.contains(database.getPatientByNhi("ABC1234")));
    }

    /**
     * Tests a name search for after a patient's NHI has been updated.
     * @throws IOException -
     */
    @Test
    public void testSearchAfterNhiUpdate() throws IOException {
    	//Bug with setup() means it has to be copied here or wont work
        searcher.clearIndex();

        // Given an index
        
    	String name = database.getPatientByNhi("def1234").getFirstName();
    	database.getPatientByNhi("def1234").setNhiNumber("def5678");
    	searcher.createFullIndex();
    	List<User> results = searcher.search(name, new UserTypes[] {UserTypes.PATIENT}, 30);

    	assertTrue(results.contains(database.getPatientByNhi("def5678")));
    }

    /**
     * Tests weird edge cases for name search.
     */
    @Test
    public void testSearchUnusualNameResults() {

        // Given patients in a db
    	Patient d1 = new Patient("abc9876", "Joe", new ArrayList<String>(), "Plaffer", LocalDate.now());
    	Patient d2 = new Patient("def9876", "Johnothan", new ArrayList<String>(), "zzne", LocalDate.now());
    	Patient d3 = new Patient("ghi9876", "John", new ArrayList<String>(), "Romera", LocalDate.now());
    	Patient d4 = new Patient("jkl9876", "Samantha", new ArrayList<String>(), "Fon", LocalDate.now());
        database.update(d4);
        database.update(d3);
        database.update(d2);
        database.update(d1);
        refreshDatabase();
        refreshIndex();

        // When searching patients
    	List<User> results = searcher.search("Jone", new UserTypes[] {UserTypes.PATIENT}, 30);

    	// Should contain Joe Plaffer, remove 'n' for Joe
    	assertTrue(results.contains(d1));

    	// Should contain Johnothan Doe, remove 'n' and replace 'J' with 'D' for Doe
    	assertTrue(results.contains(d2));

    	// Should contain John Romera, remove 'e' and insert 'h' before 'n' for John
    	assertTrue(results.contains(d3));

    	// Should contain Samantha Fon, remove 'e' and replace 'J' with 'F' for Fon
    	assertTrue(results.contains(d4));
    }

    private void refreshIndex() {
        searcher.clearIndex();
        searcher.createFullIndex();
    }
    
    private void refreshDatabase() {
    	database.resetLocalDatabase();
    	database.loadAll();
    }
    
    /**
     * Reset the logging level
     */
    @AfterClass
    public static void tearDown() {

        userActions.setLevel(Level.INFO);
    }

}