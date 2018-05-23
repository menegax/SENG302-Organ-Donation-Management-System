package service;

import com.google.gson.Gson;
import model.Clinician;
import model.Disease;
import model.Medication;
import model.Patient;
import utility.FormatterLog;
import utility.GlobalEnums;
import utility.GlobalEnums.Region;
import utility.SearchPatients;
import utility.UserActionRecord;

import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Pattern;


import static utility.UserActionHistory.userActions;

public class Database {

    private Set<Patient> patients;

    private Set<Clinician> clinicians;

    private OrganWaitlist organWaitingList;

    private static Database database = null;

    private Connection conn;

    private Database() {
        patients = new HashSet<>();
        clinicians = new HashSet<>();
        organWaitingList = new OrganWaitlist();
        initializeConnection();
    }

    public static Database getDatabase() {
        if (database == null) {
            database = new Database();
        }
        return database;
    }


    //TODO change to real database before submittion
    private void initializeConnection() {
        try {
        	//TODO Uncomment for final product
            //conn = DriverManager.getConnection("jdbc:mysql://mysql2.csse.canterbury.ac.nz:3306/seng302-2018-team800-test",
            //        "seng302-team800", "ScornsGammas5531");
        	
        	//TODO Uncomment for outside Patricks network
            conn = DriverManager.getConnection("jdbc:mysql://122.62.50.128:3306/seng302-2018-team800-test",
                    "seng302-team800", "ScornsGammas5531");
        	
        	//TODO Uncomment for inside Patricks network
//            conn = DriverManager.getConnection("jdbc:mysql://192.168.1.70:3306/seng302-2018-team800-test",
//                    "seng302-team800", "ScornsGammas5531");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //ToDO Complete
    public ArrayList<String[]> runQuery(String query, String[] params) throws SQLException {
//        try {
            String type = query.split(" ")[0].toUpperCase();
            PreparedStatement stmt = setupQuery(query, params);
            if (type.equals("SELECT")) {
                return runSelectQuery(stmt);
            }
            else if (type.equals("UPDATE") || type.equals("INSERT")) {
                runUpdateQuery(stmt);
            }
//        }
//        catch (SQLException e) {
//            e.printStackTrace();
//        }
        return null;
    }

    //TODO Complete
    private ArrayList<String[]> runSelectQuery(PreparedStatement query) throws SQLException {
        ResultSet resultSet = query.executeQuery();
        ArrayList<String[]> results = new ArrayList<>();
        int columns = resultSet.getMetaData().getColumnCount();
        int count = 0;
        while (resultSet.next()) {
            String[] result = new String[columns];
            while (count < columns) {
                result[count] = resultSet.getString(count + 1);
                count += 1;
            }
            count = 0;
            results.add(result);
        }
        return results;
    }

    //TODO Complete
    public void add(Object object) {
        if (object instanceof Patient) {
            addPatient((Patient) object);
        }
        else if (object instanceof Clinician) {
        	addClinician((Clinician) object);
        }
    }

    private PreparedStatement setupQuery(String query, String[] params) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(query);
        int count = 0;
        while (count < params.length) {
            stmt.setString(count + 1, params[count]);
            count += 1;
        }
        return stmt;
    }

    private void runUpdateQuery(PreparedStatement stmt) {
        try {
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage() + "\n" + e.getSQLState() + "\n" + e.getErrorCode());
        }
    }

    public OrganWaitlist getWaitingList() {
        return organWaitingList;
    }

