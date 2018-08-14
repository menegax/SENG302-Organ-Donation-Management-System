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

    /**
     * Gets a new local Patient data access object
     * @return The Patient DAO
     */
    @Override
    public IPatientDataAccess getPatientDataAccess() {
        return new PatientLocalDAO();
    }

    /**
     * Gets a new local User data access object
     * @return The User DAO
     */
    @Override
    public IUserDataAccess getUserDataAccess(){ return new UserLocalDAO(); }

    /**
     * Gets a new local TransplantWaitingList data access object
     * @return The TransplantWaitingList DAO
     */
    @Override
    public ITransplantWaitListDataAccess getTransplantWaitingListDataAccess() {
        return new TransplantWaitListLocalDAO();
    }

    /**
     * Gets a new local Clinician data access object
     * @return The Clinician DAO
     */
    @Override
    public IClinicianDataAccess getClinicianDataAccess() { return new ClinicianLocalDAO(); }

    /**
     * Gets a new local Administrator data access object
     * @return The Administrator DAO
     */
    @Override
    public IAdministratorDataAccess getAdministratorDataAccess() {
        return new AdministratorLocalDAO();
    }

    /**
     * Gets a new local Medication data access object
     * @return The Medication DAO
     */
    @Override
    public IMedicationDataAccess getMedicationDataAccess() {
        throw new NotImplementedException();
    }

    /**
     * Gets a new local Disease data access object
     * @return The Disease DAO
     */
    @Override
    public IDiseaseDataAccess getDiseaseDataAccess() {
        throw new NotImplementedException();
    }

    /**
     * Gets a new local Contact data access object
     * @return The Contact DAO
     */
    @Override
    public IContactDataAccess getContactDataAccess() {
        throw new NotImplementedException();
    }

    /**
     * Gets a new local PatientLog data access object
     * @return The PatientLog DAO
     */
    @Override
    public ILogDataAccess<PatientActionRecord> getPatientLogDataAccess() {
        throw new NotImplementedException();
    }

    /**
     * Gets a new local AdministratorLog data access object
     * @return The AdministratorLog DAO
     */
    @Override
    public ILogDataAccess<AdministratorActionRecord> getAdministratorLogDataAccess() {
        throw new NotImplementedException();
    }

    /**
     * Gets a new local ClinicianLog data access object
     * @return The ClinicianLog DAO
     */
    @Override
    public ILogDataAccess<ClinicianActionRecord> getClinicianLogDataAccess() {
        throw new NotImplementedException();
    }

    /**
     * Gets a new local Procedure data access object
     * @return The Procedure DAO
     */
    @Override
    public IProcedureDataAccess getProcedureDataAccess() {
        throw new NotImplementedException();
    }

	@Override
	public IRequiredOrganDataAccess getRequiredOrgansDataAccess() {
		throw new NotImplementedException();
	}

}
