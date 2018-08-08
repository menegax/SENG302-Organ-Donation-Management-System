package model_test;


import model.Clinician;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import service.AdministratorDataService;
import service.ClinicianDataService;
import utility.GlobalEnums;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;

import static java.util.logging.Level.OFF;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static utility.SystemLogger.systemLogger;
import static utility.UserActionHistory.userActions;

/**
 * Tests valid and invalid controller creation, fetching clinicians from the database, as well as updating clinicians
 */
public class ClinicianTest implements Serializable{

    private Clinician clinician;

    private static boolean validConnection = false;

    private ClinicianDataService clinicianDataService = new ClinicianDataService();

    private AdministratorDataService dataService = new AdministratorDataService();

	@BeforeClass
	public static void setUpBeforeClass() {
		userActions.setLevel(OFF);
		validConnection = validateConnection();
	}


	private static boolean validateConnection() {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection("jdbc:mysql://mysql2.csse.canterbury.ac.nz:3306/seng302-2018-team800-test?allowMultiQueries=true", "seng302-team800", "ScornsGammas5531");
		} catch (SQLException e1) {
			System.err.println("Failed to connect to UC database server.");
		}
		if (conn == null) {
			return false;
		}
		return true;
	}

    @Before
    public void setUp() {
    	Assume.assumeTrue(validConnection);
        userActions.setLevel(Level.OFF);
        systemLogger.setLevel(Level.OFF);
        clinician = new Clinician(0, "Joe", new ArrayList<>(), "Bloggs", GlobalEnums.Region.AUCKLAND);
    }

    /**
     *  verify the new staffID
     */
    @Test
    public void testIncreasingStaffID() {
        Clinician newClinician = new Clinician(Database.getNextStaffID(), "John", new ArrayList<>(), "Doe", GlobalEnums.Region.AUCKLAND);
        Database.addClinician(newClinician);
        assertEquals(newClinician.getStaffID() + 1, Database.getNextStaffID());
    }

    /**
     * Verify creation of a new clinician with an invalid first name results in an exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testIllegalFirstName() {
        Database.addClinician(new Clinician(Database.getNextStaffID(), "23-%%d", new ArrayList<>(), "Everyman", GlobalEnums.Region.GISBORNE));
    }

    /**
     * Verifys db level getting of a clinician by id
     */
    @Test
    public void testGettingClinicianById() {
        int id = 101;
        clinicianDataService.save(new Clinician(id, "Joeseph", new ArrayList<>(), "Bloggs", GlobalEnums.Region.AUCKLAND));
        assertEquals(clinicianDataService.getClinician(id).getFirstName(), "Joeseph");
    }

    /**
     * verify successful creation of clinician with an address
     */
    @Test
    public void testCreationWithAddress() {
        int id = 102;
        clinicianDataService.save(new Clinician(id, "Lorem", new ArrayList<>(), "Ipsum", "123 some street", "This place", "Ilam", GlobalEnums.Region.GISBORNE));
        assertNotNull(clinicianDataService.getClinician(id).getStreet1());
    }

    /**
     * verify that a clinician cannot be updated with an invalid name
     */
    @Test
    public void testInvalidUpdateOfFirstName() {
        clinician.setFirstName("8675309");
        assertEquals("Joe", clinician.getFirstName());
    }


    private void givenDefaultClinician() {
        clinicianDataService.getClinician(clinician.getStaffID());
    }

    private void whenDeletingClinician(Clinician clinician) {
        dataService.deleteUser(clinician);
    }

    private void thenClinicianShouldntBeRemovedFromDatabase(Clinician clinician) {
        clinicianDataService.getClinician(clinician.getStaffID());
    }

    @Test
    public void testDeletingDefaultClinician() {
        givenDefaultClinician();
        whenDeletingClinician(clinician);
        thenClinicianShouldntBeRemovedFromDatabase(clinician);
    }
    /**
     * Tests the setAttributes method
     */
    @Test
    public void testSetAttributes() {
        Clinician beforeClinician = new Clinician(1, "First", new ArrayList<>(), "Last", GlobalEnums.Region.CANTERBURY);
        ArrayList<String> middles = new ArrayList<>();
        middles.add("Middle");
        middles.add("Name");
        Clinician afterClinician = new Clinician(1, "Second", middles, "Last", GlobalEnums.Region.CANTERBURY);
        beforeClinician.setAttributes(afterClinician);
        assertEquals("Second", beforeClinician.getFirstName());
        assertEquals("Name", beforeClinician.getMiddleNames().get(1));

        // checks deep copy has occurred
        beforeClinician.getMiddleNames().remove(0);
        assertEquals("Middle", afterClinician.getMiddleNames().get(0));
    }
}