    /**
     * Gets patient's attributes and stores them in a String array
     * @param patient Patient to get attributes from
     * @return String[] attributes of patient
     */
    private String[] getPatientAttributes(Patient patient) {
        String[] attr = new String[16];
        attr[0] = patient.getNhiNumber();
        attr[1] = patient.getFirstName();
        attr[2] = String.join(" ", patient.getMiddleNames());
        attr[3] = patient.getLastName();
        attr[4] = patient.getBirth().toString();
        attr[5] = patient.getCREATED().toString();
        attr[6] = patient.getModified().toString();
        if(patient.getDeath() != null) {
            attr[7] = patient.getDeath().toString();
        }
        if(patient.getGender() != null) {
            attr[8] = patient.getGender().toString().substring(0,1);
        }
        attr[9] = null;
        attr[10] = null;
        attr[11] = String.valueOf(patient.getHeight() * 100);
        attr[12] = String.valueOf(patient.getWeight());
        attr[13] = patient.getBloodGroup().toString();
        attr[14] = String.join(",", patient.getDonations().toString())
                .replaceAll("\\[","").replaceAll("\\]", "");
        attr[15] = String.join(",", patient.getRequiredOrgans().toString())
                .replaceAll("\\[","").replaceAll("\\]", "");
        return attr;
    }

    /**
     * Gets a patient's contact details and stores them in a String array
     * @param patient Patient to get attributes from
     * @return String[] contact attributes
     */
    private String[] getPatientContactAttributes(Patient patient) {
        String[] contactAttr = new String[16];
        contactAttr[0] = patient.getNhiNumber();
        contactAttr[1] = patient.getStreet1();
        contactAttr[2] = patient.getStreet2();
        contactAttr[3] = patient.getSuburb();
        contactAttr[4] = patient.getRegion().toString();
        contactAttr[5] = String.valueOf(patient.getZip());
        contactAttr[6] = patient.getHomePhone();
        contactAttr[7] = patient.getWorkPhone();
        contactAttr[8] = patient.getMobilePhone();
        contactAttr[9] = patient.getEmailAddress();
        contactAttr[10] = patient.getContactName();
        contactAttr[11] = patient.getContactRelationship();
        contactAttr[12] = patient.getContactHomePhone();
        contactAttr[13] = patient.getContactWorkPhone();
        contactAttr[14] = patient.getContactMobilePhone();
        contactAttr[15] = patient.getContactEmailAddress();
        return contactAttr;
    }

    /**
     * Gets a clinician's attributes and stores them in a String array
     * @param clinician Clinician to get attributes from
     * @return String[] clinician attributes
     */
    private String[] getClinicianAttributes(Clinician clinician) {
    	String[] clinicianAttr = new String[9];
    	clinicianAttr[0] = String.valueOf(clinician.getStaffID());
    	clinicianAttr[1] = clinician.getFirstName();
    	clinicianAttr[2] = String.join(" ", clinician.getMiddleNames());
    	clinicianAttr[3] = clinician.getLastName();
    	clinicianAttr[4] = clinician.getStreet1();
    	clinicianAttr[5] = clinician.getStreet2();
    	clinicianAttr[6] = clinician.getSuburb();
    	clinicianAttr[7] = clinician.getRegion().toString();
    	clinicianAttr[8] = clinician.getModified().toString();
    	return clinicianAttr;
    }
    
    /**
     * Gets all attributes for a medication object
     * @param patient Patient who is taking or used to take the medication
     * @param medication The medication used by the patient
     * @return String[] medication attributes
     */
    private String[] getMedicationAttributes(Patient patient, Medication medication, boolean isCurrent) {
        String[] medAttr = new String[3];
        medAttr[0] = patient.getNhiNumber();
        medAttr[1] = medication.getMedicationName();
        if(isCurrent) {
            medAttr[2] = "0";
        } else {
            medAttr[2] = "1";
        }
        return medAttr;
    }

    /**
     * Gets all attributes for a disease object
     * @param patient Patient with disease
     * @param disease Disease to get attributes from
     * @return String[] disease attributes
     */
    private String[] getDiseaseAttributes(Patient patient, Disease disease) {
        String[] diseaseAttr = new String[4];
        diseaseAttr[0] = patient.getNhiNumber();
        diseaseAttr[1] = disease.getDiseaseName();
        diseaseAttr[2] = disease.getDateDiagnosed().toString();
        switch (disease.getDiseaseState().toString()) {
            case "chronic": diseaseAttr[3] = "1"; break;
            case "cured": diseaseAttr[3] = "2"; break;
            default: diseaseAttr[3] = "0";
        }
        return diseaseAttr;
    }

