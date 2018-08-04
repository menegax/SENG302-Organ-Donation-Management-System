package service;

import DataAccess.factories.DAOFactory;
import DataAccess.interfaces.IAdministratorDataAccess;
import DataAccess.interfaces.IPatientDataAccess;
import model.Administrator;
import service.interfaces.IAdministratorDataService;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import utility.CachedThreadPool;
import utility.GlobalEnums;

import java.util.ArrayList;

public class AdministratorDataService implements IAdministratorDataService {

    private DAOFactory mysqlFactory;
    private DAOFactory localDbFactory;
    private CachedThreadPool cachedThreadPool;

    public AdministratorDataService() {
        cachedThreadPool = CachedThreadPool.getCachedThreadPool();
        mysqlFactory = DAOFactory.getDAOFactory(GlobalEnums.FactoryType.MYSQL);
        localDbFactory = DAOFactory.getDAOFactory(GlobalEnums.FactoryType.LOCAL);
    }

    @Override
    public void deletePatient(String nhi) {
        IPatientDataAccess patientDataAccess = mysqlFactory.getPatientDataAccess(); //todo:
        patientDataAccess.deletePatientByNhi(nhi);
    }

    @Override
    public void importRecords() {
        cachedThreadPool.getThreadService().submit(() -> {
            //List<Patient> patients = parseCSV.parse(filepath); //todo:
            IPatientDataAccess patientDataAccess = mysqlFactory.getPatientDataAccess();
            patientDataAccess.addPatientsBatch(new ArrayList<>());
        });
    }

    @Override
    public void searchUsers() {
        throw new NotImplementedException();
    }

    @Override
    public Administrator getAdministratorByUsername(String username) {
        IAdministratorDataAccess administratorDataAccess = localDbFactory.getAdministratorDataAccess();
        Administrator administrator = administratorDataAccess.getAdministratorByUsername(username);
        if (administrator == null) {
            return mysqlFactory.getAdministratorDataAccess().getAdministratorByUsername(username);
        }
        return administrator;
    }

    @Override
    public void save(Administrator administrator) {
        IAdministratorDataAccess dataAccess = localDbFactory.getAdministratorDataAccess();
        dataAccess.addAdministrator(administrator);
    }
}
