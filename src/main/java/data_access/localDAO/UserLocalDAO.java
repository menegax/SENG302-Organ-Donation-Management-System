package data_access.localDAO;

import data_access.factories.DAOFactory;
import data_access.interfaces.IAdministratorDataAccess;
import data_access.interfaces.IClinicianDataAccess;
import data_access.interfaces.IPatientDataAccess;
import data_access.interfaces.IUserDataAccess;
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
        LocalDB localDB = LocalDB.getInstance();
        localDB.undelete(user);
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
        Map<Integer, List<User>> searchResults = new HashMap<>();
        if (searchTerm.equals("")) {
            searchResults.put(0, new ArrayList<>());
            searchResults.put(1, new ArrayList<>());
            searchResults.put(2, new ArrayList<>());
            searchResults.get(0).addAll(getUsers());
        } else {
            searchResults = Searcher.getSearcher().search(searchTerm, new UserTypes[]{UserTypes.PATIENT, UserTypes.CLINICIAN, UserTypes.ADMIN}, 30, null);
        }
        return searchResults;
    }
}
