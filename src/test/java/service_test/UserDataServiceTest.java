package service_test;

import DataAccess.DBHelper;
import DataAccess.localDAO.LocalDB;
import model.Administrator;
import model.Clinician;
import org.junit.BeforeClass;
import org.junit.Test;
import service.UserDataService;
import utility.GlobalEnums;

import java.util.ArrayList;

public class UserDataServiceTest {

    private static LocalDB localDB;
    private static DBHelper dbHelper;
    private static UserDataService userDataService;

    @BeforeClass
    public static void setUp() {
        localDB = LocalDB.getInstance();
        dbHelper = new DBHelper();
        userDataService = new UserDataService();
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