    private String[] getLogAttributes(Patient patient, UserActionRecord record) {
        String[] recordAttr = new String[5];
        recordAttr[0] = patient.getNhiNumber();
        recordAttr[1] = record.getTimestamp().toString();
        recordAttr[2] = record.getLevel().toString();
        recordAttr[3] = record.getMessage();
        recordAttr[4] = record.getAction();
        return recordAttr;
    }

    private String[] getTransplantRequestAttributes(OrganWaitlist.OrganRequest request) {
        String[] attr = new String[4];
        attr[0] = request.getReceiverNhi();
        attr[1] = request.getRequestDate().toString();
        attr[2] = request.getRequestedOrgan().toString();
        attr[3] = request.getRequestRegion().toString();
        return attr;
    }

    private void addPatientMedications(Patient newPatient) throws SQLException {
        for(Medication medication : newPatient.getCurrentMedications()) {
            String[] medAttr = getMedicationAttributes(newPatient, medication, true);
            String medQuery = "INSERT INTO tblMedications VALUES (?, ?, ?)";
            runQuery(medQuery, medAttr);

        }

        for(Medication medication : newPatient.getMedicationHistory()) {
            String[] medAttr = getMedicationAttributes(newPatient, medication, false);
            String medQuery = "INSERT INTO tblMedications VALUES (?, ?, ?)";
            runQuery(medQuery, medAttr);
        }
    }

    private void addPatientDiseases(Patient newPatient) throws SQLException {
        ArrayList<Disease> allDiseases = newPatient.getCurrentDiseases();
        allDiseases.addAll(newPatient.getPastDiseases());
        for(Disease disease : allDiseases) {
            String[] diseaseAttr = getDiseaseAttributes(newPatient, disease);
            String diseaseQuery = "INSERT INTO tblDiseases VALUES (?, ?, ?, ?)";
            runQuery(diseaseQuery, diseaseAttr);

        }
    }

    private void addPatientLogs(Patient patient) throws SQLException {
        ArrayList<UserActionRecord> records = patient.getUserActionsList();
        for (UserActionRecord record : records) {
            String[] recordAttr = getLogAttributes(patient, record);
            String recordQuery = "INSERT INTO tblPatientLogs VALUES (?, ?, ?, ?, ?)";
            runQuery(recordQuery, recordAttr);
        }
    }

    /**
     * Adds a patient to the database
     *
     * @param newPatient the new patient to add
     */
    private void addPatient(Patient newPatient) throws IllegalArgumentException {
        try {
            newPatient.ensureValidNhi();
            newPatient.ensureUniqueNhi();
            patients.add(newPatient);
            SearchPatients.addIndex(newPatient);

            String[] attr = getPatientAttributes(newPatient);
            String query = "INSERT INTO tblPatients " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            runQuery(query, attr);

            String[] contactAttr = getPatientContactAttributes(newPatient);
            String contactQuery = "INSERT INTO tblPatientContact " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            runQuery(contactQuery, contactAttr);

            addPatientMedications(newPatient);

            addPatientDiseases(newPatient);

            addPatientLogs(newPatient);

            userActions.log(Level.INFO, "Successfully added patient " + newPatient.getNhiNumber(), "Attempted to add a patient");
        }
        catch (SQLException o) {
            userActions.log(Level.WARNING, "Failed to add patient " + newPatient.getNhiNumber(), "Attempted to add a patient");
            throw new IllegalArgumentException(o.getMessage());
        }
    }

