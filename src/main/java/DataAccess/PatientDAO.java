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

class PatientDAO  implements IPatientDataAccess {

    private IMedicationDataAccess medicationDataAccess;
    private IDiseaseDataAccess diseaseDataAccess;
    private IContactDataAccess contactDataAccess;
    private ILogDataAccess logDataAccess;
    private IProcedureDataAccess procedureDataAccess;
    private MySqlFactory mySqlFactory;

    PatientDAO() {
        mySqlFactory = MySqlFactory.getMySqlFactory();
        medicationDataAccess = mySqlFactory.getMedicationDataAccess();
        diseaseDataAccess = mySqlFactory.getDiseaseDataAccess();
        contactDataAccess = mySqlFactory.getContactDataAccess();
        logDataAccess = mySqlFactory.getPatientLogDataAccess();
        procedureDataAccess = mySqlFactory.getProcedureDataAccess();
    }

    @Override
    public int update(List<Patient> patients) {
        try (Connection connection = mySqlFactory.getConnectionInstance()) {
            PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("UPDATE_PATIENT_QUERY"));
            connection.setAutoCommit(false);
            for (Patient patient : patients) {
                addUpdateParameters(statement, patient);
                statement.executeUpdate();
                for (Medication medication : patient.getMedicationHistory()) {
                    medicationDataAccess.update(patient.getNhiNumber(), medication, MedicationStatus.HISTORY);
                }
                for (Medication medication : patient.getCurrentMedications()) {
                    medicationDataAccess.update(patient.getNhiNumber(), medication, MedicationStatus.CURRENT);
                }
                for (Disease disease : patient.getPastDiseases()) {
                    diseaseDataAccess.update(patient.getNhiNumber(), disease);
                }
                for (Disease disease : patient.getCurrentDiseases()) {
                    diseaseDataAccess.update(patient.getNhiNumber(), disease);
                }
                logDataAccess.update(patient.getUserActionsList(), patient.getNhiNumber());
                contactDataAccess.update(patient);
                connection.commit(); //commit if no errors
            }
        } catch (SQLException e) {

        }
        return 0;
    }

    /**
     * @param patient -
     * @return
     */
    @Override
    public boolean insert(Patient patient) {
        return false;
    }


    /**
     * @param patient -
     * @return
     */
    @Override
    public boolean insert(List<Patient> patient) {
        return false;
    }

    /**
     * @return
     */
    @Override
    public List<Patient> select() {
        return null;
    }


    /**
     * @param nhi -
     * @return -
     */
    @Override
    public Patient selectOne(String nhi) {
        try (Connection connection = mySqlFactory.getConnectionInstance()){
            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("SELECT_PATIENT_BY_NHI"));
            statement.setString(1, nhi);
            ResultSet patientAttributes = statement.executeQuery();
            List<PatientActionRecord> patientLogs = logDataAccess.selectAll(nhi);
            List<Disease> diseases = diseaseDataAccess.select(nhi);
            List<Medication> medications = medicationDataAccess.select(nhi);
            List<String> contacts = contactDataAccess.select(nhi);
            List<Procedure> procedures = procedureDataAccess.select(nhi);
            if (patientAttributes.next()) {
                return constructPatientObject(patientAttributes, contacts, patientLogs, diseases, procedures, medications);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Patient> selectFiltered(String searchTerm) {
        try (Connection connection = mySqlFactory.getConnectionInstance()) {
            List<Patient> results = new ArrayList<>();
            connection.setAutoCommit(false);
            PreparedStatement statement;
            if (searchTerm.equals("")) {
                statement = connection.prepareStatement(ResourceManager.getStringForQuery("SELECT_PATIENTS"));
            } else {
                statement = connection.prepareStatement(ResourceManager.getStringForQuery("SELECT_PATIENTS_FILTERED"));
                for (int i = 1; i <= 5; i++) {
                    statement.setString(i, searchTerm);
                }
            }
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                results.add(constructMinimalPatientObject(resultSet));
            }
            return results;
        } catch (SQLException e) {
            return null;
        }
    }


    /**
     * @param statement -
     * @param patient   -
     * @throws SQLException -
     */
    private void addUpdateParameters(PreparedStatement statement, Patient patient) throws SQLException {
        statement.setString(1, patient.getNhiNumber());
        statement.setString(2, patient.getFirstName());
        statement.setString(3, patient.getMiddleNames() == null ? "" : String.join(" ", patient.getMiddleNames()));
        statement.setString(4, patient.getLastName());
        statement.setString(5, patient.getBirth().toString());
        statement.setString(6, patient.getCREATED().toString());
        statement.setString(7, patient.getModified().toString());
        statement.setString(8, patient.getDeath() == null ? null : patient.getDeath().toString());
        statement.setString(9, patient.getBirthGender() == null ? null : patient.getBirthGender().toString().substring(0, 1));
        statement.setString(10, patient.getPreferredGender() == null ? null : patient.getPreferredGender().toString().substring(0, 1));
        statement.setString(11, patient.getPreferredName());
        statement.setString(12, String.valueOf(patient.getHeight()));
        statement.setString(13, String.valueOf(patient.getWeight()));
        statement.setString(14, patient.getDeath() == null ? null : patient.getDeath().toString());
        statement.setString(15, patient.getBloodGroup() == null ? null : patient.getBloodGroup().toString());
        List<String> donationList = patient.getDonations().stream().map(Organ::toString).collect(Collectors.toList());
        String donations = String.join(",", donationList).toLowerCase();
        statement.setString(16, donations);
        List<String> organsList = patient.getRequiredOrgans().stream().map(Organ::toString).collect(Collectors.toList());
        String organs = String.join(",", organsList).toLowerCase();
        statement.setString(17, organs);
    }

    /**
     * @param attributes
     * @param contacts
     * @param logs
     * @param diseases
     * @param procedures
     * @param medications
     * @return
     * @throws SQLException
     */
    private Patient constructPatientObject(ResultSet attributes, List<String> contacts, List<PatientActionRecord> logs,
                                           List<Disease> diseases, List<Procedure> procedures, List<Medication> medications) throws SQLException {

        String nhi = attributes.getString("Nhi");
        String fName = attributes.getString("FName");
        ArrayList<String> mNames = new ArrayList<>(Arrays.asList(attributes.getString("MName").split(" ")));
        String lName = attributes.getString("LName");
        LocalDate birth = LocalDate.parse(attributes.getString("Birth"));
        Timestamp created = Timestamp.valueOf(attributes.getString("Created"));
        Timestamp modified = Timestamp.valueOf(attributes.getString("Modified"));
        LocalDate death = attributes.getString("Death") != null ? LocalDate.parse(attributes.getString("Death")) : null;
        String prefName = attributes.getString("PrefName");

        // add physical attributes
        double height = Double.parseDouble(attributes.getString("Height")) / 100;
        double weight = Double.parseDouble(attributes.getString("Weight"));

        //map enum and organ groups
        BirthGender gender = null;
        if (attributes.getString("BirthGender") != null) {
            gender = attributes.getString("BirthGender").equals("M") ? BirthGender.MALE : BirthGender.FEMALE;
        }

        PreferredGender preferredGender = null;
        if (attributes.getString("PrefGender") != null) {
            preferredGender = attributes.getString("PrefGender").equals("F") ? PreferredGender.WOMAN :
                    (attributes.getString("PrefGender").equals("M") ? PreferredGender.MAN : PreferredGender.NONBINARY);
        }

        BloodGroup bloodType = attributes.getString("BloodType") != null ?
                BloodGroup.getEnumFromString(attributes.getString("BloodType")) : null;
        List<Organ> donations = Arrays.stream(attributes.getString("DonatingOrgans")
                .split("\\s*,\\s*")).map(Organ::getEnumFromString).collect(Collectors.toList());
        List<Organ> requested = Arrays.stream(attributes.getString("ReceivingOrgans")
                .split("\\s*,\\s*")).map(Organ::getEnumFromString).collect(Collectors.toList());
        Region region = contacts.get(3) != null ? Region.getEnumFromString(contacts.get(3)) : null;
        int zip = contacts.get(4) == null ? 0 : Integer.parseInt(contacts.get(4));

        //map medications
        List<Medication> currentMedication = new ArrayList<>();
        List<Medication> pastMedication = new ArrayList<>();
        medications = medications == null ? new ArrayList<>() : medications; //must instantiate if null
        medications.forEach((x) -> {
            if (x.getMedicationStatus().equals(MedicationStatus.CURRENT)) {
                currentMedication.add(x);
            } else {
                pastMedication.add(x);
            }
        });

        //map diseases
        List<Disease> currentDiseases = new ArrayList<>();
        List<Disease> pastDiseases = new ArrayList<>();
        diseases = diseases == null ? new ArrayList<>() : diseases; //must instantiate if null
        diseases.forEach((x) -> {
            if (x.getDiseaseState().equals(DiseaseState.CURED)) {
                currentDiseases.add(x);
            } else {
                pastDiseases.add(x);
            }
        });
        donations = donations.get(0) == null ? new ArrayList<>() : donations; //must instantiate if null
        requested = requested.get(0) == null ? new ArrayList<>() : requested; //must instantiate if null
        return new Patient(nhi, fName, mNames, lName, birth, created, modified, death, gender, preferredGender, prefName, height, weight,
                bloodType, donations, requested, contacts.get(0), contacts.get(1), contacts.get(2), region, zip,
                contacts.get(5), contacts.get(6), contacts.get(7), contacts.get(8), contacts.get(9), contacts.get(10),
                contacts.get(11), contacts.get(12), contacts.get(13), contacts.get(14), logs, currentDiseases,
                pastDiseases, currentMedication, pastMedication, procedures);


    }

    private Patient constructMinimalPatientObject(ResultSet attributes) throws SQLException {
        String nhi = attributes.getString("Nhi");
        String fName = attributes.getString("FName");
        ArrayList<String> mNames = new ArrayList<>(Arrays.asList(attributes.getString("MName").split(" ")));
        String lName = attributes.getString("LName");
        LocalDate birth = LocalDate.parse(attributes.getString("Birth"));
        Timestamp created = Timestamp.valueOf(attributes.getString("Created"));
        Timestamp modified = Timestamp.valueOf(attributes.getString("Modified"));
        LocalDate death = attributes.getString("Death") != null ? LocalDate.parse(attributes.getString("Death")) : null;
        String prefName = attributes.getString("PrefName");

        //map enum and organ groups
        BirthGender gender = null;
        if (attributes.getString("BirthGender") != null) {
            gender = attributes.getString("BirthGender").equals("M") ? BirthGender.MALE : BirthGender.FEMALE;
        }

        PreferredGender preferredGender = null;
        if (attributes.getString("PrefGender") != null) {
            preferredGender = attributes.getString("PrefGender").equals("F") ? PreferredGender.WOMAN :
                    (attributes.getString("PrefGender").equals("M") ? PreferredGender.MAN : PreferredGender.NONBINARY);
        }
        return new Patient(nhi, fName, mNames, lName, birth, created, modified, death, gender, preferredGender, prefName);
    }
}
