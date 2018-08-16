package data_access.mysqlDAO;

import data_access.factories.MySqlFactory;
import data_access.interfaces.*;
import model.Disease;
import model.Medication;
import model.Patient;
import model.Procedure;
import utility.*;
import org.mockito.internal.matchers.Or;

import utility.GlobalEnums;
import utility.GlobalEnums.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;

import static utility.GlobalEnums.FilterOption.*;
import static utility.SystemLogger.systemLogger;

public class PatientDAO implements IPatientDataAccess {

    private final IMedicationDataAccess medicationDataAccess;
    private final IDiseaseDataAccess diseaseDataAccess;
    private final IContactDataAccess contactDataAccess;
    private final ILogDataAccess<PatientActionRecord> logDataAccess;
    private final IProcedureDataAccess procedureDataAccess;
    private final ITransplantWaitListDataAccess transplantWaitListDataAccess;
    private final MySqlFactory mySqlFactory;
    private final IRequiredOrganDataAccess requiredOrgansDataAccess;

    public PatientDAO() {
        mySqlFactory = MySqlFactory.getMySqlFactory();
        medicationDataAccess = mySqlFactory.getMedicationDataAccess();
        diseaseDataAccess = mySqlFactory.getDiseaseDataAccess();
        contactDataAccess = mySqlFactory.getContactDataAccess();
        logDataAccess = mySqlFactory.getPatientLogDataAccess();
        procedureDataAccess = mySqlFactory.getProcedureDataAccess();
        transplantWaitListDataAccess = mySqlFactory.getTransplantWaitingListDataAccess();
        requiredOrgansDataAccess = mySqlFactory.getRequiredOrgansDataAccess();
    }

