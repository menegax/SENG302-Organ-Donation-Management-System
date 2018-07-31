package DataAccess;

import model.Medication;
import model.Patient;
import utility.GlobalEnums;
import utility.ResourceManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

class PatientDAO extends DataAccessBase implements IPatientDataAccess{


    @Override
    public int update(List<Patient> patients) {
        IMedicationDataAccess medicationDataAccess = DataAccessBase.getMedicationDataAccess();
        IDiseaseDataAccess diseaseDataAccess = DataAccessBase.getDiseaseDataAccess();
        IContactDataAccess contactDataAccess = DataAccessBase.getContactDataAccess();
        try (Connection connection = getConnectionInstance()) {
            PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("UPDATE_PATIENT_QUERY"));
            connection.setAutoCommit(false);
            for (Patient patient : patients) {
                statement.setString(1, patient.getNhiNumber()); //todo:
                statement.setString(1, patient.getNhiNumber());
                statement.setString(1, patient.getNhiNumber());
                statement.setString(1, patient.getNhiNumber());
                statement.setString(1, patient.getNhiNumber());
                statement.setString(1, patient.getNhiNumber());
                statement.setString(1, patient.getNhiNumber());
                statement.setString(1, patient.getNhiNumber());
                statement.setString(1, patient.getNhiNumber());
                statement.setString(1, patient.getNhiNumber());
                statement.setString(1, patient.getNhiNumber());
                statement.setString(1, patient.getNhiNumber());
                statement.executeUpdate();
                for (Medication medication : patient.getMedicationHistory()) {
                    medicationDataAccess.update(patient.getNhiNumber(), medication, GlobalEnums.MedicationStatus.HISTORY);
                }
                for (Medication medication : patient.getCurrentMedications()) {
                    medicationDataAccess.update(patient.getNhiNumber(), medication, GlobalEnums.MedicationStatus.CURRENT);
                }
                diseaseDataAccess.update();
                contactDataAccess.update(patient);
                connection.commit(); //commit if no errors
            }
        } catch (SQLException e) {

        }
        return 0;
    }

    @Override
    public boolean insert(Patient patient) {
        return false;
    }

    @Override
    public boolean insert(List<Patient> patient) {
        return false;
    }

    @Override
    public List<Patient> select() {
        return null;
    }

    @Override
    public Patient selectOne(String nhi) {
        IMedicationDataAccess medicationDataAccess = DataAccessBase.getMedicationDataAccess();
        IDiseaseDataAccess diseaseDataAccess = DataAccessBase.getDiseaseDataAccess();
        try (Connection connection = getConnectionInstance()) {


        } catch (SQLException e) {

        }
        return null;
    }
}
