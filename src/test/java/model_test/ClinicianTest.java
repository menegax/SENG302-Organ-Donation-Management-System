package model_test;


import model.Clinician;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import service.Database;
import utility.GlobalEnums;

import javax.xml.crypto.Data;
import java.io.InvalidObjectException;
import java.util.ArrayList;

import static junit.framework.TestCase.*;

/**
 * Tests valid and invalid controller creation, fetching clinicians from the database, as well as updating clinicians
 */
public class ClinicianTest {
    Database database = Database.getDatabase();
    private Clinician clinician;

    @Before
    public void setUp() {
        clinician = new Clinician(0, "Joe", new ArrayList<>(), "Bloggs", GlobalEnums.Region.AUCKLAND);
    }

    @Test
    public void testIncreasingStaffID() {
        Clinician newClinician = new Clinician(database.getNextStaffID(), "John", new ArrayList<>(), "Doe", GlobalEnums.Region.AUCKLAND);
        database.add(newClinician);
        assertEquals(newClinician.getStaffID() + 1, database.getNextStaffID());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalFirstName() {
        database.add(new Clinician(database.getNextStaffID(), "23-%%d", new ArrayList<>(), "Everyman", GlobalEnums.Region.GISBORNE));
    }

    @Test
    public void testGettingClinicianById() {
        int id = database.getNextStaffID();
        database.add(new Clinician(id, "Joeseph", new ArrayList<>(), "Bloggs", GlobalEnums.Region.AUCKLAND));
        try {
            assertEquals(database.getClinicianByID(id).getFirstName(), "Joeseph");
        } catch (InvalidObjectException e) {
            Assert.fail();
        }
    }

    @Test
    public void testCreationWithAddress() {
        int id = database.getNextStaffID();
        database.add(new Clinician(id, "Lorem", new ArrayList<>(), "Ipsum", "123 some street", "This place", "Ilam", GlobalEnums.Region.GISBORNE));
        try {
            assertNotNull(database.getClinicianByID(id).getStreet1());
        } catch (InvalidObjectException e) {
            Assert.fail();
        }
    }

    @Test
    public void testInvalidUpdateOfFirstName() {
        clinician.setFirstName("8675309");
        assertEquals("Joe", clinician.getFirstName());
    }

}
