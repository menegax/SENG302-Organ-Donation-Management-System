package DataAccess.factories;

import DataAccess.interfaces.*;
import DataAccess.mysqlDAO.*;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

import static utility.SystemLogger.systemLogger;

public class MySqlFactory extends DAOFactory{
    private static HikariConfig config = new HikariConfig("src\\main\\resources\\sql\\HikariConfig.properties");
    private static HikariDataSource ds = new HikariDataSource(config);

    private static MySqlFactory mySqlFactory = null;

    private MySqlFactory() {

    }

    public static MySqlFactory getMySqlFactory(){
        if (mySqlFactory == null) {
            mySqlFactory = new MySqlFactory();
        }
        return mySqlFactory;
    }

    public Connection getConnectionInstance(){
        Connection connection = null;
        try {
            connection = ds.getConnection();
            systemLogger.log(Level.INFO, "Successfully retrieved connection from pool.", MySqlFactory.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public IMedicationDataAccess getMedicationDataAccess() {
        return new MedicationDAO();
    }

    public IDiseaseDataAccess getDiseaseDataAccess() {
        return new DiseaseDAO();
    }

    public IPatientDataAccess getPatientDataAccess() { return new PatientDAO(); }

    public IContactDataAccess getContactDataAccess() {
        return new ContactDAO();
    }

    public ILogDataAccess getAdministratorDataAccess() {
        return new AdministratorLogDAO();
    }

    public  ILogDataAccess getPatientLogDataAccess() {
        return new PatientLogDAO();
    }

    public  ILogDataAccess getClinicianLogDataAccess() {
        return new ClinicianLogDAO();
    }

    public  IProcedureDataAccess getProcedureDataAccess() {
        return new ProcedureDAO();
    }

    public IClinicianDataAccess getClinicianDataAccess() { return new ClinicianDAO(); }

}
