package DataAccess.factories;

import DataAccess.interfaces.*;
import utility.AdministratorActionRecord;
import utility.ClinicianActionRecord;
import utility.GlobalEnums.*;
import utility.PatientActionRecord;

public abstract class DAOFactory {

    public static DAOFactory getDAOFactory(FactoryType whichFactory) {

        switch (whichFactory) {
            case MYSQL:
                return MySqlFactory.getMySqlFactory();
            case LOCAL:
            default:
                return new LocalDatabaseFactory();
        }
    }
    public abstract IPatientDataAccess getPatientDataAccess();
    public abstract IMedicationDataAccess getMedicationDataAccess();
    public abstract IDiseaseDataAccess getDiseaseDataAccess();
    public abstract IContactDataAccess getContactDataAccess();
    public abstract IAdministratorDataAccess getAdministratorDataAccess();
    public abstract ILogDataAccess<PatientActionRecord> getPatientLogDataAccess();
    public abstract ILogDataAccess<AdministratorActionRecord> getAdministratorLogDataAccess();
    public abstract ILogDataAccess<ClinicianActionRecord> getClinicianLogDataAccess();
    public abstract IProcedureDataAccess getProcedureDataAccess();
    public abstract IClinicianDataAccess getClinicianDataAccess();
}
