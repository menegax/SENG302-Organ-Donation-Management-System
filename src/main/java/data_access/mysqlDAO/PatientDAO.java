package data_access.mysqlDAO;

import static utility.GlobalEnums.FilterOption.AGELOWER;
import static utility.GlobalEnums.FilterOption.AGEUPPER;
import static utility.GlobalEnums.FilterOption.BIRTHGENDER;
import static utility.GlobalEnums.FilterOption.DONATIONS;
import static utility.GlobalEnums.FilterOption.DONOR;
import static utility.GlobalEnums.FilterOption.RECIEVER;
import static utility.GlobalEnums.FilterOption.REGION;
import static utility.GlobalEnums.FilterOption.REQUESTEDDONATIONS;
import static utility.SystemLogger.systemLogger;

import data_access.factories.MySqlFactory;
import data_access.interfaces.IContactDataAccess;
import data_access.interfaces.IDiseaseDataAccess;
import data_access.interfaces.IDonationsDataAccess;
import data_access.interfaces.ILogDataAccess;
import data_access.interfaces.IMedicationDataAccess;
import data_access.interfaces.IPatientDataAccess;
import data_access.interfaces.IProcedureDataAccess;
import data_access.interfaces.IRequiredOrganDataAccess;
import data_access.interfaces.ITransplantWaitListDataAccess;
import model.Disease;
import model.Medication;
import model.OrganReceival;
import model.Patient;
import model.Procedure;
import utility.GlobalEnums;
import utility.GlobalEnums.BirthGender;
import utility.GlobalEnums.BloodGroup;
import utility.GlobalEnums.DiseaseState;
import utility.GlobalEnums.FilterOption;
import utility.GlobalEnums.MedicationStatus;
import utility.GlobalEnums.Organ;
import utility.GlobalEnums.PreferredGender;
import utility.GlobalEnums.Region;
import utility.ImportObservable;
import utility.PatientActionRecord;
import utility.ResourceManager;
import utility.SystemLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.zip.DataFormatException;

public class PatientDAO implements IPatientDataAccess {

    private final IMedicationDataAccess medicationDataAccess;

    private final IDiseaseDataAccess diseaseDataAccess;

    private final IContactDataAccess contactDataAccess;

    private final ILogDataAccess<PatientActionRecord> logDataAccess;

    private final IProcedureDataAccess procedureDataAccess;

    private final ITransplantWaitListDataAccess transplantWaitListDataAccess;

    private final MySqlFactory mySqlFactory;

    private final IRequiredOrganDataAccess requiredOrgansDataAccess;

    private final IDonationsDataAccess donatingOrgansDataAccess;


    public PatientDAO() {
        mySqlFactory = MySqlFactory.getMySqlFactory();
        medicationDataAccess = mySqlFactory.getMedicationDataAccess();
        diseaseDataAccess = mySqlFactory.getDiseaseDataAccess();
        contactDataAccess = mySqlFactory.getContactDataAccess();
        logDataAccess = mySqlFactory.getPatientLogDataAccess();
        procedureDataAccess = mySqlFactory.getProcedureDataAccess();
        transplantWaitListDataAccess = mySqlFactory.getTransplantWaitingListDataAccess();
        requiredOrgansDataAccess = mySqlFactory.getRequiredOrgansDataAccess();
        donatingOrgansDataAccess = mySqlFactory.getDonationsOrgansDataAccess();
    }


