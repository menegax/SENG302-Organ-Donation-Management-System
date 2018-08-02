package service;

import BuisnessRules.BRPatient;
import DataAccess.factories.DAOFactory;
import DataAccess.interfaces.IPatientDataAccess;
import controller.UserControl;
import model.Patient;
import utility.GlobalEnums.*;

public class PatientServiceImpl {

    private UserControl userControl;
    private DAOFactory mysqlFactory;
    private DAOFactory localDbFactory;

    public PatientServiceImpl() {
        userControl = new UserControl();
        mysqlFactory = DAOFactory.getDAOFactory(FactoryType.MYSQL);
        localDbFactory = DAOFactory.getDAOFactory(FactoryType.LOCAL);
    }
    public Patient getLoggedInPatient() {
        IPatientDataAccess patientDataAccess;
         if (userControl.getLoggedInUser() instanceof Patient){
              patientDataAccess = localDbFactory.getPatientDataAccess();
         } else {
             patientDataAccess = mysqlFactory.getPatientDataAccess();
         }
        BRPatient patientLogic = new BRPatient(patientDataAccess);
        return patientLogic.getPatientByNhi(((Patient)userControl.getLoggedInUser()).getNhiNumber());
    }

}
