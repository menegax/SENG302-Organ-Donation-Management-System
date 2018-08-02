package DataAccess.mysqlDAO;

import DataAccess.factories.MySqlFactory;
import DataAccess.interfaces.ILogDataAccess;
import utility.AdministratorActionRecord;
import utility.ClinicianActionRecord;
import utility.ResourceManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class ClinicianILogDAO implements ILogDataAccess<ClinicianActionRecord> {

    private MySqlFactory mySqlFactory;

    public ClinicianILogDAO() {
        mySqlFactory = MySqlFactory.getMySqlFactory();
    }

    @Override
    public void saveLogs(List<ClinicianActionRecord> records, String id) {
        try (Connection connection = mySqlFactory.getConnectionInstance()) {
            PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("INSERT_CLINICIAN_LOGS"));
            for (ClinicianActionRecord record : records) {
                statement.setString(1, id);
                statement.setString(4, record.getMessage());
                statement.setString(2, record.getTimestamp().toString());
                statement.setString(3, record.getLevel().toString());
                statement.setString(6, record.getTarget());
                statement.setString(5, record.getAction());
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<ClinicianActionRecord> getAllLogsByUserId(String id) {
        try (Connection connection = mySqlFactory.getConnectionInstance()){
            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("SELECT_CLINICIAN_LOGS"));
            ResultSet results = statement.executeQuery();
            List<ClinicianActionRecord> logs = new ArrayList<>();
            while (results.next()) {
                logs.add(new ClinicianActionRecord(Timestamp.valueOf(results.getString("Time")), Level.parse(results.getString("Level")),
                        results.getString("Message"), results.getString("Action"), results.getString("TargetNhi")));
            }
            return logs;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void deleteLogsByUserId(String id) {
        try (Connection connection = mySqlFactory.getConnectionInstance()) {
            PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("DELETE_ALL_CLINICIAN_LOGS"));
            statement.setString(1, id);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
