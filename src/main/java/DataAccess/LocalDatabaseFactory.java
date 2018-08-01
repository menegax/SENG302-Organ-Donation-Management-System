package DataAccess;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class LocalDatabaseFactory extends DAOFactory {


    @Override
    public IPatientDataAccess getPatientDataAccess() {
        return null;
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
}
