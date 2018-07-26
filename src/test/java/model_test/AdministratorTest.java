package model_test;

import model.Administrator;
import model.Clinician;
import model.Disease;
import model.Medication;
import model.Patient;
import model.Procedure;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import service.Database;
import utility.GlobalEnums;
import utility.PatientActionRecord;
import utility.GlobalEnums.Organ;
import utility.GlobalEnums.Region;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.logging.Level;

import static java.util.logging.Level.OFF;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static utility.UserActionHistory.userActions;

/**
 * Tests valid and invalid actions performed on/by administrators
 */
public class AdministratorTest implements Serializable {

    private Administrator defaultAdmin;

    private Administrator nonDefaultAdmin;

    private Database database;
    
    private static boolean validConnection = false;
    
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
        database = Database.getDatabase();
        defaultAdmin = new Administrator("admin", "first", new ArrayList<>(), "last", "password");
        nonDefaultAdmin = new Administrator("newAdministrator", "first", new ArrayList<>(), "last", "password");
    }


//    @Test
//    public void testDeletingNonDefaultAdmin() {
//        givenNonDefaultAdmin();
//        whenDeletingAdmin(nonDefaultAdmin);
//        thenAdminShouldBeRemovedFromDatabase(nonDefaultAdmin);
//    }


    @Test
    public void testDeletingDefaultAdmin() {
        givenDefaultAdmin();
        whenDeletingAdmin(defaultAdmin);
        thenAdminShouldntBeRemovedFromDatabase(defaultAdmin);
    }


    @Test
    public void testValidUpdateOfFirstName() {
        givenDefaultAdmin();
        whenUpdatingAdminsFirstName(defaultAdmin, "billy");
        thenAdminFirstNameShouldBeUpdated(defaultAdmin, "billy");
    }


    @Test
    public void testInvalidUpdateOfFirstName() {
        givenDefaultAdmin();
        whenUpdatingAdminsFirstName(defaultAdmin, "bil%y");
        thenAdminFirstNameShouldntBeUpdated(defaultAdmin, "bil%y");
    }

    /**
     * Tests the setAttributes method
     */
    @Test
    public void testSetAttributes() {
        Administrator beforeAdmin = new Administrator("Username", "First", new ArrayList<>(), "Last", "password");
        Administrator afterAdmin = new Administrator("Username", "Second", new ArrayList<String>(){{add("Middle"); add("Name");}}, "Last", "password");
        beforeAdmin.setAttributes(afterAdmin);
        assertEquals("Second", beforeAdmin.getFirstName());
        assertEquals("Name", beforeAdmin.getMiddleNames().get(1));

        // checks deep copy has occurred
        beforeAdmin.getMiddleNames().remove(0);
        assertEquals("Middle", afterAdmin.getMiddleNames().get(0));
    }


    private void givenDefaultAdmin() {
        try {
            database.getAdministratorByUsername(defaultAdmin.getUsername());
        }
        catch (NullPointerException e) {
            database.add(defaultAdmin);
            assert database.getAdministrators()
                    .contains(defaultAdmin);
        }
    }


    private void givenNonDefaultAdmin() {
        try {
            database.getAdministratorByUsername(nonDefaultAdmin.getUsername());
        }
        catch (NullPointerException e) {
            database.add(nonDefaultAdmin);
            assert database.getAdministrators()
                    .contains(nonDefaultAdmin);
        }
    }


    private void whenDeletingAdmin(Administrator administrator) {
        database.delete(administrator);
    }


    private void whenUpdatingAdminsFirstName(Administrator administrator, String newName) {
        administrator.setFirstName(newName);
    }


    private void thenAdminShouldBeRemovedFromDatabase(Administrator administrator) {
        try {
            if(database.getAdministratorByUsername(administrator.getUsername()) == null) {
                ;
            }
            assert false;
        }
        catch (NullPointerException e) {
            assert true;
        }
    }


    private void thenAdminShouldntBeRemovedFromDatabase(Administrator administrator) {
        try {
            database.getAdministratorByUsername(administrator.getUsername());
        }
        catch (NullPointerException e) {
            assert false;
        }
    }


    private void thenAdminFirstNameShouldBeUpdated(Administrator admin, String newName) {
        assert admin.getFirstName()
                .equals(newName);
    }


    private void thenAdminFirstNameShouldntBeUpdated(Administrator admin, String newName) {
        assert !admin.getFirstName()
                .equals(newName);
    }
}
