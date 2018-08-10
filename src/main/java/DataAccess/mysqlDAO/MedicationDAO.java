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
import java.util.logging.Level;

import static utility.SystemLogger.systemLogger;

public class MedicationDAO  implements IMedicationDataAccess {


    private MySqlFactory mySqlFactory;

    public MedicationDAO () {
        mySqlFactory = MySqlFactory.getMySqlFactory();
    }


    @Override
    public int updateMedication(String nhi, List<Medication> medications) {
        try (Connection connection = mySqlFactory.getConnectionInstance()) {
            deleteAllMedicationsByNhi(nhi);
            PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("UPDATE_PATIENT_MEDICATION_QUERY"));
            for (Medication medication : medications) {
                statement.setString(1, nhi);
                statement.setString(2, medication.getMedicationName());
                statement.setInt(3, medication.getMedicationStatus().getValue());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            systemLogger.log(Level.SEVERE, "Could not update medication to MYSQL DB", this);
        }
        return 0;
    }


    @Override
    public List<Medication> getMedicationsByNhi(String nhi) {
        try (Connection connection = mySqlFactory.getConnectionInstance()){
            List<Medication> medications = new ArrayList<>();
            PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("SELECT_PATIENT_MEDICATIONS_QUERY"));
            statement.setString(1, nhi);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Medication medication = new Medication(resultSet.getString("Name"),
                        Integer.parseInt(resultSet.getString("State")) == (MedicationStatus.CURRENT.getValue())
                                ? MedicationStatus.CURRENT : MedicationStatus.HISTORY);
                medications.add(medication);
            }
            return medications;
        } catch (SQLException e) {
            systemLogger.log(Level.SEVERE, "Could not get medication from MYSQL DB", this);        }
        return null;
    }

    @Override
    public void deleteAllMedicationsByNhi(String nhi) {
        try (Connection connection = mySqlFactory.getConnectionInstance()) {
            PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("DELETE_PATIENT_MEDICATIONS"));
            statement.setString(1, nhi);
            statement.executeUpdate();
        } catch (SQLException e) {
            systemLogger.log(Level.SEVERE, "Could not delete medication from MYSQL DB", this);
        }
    }
}
