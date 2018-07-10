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
	
	private static void resetTestData() {
		patient = new Patient("TST1234", "Test", new ArrayList<String>(), "Tester", LocalDate.of(1998, 1, 8), new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()), null, GlobalEnums.BirthGender.MALE, GlobalEnums.PreferredGender.MAN, "Pat", 1.7, 56.0, GlobalEnums.BloodGroup.O_POSITIVE, new ArrayList<Organ>(), new ArrayList<Organ>(), "Street 1", "Street 2", "Suburb", GlobalEnums.Region.CANTERBURY, 7020, "035441143", "0", "0220918384", "plaffey@mail.com", "EC", "Relationship", "0", "0", "0", "Email@email.com", new ArrayList<PatientActionRecord>(), new ArrayList<Disease>(), new ArrayList<Disease>(), new ArrayList<Medication>(), new ArrayList<Medication>(), new ArrayList<Procedure>());
		clinician = new Clinician(1234, "Test", new ArrayList<String>(), "Tester", Region.CANTERBURY);
		testDb.update(clinician);
		testDb.update(patient);
		testDb.loadAll();
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
		resetTestData();
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
		String query = "UPDATE tblPatients SET LName = 'Bober' WHERE StaffID = 1234";
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
		String query = "SELECT FName FROM tblClinicians WHERE StaffID = 1234";
		try {
			ArrayList<String[]> results = testDb.runQuery(query, new String[0]);
			assertTrue(results.get(0)[0] == "Testing");
		} catch (SQLException e2) {
			fail("Error communicating with database: " + e2.getMessage());
		}
	}

	@Test
	public void testNhiInDatabase1() {
		boolean testResult = testDb.nhiInDatabase("TST1234");
		String query = "SELECT * FROM tblPatient WHERE Nhi = 'TST1234'";
		try {
			ArrayList<String[]> results = testDb.runQuery(query, new String[0]);
			boolean manualTest = results.size() > 0;
			assertTrue(manualTest == testResult);
		} catch (SQLException e) {
			fail("Error communicating with database: " + e.getMessage());
		}
	}

	@Test
	public void testNhiInDatabase2() {
		boolean testResult = testDb.nhiInDatabase("VRG1234");
		String query = "SELECT * FROM tblPatient WHERE Nhi = 'VRG1234'";
		try {
			ArrayList<String[]> results = testDb.runQuery(query, new String[0]);
			boolean manualTest = results.size() > 0;
			assertTrue(manualTest == testResult);
		} catch (SQLException e) {
			fail("Error communicating with database: " + e.getMessage());
		}
	}

	@Test
	public void testLoadAll() {
		testDb.resetLocalDatabase();
		assertTrue(testDb.getPatients().size() == 0);
		assertTrue(testDb.getClinicians().size() == 0);
		testDb.loadAll();
		assertTrue(testDb.getPatients().size() > 0);
		assertTrue(testDb.getClinicians().size() > 0);
	}

	@Test
	public void testGetPatientByNhi() {
		Patient testResult = testDb.getPatientByNhi("TST1234");
		assertTrue(testDb.getPatients().contains(testResult));
		assertTrue(testResult.getNhiNumber().equals("TST1234"));
	}

	@Test
	public void testInDatabasePatientTrue() {
		assertTrue(testDb.inDatabase(patient));
	}

	@Test
	public void testInDatabasePatientFalse() {
		testDb.delete(patient);
		assertFalse(testDb.inDatabase(patient));
	}
	
	@Test
	public void testInDatabaseClinicianTrue() {
		assertTrue(testDb.inDatabase(clinician));
	}
	
	@Test
	public void testInDatabaseClinicianFalse() {
		testDb.delete(clinician);
		assertFalse(testDb.inDatabase(clinician));
	}
	
	@Test
	public void testGetClinicianByID() {
		Clinician testResult = testDb.getClinicianByID(1234);
		assertTrue(testDb.getClinicians().contains(testResult));
		assertTrue(testResult.getStaffID() == 1234);
	}

	@Test
	public void testResetLocalDatabase() {
		assertTrue(testDb.getPatients().size() > 0);
		assertTrue(testDb.getClinicians().size() > 0);
		testDb.resetLocalDatabase();
		assertTrue(testDb.getPatients().size() == 0);
		assertTrue(testDb.getClinicians().size() == 0);
	}
	
	@Test
	public void testDeletePatient() {
		testDb.delete(patient);
		assertFalse(testDb.getPatients().contains(patient));
		String query = "SELECT * FROM tblPatient WHERE Nhi = 'TST1234'";
		try {
			ArrayList<String[]> results = testDb.runQuery(query, new String[0]);
			assertTrue(results.size() == 0);
		} catch (SQLException e) {
			fail("Error communicating with database " + e.getMessage());
		}
	}
	
	@Test
	public void testDeleteClinician() {
		testDb.delete(clinician);
		assertFalse(testDb.getClinicians().contains(clinician));
		String query = "SELECT * FROM tblClinicians WHERE StaffID = 1234";
		try {
			ArrayList<String[]> results = testDb.runQuery(query, new String[0]);
			assertTrue(results.size() == 0);
		} catch (SQLException e) {
			fail("Error communicating with database " + e.getMessage());
		}
	}
}
