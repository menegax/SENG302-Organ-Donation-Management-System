package data_access.mysqlDAO;

import data_access.factories.MySqlFactory;
import data_access.interfaces.IAdministratorDataAccess;
import data_access.interfaces.ILogDataAccess;
import model.Administrator;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import utility.AdministratorActionRecord;
import utility.ResourceManager;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;

import static utility.SystemLogger.systemLogger;

public class AdministratorDAO implements IAdministratorDataAccess {

    private final MySqlFactory mySqlFactory;
    private ILogDataAccess<AdministratorActionRecord> administratorLogDAO;

    public AdministratorDAO() {
        mySqlFactory = MySqlFactory.getMySqlFactory();
        administratorLogDAO = mySqlFactory.getAdministratorLogDataAccess();
    }

    @Override
    public void saveAdministrator(Set<Administrator> administrators) {
        try (Connection connection = mySqlFactory.getConnectionInstance()) {
            PreparedStatement preparedStatement = connection.prepareStatement(ResourceManager.getStringForQuery("UPDATE_ADMIN_QUERY"));
            for (Administrator administrator : administrators) {
                preparedStatement = addUpdateParams(administrator, preparedStatement);
                administratorLogDAO.saveLogs(administrator.getAdminActionsList(), administrator.getUsername());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            systemLogger.log(Level.SEVERE, "Could not save administrator to MYSQL DB", this);
        }
    }

    @Override
    public void addAdministrator(Administrator clinician) {
        throw new NotImplementedException();
    }

    @Override
    public boolean deleteAdministrator(Administrator administrator) {
        try(Connection connection = mySqlFactory.getConnectionInstance()) {
            PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("DELETE_ADMINISTRATORS"));
            for (int i=1; i<=2; i++) {
                statement.setString(i, administrator.getUsername());
            }
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            systemLogger.log(Level.SEVERE, "Could not delete administrator from MYSQL DB", this);
        }
        return false;
    }

    @Override
    public Administrator getAdministratorByUsername(String username) {
        try (Connection connection = mySqlFactory.getConnectionInstance()) {
            PreparedStatement preparedStatement = connection.prepareStatement(ResourceManager.getStringForQuery("SELECT_ADMIN_USERNAME"));
            preparedStatement.setString(1, username);
            List<AdministratorActionRecord> records = administratorLogDAO.getAllLogsByUserId(username);
            return constructAdministratorObject(preparedStatement.executeQuery(), records);
        } catch (Exception e) {
            systemLogger.log(Level.SEVERE, "Could not get administrator from MYSQL DB", this);        }
        return null;
    }

    @Override
    public Map<Integer, List<Administrator>> searchAdministrators(String searchTerm) {
        try (Connection connection = mySqlFactory.getConnectionInstance()) {
            Map<Integer, List<Administrator>> resultMap = new HashMap<>();
            for (int i = 0; i <= 3; i++) {
                resultMap.put(i, new ArrayList<>());
            }
            connection.setAutoCommit(false);
            PreparedStatement statement;
            if (searchTerm.equals("")) {
                statement = connection.prepareStatement(ResourceManager.getStringForQuery("SELECT_ADMINISTRATORS"));
            } else {
                statement = connection.prepareStatement(ResourceManager.getStringForQuery("SELECT_ADMINISTRATORS_SUBSTRING"));
                for (int i = 1; i <= 4; i++) {
                    statement.setString(i, searchTerm);
                }
            }
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Administrator administrator = constructAdminObject(resultSet);
                //Add clinician to resultMap with appropriate score
                if (searchTerm.equals("")) {
                    resultMap.get(0).add(administrator);
                } else {
                    int score = resultSet.getInt("matchNum");
                    resultMap.get(score).add(administrator);
                }
            }
            return resultMap;
        } catch (SQLException e) {
            systemLogger.log(Level.SEVERE, "Could not search administrators in MYSQL DB", this);
        }
        return null;
    }

    /**
     * Constructs a minimal admin object for search tables
     * @param resultSet - results from admin table
     * @return - minimal admin object
     * @throws SQLException - thrown if an error occurs when getting results from result set
     */
    private Administrator constructAdminObject(ResultSet resultSet) throws SQLException {
        String username = resultSet.getString("Username");
        String fName = resultSet.getString("FName");
        ArrayList<String> mNames = new ArrayList<>(Arrays.asList(resultSet.getString("FName").split(" ")));
        String lName = resultSet.getString("LName");
        String salt = resultSet.getString("Salt");
        String password = resultSet.getString("Password");
        Timestamp modified = Timestamp.valueOf(resultSet.getString("Modified"));
        return new Administrator(username, fName, mNames, lName, salt, password, modified, new ArrayList<>());
    }

    @Override
    public Set<Administrator> getAdministrators() {
        throw new NotImplementedException();
    }


    /**
     * Adds params to prepared statement for updates
     * @param admin - admin object to strip attributes off
     * @param preparedStatement - statement to build
     * @return - prepared statement
     * @throws SQLException - thrown if error occurs when preparing the statement
     */
    private PreparedStatement addUpdateParams(Administrator admin, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setString(1, admin.getUsername());
        preparedStatement.setString(2, admin.getFirstName());
        preparedStatement.setString(3, admin.getMiddleNames().size() == 0 ? "" : String.join(" ", admin.getMiddleNames()));
        preparedStatement.setString(4, admin.getLastName());
        preparedStatement.setString(5, admin.getSalt());
        preparedStatement.setString(6, admin.getHashedPassword());
        preparedStatement.setString(7, admin.getModified().toString());
        return preparedStatement;
    }


    /**
     *  Constructs an admin object from the db
     * @param resultSet - results from admin table
     * @param records - records from logs table
     * @return - Admin object
     * @throws SQLException - thrown if error occurs in getting next result
     */
    private Administrator constructAdministratorObject(ResultSet resultSet, List<AdministratorActionRecord> records) throws SQLException {
        if (resultSet.next()) {
            String username = resultSet.getString("Username");
            String fName = resultSet.getString("FName");
            ArrayList<String> mNames = new ArrayList<>(Arrays.asList(resultSet.getString("MName").split(" ")));
            String lName = resultSet.getString("LName");
            String salt = resultSet.getString("salt");
            String password = resultSet.getString("password");
            Timestamp modified = Timestamp.valueOf(resultSet.getString("Modified"));
            return new Administrator(username, fName, mNames, lName, salt, password, modified, records == null ? new ArrayList<>() : records);
        }
        return null;
    }
}
