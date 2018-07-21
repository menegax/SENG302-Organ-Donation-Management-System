package model_test;

import model.Administrator;
import org.junit.Before;
import org.junit.Test;
import service.Database;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Tests valid and invalid actions performed on/by administrators
 */
public class AdministratorTest {
    private Administrator defaultAdmin;
    private Administrator nonDefaultAdmin;

    @Before
    public void setUp() {
        defaultAdmin = new Administrator("admin", "first", new ArrayList<>(), "last", "password");
        nonDefaultAdmin = new Administrator("newAdministrator", "first", new ArrayList<>(), "last", "password");
    }

    public void givenDefaultAdmin() {
        try {
            Database.getAdministratorByUsername(defaultAdmin.getUsername());
        } catch(IOException e) {
            Database.addAdministrator(defaultAdmin);
            assert Database.getAdministrators().contains(defaultAdmin);
        }
    }

    public void givenNonDefaultAdmin() {
        try {
            Database.getAdministratorByUsername(nonDefaultAdmin.getUsername());
        } catch(IOException e) {
            Database.addAdministrator(nonDefaultAdmin);
            assert Database.getAdministrators().contains(nonDefaultAdmin);
        }
    }

    public void whenDeletingAdmin(Administrator administrator) {
        Database.deleteAdministrator(administrator);
    }

    public void whenUpdatingAdminsFirstName(Administrator administrator, String newName) {
        administrator.setFirstName(newName);
    }

    public void thenAdminShouldBeRemovedFromDatabase(Administrator administrator) {
        try {
            Database.getAdministratorByUsername(administrator.getUsername());
            assert false;
        } catch(IOException e) {
            assert true;
        }
    }

    public void thenAdminShouldntBeRemovedFromDatabase(Administrator administrator) {
        try {
            Database.getAdministratorByUsername(administrator.getUsername());
        } catch(IOException e) {
            assert false;
        }
    }

    public void thenAdminFirstNameShouldBeUpdated(Administrator admin, String newName) {
        assert admin.getFirstName().equals(newName);
    }

    public void thenAdminFirstNameShouldntBeUpdated(Administrator admin, String newName) {
        assert !admin.getFirstName().equals(newName);
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
}
