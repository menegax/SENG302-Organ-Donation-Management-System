package data_access_test;

import DataAccess.DBHelper;
import DataAccess.factories.DAOFactory;
import model.Patient;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import utility.GlobalEnums;
import utility.PatientActionRecord;
import utility.SystemLogger;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;

import static java.util.logging.Level.OFF;
import static utility.UserActionHistory.userActions;

public class MYSQLPatientLogDAOTest {

    private static DAOFactory daoFactory;
    private static DBHelper dbHelper;
    private Patient patient;

    @BeforeClass
    @SuppressWarnings("Duplicates")
    public static void setUp() {
        userActions.setLevel(OFF);
        SystemLogger.systemLogger.setLevel(OFF);
        System.setProperty("connection_type", GlobalEnums.DbType.TEST.getValue());
        daoFactory = DAOFactory.getDAOFactory(GlobalEnums.FactoryType.MYSQL);
        dbHelper = new DBHelper();
    }

    /**
     * Check that logs are saved and parsed from the database correctly
     */
    @Test
    public void testSavePatientLogs() {
        givenPatientInDb();
        whenPatientLogSaved();
        thenPatientLogsAreInDb();
    }


    /**
     * Check that logs are not duplicated - test added for regression as this was an existing bug
     */
    @Test
    public void testSaveNoNewPatientLogs() {
        givenPatientInDb();
        whenPatientLogSaved();
        thenPatientLogsAreInDb();
    }

    /**
     * Check that all logs will be deleted from remote database
     */
    @Test
    public void testDeleteAllLogsByNhi() {
        givenPatientInDb();
        whenPatientLogDeleted();
        thenNoPatientLogsInDb();
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
     * Save patient logs
     */
    private void whenPatientLogSaved() {
        List<PatientActionRecord> recordList = new ArrayList<>();
        recordList.add(new PatientActionRecord(new Timestamp(1000000), Level.INFO, "Test log action 1", "Test log message 1"));
        recordList.add(new PatientActionRecord(new Timestamp(2000000), Level.INFO, "Test log action 2", "Test log message 2"));
        daoFactory.getPatientLogDataAccess().saveLogs(recordList, patient.getNhiNumber());
    }


    /**
     * Check the validity of logs from the database
     */
    private void thenPatientLogsAreInDb() {
        List<PatientActionRecord> records = daoFactory.getPatientLogDataAccess().getAllLogsByUserId(patient.getNhiNumber());
        assert records.get(0).getTimestamp().equals(new Timestamp(1000000));
        assert records.get(0).getLevel().equals(Level.INFO);
        assert records.get(0).getAction().equals("Test log action 1");
        assert records.get(0).getMessage().equals("Test log message 1");
        assert records.get(1).getTimestamp().equals(new Timestamp(2000000));
        assert records.get(1).getLevel().equals(Level.INFO);
        assert records.get(1).getAction().equals("Test log action 2");
        assert records.get(1).getMessage().equals("Test log message 2");

    }

    /**
     * Delete all logs in database by log
     */
    private void whenPatientLogDeleted() {
        daoFactory.getPatientLogDataAccess().deleteLogsByUserId(patient.getNhiNumber());
    }

    /**
     * Check that no logs are in the database for the given patient
     */
    private void thenNoPatientLogsInDb() {
        List<PatientActionRecord> records = daoFactory.getPatientLogDataAccess().getAllLogsByUserId(patient.getNhiNumber());
        assert records.size() == 0;
    }

    @AfterClass
    public static void reset() {
        dbHelper.reset();
    }
}
