package model_test;

import model.Administrator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import service.Database;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Level;

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
    
    @Before
    public void setUp() {
        userActions.setLevel(Level.OFF);
        database = Database.getDatabase();
        defaultAdmin = new Administrator("admin", "first", new ArrayList<>(), "last", "password");
        nonDefaultAdmin = new Administrator("newAdministrator", "first", new ArrayList<>(), "last", "password");
    }


    @Test
    public void testDeletingNonDefaultAdmin() {
        givenNonDefaultAdmin();
        whenDeletingAdmin(nonDefaultAdmin);
        thenAdminShouldBeRemovedFromDatabase(nonDefaultAdmin);
    }


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
                assertTrue(true);
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
