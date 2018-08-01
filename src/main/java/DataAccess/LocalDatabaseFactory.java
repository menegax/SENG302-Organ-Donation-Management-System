package DataAccess;

public class LocalDatabaseFactory extends DAOFactory {
    @Override
    public IPatientDataAccess getPatientDataAccess() {
        return null;
    }

    @Override
    public IMedicationDataAccess getMedicationDataAccess() {
        return null;
    }

    @Override
    public IDiseaseDataAccess getDiseaseDataAccess() {
        return null;
    }

    @Override
    public IContactDataAccess getContactDataAccess() {
        return null;
    }

    @Override
    public ILogDataAccess getAdministratorDataAccess() {
        return null;
    }

    @Override
    public ILogDataAccess getPatientLogDataAccess() {
        return null;
    }

    @Override
    public ILogDataAccess getClinicianLogDataAccess() {
        return null;
    }

    @Override
    public IProcedureDataAccess getProcedureDataAccess() {
        return null;
    }
}
