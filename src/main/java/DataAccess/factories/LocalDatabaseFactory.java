package DataAccess.factories;

import DataAccess.LocalDB;
import DataAccess.interfaces.*;
import DataAccess.localDAO.AdministratorLocalDAO;
import DataAccess.localDAO.ClinicianLocalDAO;
import DataAccess.localDAO.PatientLocalDAO;
import DataAccess.localDAO.UserLocalDAO;
import DataAccess.mysqlDAO.AdministratorLogDAO;
import DataAccess.mysqlDAO.PatientDAO;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import utility.AdministratorActionRecord;
import utility.ClinicianActionRecord;
import utility.PatientActionRecord;

public class LocalDatabaseFactory extends DAOFactory {

    public static LocalDB getLocalDbInstance() {
        return LocalDB.getInstance();
    }

    @Override
    public IPatientDataAccess getPatientDataAccess() {
        return new PatientLocalDAO();
    }

    @Override
    public IUserDataAccess getUserDataAccess(){ return new UserLocalDAO(); }

    @Override
    public ITransplantWaitListDataAccess getTransplantWaitingListDataAccess() {
        return null;
    }

    @Override
    public IClinicianDataAccess getClinicianDataAccess() { return new ClinicianLocalDAO(); }

    @Override
    public IAdministratorDataAccess getAdministratorDataAccess() {
        return new AdministratorLocalDAO();
    }

    @Override
    public IMedicationDataAccess getMedicationDataAccess() {
        throw new NotImplementedException();
    }

    @Override
    public IDiseaseDataAccess getDiseaseDataAccess() {
        throw new NotImplementedException();
    }

    @Override
    public IContactDataAccess getContactDataAccess() {
        throw new NotImplementedException();
    }

    @Override
    public ILogDataAccess<PatientActionRecord> getPatientLogDataAccess() {
        throw new NotImplementedException();
    }

    @Override
    public ILogDataAccess<AdministratorActionRecord> getAdministratorLogDataAccess() {
        throw new NotImplementedException();
    }

    @Override
    public ILogDataAccess<ClinicianActionRecord> getClinicianLogDataAccess() {
        throw new NotImplementedException();
    }

    @Override
    public IProcedureDataAccess getProcedureDataAccess() {
        throw new NotImplementedException();
    }

}
