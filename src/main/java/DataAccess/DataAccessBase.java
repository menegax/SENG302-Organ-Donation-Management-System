package DataAccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static java.util.logging.Level.INFO;
import static utility.SystemLogger.systemLogger;

abstract class  DataAccessBase {



    private Connection connectionInstance;

    /**
     *
     * @param object -
     * @param <T> -
     * @return -
     */
    protected abstract  <T> int update (T object);

    /**
     *
     * @param object -
     * @param <T> -
     * @return -
     */
    protected abstract <T> boolean insert (T object);

    /**
     *
     * @param <T> -
     * @return -
     */
    protected abstract <T> T select ();


    /**
     * Initialize the connection to the remote database.
     */
    private void initializeConnection() {
        try {
            connectionInstance = DriverManager.getConnection("jdbc:mysql://mysql2.csse.canterbury.ac.nz:3306/seng302-2018-team800-test?allowMultiQueries=true", "seng302-team800", "ScornsGammas5531");
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
    }


    /**
     * Gets the connection instance of the session
     * @return -
     */
    public Connection getConnectionInstance() {
        return connectionInstance;
    }

}
