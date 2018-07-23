package model_test;


import model.Clinician;
import model.Patient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import service.Database;
import utility.GlobalEnums;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Level;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static utility.UserActionHistory.userActions;

/**
 * Tests valid and invalid controller creation, fetching clinicians from the database, as well as updating clinicians
 */
public class ClinicianTest implements Serializable{

    private Clinician clinician;

    /**
     * Create new clinician
     */
    @Before
    public void setUp() {
        userActions.setLevel(Level.OFF);
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
        int id = Database.getNextStaffID();
        Database.addClinician(new Clinician(id, "Joeseph", new ArrayList<>(), "Bloggs", GlobalEnums.Region.AUCKLAND));
        try {
            assertEquals(Database.getClinicianByID(id).getFirstName(), "Joeseph");
        } catch (InvalidObjectException e) {
            Assert.fail();
        }
    }

    /**
     * verify successful creation of clinician with an address
     */
    @Test
    public void testCreationWithAddress() {
        int id = Database.getNextStaffID();
        Database.addClinician(new Clinician(id, "Lorem", new ArrayList<>(), "Ipsum", "123 some street", "This place", "Ilam", GlobalEnums.Region.GISBORNE));
        try {
            assertNotNull(Database.getClinicianByID(id).getStreet1());
        } catch (InvalidObjectException e) {
            Assert.fail();
        }
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
        try {
            Database.getClinicianByID(clinician.getStaffID());
        } catch(IOException e) {
            Database.addClinician(clinician);
            assert Database.getClinicians().contains(clinician);
        }
    }

    private void whenDeletingClinician(Clinician clinician) {
        Database.deleteClinician(clinician);
    }

    private void thenClinicianShouldntBeRemovedFromDatabase(Clinician clinician) {
        try {
            Database.getClinicianByID(clinician.getStaffID());
        } catch(IOException e) {
            assert false;
        }
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
        Clinician afterClinician = new Clinician(1, "Second", new ArrayList<String>(){{add("Middle"); add("Name");}}, "Last", GlobalEnums.Region.CANTERBURY);
        beforeClinician.setAttributes(afterClinician);
        assertEquals("Second", beforeClinician.getFirstName());
        assertEquals("Name", beforeClinician.getMiddleNames().get(1));

        // checks deep copy has occurred
        beforeClinician.getMiddleNames().remove(0);
        assertEquals("Middle", afterClinician.getMiddleNames().get(0));
    }
}
