package model_test;

import model.Administrator;

import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import service.AdministratorDataService;
import service.interfaces.IAdministratorDataService;
import utility.GlobalEnums;
import utility.SystemLogger;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static java.util.logging.Level.OFF;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static utility.UserActionHistory.userActions;

/**
 * Tests valid and invalid actions performed on/by administrators
 */
public class AdministratorTest implements Serializable {

    private static Administrator defaultAdmin;

    private IAdministratorDataService dataService = new AdministratorDataService();

    @BeforeClass
    public static void setUp() {
        userActions.setLevel(OFF);
        SystemLogger.systemLogger.setLevel(OFF);
        defaultAdmin = new Administrator("admin", "first", new ArrayList<>(), "last", "password");
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
        List<String> middlesNames= new ArrayList<>();
        middlesNames.add("Middle");
        middlesNames.add("Name");
        Administrator beforeAdmin = new Administrator("Username", "First", new ArrayList<>(), "Last", "password");
        Administrator afterAdmin = new Administrator("Username", "Second", middlesNames, "Last", "password");
        beforeAdmin.setAttributes(afterAdmin);
        assertEquals("Second", beforeAdmin.getFirstName());
        assertEquals("Name", beforeAdmin.getMiddleNames().get(1));

        // checks deep copy has occurred
        beforeAdmin.getMiddleNames().remove(0);
        assertEquals("Middle", afterAdmin.getMiddleNames().get(0));
    }


    private void givenDefaultAdmin() {
        try {
            dataService.getAdministratorByUsername(defaultAdmin.getUsername());
        }
        catch (NullPointerException e) {
            dataService.save(defaultAdmin);
            assert dataService.getAdministratorByUsername(defaultAdmin.getUsername()) != null;
        }
    }




    private void whenDeletingAdmin(Administrator administrator) {
        dataService.deleteUser(administrator);
    }


    private void whenUpdatingAdminsFirstName(Administrator administrator, String newName) {
        administrator.setFirstName(newName);
    }



    private void thenAdminShouldntBeRemovedFromDatabase(Administrator administrator) {
        try {
            dataService.getAdministratorByUsername(administrator.getUsername());
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