    /**
     * Adds a clinician to the database
     *
     * @param newClinician the new clinician to add
     */
    private void addClinician(Clinician newClinician) throws IllegalArgumentException {
        if (!Pattern.matches("^[-a-zA-Z]+$", newClinician.getFirstName())) {
            userActions.log(Level.WARNING, "Couldn't add clinician due to invalid field: first name", "Attempted to add a clinician");
            throw new IllegalArgumentException("firstname");
        }

        if (!Pattern.matches("^[-a-zA-Z]+$", newClinician.getLastName())) {
            userActions.log(Level.WARNING, "Couldn't add clinician due to invalid field: last name", "Attempted to add a clinician");
            throw new IllegalArgumentException("lastname");
        }

        if (newClinician.getStreet1() != null && !Pattern.matches("^[- a-zA-Z0-9]+$", newClinician.getStreet1())) {
            userActions.log(Level.WARNING, "Couldn't add clinician due to invalid field: street1", "Attempted to add a clinician");
            throw new IllegalArgumentException("street1");
        }

        if (newClinician.getStaffID() == getNextStaffID()) {
            clinicians.add(newClinician);
            
            String[] attr = getClinicianAttributes(newClinician);
            String query = "INSERT INTO tblClinicians " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
			try {
				runQuery(query, attr);
				userActions.log(Level.INFO, "Successfully added clinician " + newClinician.getStaffID(), "Attempted to add a clinician");
			} catch (SQLException e) {
	            userActions.log(Level.WARNING, "Couldn't add clinician due to database error", "Attempted to add a clinician");
			}   
        }

        else {
            userActions.log(Level.WARNING, "Couldn't add clinician due to invalid field staffID", "Attempted to add a clinician");
            throw new IllegalArgumentException("staffID");
        }
    }