    @Override
    public void savePatients(Set<Patient> patients) {
        try (Connection connection = mySqlFactory.getConnectionInstance()) {
            PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("UPDATE_PATIENT_QUERY"));
            for (Patient patient : patients) {
                statement = addUpdateParameters(statement, patient);
                statement.executeUpdate();
                for (Organ organ : patient.getRequiredOrgans().keySet()) {
                    requiredOrgansDataAccess.updateRequiredOrgans(patient.getNhiNumber(), organ, patient.getRequiredOrgans().get(organ));
                }
                List<Medication> fullList = new ArrayList<>();
                fullList.addAll(patient.getCurrentMedications());
                fullList.addAll(patient.getMedicationHistory());
                medicationDataAccess.updateMedication(patient.getNhiNumber(), fullList);

                diseaseDataAccess.deleteAllDiseasesByNhi(patient.getNhiNumber());
                for (Disease disease : patient.getPastDiseases()) {
                    diseaseDataAccess.updateDisease(patient.getNhiNumber(), disease);
                }
                for (Disease disease : patient.getCurrentDiseases()) {
                    diseaseDataAccess.updateDisease(patient.getNhiNumber(), disease);
                }
                logDataAccess.saveLogs(patient.getUserActionsList(), patient.getNhiNumber());
                procedureDataAccess.deleteAllProceduresByNhi(patient.getNhiNumber());
                contactDataAccess.updateContact(patient);
                for (Procedure procedure : patient.getProcedures()) {
                    procedureDataAccess.updateProcedure(patient.getNhiNumber(), procedure);
                }
            }
        } catch (SQLException e) {
            systemLogger.log(Level.SEVERE, "Could not save patients to MySQL database");
        }
    }



    @Override
    public boolean addPatientsBatch(List<Patient> patient) {
        ImportObservable importObservable = ImportObservable.getInstance();
        try (Connection connection = mySqlFactory.getConnectionInstance()) {
            connection.setAutoCommit(false);
            int extendedQueryCount = 0;
            StringBuilder statements = new StringBuilder(ResourceManager.getStringForQuery("BARE_INSERT_PATIENT_BATCH"));
            PreparedStatement preparedStatement;
            String extendedInsert;
            for (Patient aPatient : patient) {
                statements.append(getNextRecordString(aPatient));
                if (extendedQueryCount == 4000) {
                    extendedInsert = statements.toString().substring(0, statements.toString().length() - 1)
                            + " " + ResourceManager.getStringForQuery("ON_DUPLICATE_UPDATE_PATIENT");
                    preparedStatement = connection.prepareStatement(extendedInsert);
                    preparedStatement.execute();
                    importObservable.setCompleted(importObservable.getCompleted() + 2000);
                    extendedQueryCount = 0;
                }
                extendedQueryCount++;
            }
            if (extendedQueryCount != 0) {
                extendedInsert = statements.toString().substring(0, statements.toString().length() - 1)
                        + " " + ResourceManager.getStringForQuery("ON_DUPLICATE_UPDATE_PATIENT");
                connection.prepareStatement(extendedInsert).execute();
            }
            connection.commit();
            contactDataAccess.addContactBatch(patient);
        } catch (Exception e) {
            SystemLogger.systemLogger.log(Level.SEVERE, "Could not import patient batch", this);
        }
        return true;
    }


    @Override
    public Set<Patient> getPatients() {
        return null;
    }

    @Override
    public int getPatientCount() {
        try (Connection connection = mySqlFactory.getConnectionInstance()) {
            PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("GET_PATIENT_COUNT"));
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("count");
            }
            return 0;
        } catch (SQLException e) {
            return 0;
        }
    }

    @Override
    public boolean deletePatient(Patient patient) {
        try (Connection connection = mySqlFactory.getConnectionInstance()) {
            PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("DELETE_PATIENTS"));
            for (int i = 1; i <= 7; i++) {
                statement.setString(i, patient.getNhiNumber());
            }
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public Patient getPatientByNhi(String nhi) {
        try (Connection connection = mySqlFactory.getConnectionInstance()) {
            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("SELECT_PATIENT_BY_NHI"));
            statement.setString(1, nhi);
            ResultSet patientAttributes = statement.executeQuery();
            List<PatientActionRecord> patientLogs = logDataAccess.getAllLogsByUserId(nhi);
            List<Disease> diseases = diseaseDataAccess.getDiseaseByNhi(nhi);
            List<Medication> medications = medicationDataAccess.getMedicationsByNhi(nhi);
            List<String> contacts = contactDataAccess.getContactByNhi(nhi);
            List<Procedure> procedures = procedureDataAccess.getProceduresByNhi(nhi);
            Map<Organ, LocalDate> requiredOrgans = requiredOrgansDataAccess.getRequiredOrganByNhi(nhi);
            if (patientAttributes.next()) {
                return constructPatientObject(patientAttributes, contacts, patientLogs, diseases, procedures, medications, requiredOrgans);
            }
            return null;
        } catch (Exception e) {
            systemLogger.log(Level.SEVERE, "Could not get patient from remote db", this);
        }
        return null;
    }

    @Override
    public Map<Integer, List<Patient>> searchPatients(String searchTerm, Map<FilterOption, String> filters, int numResults) {
        try (Connection connection = mySqlFactory.getConnectionInstance()) {
            Map<Integer, List<Patient>> resultMap = new HashMap<>();
            for (int i = 0; i <= 3; i++) {
                resultMap.put(i, new ArrayList<>());
            }
            connection.setAutoCommit(false);
            PreparedStatement statement;
            if (searchTerm.equals("")) {
                statement = connection.prepareStatement(ResourceManager.getStringForQuery("SELECT_PATIENTS_FILTERED").replaceAll("%FILTER%", getFilterString(filters)));
            } else {
                statement = connection.prepareStatement(ResourceManager.getStringForQuery("SELECT_PATIENTS_SUBSTRING_FILTERED").replaceAll("%FILTER%", getFilterString(filters)));
                for (int i = 1; i <= 4; i++) {
                    statement.setString(i, searchTerm);
                }
            }
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Patient patient = constructPatientObject(resultSet, new ArrayList<String>() {{
                    add(resultSet.getString("Region"));
                }});
                //Add patient to resultMap with appropriate score
                if (searchTerm.equals("")) {
                    resultMap.get(0).add(patient);
                } else {
                    int score = resultSet.getInt("matchNum");
                    resultMap.get(score).add(patient);
                }
            }
            return resultMap;
        } catch (Exception e) {
            systemLogger.log(Level.SEVERE, "Could not search patients from MYSQL DB", this);
        }
        return null;
    }

    @Override
    public void deletePatientByNhi(String nhi) {
        try (Connection connection = mySqlFactory.getConnectionInstance()) {
            medicationDataAccess.deleteAllMedicationsByNhi(nhi);
            diseaseDataAccess.deleteAllDiseasesByNhi(nhi);
            contactDataAccess.deleteContactByNhi(nhi);
            logDataAccess.deleteLogsByUserId(nhi);
            procedureDataAccess.deleteAllProceduresByNhi(nhi);
            transplantWaitListDataAccess.deleteRequestsByNhi(nhi);
            requiredOrgansDataAccess.deleteAllRequiredOrgansByNhi(nhi);
            PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("DELETE_PATIENT_BY_NHI"));
            statement.setString(1, nhi);
            statement.execute();
        } catch (Exception e) {
            systemLogger.log(Level.SEVERE, "Could not delete patient", this);
        }
    }

    @Override
    public List<Patient> getDeadPatients() {
        try(Connection connection = mySqlFactory.getConnectionInstance()) {
            PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("SELECT_DEAD_PATIENTS"));
            List<Patient> patients = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                patients.add(constructPatientObject(resultSet,
                        new ArrayList<String>(){{add(resultSet.getString("Region"));}}));
            }
            return patients;
        }catch (SQLException e) {
            systemLogger.log(Level.SEVERE, "Could not get dead patients from MYSQL db", this);
        }
        return new ArrayList<>();
    }

    /**
     * Builds up a filter string for searching in db
     *
     * @param filters - filters to apply to the patient data set
     * @return - string built from filters
     */
    private String getFilterString(Map<FilterOption, String> filters) {
        if (filters == null) {
            return "1=1";
        }
        StringBuilder query = new StringBuilder();
        for (FilterOption filterOption : filters.keySet()) {
            if (!filters.get(filterOption).equals(GlobalEnums.NONE_ID)) {
                switch (filterOption) {
                    case AGELOWER:
                        query.append(String.format("timestampdiff(YEAR, birth, now()) >= %d AND ", Double.valueOf(filters.get(AGELOWER)).intValue()));
                        break;
                    case AGEUPPER:
                        query.append(String.format("timestampdiff(YEAR, birth, now()) <= %d AND ", Double.valueOf(filters.get(AGEUPPER)).intValue()));
                        break;
                    case BIRTHGENDER:
                        query.append(String.format("birthgender = '%s' AND ", filters.get(BIRTHGENDER).charAt(0)));
                        break;
                    case REGION:
                        query.append(String.format("EXISTS (SELECT * FROM tblPatientContact c WHERE c.Patient=Nhi and c.Region='%s') AND ", filters.get(REGION)));
                        break;
                    case DONOR:
                        if (Boolean.valueOf(filters.get(DONOR))) {
                            query.append("DonatingOrgans <> '' AND ");
                        }
                        break;
                    case RECIEVER:
                        if (Boolean.valueOf(filters.get(RECIEVER))) {
                            query.append("EXISTS (SELECT * FROM tblRequiredOrgans o WHERE o.Patient = P.NHI) AND ");
                        }
                        break;
                    case DONATIONS:
                        query.append(String.format("FIND_IN_SET('%s', DonatingOrgans) > 0 AND ", filters.get(DONATIONS)));
                        break;
                    case REQUESTEDDONATIONS:
                        query.append(String.format("EXISTS (SELECT * FROM tblRequiredOrgans o WHERE o.Organ = '%s' AND o.Patient = P.NHI) AND ", filters.get(REQUESTEDDONATIONS)));
                        break;
                }
            }
        }
        query.append("1=1");
        return query.toString();
    }


    /**
     * Adds params to statement for execution
     *
     * @param statement - statement to add params to
     * @param patient   - patient to strip params off
     * @return - prepared statement
     * @throws SQLException - thrown if there is a type error when setting statement params
     */
    private PreparedStatement addUpdateParameters(PreparedStatement statement, Patient patient) throws SQLException {
        statement.setString(1, patient.getNhiNumber());
        statement.setString(2, patient.getFirstName());
        statement.setString(3, patient.getMiddleNames() != null ? (patient.getMiddleNames().size() == 0 ? "" : String.join(" ", patient.getMiddleNames())) : null);
        statement.setString(4, patient.getLastName());
        statement.setString(5, patient.getBirth().toString());
        statement.setString(6, patient.getCREATED().toString());
        statement.setString(7, patient.getModified().toString());
        statement.setString(8, patient.getDeathDate() == null ? null : patient.getDeathDate().toString());
        statement.setString(9, patient.getDeathCity());
        statement.setString(10, patient.getDeathRegion() == null ? null : patient.getDeathRegion().getValue());
        statement.setString(11, patient.getDeathStreet());
        statement.setString(12, patient.getBirthGender() == null ? null : patient.getBirthGender().toString().substring(0, 1));
        statement.setString(13, patient.getPreferredGender() == null ? null : patient.getPreferredGender().toString().substring(0, 1));
        statement.setString(14, patient.getPreferredName());
        statement.setString(15, String.valueOf(patient.getHeight()));
        statement.setString(16, String.valueOf(patient.getWeight()));
        statement.setString(17, patient.getBloodGroup() == null ? null : patient.getBloodGroup().toString());
        List<String> donationList = patient.getDonations().stream().map(Organ::toString).collect(Collectors.toList());
        String donations = String.join(",", donationList).toLowerCase();
        statement.setString(18, donations);
        return statement;
    }

    private Patient constructPatientObject(ResultSet attributes, List<String> contacts) throws SQLException {
    	Map<GlobalEnums.Organ, LocalDate> required = new HashMap();
    	try {
            if (attributes.getInt("hasRequired") == 1) {
                required.put(Organ.BONE, LocalDate.now());
            }
        } catch (SQLException ignore) {
    	    //pass through
        }

        return constructPatientObject(attributes, contacts, null, null, null, null, required);
    }

    /**
     * Constructs a full patient object
     *
     * @param attributes  - from patient table
     * @param contacts    - from contacts table
     * @param logs        - from logs table
     * @param diseases    - from diseases table
     * @param procedures  - from procedures table
     * @param medications - from medications table
     * @return - full patient object
     * @throws SQLException - thrown if an error occurs when getting values from result set
     */
    private Patient constructPatientObject(ResultSet attributes, List<String> contacts, List<PatientActionRecord> logs,
                                           List<Disease> diseases, List<Procedure> procedures, List<Medication> medications,
                                           Map<Organ, LocalDate> requiredOrgans) throws SQLException {
        String nhi = attributes.getString("Nhi");
        String fName = attributes.getString("FName");
        ArrayList<String> mNames;
        if (attributes.getString("MName") != null) {
            mNames = new ArrayList<>(Arrays.asList(attributes.getString("MName").split(" ")));
        } else {
            mNames = new ArrayList<>();
        }
        ArrayList<String> list = new ArrayList<>();
        for (String mName : mNames) {
            list.add(mName.replace(" ", ""));
        }
        mNames = list;
        String lName = attributes.getString("LName");
        LocalDate birth = LocalDate.parse(attributes.getString("Birth"));
        Timestamp created = Timestamp.valueOf(attributes.getString("Created"));
        Timestamp modified = Timestamp.valueOf(attributes.getString("Modified"));
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.0");
        LocalDateTime death = attributes.getString("Death") != null ? LocalDateTime.parse(attributes.getString("Death"), dtf) : null;
        Region deathRegion = attributes.getString("DeathRegion") == null ? null : Region.getEnumFromString(attributes.getString("DeathRegion"));
        String deathCity = attributes.getString("DeathCity");
        String deathLocation = attributes.getString("DeathStreet");
        String prefName = attributes.getString("PrefName");
        //map enum and organ groups
        BirthGender gender = null;
        if (attributes.getString("BirthGender") != null) {
            gender = attributes.getString("BirthGender").equals("M") ? BirthGender.MALE : BirthGender.FEMALE;
        }
        PreferredGender preferredGender = null;
        if (attributes.getString("PrefGender") != null) {
            preferredGender = attributes.getString("PrefGender").equals("W") ? PreferredGender.WOMAN :
                    (attributes.getString("PrefGender").equals("M") ? PreferredGender.MAN : PreferredGender.NONBINARY);
        }
        Patient patient = new Patient(nhi, fName, mNames, lName, birth, created, modified, death, gender, preferredGender, prefName);
        patient.setDeathStreet(deathLocation);
        patient.setDeathCity(deathCity);
        patient.setDeathRegion(deathRegion);
        patient.setHeight(attributes.getDouble("Height") / 100);
        patient.setWeight(attributes.getDouble("Weight"));
        patient.setBloodGroup(attributes.getString("BloodType") != null ?
                BloodGroup.getEnumFromString(attributes.getString("BloodType")) : null);
        patient.setDonations(attributes.getString("DonatingOrgans") == null ? new ArrayList<>() :
                Arrays.stream(attributes.getString("DonatingOrgans").split("\\s*,\\s*"))
                        .map(Organ::getEnumFromString).collect(Collectors.toList()));
        //must instantiate if null
        if (patient.getDonations().size() > 0 && patient.getDonations().get(0) == null) {
            patient.setDonations(new ArrayList<>());
        }
        if (requiredOrgans == null) {
            patient.setRequiredOrgans(new HashMap<>());
        } else {
            patient.setRequiredOrgans(requiredOrgans);
        }

        //must instantiate if null
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
        if (contacts.size() > 1) {
            patient.setRegion(contacts.get(4) != null ? Region.getEnumFromString(contacts.get(4)) : null);
            patient.setZip(contacts.get(5) == null ? 0 : Integer.parseInt(contacts.get(5)));
            patient.setCurrentDiseases(currentDiseases);
            patient.setPastDiseases(pastDiseases);
            patient.setStreetNumber(contacts.get(0));
            patient.setStreetName(contacts.get(1));
            patient.setCity(contacts.get(3));
            try {
                patient.setSuburb(contacts.get(2));
            } catch (DataFormatException ignored) {

            }
            patient.setHomePhone(contacts.get(6));
            patient.setWorkPhone(contacts.get(7));
            patient.setMobilePhone(contacts.get(8));
            patient.setEmailAddress(contacts.get(9));
            patient.setContactName(contacts.get(10));
            patient.setContactRelationship(contacts.get(11));
            patient.setContactHomePhone(contacts.get(12));
            patient.setContactWorkPhone(contacts.get(13));
            patient.setContactMobilePhone(contacts.get(14));
            patient.setContactEmailAddress(contacts.get(15));
            patient.setUserActionsList(logs == null ? new ArrayList<>() : logs);
            patient.setProcedures(procedures == null ? new ArrayList<>() : procedures);
        } else if (!contacts.isEmpty()) {
            patient.setRegion(contacts.get(0) != null ? Region.getEnumFromString(contacts.get(0)) : null);
        }

        return patient;
    }

    private String getNextRecordString(Patient aPatient) {
        List<String> donationList = aPatient.getDonations().stream().map(Organ::toString).collect(Collectors.toList());
        String donations = String.join(",", donationList).toLowerCase();
        
        return String.format(ResourceManager.getStringForQuery("PATIENT_INSERT_ANOTHER"),
                aPatient.getNhiNumber(),
                aPatient.getFirstName().replaceAll("'", "''"),
                aPatient.getMiddleNames() != null ? (aPatient.getMiddleNames().size() == 0 ? "" : String.format("\'%s\'",
                        String.join(" ", aPatient.getMiddleNames()).replaceAll("'", "''"))) : null,
                aPatient.getLastName().replaceAll("'", "''"),
                aPatient.getBirth().toString(),
                aPatient.getCREATED().toString(),
                aPatient.getModified().toString(),
                aPatient.getDeathDate() == null ? null : String.format("\'%s\'", aPatient.getDeathDate().toString()),
                aPatient.getDeathCity() == null ? null : String.format("\'%s\'", aPatient.getDeathCity()),
                aPatient.getDeathRegion() == null ? null : String.format("\'%s\'",aPatient.getDeathRegion().getValue().replaceAll("'", "")),
                aPatient.getDeathStreet() == null ? null : String.format("\'%s\'", aPatient.getDeathStreet()),
                aPatient.getBirthGender() == null ? null : String.format("\'%s\'",aPatient.getBirthGender().toString().substring(0, 1)),
                aPatient.getPreferredGender() == null ? null : String.format("\'%s\'",aPatient.getPreferredGender().toString().substring(0, 1)),
                aPatient.getPreferredName() == null ? aPatient.getFirstName().replaceAll("'", "''"): aPatient.getPreferredName(),
                String.valueOf(aPatient.getHeight()),
                String.valueOf(aPatient.getWeight()),
                aPatient.getBloodGroup() == null ? null : String.format("\'%s\'", aPatient.getBloodGroup().toString()),
                donations.isEmpty() ? null : String.format("\'%s\'", donations));
    }

}