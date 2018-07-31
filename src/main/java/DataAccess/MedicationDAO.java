package DataAccess;

import model.Medication;
import utility.GlobalEnums;
import utility.ResourceManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MedicationDAO extends DataAccessBase implements IMedicationDataAccess {

    @Override
    public int update(String nhi, Medication medication, GlobalEnums.MedicationStatus state) {
        deleteAll(nhi);
        try (Connection connection = getConnectionInstance()) {
            PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("UPDATE_PATIENT_MEDICATION_QUERY"));
            connection.setAutoCommit(false);
            statement.setString(1, nhi);
            statement.setString(2, medication.getMedicationName());
            statement.setString(3, state.toString());
            return statement.executeUpdate();
        } catch (SQLException e) {
            return 0;
        }
    }

    @Override
    public List<Medication> select(String nhi) {
        List<Medication> medications = new ArrayList<>();
        try (Connection connection = getConnectionInstance()) {
            PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("SELECT_PATIENT_MEDICATIONS_QUERY"));
            connection.setAutoCommit(false);
            statement.setString(1, nhi);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                medications.add(new Medication(resultSet.getString("Name")));
            }
            return medications;
        } catch (SQLException e) {
            return null;
        }
    }

    private void deleteAll(String nhi) {
        try (Connection connection = getConnectionInstance()) {
            PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("DELETE_PATIENT_MEDICATIONS"));
            connection.setAutoCommit(false);
            statement.setString(1, nhi);
            statement.executeUpdate();
        } catch (SQLException ignored) {
        }
    }
}
