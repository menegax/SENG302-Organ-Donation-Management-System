package data_access.mysqlDAO;

import data_access.factories.MySqlFactory;
import data_access.interfaces.ILogDataAccess;
import utility.AdministratorActionRecord;
import utility.ResourceManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static utility.SystemLogger.systemLogger;

public class AdministratorLogDAO implements ILogDataAccess<AdministratorActionRecord> {

    private MySqlFactory mysqlFactory;

    public AdministratorLogDAO() {
        mysqlFactory = MySqlFactory.getMySqlFactory();
    }

    @Override
    public void saveLogs(List<AdministratorActionRecord> records, String id) {
       try (Connection connection = mysqlFactory.getConnectionInstance()) {
           PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("INSERT_ADMINISTRATOR_LOGS"));
           deleteLogsByUserId(id);
           for (AdministratorActionRecord record : records) {
               statement.setString(1, id);
               statement.setString(4, record.getMessage());
               statement.setString(2, record.getTimestamp().toString());
               statement.setString(3, record.getLevel().toString());
               statement.setString(5, record.getAction());
               statement.setString(6, record.getTarget());
               statement.executeUpdate();
           }
       } catch (SQLException e) {
           e.printStackTrace();
           systemLogger.log(Level.SEVERE, "Could not save administrator logs for the username " + id + " to the MySQL database");
       }
    }

    @Override
    public List<AdministratorActionRecord> getAllLogsByUserId(String username) {
        try (Connection connection = mysqlFactory.getConnectionInstance()) {
            PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("SELECT_ADMINISTRATOR_LOGS"));
            statement.setString(1,username);
            ResultSet results = statement.executeQuery();
            List<AdministratorActionRecord> logs = new ArrayList<>();
            while (results.next()) {
                logs.add(new AdministratorActionRecord(results.getTimestamp("Time"),
                        Level.parse(results.getString("Level")), results.getString("Action"),
                        results.getString("Message"), results.getString("Target")));
            }
            return logs;
        } catch (SQLException e) {
            systemLogger.log(Level.SEVERE, "Could not get administrator logs for the username " + username + " from the MySQL database");
        }
        return null;
    }

    @Override
    public void deleteLogsByUserId(String username) {
        try (Connection connection = mysqlFactory.getConnectionInstance()) {
            PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("DELETE_ALL_ADMIN_LOGS"));
            statement.setString(1, username);
            statement.execute();
        } catch (SQLException e) {
            systemLogger.log(Level.SEVERE, "Could not delete administrator logs for the username " + username + " from the MySQL database");
        }
    }
}
