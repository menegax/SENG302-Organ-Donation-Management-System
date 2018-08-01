package DataAccess.mysqlDAO;

import DataAccess.interfaces.IProcedureDataAccess;
import DataAccess.factories.MySqlFactory;
import model.Procedure;
import utility.GlobalEnums.Organ;
import utility.ResourceManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ProcedureDAO implements IProcedureDataAccess {

    private MySqlFactory mySqlFactory;

    public ProcedureDAO () {
        mySqlFactory = MySqlFactory.getMySqlFactory();
    }

    @Override
    public int update(String nhi, Procedure procedure) {
        try (Connection connection = mySqlFactory.getConnectionInstance()) {
            deleteAll(connection, nhi);
            PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("UPDATE_PATIENT_PROCEDURES_QUERY"));
            connection.setAutoCommit(false);
            statement.setString(1, nhi);
            statement.setString(2, procedure.getSummary());
            statement.setString(3, procedure.getDescription());
            statement.setString(4, procedure.getDate().toString());
            String organs = String.join(",", procedure.getAffectedDonations().stream().map(Organ::toString).collect(Collectors.toList()));
            statement.setString(5, organs);
            return statement.executeUpdate();
        } catch (SQLException e) {
            return 0;
        }
    }

    @Override
    public List<Procedure> select(String nhi) {
        try (Connection connection = mySqlFactory.getConnectionInstance()){
            connection.setAutoCommit(false);
            List<Procedure> procedures = new ArrayList<>();
            PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("SELECT_PATIENT_PROCEDURES_QUERY"));
            statement.setString(1, nhi);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String summary = resultSet.getString("Summary");
                String description = resultSet.getString("Description");
                LocalDate date = LocalDate.parse(resultSet.getString("ProDate"));
                Set<Organ> affected = new HashSet<>();
                for (String string : resultSet.getString("AffectedOrgans").split(",")) {
                    affected.add(Organ.getEnumFromString(string));
                }
                Procedure procedure = new Procedure(summary, description, date, affected);
                procedures.add(procedure);
            }
            return procedures;
        } catch (SQLException e) {
            return null;
        }
    }

    private void deleteAll(Connection connection, String nhi) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("DELETE_PATIENT_PROCEDURES"));
        connection.setAutoCommit(false);
        statement.setString(1, nhi);
        statement.executeUpdate();
    }
}
