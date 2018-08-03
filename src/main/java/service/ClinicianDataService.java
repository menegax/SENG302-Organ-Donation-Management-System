package service;

import DataAccess.factories.DAOFactory;
import DataAccess.interfaces.IClinicianDataAccess;
import DataAccess.interfaces.IPatientDataAccess;
import model.Clinician;
import model.Patient;
import service.interfaces.IClinicianDataService;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import utility.GlobalEnums;

import java.util.List;
import java.util.Map;

public class ClinicianDataService implements IClinicianDataService {

    private DAOFactory mysqlFactory;
    private DAOFactory localDbFactory;

    public ClinicianDataService() {
        mysqlFactory = DAOFactory.getDAOFactory(GlobalEnums.FactoryType.MYSQL);
        localDbFactory = DAOFactory.getDAOFactory(GlobalEnums.FactoryType.LOCAL);
    }

    public OrganWaitlist getOrganWaitList() {
        if (localDbFactory.getTransplantWaitingListDataAccess().getWaitingList() == null) {
            return mysqlFactory.getTransplantWaitingListDataAccess().getWaitingList();
        }
        return  localDbFactory.getTransplantWaitingListDataAccess().getWaitingList();
    }

    @Override
    public Clinician getClinician(String staffId) {
        IClinicianDataAccess clinicianDataAccessLocal = localDbFactory.getClinicianDataAccess();
        if (clinicianDataAccessLocal.getClinicianByStaffId(Integer.parseInt(staffId)) == null) {
            return mysqlFactory.getClinicianDataAccess().getClinicianByStaffId(Integer.parseInt(staffId));
        }
        return  clinicianDataAccessLocal.getClinicianByStaffId(Integer.parseInt(staffId));
    }

    @Override
    public List<Patient> searchPatient(String searchTerm, Map<GlobalEnums.FilterOption, String> filters, int numResults) {
        IPatientDataAccess patientDataAccess = mysqlFactory.getPatientDataAccess();
        return patientDataAccess.searchPatient(searchTerm, filters, numResults); //TODO: local db search logic here also
    }

    @Override
    public void save(Clinician clinician) {
        throw new NotImplementedException();
    }

}
