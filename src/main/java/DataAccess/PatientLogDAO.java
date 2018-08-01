package DataAccess;

import utility.PatientActionRecord;
import utility.ResourceManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class PatientLogDAO implements ILogDataAccess<PatientActionRecord> {

    private MySqlFactory mySqlFactory;

    PatientLogDAO () {
        mySqlFactory = MySqlFactory.getMySqlFactory();
    }

    @Override
    public int update(List<PatientActionRecord> records, String id) {
        return 0;
    }

    @Override
    public List<PatientActionRecord> selectAll(String id) {
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
}
