package DataAccess.localDAO;

import DataAccess.factories.DAOFactory;
import DataAccess.interfaces.IAdministratorDataAccess;
import DataAccess.interfaces.IClinicianDataAccess;
import DataAccess.interfaces.IPatientDataAccess;
import DataAccess.interfaces.IUserDataAccess;
import model.Administrator;
import model.Clinician;
import model.Patient;
import model.User;
import utility.GlobalEnums.*;
import utility.Searcher;

import java.util.*;

public class UserLocalDAO implements IUserDataAccess {

    private DAOFactory factory = DAOFactory.getDAOFactory(FactoryType.LOCAL);

    public void addUser(User user) {
        if (user instanceof Patient) {
            IPatientDataAccess patientDAO = factory.getPatientDataAccess();
            patientDAO.addPatientsBatch(new ArrayList<Patient>(){{add((Patient) user);}});
        } else if (user instanceof Clinician) {
            IClinicianDataAccess clinicianDAO = factory.getClinicianDataAccess();
            clinicianDAO.addClinician((Clinician) user);
        } else if (user instanceof Administrator) {
            IAdministratorDataAccess administratorDAO = factory.getAdministratorDataAccess();
            administratorDAO.addAdministrator((Administrator) user);
        }
    }

    public void deleteUser(User user) {
        if (user instanceof Patient) {
            IPatientDataAccess patientDAO = factory.getPatientDataAccess();
            patientDAO.deletePatient((Patient) user);
        } else if (user instanceof Clinician) {
            IClinicianDataAccess clinicianDAO = factory.getClinicianDataAccess();
            clinicianDAO.deleteClinician((Clinician) user);
        } else if (user instanceof Administrator) {
            IAdministratorDataAccess administratorDAO = factory.getAdministratorDataAccess();
            administratorDAO.deleteAdministrator((Administrator) user);
        }
    }

    @Override
    public Set<User> getUsers() {
        IPatientDataAccess patientDataAccess = factory.getPatientDataAccess();
        IClinicianDataAccess clinicianDataAccess = factory.getClinicianDataAccess();
        IAdministratorDataAccess administratorDataAccess = factory.getAdministratorDataAccess();
        Set<User> users = new HashSet<>(patientDataAccess.getPatients());
        users.addAll(clinicianDataAccess.getClinicians());
        users.addAll(administratorDataAccess.getAdministrators());
        return users;
    }

    @Override
    public Set<User> getDeletedUsers() {
        return LocalDB.getInstance().getDeletedUsers();
    }

    @Override
    public Map<Integer, List<User>> searchUsers(String searchTerm) {
        return Searcher.getSearcher().search(searchTerm, new UserTypes[]{UserTypes.PATIENT, UserTypes.CLINICIAN, UserTypes.ADMIN}, 30, null);
    }
}
