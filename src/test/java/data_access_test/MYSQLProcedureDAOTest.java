package data_access_test;

import data_access.DBHelper;
import data_access.factories.DAOFactory;
import model.Patient;
import model.Procedure;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import utility.GlobalEnums.*;
import utility.GlobalEnums;
import utility.SystemLogger;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static java.util.logging.Level.OFF;
import static utility.UserActionHistory.userActions;

public class MYSQLProcedureDAOTest {

    private static DAOFactory daoFactory;
    private static DBHelper dbHelper;
    private Patient patient;

    @BeforeClass
    @SuppressWarnings("Duplicates")
    public static void setUp() {
        userActions.setLevel(OFF);
        SystemLogger.systemLogger.setLevel(OFF);
        System.setProperty("connection_type", GlobalEnums.DbType.STORY44.getValue());
        daoFactory = DAOFactory.getDAOFactory(FactoryType.MYSQL);
        dbHelper = new DBHelper();
    }


    @Test
    public void testUpdateProcedures() {
        givenPatientInDb();
        whenProcedureIsUpdated();
        thenProceduresAreInDb();
    }

    @Test
    public void testDeleteAllProcedures() {
        givenPatientInDb();
        whenProcedureIsDeleted();
        thenNoProceduresAreInDb();
    }


    /**
     * Add patient in the database if its not already there
     */
    private void givenPatientInDb() {
        if (patient == null) {
            patient = new Patient("ZLH0909", "Henry", new ArrayList<>(), "Smith", LocalDate.of(1950, 2, 2));
            daoFactory.getPatientDataAccess().savePatients(new HashSet<Patient>(){{add(patient);}});
        }
    }

    /**
     * Update procedure for given patient
     */
    private void whenProcedureIsUpdated() {
        Procedure procedure = new Procedure("test summary", "test description",
                LocalDate.of(2018, 9, 9), new HashSet<Organ>(){{
                    add(Organ.KIDNEY);
                    add(Organ.LIVER);
        }});
        daoFactory.getProcedureDataAccess().updateProcedure(patient.getNhiNumber(), procedure);
    }

    /**
     * Delete all procedures for a given patient
     */
    private void whenProcedureIsDeleted() {
        daoFactory.getProcedureDataAccess().deleteAllProceduresByNhi(patient.getNhiNumber());
    }

    /**
     * Check that the procedures are parsed correctly from the db
     */
    private void thenProceduresAreInDb() {
        List<Procedure> procedures = daoFactory.getProcedureDataAccess().getProceduresByNhi(patient.getNhiNumber());
        assert procedures.get(0).getSummary().equals("test summary");
        assert procedures.get(0).getDescription().equals("test description");
        assert procedures.get(0).getDate().equals(LocalDate.of(2018, 9, 9));
        assert procedures.get(0).getAffectedDonations().contains(Organ.LIVER);
        assert procedures.get(0).getAffectedDonations().contains(Organ.KIDNEY);
        assert procedures.get(0).getAffectedDonations().size() == 2;
    }


    /**
     * Check that no procedures remain for the given patient
     */
    private void thenNoProceduresAreInDb() {
        List<Procedure> procedures = daoFactory.getProcedureDataAccess().getProceduresByNhi(patient.getNhiNumber());
        assert procedures.size() == 0;
    }

    @AfterClass
    public static void reset() {
        dbHelper.reset();
    }

}
