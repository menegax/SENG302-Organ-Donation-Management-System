package model_test;


import model.Clinician;
import model.Patient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
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
import static utility.SystemLogger.systemLogger;
import static utility.UserActionHistory.userActions;

/**
 * Tests valid and invalid controller creation, fetching clinicians from the database, as well as updating clinicians
 */
public class ClinicianTest implements Serializable{

    Database database = Database.getDatabase();

    private Clinician clinician;

    /**
     * Create new clinician
     */
    @Before
    public void setUp() {
        userActions.setLevel(Level.OFF);
        systemLogger.setLevel(Level.OFF);
        clinician = new Clinician(0, "Joe", new ArrayList<>(), "Bloggs", GlobalEnums.Region.AUCKLAND);
    }

    /**
     * Turn off logging
     */
    @BeforeClass
    public static void turnOff() {
        userActions.setLevel(Level.OFF);
    }

    /**
     * Verifys db level getting of a clinician by id
     */
    @Test
    public void testGettingClinicianById() {
        int id = 101;
        database.add(new Clinician(id, "Joeseph", new ArrayList<>(), "Bloggs", GlobalEnums.Region.AUCKLAND));
        assertEquals(database.getClinicianByID(id).getFirstName(), "Joeseph");
    }

    /**
     * verify successful creation of clinician with an address
     */
    @Test
    public void testCreationWithAddress() {
        int id = 102;
        database.add(new Clinician(id, "Lorem", new ArrayList<>(), "Ipsum", "123 some street", "This place", "Ilam", GlobalEnums.Region.GISBORNE));
        assertNotNull(database.getClinicianByID(id).getStreet1());
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
        database.getClinicianByID(clinician.getStaffID());
    }

    private void whenDeletingClinician(Clinician clinician) {
        database.delete(clinician);
    }

    private void thenClinicianShouldntBeRemovedFromDatabase(Clinician clinician) {
        database.getClinicianByID(clinician.getStaffID());
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
