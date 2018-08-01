package DataAccess.factories;

import DataAccess.interfaces.*;
import utility.GlobalEnums.*;

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
    public abstract ILogDataAccess getAdministratorDataAccess();
    public abstract ILogDataAccess getPatientLogDataAccess();
    public abstract ILogDataAccess getClinicianLogDataAccess();
    public abstract IProcedureDataAccess getProcedureDataAccess();
    public abstract IClinicianDataAccess getClinicianDataAccess();
}
