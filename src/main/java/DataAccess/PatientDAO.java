package DataAccess;

import model.Medication;
import model.Patient;
import utility.GlobalEnums;
import utility.ResourceManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

class PatientDAO extends DataAccessBase implements IPatientDataAccess{


    @Override
    public int update(List<Patient> patients) {
        IMedicationDataAccess medicationDataAccess = DataAccessBase.getMedicationDataAccess();
        IDiseaseDataAccess diseaseDataAccess = DataAccessBase.getDiseaseDataAccess();
        IContactDataAccess contactDataAccess = DataAccessBase.getContactDataAccess();
        ILogDataAccess logDataAccess = DataAccessBase.getPatientLogDataAccess();
        try (Connection connection = getConnectionInstance()) {
            PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("UPDATE_PATIENT_QUERY"));
            connection.setAutoCommit(false);
            for (Patient patient : patients) {
                statement.setString(1, patient.getNhiNumber());
                statement.setString(2, patient.getFirstName());
                statement.setString(3, patient.getMiddleNames() == null ? "" :String.join(" ", patient.getMiddleNames()));
                statement.setString(4, patient.getLastName());
                statement.setString(5, patient.getBirth().toString());
                statement.setString(6, patient.getCREATED().toString());
                statement.setString(7, patient.getModified().toString());
                statement.setString(8, patient.getDeath() == null ? null : patient.getDeath().toString());
                statement.setString(9, patient.getBirthGender() == null ? null : patient.getBirthGender().toString().substring(0,1));
                statement.setString(10, patient.getPreferredGender() == null ? null : patient.getPreferredGender().toString().substring(0,1));
                statement.setString(11, patient.getPreferredName());
                statement.setString(12, String.valueOf(patient.getHeight()));
                statement.setString(13, String.valueOf(patient.getWeight()));
                statement.setString(14, patient.getDeath() == null ? null : patient.getDeath().toString());
                statement.setString(15, patient.getBloodGroup() == null ? null : patient.getBloodGroup().toString());
                List<String> donationList = patient.getDonations().stream().map(GlobalEnums.Organ::toString).collect(Collectors.toList());
                String donations = String.join(",", donationList).toLowerCase();

                int lastComma = donations.lastIndexOf(',');
                donations = lastComma > 0 ? donations.substring(0, lastComma - 1) : "";
                statement.setString(16, donations);

                List<String> organsList = patient.getRequiredOrgans().stream().map(GlobalEnums.Organ::toString).collect(Collectors.toList());
                String organs = String.join(",", organsList).toLowerCase();

                lastComma = organs.lastIndexOf(',');
                organs = lastComma > 0 ? organs.substring(0, lastComma - 1) : "";
                statement.setString(17, organs);

                statement.executeUpdate();
                for (Medication medication : patient.getMedicationHistory()) {
                    medicationDataAccess.update(patient.getNhiNumber(), medication, GlobalEnums.MedicationStatus.HISTORY);
                }
                for (Medication medication : patient.getCurrentMedications()) {
                    medicationDataAccess.update(patient.getNhiNumber(), medication, GlobalEnums.MedicationStatus.CURRENT);
                }
                diseaseDataAccess.update();
                logDataAccess.update(patient.getUserActionsList(), patient.getNhiNumber());
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
