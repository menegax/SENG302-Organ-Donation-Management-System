package data_access_test;

import DataAccess.DBHelper;
import DataAccess.factories.DAOFactory;
import model.Administrator;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import utility.AdministratorActionRecord;
import utility.GlobalEnums;
import utility.SystemLogger;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;

import static java.util.logging.Level.OFF;
import static utility.UserActionHistory.userActions;

public class MYSQLAdministratorLogDAOTest {

    private static DAOFactory daoFactory;
    private static DBHelper dbHelper;
    private Administrator administrator;

    @BeforeClass
    @SuppressWarnings("Duplicates")
    public static void setUp() {
        userActions.setLevel(OFF);
        SystemLogger.systemLogger.setLevel(OFF);
        daoFactory = DAOFactory.getDAOFactory(GlobalEnums.FactoryType.MYSQL);
        dbHelper = new DBHelper();
    }

    @Test
    public void testSaveAdministratorLogs() {
        givenAdministratorInDb();
        whenAdministratorLogsAreSaved();
        thenAdministratorLogsAreInDb();
    }

    @Test
    public void testDeleteAdministratorLogs() {
        givenAdministratorInDb();
        whenAdministratorLogsAreDeleted();
        thenNoAdministratorLogsAreInDb();
    }

    /**
     * Add administrator in the database if its not already there
     */
    private void givenAdministratorInDb() {
        if (administrator == null) {
            administrator = new Administrator("Username", "Bob", new ArrayList<>(), "Ross", "passw0rd");
            daoFactory.getAdministratorDataAccess().saveAdministrator(new HashSet<Administrator>(){{add(administrator);}});
        }
    }

    /**
     * Save administrator logs to the remote database
     */
    private void whenAdministratorLogsAreSaved() {
        List<AdministratorActionRecord> recordList = new ArrayList<>();
        recordList.add(new AdministratorActionRecord(new Timestamp(1000000), Level.INFO, "Test log action 1", "Test log message 1", "ABC1238"));
        recordList.add(new AdministratorActionRecord(new Timestamp(2000000), Level.INFO, "Test log action 2", "Test log message 2", "ZYX8321"));
        daoFactory.getAdministratorLogDataAccess().saveLogs(recordList, String.valueOf(administrator.getUsername()));
    }

    /**
     * Check that the logs are in the database
     */
    private void thenAdministratorLogsAreInDb() {
        List<AdministratorActionRecord> records = daoFactory.getAdministratorLogDataAccess().getAllLogsByUserId(String.valueOf(administrator.getUsername()));
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
     * Delete all administrator logs
     */
    private void whenAdministratorLogsAreDeleted() {
        daoFactory.getAdministratorLogDataAccess().deleteLogsByUserId(administrator.getUsername());
    }

    /**
     * Check that no records are in the remote database
     */
    private void thenNoAdministratorLogsAreInDb() {
        List<AdministratorActionRecord> records = daoFactory.getAdministratorLogDataAccess().getAllLogsByUserId(administrator.getUsername());
        assert records.size() == 0;
    }

    @AfterClass
    public static void reset() {
        dbHelper.reset();
    }
}
