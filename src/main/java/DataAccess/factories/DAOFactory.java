package DataAccess.factories;

import DataAccess.interfaces.*;

public abstract class DAOFactory {

    private static final int MYSQL = 1;
    private static final int LOCAL = 1;

    public abstract IPatientDataAccess getPatientDataAccess();
    public abstract IMedicationDataAccess getMedicationDataAccess();
    public abstract IDiseaseDataAccess getDiseaseDataAccess();
    public abstract IContactDataAccess getContactDataAccess();
    public abstract ILogDataAccess getAdministratorDataAccess();
    public abstract ILogDataAccess getPatientLogDataAccess();
    public abstract ILogDataAccess getClinicianLogDataAccess();
    public abstract IProcedureDataAccess getProcedureDataAccess();
}
