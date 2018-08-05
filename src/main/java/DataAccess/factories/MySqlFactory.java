package DataAccess.factories;

import DataAccess.interfaces.*;
import DataAccess.mysqlDAO.*;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import utility.ClinicianActionRecord;
import utility.PatientActionRecord;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

import static utility.GlobalEnums.DbType.TEST;
import static utility.SystemLogger.systemLogger;

public class MySqlFactory extends DAOFactory{
    private HikariConfig config;
    private HikariDataSource ds;
    private static MySqlFactory mySqlFactory = null;

    private MySqlFactory() {
        String connection_type = System.getProperty("connection_type");
        if (connection_type != null && connection_type.equals(TEST.getValue())) {
            config = new HikariConfig("src/main/resources/sql/HikariConfigTest.properties"); //todo:  check in jar, apparently it wraps in class loader
        } else {
            config = new HikariConfig("src/main/resources/sql/HikariConfigProd.properties"); //todo:  check in jar, apparently it wraps in class loader
        }
        ds = new HikariDataSource(config);
    }

    public static MySqlFactory getMySqlFactory(){
        if (mySqlFactory == null) {
            mySqlFactory = new MySqlFactory();
        }
        return mySqlFactory;
    }

    public Connection getConnectionInstance(){
        Connection connection = null;
        config.setAutoCommit(false);
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

    public IAdministratorDataAccess getAdministratorDataAccess() {
        return new AdministratorDAO();
    }

    public ILogDataAccess getAdministratorLogDataAccess() { return new AdministratorLogDAO(); }


    public ILogDataAccess<PatientActionRecord> getPatientLogDataAccess() {
        return new PatientILogDAO();
    }


    public ILogDataAccess<ClinicianActionRecord> getClinicianLogDataAccess() {
        return new ClinicianILogDAO();
    }

    public  IProcedureDataAccess getProcedureDataAccess() {
        return new ProcedureDAO();
    }

    public IClinicianDataAccess getClinicianDataAccess() { return new ClinicianDAO(); }

    public IUserDataAccess getUserDataAccess() { throw new NotImplementedException(); }

    public ITransplantWaitListDataAccess getTransplantWaitingListDataAccess() {
        return new TransplantWaitingListDAO();
    }

}
