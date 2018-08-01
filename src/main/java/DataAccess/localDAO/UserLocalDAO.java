package DataAccess.localDAO;

import DataAccess.factories.DAOFactory;
import DataAccess.factories.LocalDatabaseFactory;
import DataAccess.interfaces.IAdministratorDataAccess;
import DataAccess.interfaces.IClinicianDataAccess;
import DataAccess.interfaces.IPatientDataAccess;
import DataAccess.interfaces.IUserDataAccess;
import model.Administrator;
import model.Clinician;
import model.Patient;
import model.User;
import utility.GlobalEnums;

import java.util.ArrayList;

public class UserLocalDAO implements IUserDataAccess {

    DAOFactory factory = DAOFactory.getDAOFactory(GlobalEnums.FactoryType.LOCAL);

    public boolean addUser(User user) {
        if (user instanceof Patient) {
            IPatientDataAccess patientDAO = factory.getPatientDataAccess();
            return patientDAO.addPatientsBatch(new ArrayList<Patient>(){{add((Patient) user);}});
        } else if (user instanceof Clinician) {
            IClinicianDataAccess clinicianDAO = factory.getClinicianDataAccess();
            return clinicianDAO.addClinician((Clinician) user);
        } else if (user instanceof Administrator) {
            IAdministratorDataAccess administratorDAO = factory.getAdministratorDataAccess();
            return administratorDAO.addAdministrator((Administrator) user);
        }
        return false;
    }

    public boolean deleteUser(User user) {
        if (user instanceof Patient) {
            IPatientDataAccess patientDAO = factory.getPatientDataAccess();
            return patientDAO.deletePatient((Patient) user);
        } else if (user instanceof Clinician) {
            IClinicianDataAccess clinicianDAO = factory.getClinicianDataAccess();
            return clinicianDAO.deleteClinician((Clinician) user);
        } else if (user instanceof Administrator) {
            IAdministratorDataAccess administratorDAO = factory.getAdministratorDataAccess();
            return administratorDAO.deleteAdministrator((Administrator) user);
        }
        return false;
    }
}
