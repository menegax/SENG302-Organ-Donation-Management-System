package data_access_test;

import data_access.DBHelper;
import data_access.factories.DAOFactory;
import model.Patient;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import service.OrganWaitlist;
import utility.GlobalEnums;
import utility.SystemLogger;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;

import static java.util.logging.Level.OFF;
import static utility.UserActionHistory.userActions;

public class MYSQLTransplantWaitListDAOTest {

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
    public void testUpdateWaitingList() {
        givenPatientInDb();
        whenWaitingListIsUpdated();
        thenWaitingListIsInDb();
    }

    @Test
    public void testDeleteWaitingList() {
        givenPatientInDb();
        whenWaitingListIsDeleted();
        thenNoWaitingListInDb();
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
     * Update waiting list in remote database
     */
    private void whenWaitingListIsUpdated() {
        OrganWaitlist waitinglist = new OrganWaitlist();
        waitinglist.add("Test1", GlobalEnums.Organ.LIVER, LocalDate.of(2018,9,9),
                GlobalEnums.Region.CANTERBURY, patient.getNhiNumber());
        waitinglist.add("Test2", GlobalEnums.Organ.LIVER, LocalDate.of(2018,9,9),
                GlobalEnums.Region.CANTERBURY, patient.getNhiNumber());
        daoFactory.getTransplantWaitingListDataAccess().updateWaitingList(waitinglist);
    }

    /**
     * Delete the waiting list
     */
    private void whenWaitingListIsDeleted() {
        daoFactory.getTransplantWaitingListDataAccess().deleteWaitingList();
    }

    /**
     * Check validity of waiting list from remote database
     */
    private void thenWaitingListIsInDb() {
        OrganWaitlist waitlist = daoFactory.getTransplantWaitingListDataAccess().getWaitingList();
        assert waitlist.getRequests().size() == 2;
    }

    /**
     * Check that there is no waiting list
     */
    private void thenNoWaitingListInDb() {
        OrganWaitlist waitlist = daoFactory.getTransplantWaitingListDataAccess().getWaitingList();
        assert waitlist.getRequests().size() == 0;
    }


    @AfterClass
    public static void reset() {
        dbHelper.reset();
    }
}
