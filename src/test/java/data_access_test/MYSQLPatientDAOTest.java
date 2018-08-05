package data_access_test;

import DataAccess.factories.DAOFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import utility.GlobalEnums.*;
import utility.GlobalEnums.DbType.*;

public class MYSQLPatientDAOTest {


    @BeforeClass
    public static void setUp() {
        System.setProperty("connection_type", DbType.PRODUCTION.toString());
    }


    @Test
    public void testTest(){
        DAOFactory daoFactory = DAOFactory.getDAOFactory(FactoryType.MYSQL);
        System.out.println(daoFactory.getClinicianLogDataAccess().getAllLogsByUserId(String.valueOf(0)));

    }

}
