package data_access_test;

import DataAccess.DBHelper;
import DataAccess.factories.DAOFactory;
import model.Patient;
import org.junit.BeforeClass;
import org.junit.Test;
import utility.GlobalEnums;
import utility.SystemLogger;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

import static java.util.logging.Level.OFF;
import static utility.UserActionHistory.userActions;

public class MYSQLRequiredOrgansDAOTest {
    private static DAOFactory daoFactory;
    private static DBHelper dbHelper;
    private Patient patient;

    @BeforeClass
    @SuppressWarnings("Duplicates")
    public static void setUp() {
        userActions.setLevel(OFF);
        SystemLogger.systemLogger.setLevel(OFF);
        daoFactory = DAOFactory.getDAOFactory(GlobalEnums.FactoryType.MYSQL);
        dbHelper = new DBHelper();
    }

    @Test
    public void testUpdateRequiredOrgans() {
        givenPatientInDb();
        whenRequiredOrganIsUpdated();
        thenRequiredOrgansAreInDb();
    }

    @Test
    public void testDeleteRequiredOrgan() {
        givenPatientInDb();
        whenRequiredOrganIsUpdated();
        whenRequiredOrganIsDeleted();
        thenRequiredOrganIsInDb();
    }

    @Test
    public void testDeleteAllRequiredOrgans() {
        givenPatientInDb();
        whenRequiredOrganIsUpdated();
        whenAllRequiredOrgansAreDeleted();
        thenNoRequiredOrganIsInDb();
    }

    /**
     * Add patient in the database if its not already there
     */
    private void givenPatientInDb() {
        if (patient == null) {
            patient = new Patient("ZLH0909", "Henry", new ArrayList<>(), "Smith", LocalDate.of(1950, 2, 2));
            daoFactory.getPatientDataAccess().savePatients(new HashSet<Patient>() {{
                add(patient);
            }});
        }
    }

    /**
     * Update required organs for given patient
     */
    private void whenRequiredOrganIsUpdated() {
        daoFactory.getRequiredOrganDataAccess().updateRequiredOrgans(patient.getNhiNumber(), GlobalEnums.Organ.LIVER
                , LocalDate.of(2018, 8, 9));
        daoFactory.getRequiredOrganDataAccess().updateRequiredOrgans(patient.getNhiNumber(), GlobalEnums.Organ.PANCREAS
                , LocalDate.of(2017, 12, 30));
    }

    /**
     * Deletes a required organ for a given patient
     */
    private void whenRequiredOrganIsDeleted() {
        daoFactory.getRequiredOrganDataAccess().deleteRequiredOrganByNhi(patient.getNhiNumber(), GlobalEnums.Organ.LIVER);
    }

    /**
     * Deletes all required organs for a given patient
     */
    private void whenAllRequiredOrgansAreDeleted() {
        daoFactory.getRequiredOrganDataAccess().deleteAllRequiredOrgansByNhi(patient.getNhiNumber());
    }

    /**
     * Checks that the required organs are parsed correctly into the database
     */
    private void thenRequiredOrgansAreInDb() {
        Map<GlobalEnums.Organ, LocalDate> requiredOrgans = daoFactory.getRequiredOrganDataAccess()
                .getRequiredOrganByNhi(patient.getNhiNumber());
        assert requiredOrgans.get(GlobalEnums.Organ.LIVER.getValue()).equals(LocalDate.of(2018,8,9));
        assert requiredOrgans.get(GlobalEnums.Organ.PANCREAS.getValue()).equals(LocalDate.of(2017,12,30));
        assert requiredOrgans.size() == 2;
    }

    /**
     * Checks that the required organ was deleted correctly from the database
     */
    private void thenRequiredOrganIsInDb() {
        Map<GlobalEnums.Organ, LocalDate> requiredOrgans = daoFactory.getRequiredOrganDataAccess()
                .getRequiredOrganByNhi(patient.getNhiNumber());
        assert requiredOrgans.get(GlobalEnums.Organ.PANCREAS.getValue()).equals(LocalDate.of(2017,12,30));
        assert requiredOrgans.size() == 1;
    }

    /**
     * Checks that the required organs are all correctly deleted from the database
     */
    private void thenNoRequiredOrganIsInDb() {
        Map<GlobalEnums.Organ, LocalDate> requiredOrgans = daoFactory.getRequiredOrganDataAccess()
                .getRequiredOrganByNhi(patient.getNhiNumber());
        assert requiredOrgans.size() == 0;
    }
}
