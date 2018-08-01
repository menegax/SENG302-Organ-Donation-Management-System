package DataAccess.mysqlDAO;

import DataAccess.factories.MySqlFactory;
import DataAccess.interfaces.*;
import com.sun.org.apache.regexp.internal.RE;
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

public class PatientDAO  implements IPatientDataAccess {

    private IMedicationDataAccess medicationDataAccess;
    private IDiseaseDataAccess diseaseDataAccess;
    private IContactDataAccess contactDataAccess;
    private ILogDataAccess logDataAccess;
    private IProcedureDataAccess procedureDataAccess;
    private MySqlFactory mySqlFactory;

    public PatientDAO() {
        mySqlFactory = MySqlFactory.getMySqlFactory();
        medicationDataAccess = mySqlFactory.getMedicationDataAccess();
        diseaseDataAccess = mySqlFactory.getDiseaseDataAccess();
        contactDataAccess = mySqlFactory.getContactDataAccess();
        logDataAccess = mySqlFactory.getPatientLogDataAccess();
        procedureDataAccess = mySqlFactory.getProcedureDataAccess();
    }

    @Override
    public int savePatients(List<Patient> patients) {
        try (Connection connection = mySqlFactory.getConnectionInstance()) {
            PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("UPDATE_PATIENT_QUERY"));
            for (Patient patient : patients) {
                statement = addUpdateParameters(statement, patient);
                statement.executeUpdate();
                for (Medication medication : patient.getMedicationHistory()) {
                    medicationDataAccess.updateMedication(patient.getNhiNumber(), medication, MedicationStatus.HISTORY);
                }
                for (Medication medication : patient.getCurrentMedications()) {
                    medicationDataAccess.updateMedication(patient.getNhiNumber(), medication, MedicationStatus.CURRENT);
                }
                for (Disease disease : patient.getPastDiseases()) {
                    diseaseDataAccess.updateDisease(patient.getNhiNumber(), disease);
                }
                for (Disease disease : patient.getCurrentDiseases()) {
                    diseaseDataAccess.updateDisease(patient.getNhiNumber(), disease);
                }
                logDataAccess.updateLogs(patient.getUserActionsList(), patient.getNhiNumber());
                contactDataAccess.updateContact(patient);
                connection.commit(); //commit if no errors
            }
        } catch (SQLException ignore) {

        }
        return 0;
    }


    @Override
    public boolean addPatientsBatch(List<Patient> patient) {
        return false;
    }


    @Override
    public List<Patient> getPatients() {
        return null;
    }

    @Override
    public Patient getPatientByNhi(String nhi) {
        try (Connection connection = mySqlFactory.getConnectionInstance()){
            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("SELECT_PATIENT_BY_NHI"));
            statement.setString(1, nhi);
            ResultSet patientAttributes = statement.executeQuery();
            List<PatientActionRecord> patientLogs = logDataAccess.getAllLogsByUserId(nhi);
            List<Disease> diseases = diseaseDataAccess.getDiseaseByNhi(nhi);
            List<Medication> medications = medicationDataAccess.getMedicationsByNhi(nhi);
            List<String> contacts = contactDataAccess.getContactByNhi(nhi);
            List<Procedure> procedures = procedureDataAccess.getProceduresByNhi(nhi);
            if (patientAttributes.next()) {
                return constructPatientObject(patientAttributes, contacts, patientLogs, diseases, procedures, medications);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Patient> searchPatient(String searchTerm) {
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
                List<String> contacts = contactDataAccess.getContactByNhi(resultSet.getString("Nhi"));
                results.add(constructPatientObject(resultSet, contacts));
            }
            return results;
        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    public void deletePatientByNhi(String nhi) {
        try (Connection connection = mySqlFactory.getConnectionInstance()) {
            medicationDataAccess.deleteAllMedicationsByNhi(nhi);
            diseaseDataAccess.deleteAllDiseasesByNhi(nhi);
            contactDataAccess.deleteContactByNhi(nhi);
            logDataAccess.deleteLogsByUserId(nhi);
            procedureDataAccess.deleteAllProceduresByNhi(nhi);
            PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("DELETE_PATIENT_BY_NHI"));
            statement.setString(1,nhi);
            statement.execute();
        }catch (SQLException e) {

        }
    }


    private PreparedStatement addUpdateParameters(PreparedStatement statement, Patient patient) throws SQLException {
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
        return statement;
    }

    private Patient constructPatientObject(ResultSet attributes, List<String> contacts) throws SQLException {
        return constructPatientObject(attributes, contacts, null, null, null, null);
    }

    private Patient constructPatientObject(ResultSet attributes, List<String> contacts, List<PatientActionRecord> logs,
                                           List<Disease> diseases, List<Procedure> procedures, List<Medication> medications) throws SQLException {

        Patient patient = constructMinimalPatientObject(attributes);
        patient.setHeight(Double.parseDouble(attributes.getString("Height")) / 100);
        patient.setHeight(Double.parseDouble(attributes.getString("Weight")));
        patient.setBloodGroup(attributes.getString("BloodType") != null ?
                BloodGroup.getEnumFromString(attributes.getString("BloodType")) : null);
        patient.setDonations(Arrays.stream(attributes.getString("DonatingOrgans").split("\\s*,\\s*"))
                .map(Organ::getEnumFromString).collect(Collectors.toList()));
        //must instantiate if null
        if (patient.getDonations().get(0) == null) {
            patient.setDonations(new ArrayList<>());
        }
        patient.setRequiredOrgans(Arrays.stream(attributes.getString("ReceivingOrgans").split("\\s*,\\s*"))
                .map(Organ::getEnumFromString).collect(Collectors.toList()));
        //must instantiate if null
        if (patient.getRequiredOrgans().get(0) == null) {
            patient.setRequiredOrgans(new ArrayList<>());
        }
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
        patient.setCurrentMedications(currentMedication);
        patient.setMedicationHistory(pastMedication);

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

        //map contact info etc
        patient.setRegion(contacts.get(3) != null ? Region.getEnumFromString(contacts.get(3)) : null);
        patient.setZip(contacts.get(4) == null ? 0 : Integer.parseInt(contacts.get(4)));
        patient.setCurrentDiseases(currentDiseases);
        patient.setPastDiseases(pastDiseases);
        patient.setStreet1(contacts.get(0));
        patient.setStreet2(contacts.get(1));
        patient.setSuburb(contacts.get(2));
        patient.setHomePhone(contacts.get(5));
        patient.setWorkPhone(contacts.get(6));
        patient.setMobilePhone(contacts.get(7));
        patient.setEmailAddress(contacts.get(8));
        patient.setContactName(contacts.get(9));
        patient.setContactRelationship(contacts.get(10));
        patient.setContactHomePhone(contacts.get(11));
        patient.setContactWorkPhone(contacts.get(12));
        patient.setContactMobilePhone(contacts.get(13));
        patient.setContactEmailAddress(contacts.get(14));
        patient.setUserActionsList(logs);
        patient.setProcedures(procedures);
        return patient;
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
