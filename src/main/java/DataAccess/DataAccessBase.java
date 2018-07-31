package DataAccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static java.util.logging.Level.INFO;
import static utility.SystemLogger.systemLogger;

public abstract class  DataAccessBase {


    private Connection connectionInstance;

    /**
     * Initialize the connection to the remote database.
     */
    private Connection initializeConnection() {
        try {
            connectionInstance = DriverManager.getConnection("jdbc:mysql://132.181.16.98:3306/seng302-2018-team800-test?allowMultiQueries=true", "seng302-team800", "ScornsGammas5531");
            systemLogger.log(INFO, "Connected to UC database");
        } catch (SQLException e1) {
            System.err.println("Failed to connect to UC database server.");
            try {
                connectionInstance = DriverManager.getConnection("jdbc:mysql://222.154.74.253:3306/seng302-2018-team800-test?allowMultiQueries=true", "seng302-team800", "ScornsGammas5531");
                systemLogger.log(INFO, "Connected to Patrick's database remotely");
            } catch (SQLException e2) {
                System.err.println("Failed to connect to database mimic from external source.");
                try {
                    systemLogger.log(INFO, "Connected to Patrick's database locally");
                    connectionInstance = DriverManager.getConnection("jdbc:mysql://192.168.1.70:3306/seng302-2018-team800-test?allowMultiQueries=true", "seng302-team800", "ScornsGammas5531");
                } catch (SQLException e3) {
                    System.err.println("Failed to connect to database mimic from internal source.");
                    System.err.println("All database connections failed.");
                    connectionInstance = null;
                }
            }
        }
        return connectionInstance;
    }


    /**
     *
     * @return -
     */
    public Connection getConnectionInstance(){
        boolean closedConnection;
        try {
            closedConnection = connectionInstance == null || connectionInstance.isClosed();
        } catch (SQLException e) {
            closedConnection = true;
        }
        return connectionInstance = closedConnection ? initializeConnection() : connectionInstance;
    }

    /**
     *
     * @return
     */
    public static IMedicationDataAccess getMedicationDataAccess() {
        return new MedicationDAO();
    }


    /**
     *
     * @return
     */
    public static IDiseaseDataAccess getDiseaseDataAccess() {
        return new DiseaseDAO();
    }


    /**
     *
     * @return
     */
    public static IPatientDataAccess getPatientDataAccess() {
        return new PatientDAO(); }


    /**
     *
     * @return
     */
    public static IContactDataAccess getContactDataAccess() {
        return new ContactDataAccessDAO();
    }

    /**
     *
     * @return
     */
    public static ILogDataAccess getAdministratorDataAccess() {
        return new AdministratorLogDAO();
    }


    /**
     *
     * @return
     */
    public static ILogDataAccess getPatientLogDataAccess() {
        return new PatientLogDAO();
    }


    /**
     *
     * @return
     */
    public static ILogDataAccess getClinicianLogDataAccess() {
        return new ClinicianLogDAO();
    }


    /**
     *
     * @return
     */
    public static IProcedureDataAccess getProcedureDataAccess() {
        return new ProcedureDAO();
    }


}
