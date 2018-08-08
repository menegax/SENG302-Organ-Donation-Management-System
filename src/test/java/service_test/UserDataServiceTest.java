package service_test;

import DataAccess.DBHelper;
import DataAccess.factories.DAOFactory;
import DataAccess.localDAO.LocalDB;
import model.Administrator;
import model.Clinician;
import model.User;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import service.ClinicianDataService;
import service.UserDataService;
import utility.GlobalEnums;

import java.util.ArrayList;

@RunWith(MockitoJUnitRunner.class)
public class UserDataServiceTest {

    @Mock
    private DAOFactory mysqlFactory;

    @Mock
    private DAOFactory localDatabase;

    @InjectMocks
    private UserDataService userDataService;

    private static LocalDB localDB;
    private static DBHelper dbHelper;

    @BeforeClass
    public static void setUp() {
        System.setProperty("connection_type", GlobalEnums.DbType.TEST.getValue());
        localDB = LocalDB.getInstance();
        dbHelper = new DBHelper();
    }

    @Test
    public void testPrepareApplication() {
        givenNoAdminOrClinicianInDB();
        userDataService.prepareApplication();
        thenClinicianAndAdminInDB();
    }

    /*@Test
    public void testPrepareApplicationAlreadyInDB() {
        givenAdminAndClinicianInDB();
        userDataService.prepareApplication();
        thenClinicianAndAdminInDB();
    }*/


    private void givenNoAdminOrClinicianInDB() {
        localDB.clear();
        dbHelper.reset();
    }

    private void givenAdminAndClinicianInDB() {
        localDB.storeAdministrator(new Administrator("admin", "John", new ArrayList<>(), "Smith", "password"));
        localDB.storeClinician(new Clinician(0, "Rob", new ArrayList<>(), "Burns", GlobalEnums.Region.CANTERBURY));
        userDataService.save();
    }

    private void thenClinicianAndAdminInDB() {
        assert localDB.getAdministrators().size() == 1; //not going to add check if admin object in case we change them
        assert localDB.getClinicians().size() == 1;
    }
}
