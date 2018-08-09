package DataAccess.factories;

import DataAccess.interfaces.*;
import utility.AdministratorActionRecord;
import utility.ClinicianActionRecord;
import utility.GlobalEnums.*;
import utility.PatientActionRecord;

public abstract class DAOFactory {

    /**
     * Gets the correct factory object
     * @param whichFactory - FactoryType type of factory to get
     * @return - DAOFactory
     */
    public static DAOFactory getDAOFactory(FactoryType whichFactory) {

        switch (whichFactory) {
            case MYSQL:
                return MySqlFactory.getMySqlFactory();
            case LOCAL:
            default:
                return new LocalDatabaseFactory();
        }
    }

    /**
     * Gets the correct implementation of PatientDAO
     * @return - IPatientDataAccess implementation
     */
    public abstract IPatientDataAccess getPatientDataAccess();

    /**
     * Gets the correct implementation of MedicationDAO
     * @return - IMedicationDataAccess implementation
     */
    public abstract IMedicationDataAccess getMedicationDataAccess();

    /**
     * Gets the correct implementation of DiseaseDAO
     * @return - IDiseaseDataAccess implementation
     */
    public abstract IDiseaseDataAccess getDiseaseDataAccess();

    /**
     * Gets the correct implementation of ContactDAO
     * @return - IContactDataAccess implementation
     */
    public abstract IContactDataAccess getContactDataAccess();

    /**
     * Gets the correct implementation of AdministratorDAO
     * @return - IAdministratorDataAccess implementation
     */
    public abstract IAdministratorDataAccess getAdministratorDataAccess();

    /**
     * Gets the correct implementation of PatientLogDAO
     * @return - ILogDataAccess<PatientActionRecord> implementation
     */
    public abstract ILogDataAccess<PatientActionRecord> getPatientLogDataAccess();

    /**
     * Gets the correct implementation of AdministratorLogDAO
     * @return - ILogDataAccess<AdministratorActionRecord> implementation
     */
    public abstract ILogDataAccess<AdministratorActionRecord> getAdministratorLogDataAccess();

    /**
     * Gets the correct implementation of ClinicianLogDAO
     * @return - ILogDataAccess<ClinicianActionRecord> implementation
     */
    public abstract ILogDataAccess<ClinicianActionRecord> getClinicianLogDataAccess();

    /**
     * Gets the correct implementation of ProcedureDAO
     * @return - IProcedureDataAccess implementation
     */
    public abstract IProcedureDataAccess getProcedureDataAccess();

    /**
     * Gets the correct implementation of ClinicianDAO
     * @return - IClinicianDataAccess implementation
     */
    public abstract IClinicianDataAccess getClinicianDataAccess();

    /**
     * Gets the correct implementation of UserDAO
     * @return - IUserDataAccess implementation
     */
    public abstract IUserDataAccess getUserDataAccess();

    /**
     * Gets the correct implementation of Transplant wait list
     * @return - ITransplantWaitListDataAccess implementation
     */
    public abstract ITransplantWaitListDataAccess getTransplantWaitingListDataAccess();
}
