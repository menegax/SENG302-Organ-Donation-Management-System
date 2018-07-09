package service_test;

import static java.util.logging.Level.OFF;
import static org.junit.Assert.*;
import static utility.UserActionHistory.userActions;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import model.Clinician;
import model.Disease;
import model.Medication;
import model.Patient;
import model.Procedure;
import service.Database;
import utility.GlobalEnums;
import utility.PatientActionRecord;
import utility.GlobalEnums.Organ;
import utility.GlobalEnums.Region;

public class DatabaseTest {

	private static boolean validConnection = false;
	private static Database testDb = null;
	
	private static Patient patient = new Patient("TST1234", "Test", new ArrayList<String>(), "Tester", LocalDate.of(1998, 1, 8), new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()), null, GlobalEnums.BirthGender.MALE, GlobalEnums.PreferredGender.MAN, "Pat", 1.7, 56.0, GlobalEnums.BloodGroup.O_POSITIVE, new ArrayList<Organ>(), new ArrayList<Organ>(), "Street 1", "Street 2", "Suburb", GlobalEnums.Region.CANTERBURY, 7020, "035441143", "0", "0220918384", "plaffey@mail.com", "EC", "Relationship", "0", "0", "0", "Email@email.com", new ArrayList<PatientActionRecord>(), new ArrayList<Disease>(), new ArrayList<Disease>(), new ArrayList<Medication>(), new ArrayList<Medication>(), new ArrayList<Procedure>());
	private static Clinician clinician = new Clinician(1234, "Test", new ArrayList<String>(), "Tester", Region.CANTERBURY);
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		userActions.setLevel(OFF);
		validConnection = validateConnection();
		testDb = Database.getDatabase();
		addTestData();
	}
	
	private static void addTestData() {
        testDb.add(clinician);
        testDb.add(patient);
	}
	
	private static boolean validateConnection() {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection("jdbc:mysql://mysql2.csse.canterbury.ac.nz:3306/seng302-2018-team800-test?allowMultiQueries=true", "seng302-team800", "ScornsGammas5531");
		} catch (SQLException e1) {
			System.err.println("Failed to connect to UC database server.");
			try {
				conn = DriverManager.getConnection("jdbc:mysql://122.62.50.128:3306/seng302-2018-team800-test?allowMultiQueries=true", "seng302-team800", "ScornsGammas5531");
			} catch (SQLException e2) {
				System.err.println("Failed to connect to database mimic from external source.");
				try {
					conn = DriverManager.getConnection("jdbc:mysql://192.168.1.70:3306/seng302-2018-team800-test?allowMultiQueries=true", "seng302-team800", "ScornsGammas5531");					
				} catch (SQLException e3) {
					System.err.println("Failed to connect to database mimic from internal source.");
					System.err.println("All database connections failed, skipping tests.");
				}
			}
		}
		if (conn == null) {
			return false;
		}
		return true;
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		Assume.assumeTrue(validConnection);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetDatabase() {
		assertTrue(Database.getDatabase() != null);
	}

	@Test
	public void testRunQueryWithSelectOnClinician() {
		String query = "SELECT * FROM tblClinicians";
		try {
			ArrayList<String[]> results = testDb.runQuery(query, new String[0]);
			assertTrue(results.size() >= 1);
		} catch (SQLException e) {
			fail("Error communicating with database: " + e.getMessage());
		}
	}

	@Test
	public void testRunQueryWithSelectOnPatient() {
		String query = "SELECT * FROM tblPatients";
		try {
			ArrayList<String[]> results = testDb.runQuery(query, new String[0]);
			assertTrue(results.size() >= 1);
		} catch (SQLException e) {
			fail("Error communicating with database: " + e.getMessage());
		}
	}
	
	@Test
	public void testRunQueryWithUpdateOnClinician() {
		String query = "UPDATE tblPatients SET LName = 'Bober' WHERE StaffID = '1234'";
		try {
			ArrayList<String[]> results = testDb.runQuery(query, new String[0]);
			assertTrue(results == null);
		} catch (SQLException e) {
			fail("Error communicating with database: " + e.getMessage());
		}
	}
	
	@Test
	public void testRunQueryWithUpdateOnPatient() {
		String query = "UPDATE tblPatients SET LName = 'Bober' WHERE Nhi = 'TST1234'";
		try {
			ArrayList<String[]> results = testDb.runQuery(query, new String[0]);
			assertTrue(results == null);
		} catch (SQLException e) {
			fail("Error communicating with database: " + e.getMessage());
		}
	}
	
	@Test
	public void testAddClinician() {
		//TODO add remove here
		try {
			testDb.add(clinician);
		} catch (IllegalArgumentException e1) {
			fail(e1.getMessage());
		}
		String query = "SELECT * FROM tblClinicians WHERE StaffID = 1234";
		try {
			ArrayList<String[]> results = testDb.runQuery(query, new String[0]);
			assertTrue(results.size() == 1);
		} catch (SQLException e2) {
			fail("Error communicating with database: " + e2.getMessage());
		}
	}

	@Test
	public void testAddExistingClinician() {
		try {
			testDb.add(clinician);

		} catch (IllegalArgumentException e1) {
			String query = "SELECT * FROM tblClinicians WHERE StaffID = 1234";
			try {
				ArrayList<String[]> results = testDb.runQuery(query, new String[0]);
				assertTrue(results.size() == 1);
			} catch (SQLException e2) {
				fail("Error communicating with database: " + e2.getMessage());
			}
		}
		fail("Didn't catch clinician already existing when adding.");
	}

	@Test
	public void testUpdateClinician() {
		clinician.setFirstName("Testing");
		testDb.update(clinician);
	}

	@Test
	public void testNhiInDatabase() {
		fail("Not yet implemented");
	}

	@Test
	public void testSaveTransplantRequest() {
		fail("Not yet implemented");
	}

	@Test
	public void testLoadAll() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemovePatient() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPatientByNhi() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsPatientInDb() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsClinicianInDb() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetClinicianByID() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetNextStaffID() {
		fail("Not yet implemented");
	}

	@Test
	public void testSaveToDisk() {
		fail("Not yet implemented");
	}

	@Test
	public void testImportFromDiskPatients() {
		fail("Not yet implemented");
	}

	@Test
	public void testImportFromDiskClinicians() {
		fail("Not yet implemented");
	}

	@Test
	public void testResetDatabase() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPatients() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetClinicians() {
		fail("Not yet implemented");
	}

	@Test
	public void testShowPatients() {
		fail("Not yet implemented");
	}

	@Test
	public void testShowClinicians() {
		fail("Not yet implemented");
	}

	@Test
	public void testMain() {
		fail("Not yet implemented");
	}

}