    public void saveTransplantRequest(OrganWaitlist.OrganRequest request) {
        String[] attr = getTransplantRequestAttributes(request);
        String query = "INSERT INTO tblTransplantWaitList " +
                "VALUES (?, ?, ?, ?)";
        try {
            runQuery(query, attr);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadAll() {
        loadAllPatients();
        loadAllClinicians();
        loadTransplantWaitingList();
    }

    private ArrayList<GlobalEnums.Organ> loadOrgans(String organs) {
        String[] organArray = organs.split(",");
        ArrayList<GlobalEnums.Organ> organArrayList = new ArrayList<>();
        for(String organ : organArray) {
            organArrayList.add(GlobalEnums.Organ.valueOf(organ));
        }
        return organArrayList;
    }

    private ArrayList<UserActionRecord> loadPatientLogs(String nhi) {
        ArrayList<UserActionRecord> patientLogs = new ArrayList<>();
        try {
            String[] nhiArray = {nhi};
            ArrayList<String[]> logsRaw = runQuery("SELECT * FROM tblPatientLogs WHERE Patient = ?", nhiArray);
            for (String[] attr : logsRaw) {
                patientLogs.add(parseLog(attr));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patientLogs;
    }

    private UserActionRecord parseLog(String[] attr) {
        Timestamp timestamp = Timestamp.valueOf(attr[1]);
        Level level = Level.parse(attr[2]);
        String message = attr[3];
        String action = attr[4];
        return new UserActionRecord(timestamp, level, message, action);
    }

    private String[] parsePatientContacts(String nhi) throws SQLException {
        String[] contactsRaw = runQuery("SELECT * FROM tblPatientContact WHERE Patient = " + nhi, null).get(0);
        return Arrays.copyOfRange(contactsRaw, 1, contactsRaw.length);
    }

    private Patient parsePatient(String[] attr) {
        String nhi = attr[0];
        String fName = attr[1];
        ArrayList<String> mNames = new ArrayList<>();
        mNames.addAll(Arrays.asList(attr[2].split(" ")));
        String lName = attr[3];
        LocalDate birth = LocalDate.parse(attr[4]);
        Timestamp created = Timestamp.valueOf(attr[5]);
        Timestamp modified = Timestamp.valueOf(attr[6]);
        LocalDate death = LocalDate.parse(attr[7]);
        GlobalEnums.Gender gender;
        switch (attr[8]) {
            case "M": gender = GlobalEnums.Gender.MALE;break;
            case "F": gender = GlobalEnums.Gender.FEMALE;break;
            default: gender = GlobalEnums.Gender.OTHER;break;
        }
        //todo: set pref gender and name here after story 29 is in
        double height = Double.parseDouble(attr[11]) / 100;
        double weight = Double.parseDouble(attr[12]);
        GlobalEnums.BloodGroup bloodType = GlobalEnums.BloodGroup.valueOf(attr[13]);
        ArrayList<GlobalEnums.Organ> donations = loadOrgans(attr[14]);
        ArrayList<GlobalEnums.Organ> requested = loadOrgans(attr[15]);

        ArrayList<UserActionRecord> records = loadPatientLogs(nhi);

        ArrayList<Disease> currentDiseases = new ArrayList<>();
        ArrayList<Disease> pastDiseases = new ArrayList<>();
        ArrayList<Medication> currentMeds = new ArrayList<>();
        ArrayList<Medication> medHistory = new ArrayList<>();
        GlobalEnums.Region region = null;
        int zip = 0;
        String[] contactAttr = new String[15];
        try {
            //TODO rewrite method to do medication history and current assigning within load method
            contactAttr = parsePatientContacts(nhi);
            //3 and 4 need parsing
            region = GlobalEnums.Region.valueOf(contactAttr[3]);
            zip = Integer.parseInt(contactAttr[4]);
            ArrayList<Medication>[] meds = loadMedications(nhi);
            currentMeds = meds[0];
            medHistory = meds[1];
            ArrayList<Disease>[] diseases = loadDiseases(birth, nhi);
            currentDiseases = diseases[0];
            pastDiseases = diseases[1];

        } catch (InvalidObjectException | SQLException e) {
            e.printStackTrace();
        }

        return new Patient(nhi, fName, mNames, lName, birth, created, modified, death, gender, height, weight,
                bloodType, donations, requested, contactAttr[0], contactAttr[1], contactAttr[2], region, zip,
                contactAttr[5], contactAttr[6], contactAttr[7], contactAttr[8], contactAttr[9], contactAttr[10],
                contactAttr[11], contactAttr[12], contactAttr[13], contactAttr[14], null, currentDiseases,
                pastDiseases, currentMeds, medHistory);
    }

    private ArrayList<Medication>[] loadMedications(String nhi) throws SQLException {
        ArrayList<String[]> medicationsRaw = runQuery("SELECT * FROM tblMedications WHERE Patient = " + nhi, null);
        ArrayList<Medication> currentMedications = new ArrayList<>();
        ArrayList<Medication> medicationHistory = new ArrayList<>();
        ArrayList<Medication>[] medArray = new ArrayList[2];
        medArray[0] = currentMedications;
        medArray[1] = medicationHistory;
        for(String[] attr : medicationsRaw) {
            medArray = addMedication(attr, medArray);
        }
        return medArray;
    }

    private ArrayList<Disease>[] loadDiseases(LocalDate birth, String nhi) throws InvalidObjectException, SQLException {
        ArrayList<String[]> diseasesRaw = runQuery("SELECT * FROM tblDiseases WHERE Patient = " + nhi, null);
        ArrayList<Disease> patientDiseases = new ArrayList<>();
        for(String[] attr : diseasesRaw) {
            patientDiseases.add(addDisease(attr, birth));
        }
        ArrayList<Disease>[] diseaseArray = new ArrayList[2];
        ArrayList<Disease> pastDiseases = new ArrayList<>();
        ArrayList<Disease> currentDiseases = new ArrayList<>();
        for(Disease disease : patientDiseases) {
            if(disease.getDiseaseState() == GlobalEnums.DiseaseState.CURED) {
                pastDiseases.add(disease);
            } else {
                currentDiseases.add(disease);
            }
        }
        diseaseArray[0] = currentDiseases;
        diseaseArray[1] = pastDiseases;
        return diseaseArray;
    }


    private ArrayList<Medication>[] addMedication(String[] attr, ArrayList<Medication>[] meds) {
        switch (attr[2]) {
            case "0": meds[0].add(new Medication(attr[1])); break;
            case "1": meds[1].add(new Medication(attr[1])); break;
        }
        return meds;
    }

    private Disease addDisease(String[] attr, LocalDate birthDate) throws InvalidObjectException {
        Disease disease = new Disease(null, null);
        disease.setDiseaseName(attr[1]);
        disease.setDateDiagnosed(LocalDate.parse(attr[2]), birthDate);
        switch (attr[3]) {
            case "0":
                disease.setDiseaseState(null);
                break;
            case "1":
                disease.setDiseaseState(GlobalEnums.DiseaseState.CHRONIC);
                break;
            case "2":
                disease.setDiseaseState(GlobalEnums.DiseaseState.CURED);
                break;
        }
        return disease;
    }


    /**
     * Creates a clinician object from a String array of its attributes.
     * @param attr String array of the clinicians attributes.
     * @return The clinician object.
     */
	private Clinician parseClinician(String[] attr) {
		int staffID = Integer.parseInt(attr[0]);
	    String fName = attr[1];
	    ArrayList<String> mNames = new ArrayList<>();
	    mNames.addAll(Arrays.asList(attr[2].split(" ")));
	    String lName = attr[3];
	    String street1 = attr[4];
	    String street2 = attr[5];
	    String suburb = attr[6];
	    Region region = GlobalEnums.Region.valueOf(attr[7]);
	    Timestamp modified = Timestamp.valueOf(attr[8]);
	    
	    Clinician newClinician = new Clinician(staffID, fName, mNames, lName, street1, street2, suburb, region);
	    newClinician.setModified(modified);
		return newClinician;
	}

	/**
	 * Loads all patients into the application from the remote database.
	 */
	private void loadAllPatients() {
	    try {
            ArrayList<String[]> patientsRaw = runQuery("SELECT * FROM tblPatients", null);
            for (String[] attr : patientsRaw) {
                patients.add(parsePatient(attr));
            }
        } catch (SQLException e) {
	        e.printStackTrace();
        }
	}
	
	/**
	 * Loads all clinicians into the application from the remote database.
	 */
	private void loadAllClinicians() {
	    try {
            ArrayList<String[]> clinicianRaw = runQuery("SELECT * FROM tblClincians", null);
            for (String[] attr : clinicianRaw) {
                clinicians.add(parseClinician(attr));
            }
        } catch(SQLException e) {
	        e.printStackTrace();
        }
	}

    private void loadTransplantWaitingList() {
        try {
            ArrayList<String[]> waitlistRaw = runQuery("SELECT * FROM tblTransplantWaitList", null);
            for (String[] attr : waitlistRaw) {
                String nhi = attr[0];
                LocalDate date = LocalDate.parse(attr[1]);
                GlobalEnums.Organ organ = GlobalEnums.Organ.valueOf(attr[2]);
                GlobalEnums.Region region = GlobalEnums.Region.valueOf(attr[3]);
                String name = getPatientByNhi(nhi).getNameConcatenated();
                organWaitingList.add(name, organ, date, region, nhi);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InvalidObjectException ex) {
            ex.printStackTrace();
        }
    }

////TODO Local version remove?????
//    /**
//     * Adds a patient to the database
//     *
//     * @param newPatient the new patient to add
//     */
//    public void addPatient(Patient newPatient) throws IllegalArgumentException {
//        try {
//            newPatient.ensureValidNhi();
//            newPatient.ensureUniqueNhi();
//            patients.add(newPatient);
//            SearchPatients.addIndex(newPatient);
//            userActions.log(Level.INFO, "Successfully added patient " + newPatient.getNhiNumber(), "Attempted to add a patient");
//        }
//        catch (IllegalArgumentException o) {
//            userActions.log(Level.WARNING, "Failed to add patient " + newPatient.getNhiNumber(), "Attempted to add a patient");
//            throw new IllegalArgumentException(o.getMessage());
//        }
//    }
//
//    /**
//     * Adds a clinician to the database
//     *
//     * @param newClinician the new clinician to add
//     */
//    private void addClinician(Clinician newClinician) throws IllegalArgumentException {
//        if (!Pattern.matches("^[-a-zA-Z]+$", newClinician.getFirstName())) {
//            userActions.log(Level.WARNING, "Couldn't add clinician due to invalid field: first name", "Attempted to add a clinician");
//            throw new IllegalArgumentException("firstname");
//        }
//
//        if (!Pattern.matches("^[-a-zA-Z]+$", newClinician.getLastName())) {
//            userActions.log(Level.WARNING, "Couldn't add clinician due to invalid field: last name", "Attempted to add a clinician");
//            throw new IllegalArgumentException("lastname");
//        }
//
//        if (newClinician.getStreet1() != null && !Pattern.matches("^[- a-zA-Z0-9]+$", newClinician.getStreet1())) {
//            userActions.log(Level.WARNING, "Couldn't add clinician due to invalid field: street1", "Attempted to add a clinician");
//            throw new IllegalArgumentException("street1");
//        }
//
//        if (newClinician.getStaffID() == getNextStaffID()) {
//            clinicians.add(newClinician);
//            userActions.log(Level.INFO, "Successfully added clinician " + newClinician.getStaffID(), "Attempted to add a clinician");
//        }
//
//        else {
//            userActions.log(Level.WARNING, "Couldn't add clinician due to invalid field staffID", "Attempted to add a clinician");
//            throw new IllegalArgumentException("staffID");
//        }
//    }

    /**
     * Removes a patient from the database
     *
     * @param nhi the nhi to search patients by
     * @exception InvalidObjectException when the object cannot be found
     */
    public void removePatient(String nhi) throws InvalidObjectException {
        patients.remove(getPatientByNhi(nhi));
        userActions.log(Level.INFO, "Successfully removed patient " + nhi, "attempted to remove a patient");
    }


    /**
     * Searches patients by nhi
     *
     * @param nhi the nhi to search patients by
     * @return Patient object
     *
     * @exception InvalidObjectException when the object cannot be found
     */
    public Patient getPatientByNhi(String nhi) throws InvalidObjectException {
        for (Patient p : getPatients()) {
            if (p.getNhiNumber()
                    .equals(nhi.toUpperCase())) {
                return p;
            }
        }
        throw new InvalidObjectException("Patient with NHI number " + nhi + " does not exist.");
    }


    /**
     * Checks if a patient with the given nhi exists in the database
     *
     * @param nhi the nhi of the patient to search
     * @return true if exists else false
     */
    public boolean isPatientInDb(String nhi) {
        for (Patient d : getPatients()) {
            if (d.getNhiNumber()
                    .equals(nhi.toUpperCase())) {
                return true;
            }
        }
        return false;
    }


    /**
     * Checks if a clinician with the given staffID exists in the database
     *
     * @param staffID the staffID of the clinician to search for
     * @return true if exists else false
     */
    public boolean isClinicianInDb(int staffID) {
        for (Clinician c : getClinicians()) {
            if (c.getStaffID() == staffID) {
                return true;
            }
        }
        return false;
    }


    /**
     * Searches clinicians by staffID
     *
     * @param staffID the staff ID to search clinicians by
     * @return Clinician object
     *
     * @exception InvalidObjectException when the object cannot be found
     */
    public Clinician getClinicianByID(int staffID) throws InvalidObjectException {
        for (Clinician c : getClinicians()) {
            if (c.getStaffID() == staffID) {
                return c;
            }
        }
        throw new InvalidObjectException("Clinician with staff ID number " + staffID + " does not exist.");
    }


    /**
     * Returns the next valid staffID based on IDs in the clinician list
     *
     * @return the valid id
     */
    public int getNextStaffID() {
        if (clinicians.size() == 0) {
            return 0;
        }
        else {
            int currentID = clinicians.stream()
                    .max(Comparator.comparing(Clinician::getStaffID))
                    .get()
                    .getStaffID();
            return currentID + 1;
        }
    }


    /**
     * Calls all sub-methods to save data to disk
     */
    public void saveToDisk() {
        try {
            saveToDiskPatients();
            saveToDiskWaitlist();
            saveToDiskClinicians();
        } catch (IOException e) {
            userActions.log(Level.SEVERE, e.getMessage(), "attempted to save to disk");
        }
    }

    private void saveToDiskWaitlist() throws IOException {
        Gson gson = new Gson();
        String json = gson.toJson(organWaitingList);

        String PatientPath = "./";
        Writer writer = new FileWriter(new File(PatientPath, "waitlist.json"));
        writer.write(json);
        writer.close();
    }
    
    /**
     * Writes database patients to file on disk
     *
     * @exception IOException when the file cannot be found nor created
     */
    private void saveToDiskPatients() throws IOException {
        Gson gson = new Gson();
        String json = gson.toJson(patients);

        String patientPath = "./";
        Writer writer = new FileWriter(new File(patientPath, "patient.json"));
        writer.write(json);
        writer.close();
    }


    /**
     * Writes database clinicians to file on diskreturn null;
     *
     * @exception IOException when the file cannot be found nor created
     */
    private void saveToDiskClinicians() throws IOException {
        Gson gson = new Gson();
        String json = gson.toJson(clinicians);

        String clinicianPath = "./";
        Writer writer = new FileWriter(new File(clinicianPath, "clinician.json"));
        writer.write(json);
        writer.close();
    }

//
//    /**
//     * Calls importFromDisk and handles any errors
//     * @param fileName The file to import from
//     */
//    public static void importFromDisk(String fileName) {
//        try {
//            importFromDiskPatients(fileName);
//            userActions.log(Level.INFO, "Imported patients from disk", "Attempted to import from disk");
//            SearchPatients.createFullIndex();
//        } catch (IOException e) {
//            userActions.log(Level.WARNING, e.getMessage(), "attempted to import from disk");
//        }
//    }

    /**
     * Reads patient data from disk
     * @param fileName file to import from
     */
    public void importFromDiskPatients(String fileName) {
        Gson gson = new Gson();
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(fileName));
            Patient[] patient = gson.fromJson(br, Patient[].class);
            for (Patient d : patient) {
                try {
                    addPatient(d);
                }
                catch (IllegalArgumentException e) {
                    userActions.log(Level.WARNING, "Error importing donor from file", "Attempted to import donor from file");
                }
            }
        }
        catch (FileNotFoundException e) {
            userActions.log(Level.WARNING, "Patient import file not found", "Attempted to read patient file");
        }
    }

    public void importFromDiskWaitlist(String directory) throws FileNotFoundException {
    	String fileName = directory + "waitlist.json";
        Gson gson = new Gson();
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        organWaitingList = gson.fromJson(br, OrganWaitlist.class);
    }

    /**
     * Reads clinician data from disk
     * @param fileName file to import from
     */
    public void importFromDiskClinicians(String fileName) {
        Gson gson = new Gson();
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(fileName));
            Clinician[] clinician = gson.fromJson(br, Clinician[].class);
            for (Clinician c : clinician) {
                try {
                    addClinician(c);
                } catch (IllegalArgumentException e) {
                    userActions.log(Level.WARNING, "Error importing clinician from file", "Attempted to import clinician from file");
                }
            }
        }
        catch (FileNotFoundException e) {
            userActions.log(Level.WARNING, "Failed to import clinicians", "Attempted to import clinicians");
        }
    }


    /**
     * Clears the database of all patients
     */
    public void resetDatabase() {
        patients = new HashSet<>();
        clinicians = new HashSet<>();
    }



    public Set<Patient> getPatients() {
        return patients;
    }


    public Set<Clinician> getClinicians() {
        return clinicians;
    }


    public static void main(String[] argv) {
        try {
            Database test = Database.getDatabase();
            String stmt = "SELECT * FROM tblPatients WHERE LName = ?";
            String[] params = {"Joeson"};
            ArrayList<String[]> results = test.runQuery(stmt, params);
            System.out.println("All Patients with last name Joeson");
            for (String[] col : results) {
                System.out.println(String.join(" ", col));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
