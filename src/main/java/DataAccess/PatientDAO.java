package DataAccess;

import model.Disease;
import model.Medication;
import model.Patient;
import model.Procedure;
import utility.GlobalEnums.*;
import utility.PatientActionRecord;
import utility.ResourceManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.logging.Level.INFO;
import static utility.UserActionHistory.userActions;

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
                statement.setString(16, donations);
                List<String> organsList = patient.getRequiredOrgans().stream().map(GlobalEnums.Organ::toString).collect(Collectors.toList());
                String organs = String.join(",", organsList).toLowerCase();
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
            PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("SELECT_PATIENT_BY_NHI"));
            statement.setString(1, nhi);
            ResultSet resultsBasic = statement.executeQuery();
            medicationDataAccess.select(nhi);

        } catch (SQLException e) {

        }
        return null;
    }

    /**
     * Creates a patient object from a string array of attributes retrieved from the server
     * @return Patient parsed patient
     */
    private Patient parsePatient(ResultSet patientAttributes) throws SQLException {
        String nhi = patientAttributes.getString("Nhi");
        String fName = patientAttributes.getString("FName");
        ArrayList<String> mNames = new ArrayList<>(Arrays.asList(patientAttributes.getString("MName").split(" ")));
        String lName = patientAttributes.getString("LName");
        LocalDate birth = LocalDate.parse(patientAttributes.getString("Birth"));
        Timestamp created = Timestamp.valueOf(patientAttributes.getString("Created"));
        Timestamp modified = Timestamp.valueOf(patientAttributes.getString("Modified"));
        LocalDate death = patientAttributes.getString("Death") != null ?
                LocalDate.parse(patientAttributes.getString("Death")) : null;

        BirthGender gender = patientAttributes.getString("BirthGender").equals("M") ? BirthGender.MALE : BirthGender.FEMALE;
        PreferredGender preferredGender = patientAttributes.getString("PrefGender").equals("F") ? PreferredGender.WOMAN :
                (patientAttributes.getString("PrefGender").equals("M") ? PreferredGender.MAN : PreferredGender.NONBINARY);

        String prefName = attr[10];
        double height = Double.parseDouble(attr[11]) / 100;
        double weight = Double.parseDouble(attr[12]);
        BloodGroup bloodType = attr[13] != null ? BloodGroup.getEnumFromString(attr[13]): null;

        List<Organ> donations = loadOrgans(attr[14]);
        List<Organ> requested = loadOrgans(attr[15]);
        List<PatientActionRecord> records = loadPatientLogs(nhi);

        String[]contactAttr = parsePatientContacts(nhi);
        Region region = contactAttr[3] != null ? Region.getEnumFromString(contactAttr[3]) : null;

        int zip = Integer.parseInt(contactAttr[4]);
        List<Medication>[] meds = loadMedications(nhi);
        List<Medication> currentMeds = meds[0];
        List<Medication> medHistory = meds[1];
        List<Disease>[] diseases = loadDiseases(birth, nhi);
        List<Disease> currentDiseases = diseases[0];
        List<Disease> pastDiseases = diseases[1];
        List<Procedure> procedures = loadProcedures(nhi);

        return new Patient(nhi, fName, mNames, lName, birth, created, modified, death, gender, preferredGender, prefName, height, weight,
                bloodType, donations, requested, contactAttr[0], contactAttr[1], contactAttr[2], region, zip,
                contactAttr[5], contactAttr[6], contactAttr[7], contactAttr[8], contactAttr[9], contactAttr[10],
                contactAttr[11], contactAttr[12], contactAttr[13], contactAttr[14], records, currentDiseases,
                pastDiseases, currentMeds, medHistory, procedures);
    }
}
