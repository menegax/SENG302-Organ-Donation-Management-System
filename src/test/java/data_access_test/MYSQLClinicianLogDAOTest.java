package data_access_test;

import DataAccess.DBHelper;
import DataAccess.factories.DAOFactory;
import model.Clinician;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import utility.ClinicianActionRecord;
import utility.GlobalEnums.*;
import utility.SystemLogger;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;

import static java.util.logging.Level.OFF;
import static utility.UserActionHistory.userActions;

public class MYSQLClinicianLogDAOTest {

    private static DAOFactory daoFactory;
    private static DBHelper dbHelper;
    private Clinician clinician;

    @BeforeClass
    @SuppressWarnings("Duplicates")
    public static void setUp() {
        userActions.setLevel(OFF);
        SystemLogger.systemLogger.setLevel(OFF);
        daoFactory = DAOFactory.getDAOFactory(FactoryType.MYSQL);
        dbHelper = new DBHelper();
    }

    /**
     * Check that the logs are correctly saved and parsed from the remote database
     */
    @Test
    public void testSaveClinicianLogs() {
        givenClinicianInDb();
        whenClinicianLogsAreSaved();
        thenClinicianLogsAreInDb();
    }

    @Test
    public void testDeleteClinicianLogs() {
        givenClinicianInDb();
        whenClinicianLogsAreDeleted();
        thenNoClinicianLogsAreInDb();
    }

    /**
     * Add clinician in the database if its not already there
     */
    private void givenClinicianInDb() {
        if (clinician == null) {
            clinician = new Clinician(1221, "Bob", new ArrayList<>(), "Smith", Region.CANTERBURY);
            daoFactory.getClinicianDataAccess().saveClinician(new HashSet<Clinician>(){{add(clinician);}});
        }
    }

    /**
     * Save clinician logs
     */
    private void whenClinicianLogsAreSaved() {
        List<ClinicianActionRecord> recordList = new ArrayList<>();
        recordList.add(new ClinicianActionRecord(new Timestamp(1000000), Level.INFO, "Test log action 1", "Test log message 1", "ABC1238"));
        recordList.add(new ClinicianActionRecord(new Timestamp(2000000), Level.INFO, "Test log action 2", "Test log message 2", "ZYX8321"));
        daoFactory.getClinicianLogDataAccess().saveLogs(recordList, String.valueOf(clinician.getStaffID()));
    }


    /**
     * Delete clinician logs
     */
    private void whenClinicianLogsAreDeleted() {
        daoFactory.getClinicianLogDataAccess().deleteLogsByUserId(String.valueOf(clinician.getStaffID()));
    }

    /**
     * Check the validity of records from the remote database
     */
    private void thenClinicianLogsAreInDb() {
        List<ClinicianActionRecord> records = daoFactory.getClinicianLogDataAccess().getAllLogsByUserId(String.valueOf(clinician.getStaffID()));
        assert records.get(0).getTimestamp().equals(new Timestamp(1000000));
        assert records.get(0).getLevel().equals(Level.INFO);
        assert records.get(0).getAction().equals("Test log action 1");
        assert records.get(0).getMessage().equals("Test log message 1");
        assert records.get(0).getTarget().equals("ABC1238");
        assert records.get(1).getTimestamp().equals(new Timestamp(2000000));
        assert records.get(1).getLevel().equals(Level.INFO);
        assert records.get(1).getAction().equals("Test log action 2");
        assert records.get(1).getMessage().equals("Test log message 2");
        assert records.get(1).getTarget().equals("ZYX8321");
    }

    /**
     * Check that no clinician records are in the remote database
     */
    private void thenNoClinicianLogsAreInDb() {
        List<ClinicianActionRecord> records = daoFactory.getClinicianLogDataAccess().getAllLogsByUserId(String.valueOf(clinician.getStaffID()));
        assert records.size() == 0;
    }

    @AfterClass
    public static void reset() {
        dbHelper.reset();
    }
}
