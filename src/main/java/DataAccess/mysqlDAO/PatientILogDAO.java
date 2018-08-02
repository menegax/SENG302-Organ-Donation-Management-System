package DataAccess.mysqlDAO;

import DataAccess.factories.MySqlFactory;
import DataAccess.interfaces.ILogDataAccess;
import utility.PatientActionRecord;
import utility.ResourceManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class PatientILogDAO implements ILogDataAccess<PatientActionRecord>{

    private MySqlFactory mySqlFactory;

    public PatientILogDAO() {
        mySqlFactory = MySqlFactory.getMySqlFactory();
    }


    @Override
    public void saveLogs(List<PatientActionRecord> records, String id) {
        try (Connection connection = mySqlFactory.getConnectionInstance()) {
            PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("INSERT_PATIENT_LOGS"));
            for (PatientActionRecord record : records) {
                statement.setString(1, id);
                statement.setString(4, record.getMessage());
                statement.setString(2, record.getTimestamp().toString());
                statement.setString(3, record.getLevel().toString());
                statement.setString(5, record.getAction());
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<PatientActionRecord> getAllLogsByUserId (String id) {
        try (Connection connection = mySqlFactory.getConnectionInstance()){
            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("SELECT_PATIENT_LOGS"));
            ResultSet results = statement.executeQuery();
            List<PatientActionRecord> logs = new ArrayList<>();
            while (results.next()) {
                logs.add(new PatientActionRecord(results.getTimestamp("Time"),
                        Level.parse(results.getString("Level")), results.getString("Action"),
                        results.getString("Message")));
            }
            return logs;
        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    public void deleteLogsByUserId(String id) {
        try (Connection connection = mySqlFactory.getConnectionInstance()) {
            PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("DELETE_ALL_PATIENT_LOGS"));
            statement.setString(1, id);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
