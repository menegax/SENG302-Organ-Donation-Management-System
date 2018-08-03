package service;

import DataAccess.factories.DAOFactory;
import model.Patient;
import utility.GlobalEnums.*;

public class PatientServiceImpl {

    private DAOFactory mysqlFactory;
    private DAOFactory localDbFactory;

    public PatientServiceImpl() {
        mysqlFactory = DAOFactory.getDAOFactory(FactoryType.MYSQL);
        localDbFactory = DAOFactory.getDAOFactory(FactoryType.LOCAL);
    }


    /**
     * Will check local db first before checking remote
     * @param nhi -
     * @return -
     */
    public Patient getPatientByNhi(String nhi) {
        if (localDbFactory.getPatientDataAccess().getPatientByNhi(nhi) == null) { //not in local db
            return mysqlFactory.getPatientDataAccess().getPatientByNhi(nhi);
        }
        return localDbFactory.getPatientDataAccess().getPatientByNhi(nhi);
    }

    public void deletePatientByNhi(String nhi) {
        localDbFactory.getPatientDataAccess().deletePatientByNhi(nhi); //todo:
    }


}
