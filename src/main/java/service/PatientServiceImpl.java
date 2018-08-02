package service;

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
             String nhi = ((Patient) userControl.getLoggedInUser()).getNhiNumber();
              patientDataAccess = localDbFactory.getPatientDataAccess();
              Patient patient = patientDataAccess.getPatientByNhi(nhi);
              if (patient != null) { //if local db does not contain patient
                  return patient;
              }
              return mysqlFactory.getPatientDataAccess().getPatientByNhi(nhi); //get from remote
         }
         return null;
    }

}
