package DataAccess.factories;

import DataAccess.LocalDB;
import DataAccess.interfaces.*;
import DataAccess.localDAO.PatientLocalDAO;
import DataAccess.mysqlDAO.PatientDAO;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class LocalDatabaseFactory extends DAOFactory {

    public static LocalDB getLocalDbInstance() {
        return LocalDB.getInstance();
    }

    @Override
    public IPatientDataAccess getPatientDataAccess() {
        return new PatientLocalDAO();
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
    public ILogDataAccess getAdministratorDataAccess() {
        throw new NotImplementedException();
    }

    @Override
    public ILogDataAccess getPatientLogDataAccess() {
        throw new NotImplementedException();
    }

    @Override
    public ILogDataAccess getClinicianLogDataAccess() {
        throw new NotImplementedException();
    }

    @Override
    public IProcedureDataAccess getProcedureDataAccess() {
        throw new NotImplementedException();
    }

    @Override
    public IClinicianDataAccess getClinicianDataAccess() { throw new NotImplementedException(); }

}
