package DataAccess.mysqlDAO;

import DataAccess.interfaces.IMedicationDataAccess;
import DataAccess.factories.MySqlFactory;
import model.Medication;
import utility.GlobalEnums.*;
import utility.ResourceManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MedicationDAO  implements IMedicationDataAccess {


    private MySqlFactory mySqlFactory;

    public MedicationDAO () {
        mySqlFactory = MySqlFactory.getMySqlFactory();
    }


    @Override
    public int updateMedication(String nhi, Medication medication, MedicationStatus state) {
        try (Connection connection = mySqlFactory.getConnectionInstance()) {
            deleteAll(connection, nhi);
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
    public List<Medication> getMedicationsByNhi(String nhi) {
        try (Connection connection = mySqlFactory.getConnectionInstance()){
            connection.setAutoCommit(false);
            List<Medication> medications = new ArrayList<>();
            PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("SELECT_PATIENT_MEDICATIONS_QUERY"));
            statement.setString(1, nhi);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Medication medication = new Medication(resultSet.getString("Name"));
                MedicationStatus status = resultSet.getString("State").equals(MedicationStatus.CURRENT.toString()) ? MedicationStatus.CURRENT : MedicationStatus.HISTORY;
                medication.setMedicationStatus(status);
                medications.add(medication);
            }
            return medications;
        } catch (SQLException e) {
            return null;
        }
    }

    private void deleteAll(Connection connection, String nhi) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("DELETE_PATIENT_MEDICATIONS"));
        connection.setAutoCommit(false);
        statement.setString(1, nhi);
        statement.executeUpdate();
    }
}
