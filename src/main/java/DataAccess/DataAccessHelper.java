package DataAccess;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

import static utility.SystemLogger.systemLogger;

public class DataAccessHelper {
    private static HikariConfig config = new HikariConfig("src\\main\\resources\\sql\\HikariConfig.properties");
    private static HikariDataSource ds = new HikariDataSource(config);

    private static DataAccessHelper dataAccessHelper = null;

    private DataAccessHelper() {

    }

    public static DataAccessHelper getDataAccessHelper(){
        if (dataAccessHelper == null) {
            dataAccessHelper = new DataAccessHelper();
        }
        return dataAccessHelper;
    }
    /**
     *
     * @return -
     */
     Connection getConnectionInstance(){
        Connection connection = null;
        try {
            connection = ds.getConnection();
            systemLogger.log(Level.INFO, "Successfully retrieved connection from pool.", DataAccessHelper.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
    /**
     *
     * @return
     */
    public  IMedicationDataAccess getMedicationDataAccess() {
        return new MedicationDAO();
    }


    /**
     *
     * @return
     */
    public IDiseaseDataAccess getDiseaseDataAccess() {
        return new DiseaseDAO();
    }


    /**
     *
     * @return
     */
    public  IPatientDataAccess getPatientDataAccess() {
        return new PatientDAO(); }


    /**
     *
     * @return
     */
    public  IContactDataAccess getContactDataAccess() {
        return new ContactDAO();
    }

    /**
     *
     * @return
     */
    public  ILogDataAccess getAdministratorDataAccess() {
        return new AdministratorLogDAO();
    }


    /**
     *
     * @return
     */
    public  ILogDataAccess getPatientLogDataAccess() {
        return new PatientLogDAO();
    }


    /**
     *
     * @return
     */
    public  ILogDataAccess getClinicianLogDataAccess() {
        return new ClinicianLogDAO();
    }


    /**
     *
     * @return
     */
    public  IProcedureDataAccess getProcedureDataAccess() {
        return new ProcedureDAO();
    }



}
