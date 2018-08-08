package DataAccess.factories;


import DataAccess.interfaces.*;
import DataAccess.localDAO.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import utility.AdministratorActionRecord;
import utility.ClinicianActionRecord;
import utility.PatientActionRecord;

/**
 * Local DB factory, methods point to local db implementations rather that mysql
 */
public class LocalDatabaseFactory extends DAOFactory {

    /**
     * Gets the local db instance
     * @return - LOCAL DB
     */
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
        return new TransplantWaitListLocalDAO();
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
