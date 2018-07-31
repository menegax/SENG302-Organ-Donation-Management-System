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

public class PatientLogDAO extends DataAccessBase implements ILogDataAccess<PatientActionRecord>{

    @Override
    public int update(ArrayList records, String id) {
        return 0;
    }

    @Override
    public  List<PatientActionRecord> selectAll(String nhi) {
        try (Connection connection = getConnectionInstance()) {
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
        } catch (SQLException ignore) {
        }
        return null;
    }
}