    @Override
    public void savePatients(Set<Patient> patients) {
        try (Connection connection = mySqlFactory.getConnectionInstance()) {
            PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("UPDATE_PATIENT_QUERY"));
            for (Patient patient : patients) {
                statement = addUpdateParameters(statement, patient);
                statement.executeUpdate();

                for (Organ organ : patient.getRequiredOrgans().keySet()) {
                    requiredOrgansDataAccess.updateRequiredOrgans(patient.getNhiNumber(),
                            organ,
                            patient.getRequiredOrgans()
                                    .get(organ).getRegisteredOn());
                }
                donatingOrgansDataAccess.deleteAllDonatingOrganByNhi(patient.getNhiNumber());
                for (Organ organ : patient.getDonations().keySet()) {
                    donatingOrgansDataAccess.updateDonatingOrgans(patient.getNhiNumber(),
                            organ,
                            patient.getDonations()
                                    .get(organ));
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
        }
        catch (SQLException e) {
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
                    extendedInsert = statements.toString()
                            .substring(0,
                                    statements.toString()
                                            .length() - 1) + " " + ResourceManager.getStringForQuery("ON_DUPLICATE_UPDATE_PATIENT");
                    preparedStatement = connection.prepareStatement(extendedInsert);
                    preparedStatement.execute();
                    importObservable.setCompleted(importObservable.getCompleted() + 2000);
                    extendedQueryCount = 0;
                }
                extendedQueryCount++;
            }
            if (extendedQueryCount != 0) {
                extendedInsert = statements.toString()
                        .substring(0,
                                statements.toString()
                                        .length() - 1) + " " + ResourceManager.getStringForQuery("ON_DUPLICATE_UPDATE_PATIENT");
                connection.prepareStatement(extendedInsert)
                        .execute();
            }
            connection.commit();
            contactDataAccess.addContactBatch(patient);
        }
        catch (Exception e) {
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
        }
        catch (SQLException e) {
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
        }
        catch (SQLException e) {
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
            Map<Organ, OrganReceival> requiredOrgans = requiredOrgansDataAccess.getRequiredOrganByNhi(nhi);
            Map<Organ, String> donatingOrgans = donatingOrgansDataAccess.getDonatingOrgansByDonorNhi(nhi);
            if (patientAttributes.next()) {
                return constructPatientObject(patientAttributes, contacts, patientLogs, diseases, procedures, medications, requiredOrgans, donatingOrgans);
            }
            return null;
        }
        catch (Exception e) {
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
                statement = connection.prepareStatement(ResourceManager.getStringForQuery("SELECT_PATIENTS_FILTERED")
                        .replaceAll("%FILTER%", getFilterString(filters)));
            }
            else {
                statement = connection.prepareStatement(ResourceManager.getStringForQuery("SELECT_PATIENTS_SUBSTRING_FILTERED")
                        .replaceAll("%FILTER%", getFilterString(filters)));
                for (int i = 1; i <= 4; i++) {
                    statement.setString(i, searchTerm);
                }
            }
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Map<Organ, String> donations = donatingOrgansDataAccess.getDonatingOrgansByDonorNhi(resultSet.getString("nhi"));
                Map<Organ, OrganReceival> requiredOrgans = requiredOrgansDataAccess.getRequiredOrganByNhi(resultSet.getString("nhi"));
                Patient patient = mapBasicPatient(resultSet, requiredOrgans, donations);
                //Add patient to resultMap with appropriate score
                if (searchTerm.equals("")) {
                    resultMap.get(0)
                            .add(patient);
                }
                else {
                    int score = resultSet.getInt("matchNum");
                    resultMap.get(score)
                            .add(patient);
                }
            }
            return resultMap;
        }
        catch (Exception e) {
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
        }
        catch (Exception e) {
            systemLogger.log(Level.SEVERE, "Could not delete patient", this);
        }
    }


    @Override
    public List<Patient> getDeadDonors() {
        try (Connection connection = mySqlFactory.getConnectionInstance()) {
            PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("SELECT_DEAD_PATIENTS"));
            List<Patient> patients = new ArrayList<>();
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Map<Organ, String> donations = donatingOrgansDataAccess.getDonatingOrgansByDonorNhi(rs.getString("nhi"));
                Map<Organ, OrganReceival> requiredOrgans = requiredOrgansDataAccess.getRequiredOrganByNhi(rs.getString("nhi"));
                patients.add(mapBasicPatient(rs, requiredOrgans, donations));
            }
            return patients;
        }
        catch (SQLException e) {
            systemLogger.log(Level.SEVERE, "Could not get dead patients from MYSQL db", this);
        }
        return new ArrayList<>();
    }


    /**
     * Helper method to map basic patient
     * @param rs - result set to strip basic contact information off
     * @return - return basic patient object with basic info + current address
     * @throws SQLException - thrown when column does not exist
     */
    private Patient mapBasicPatient(ResultSet rs, Map<Organ, OrganReceival> required, Map<Organ, String> donations) throws SQLException{
        return constructPatientObject(rs,
                new ArrayList<String>(){{
                    add(rs.getString("StreetNumber"));
                    add(rs.getString("StreetName"));
                    add(rs.getString("Suburb"));
                    add(rs.getString("Zip"));
                    add(rs.getString("Region"));
                    add(rs.getString("City"));}}, required, donations);
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
            if (!filters.get(filterOption)
                    .equals(GlobalEnums.NONE_ID)) {
                switch (filterOption) {
                    case AGELOWER:
                        query.append(String.format("timestampdiff(YEAR, birth, now()) >= %d AND ",
                                Double.valueOf(filters.get(AGELOWER))
                                        .intValue()));
                        break;
                    case AGEUPPER:
                        query.append(String.format("timestampdiff(YEAR, birth, now()) <= %d AND ",
                                Double.valueOf(filters.get(AGEUPPER))
                                        .intValue()));
                        break;
                    case BIRTHGENDER:
                        query.append(String.format("birthgender = '%s' AND ",
                                filters.get(BIRTHGENDER)
                                        .charAt(0)));
                        break;
                    case REGION:
                        query.append(String.format("EXISTS (SELECT * FROM tblPatientContact c WHERE c.Patient=Nhi and c.Region='%s') AND ",
                                filters.get(REGION)));
                        break;
                    case DONOR:
                        if (Boolean.valueOf(filters.get(DONOR))) {
                            query.append("EXISTS (SELECT * FROM tblPatientDonations o WHERE o.donor = P.NHI) AND ");
                        }
                        break;
                    case RECIEVER:
                        if (Boolean.valueOf(filters.get(RECIEVER))) {
                            query.append("EXISTS (SELECT * FROM tblRequiredOrgans o WHERE o.Patient = P.NHI) AND ");
                        }
                        break;
                    case DONATIONS:
                        query.append(String.format("EXISTS (SELECT * FROM tblPatientDonations o WHERE o.Organ = '%s' AND o.donor = P.NHI) AND ", filters.get(DONATIONS)));
                        break;
                    case REQUESTEDDONATIONS:
                        query.append(String.format("EXISTS (SELECT * FROM tblRequiredOrgans o WHERE o.Organ = '%s' AND o.Patient = P.NHI) AND ",
                                filters.get(REQUESTEDDONATIONS)));
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
     *
     * @exception SQLException - thrown if there is a type error when setting statement params
     */
    private PreparedStatement addUpdateParameters(PreparedStatement statement, Patient patient) throws SQLException {
        statement.setString(1, patient.getNhiNumber());
        statement.setString(2, patient.getFirstName());
        statement.setString(3,
                patient.getMiddleNames() != null ? (patient.getMiddleNames()
                        .size() == 0 ? "" : String.join(" ", patient.getMiddleNames())) : null);
        statement.setString(4, patient.getLastName());
        statement.setString(5,
                patient.getBirth()
                        .toString());
        statement.setString(6,
                patient.getCREATED()
                        .toString());
        statement.setString(7,
                patient.getModified()
                        .toString());
        statement.setString(8,
                patient.getDeathDate() == null ? null : patient.getDeathDate()
                        .toString());
        statement.setString(9, patient.getDeathCity());
        statement.setString(10,
                patient.getDeathRegion() == null ? null : patient.getDeathRegion()
                        .getValue());
        statement.setString(11, patient.getDeathStreet());
        statement.setString(12,
                patient.getBirthGender() == null ? null : patient.getBirthGender()
                        .toString()
                        .substring(0, 1));
        statement.setString(13,
                patient.getPreferredGender() == null ? null : patient.getPreferredGender()
                        .toString()
                        .substring(0, 1));
        statement.setString(14, patient.getPreferredName());
        statement.setString(15, String.valueOf(patient.getHeight()));
        statement.setString(16, String.valueOf(patient.getWeight()));
        statement.setString(17,
                patient.getBloodGroup() == null ? null : patient.getBloodGroup()
                        .toString());
        return statement;
    }


    private Patient constructPatientObject(ResultSet attributes, List<String> contacts, Map<Organ, OrganReceival> required, Map<Organ, String> donations) throws SQLException {
        try {
            if (attributes.getInt("hasRequired") == 1) {
                OrganReceival organReceival = new OrganReceival(LocalDate.now());
                if (required == null) {
                    required = new HashMap<>();
                }
            }
        }
        catch (SQLException ignore) {
            // pass through
        }

        try {
            if (attributes.getInt("hasDonations") == 1) {
                if (donations == null) {
                    donations = new HashMap<>();
                }
            }
        }
        catch (SQLException ignore) {
            // pass through
        }
        Patient patient = constructPatientObject(attributes, new ArrayList<>(), null, null, null, null, required, donations);
        mapBasicAddress(patient, contacts);
        return patient;
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
     *
     * @exception SQLException - thrown if an error occurs when getting values from result set
     */
    private Patient constructPatientObject(ResultSet attributes, List<String> contacts, List<PatientActionRecord> logs, List<Disease> diseases,
                                           List<Procedure> procedures, List<Medication> medications, Map<Organ, OrganReceival> requiredOrgans, Map<Organ, String> donatingOrgans)
            throws SQLException {
        String nhi = attributes.getString("Nhi");
        String fName = attributes.getString("FName");
        ArrayList<String> mNames;
        if (attributes.getString("MName") != null) {
            mNames = new ArrayList<>(Arrays.asList(attributes.getString("MName")
                    .split(" ")));
        }
        else {
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
            gender = attributes.getString("BirthGender")
                    .equals("M") ? BirthGender.MALE : BirthGender.FEMALE;
        }
        PreferredGender preferredGender = null;
        if (attributes.getString("PrefGender") != null) {
            preferredGender = attributes.getString("PrefGender")
                    .equals("W") ? PreferredGender.WOMAN : (attributes.getString("PrefGender")
                    .equals("M") ? PreferredGender.MAN : PreferredGender.NONBINARY);
        }
        Patient patient = new Patient(nhi, fName, mNames, lName, birth, created, modified, death, gender, preferredGender, prefName);

        patient.setDeathStreet(deathLocation);
        patient.setDeathCity(deathCity);
        patient.setDeathRegion(deathRegion);
        patient.setHeight(attributes.getDouble("Height") / 100);
        patient.setWeight(attributes.getDouble("Weight"));
        patient.setBloodGroup(attributes.getString("BloodType") != null ? BloodGroup.getEnumFromString(attributes.getString("BloodType")) : null);
        patient.setDonations(donatingOrgans);
        //must instantiate if null
        if (patient.getDonations()
                .size() > 0 && patient.getDonations()
                .get(0) == null) {
            patient.setDonations(new HashMap<>());
        }

        if (requiredOrgans == null) {
            patient.setRequiredOrgans(new HashMap<>());
        }
        else {
            patient.setRequiredOrgans(requiredOrgans);
        }
        if (donatingOrgans == null) {
            patient.setDonations(new HashMap<>());
        }
        else {
            patient.setDonations(donatingOrgans);
        }

        //must instantiate if null
        //map medications
        List<Medication> currentMedication = new ArrayList<>();
        List<Medication> pastMedication = new ArrayList<>();
        medications = medications == null ? new ArrayList<>() : medications; //must instantiate if null
        medications.forEach((x) -> {
            if (x.getMedicationStatus()
                    .equals(MedicationStatus.CURRENT)) {
                currentMedication.add(x);
            }
            else {
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
            if (x.getDiseaseState()
                    .equals(DiseaseState.CURED)) {
                currentDiseases.add(x);
            }
            else {
                pastDiseases.add(x);
            }
        });

        //map contact info etc
        if (contacts != null) {
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
        }
        patient.setModified(modified);
        return patient;
    }

    /**
     * Helper method to map basic address info to patient object
     * @param patient - patient obj to map to
     * @param contacts - streetNo, StreetName, Suburb, Zip, Region, City
     */
    private void mapBasicAddress(Patient patient, List<String> contacts) {
        patient.setStreetNumber(contacts.get(0));
        patient.setStreetName(contacts.get(1));
        try {
            patient.setSuburb(contacts.get(2));
        } catch (DataFormatException ignored) {
            //pass through
        }
        patient.setZip(Integer.parseInt(contacts.get(3)));
        patient.setRegion(contacts.get(4) != null ? Region.getEnumFromString(contacts.get(4)) : null);
        patient.setCity(contacts.get(5));
    }
    private String getNextRecordString(Patient aPatient) {
        return String.format(ResourceManager.getStringForQuery("PATIENT_INSERT_ANOTHER"),
                aPatient.getNhiNumber(),
                aPatient.getFirstName()
                        .replaceAll("'", "''"),
                aPatient.getMiddleNames() != null ? (aPatient.getMiddleNames()
                        .size() == 0 ? "" : String.format("\'%s\'",
                        String.join(" ", aPatient.getMiddleNames())
                                .replaceAll("'", "''"))) : null,
                aPatient.getLastName()
                        .replaceAll("'", "''"),
                aPatient.getBirth()
                        .toString(),
                aPatient.getCREATED()
                        .toString(),
                aPatient.getModified()
                        .toString(),
                aPatient.getDeathDate() == null ? null : String.format("\'%s\'",
                        aPatient.getDeathDate()
                                .toString()),
                aPatient.getDeathCity() == null ? null : String.format("\'%s\'", aPatient.getDeathCity()),
                aPatient.getDeathRegion() == null ? null : String.format("\'%s\'",
                        aPatient.getDeathRegion()
                                .getValue()
                                .replaceAll("'", "")),
                aPatient.getDeathStreet() == null ? null : String.format("\'%s\'", aPatient.getDeathStreet()),
                aPatient.getBirthGender() == null ? null : String.format("\'%s\'",
                        aPatient.getBirthGender()
                                .toString()
                                .substring(0, 1)),
                aPatient.getPreferredGender() == null ? null : String.format("\'%s\'",
                        aPatient.getPreferredGender()
                                .toString()
                                .substring(0, 1)),
                aPatient.getPreferredName() == null ? aPatient.getFirstName()
                        .replaceAll("'", "''") : aPatient.getPreferredName(),
                String.valueOf(aPatient.getHeight()),
                String.valueOf(aPatient.getWeight()),
                aPatient.getBloodGroup() == null ? null : String.format("\'%s\'",
                        aPatient.getBloodGroup()
                                .toString()));
    }

}