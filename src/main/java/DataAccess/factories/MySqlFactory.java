package DataAccess.factories;

import DataAccess.interfaces.*;
import DataAccess.mysqlDAO.*;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import sun.misc.Resource;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import utility.ClinicianActionRecord;
import utility.PatientActionRecord;

import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

import static utility.GlobalEnums.DbType.TEST;
import static utility.SystemLogger.systemLogger;

public class MySqlFactory extends DAOFactory {
    private HikariConfig config;
    private HikariDataSource ds;
    private static MySqlFactory mySqlFactory = null;

    /**
     * Sets up the MySql factory that provides MySql data access objects
     * On creation, the connection type variable is retrieved, which determines whether
     * the MySql connection is to the production or test database
     */
    private MySqlFactory() {
        String connection_type = System.getProperty("connection_type");
        if (connection_type != null && connection_type.equals(TEST.getValue())) {
            config = new HikariConfig("/sql/HikariConfigTest.properties");
        } else {
            config = new HikariConfig("/sql/HikariConfigProd.properties");
        }
        ds = new HikariDataSource(config);
    }

    /**
     * Returns the singleton factory. If it is not initialised, a new factory is created
     *
     * @return The factory instance
     */
    public static MySqlFactory getMySqlFactory() {
        if (mySqlFactory == null) {
            mySqlFactory = new MySqlFactory();
        }
        return mySqlFactory;
    }

    /**
     * Returns the connection instance to the database
     *
     * @return The connection instance
     */
    public Connection getConnectionInstance() {
        Connection connection = null;
        config.setAutoCommit(false);
        try {
            connection = ds.getConnection();
            systemLogger.log(Level.INFO, "Successfully retrieved connection from pool.", MySqlFactory.class);
        } catch (SQLException e) {
            systemLogger.log(Level.SEVERE, "Could not get connection", this);        }
        return connection;
    }

    /**
     * Returns a new MySql Medication data access object
     *
     * @return The Medication DAO
     */
    public IMedicationDataAccess getMedicationDataAccess() {
        return new MedicationDAO();
    }

    /**
     * Returns a new MySql Disease data access object
     *
     * @return The Disease DAO
     */
    public IDiseaseDataAccess getDiseaseDataAccess() {
        return new DiseaseDAO();
    }

    /**
     * Returns a new MySql Patient data access object
     *
     * @return The Patient DAO
     */
    public IPatientDataAccess getPatientDataAccess() {
        return new PatientDAO();
    }

    /**
     * Returns a new MySql Contact data access object
     *
     * @return The Contact DAO
     */
    public IContactDataAccess getContactDataAccess() {
        return new ContactDAO();
    }

    /**
     * Returns a new MySql Administrator data access object
     *
     * @return The Administrator DAO
     */
    public IAdministratorDataAccess getAdministratorDataAccess() {
        return new AdministratorDAO();
    }

    /**
     * Returns a new MySql AdministratorLog data access object
     *
     * @return The AdministratorLog DAO
     */
    public ILogDataAccess getAdministratorLogDataAccess() {
        return new AdministratorLogDAO();
    }

    /**
     * Returns a new MySql PatientLog data access object
     *
     * @return The PatientLog DAO
     */
    public ILogDataAccess<PatientActionRecord> getPatientLogDataAccess() {
        return new PatientILogDAO();
    }

    /**
     * Returns a new MySql ClinicianLog data access object
     *
     * @return The ClinicianLog DAO
     */
    public ILogDataAccess<ClinicianActionRecord> getClinicianLogDataAccess() {
        return new ClinicianILogDAO();
    }

    /**
     * Returns a new MySql Procedure data access object
     *
     * @return The Procedure DAO
     */
    public IProcedureDataAccess getProcedureDataAccess() {
        return new ProcedureDAO();
    }

    /**
     * Returns a new MySql Clinician data access object
     *
     * @return The Clinician DAO
     */
    public IClinicianDataAccess getClinicianDataAccess() {
        return new ClinicianDAO();
    }

    /**
     * Returns a new User data access object
     *
     * @return The User DAO
     */
    public IUserDataAccess getUserDataAccess() {
        throw new NotImplementedException();
    }

    /**
     * Returns a new TransplantWaitingList data access object
     *
     * @return The TransplantWaitingList DAO
     */
    public ITransplantWaitListDataAccess getTransplantWaitingListDataAccess() {
        return new TransplantWaitingListDAO();
    }

}
