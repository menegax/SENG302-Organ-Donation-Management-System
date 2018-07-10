package service;

import com.google.gson.Gson;
import model.*;
import utility.GlobalEnums;
import utility.GlobalEnums.Organ;
import utility.GlobalEnums.Region;
import utility.PatientActionRecord;
import utility.SearchPatients;
import utility.UserActionRecord;

import java.io.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;

import org.apache.commons.lang3.ArrayUtils;

import static utility.UserActionHistory.userActions;

public class Database {

    private Set<Patient> patients;

    private Set<Clinician> clinicians;

    private OrganWaitlist organWaitingList;

    private static Database database = null;

    private Connection conn;
    
    private final String UPDATEPATIENTQUERYSTRING = "INSERT INTO tblPatients "
            + "(Nhi, FName, MName, LName, Birth, Created, Modified, Death, BirthGender, "
            + "PrefGender, PrefName, Height, Weight, BloodType, DonatingOrgans, ReceivingOrgans) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) "
            + "ON DUPLICATE KEY UPDATE "
            + "FName = VALUES (FName), "
            + "MName = VALUES (MName), "
            + "LName = VALUES (LName), "
            + "Birth = VALUES (Birth), "
            + "Created = VALUES (Created), "
            + "Modified = VALUES (Modified), "
            + "Death = VALUES (Death), "
            + "BirthGender = VALUES (BirthGender), "
            + "PrefGender = VALUES (PrefGender), "
            + "PrefName = VALUES (PrefName), "
            + "Height = VALUES (Height), "
            + "Weight = VALUES (Weight), "
            + "BloodType = VALUES (BloodType), "
            + "DonatingOrgans = VALUES (DonatingOrgans), "
            + "ReceivingOrgans = VALUES (ReceivingOrgans)";
    
    private final String UPDATECLINICIANQUERYSTRING = "INSERT INTO tblClinicians "
    		+ "(StaffID, FName, MName, LName, Street1, Street2, Suburb, Region, Modified) "
    		+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) "
    		+ "ON DUPLICATE KEY UPDATE "
    		+ "FName = VALUES (FName), "
    		+ "MName = VALUES (MName), "
    		+ "LName = VALUES (LName), "
    		+ "Street1 = VALUES (Street1), "
    		+ "Street2 = VALUES (Street2), "
    		+ "Suburb = VALUES (Suburb), "
    		+ "Region = VALUES (Region), "
    		+ "Modified = VALUES (Modified)";

    private final String UPDATEPATIENTCONTACTQUERYSTRING = "INSERT INTO tblPatientContact "
    		+ "(Patient, Street1, Street2, Suburb, Region, Zip, HomePhone, WorkPhone, "
    		+ "MobilePhone, Email, ECName, ECRelationship, ECHomePhone, ECWorkPhone, "
    		+ "ECMobilePhone, ECEmail) "
    		+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) "
    		+ "ON DUPLICATE KEY UPDATE "
    		+ "Street1 = VALUES (Street1), "
    		+ "Street2 = VALUES (Street2), "
    		+ "Suburb = VALUES (Suburb), "
    		+ "Region = VALUES (Region), "
    		+ "Zip = VALUES (Zip), "
    		+ "HomePhone = VALUES (HomePhone), "
    		+ "WorkPhone = VALUES (WorkPhone), "
    		+ "MobilePhone = VALUES (MobilePhone), "
    		+ "Email = VALUES (Email), "
    		+ "ECName = VALUES (ECName), "
    		+ "ECRelationship = VALUES (ECRelationship), "
    		+ "ECHomePhone = VALUES (ECHomePhone), "
    		+ "ECWorkPhone = VALUES (ECWorkPhone), "
    		+ "ECMobilePhone = VALUES (ECMobilePhone), "
    		+ "ECEmail = VALUES (ECEmail)";

    private final String UPDATEPATIENTLOGQUERYSTRING = "INSERT INTO tblPatientLogs "
            + "(Patient, Time, Level, Message, Action) "
            + "VALUES (?, ?, ?, ?, ?) "
            + "ON DUPLICATE KEY UPDATE "
            + "Time = VALUES (Time)";

    private final String UPDATEPATIENTDISEASESQUERYSTRING = "INSERT INTO tblDiseases "
            + "(Patient, Name, DateDiagnosed, State) "
            + "VALUES (?, ?, ?, ?) "
            + "ON DUPLICATE KEY UPDATE "
            + "State = VALUES (State)";

    private final String UPDATEPATIENTMEDICATIONQUERYSTRING = "INSERT INTO tblMedications "
            + "(Patient, Name) "
            + "VALUES (?, ?) "
            + "ON DUPLICATE KEY UPDATE "
            + "Name = VALUES (Name)";

    private final String UPDATEPATIENTPROCEDURESQUERYSTRING = "INSERT INTO tblProcedures "
            + "(Patient, Summary, Description, ProDate, AffectedOrgans) "
            + "VALUES (?, ?, ?, ?, ?) "
            + "ON DUPLICATE KEY UPDATE "
            + "Description = VALUES (Description), "
            + "AffectedOrgans = VALUES (AffectedOrgans)";

    /**
     * Private constructor for creating instance of Database for Singleton.
     */
    private Database() {
        patients = new HashSet<>();
        clinicians = new HashSet<>();
        organWaitingList = new OrganWaitlist();
        initializeConnection();
        if (conn != null) {
        	loadAll();
        }
    }

    /**
     * Returns the instance of the database.
     * If one does not exist, it creates one.
     *
     * @return The instance of the database for Singleton.
     */
    public static Database getDatabase() {
        if (database == null) {
            database = new Database();
        }
        return database;
    }


    //TODO change to real database before submission

    /**
     * Initialize the connection to the remote database.
     */
    private void initializeConnection() {
		try {
			conn = DriverManager.getConnection("jdbc:mysql://mysql2.csse.canterbury.ac.nz:3306/seng302-2018-team800-test?allowMultiQueries=true", "seng302-team800", "ScornsGammas5531");
		} catch (SQLException e1) {
			System.err.println("Failed to connect to UC database server.");
			try {
				conn = DriverManager.getConnection("jdbc:mysql://122.62.50.128:3306/seng302-2018-team800-test?allowMultiQueries=true", "seng302-team800", "ScornsGammas5531");
			} catch (SQLException e2) {
				System.err.println("Failed to connect to database mimic from external source.");
				try {
					conn = DriverManager.getConnection("jdbc:mysql://192.168.1.70:3306/seng302-2018-team800-test?allowMultiQueries=true", "seng302-team800", "ScornsGammas5531");					
				} catch (SQLException e3) {
					System.err.println("Failed to connect to database mimic from internal source.");
					System.err.println("All database connections failed.");
					conn = null;
				}
			}
		}
    }


    /**
     * Runs a SQL query on the database.
     *
     * @param query The SQL query to run.
     * @param params The parameters to put into the query. Cannot be null, but can be an empty array.
     * @return Null if update or insert query or if select query ArrayList of String arrays
     * with the ArrayList being the full set of results and each internal String
     * array being a row of the queried table.
     * @throws SQLException If there is an error communicating with the database or SQL syntax error.
     */
    public ArrayList<String[]> runQuery(String query, String[] params) throws SQLException {
        String type = query.split(" ")[0].toUpperCase();
        PreparedStatement stmt = setupQuery(query, params);
        if (type.equals("SELECT")) {
            return runSelectQuery(stmt);
        } else if (type.equals("UPDATE") || type.equals("INSERT") || type.equals("DELETE")) {
            runUpdateQuery(stmt);
        }
        return null;
    }

    /**
     * @param query The select query to run on the database.
     * @return ArrayList of String arrays with the ArrayList being the full set of results and each internal String
     * array being a row of the queried table.
     * @throws SQLException If there is an error communicating with the database or SQL syntax error.
     */
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


    /**
     * Adds a object to the database.
     * @param object Object to add to the database.
     * @return True if added object to database, false otherwise.
     */
    public boolean add(Object object) {
        if (object instanceof Patient) {
            return addPatient((Patient) object);
        } else if (object instanceof Clinician) {
            return addClinician((Clinician) object);
        }
        return false;
    }

    /**
     * Sets a query up as a Prepared Statement from the query string and a string array of the parameters.
     * @param query String representation of a MySQL query.
     * @param params String array of the parameters of the query.
     * @return Prepared Statement of the MySQL query.
     * @throws SQLException If there is an error communicating with the database or SQL syntax error.
     */
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
     *
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
        if (patient.getDeath() != null) {
            attr[7] = patient.getDeath().toString();
        }
        if (patient.getBirthGender() != null) {
            attr[8] = patient.getBirthGender().toString().substring(0, 1);
        }
        attr[9] = patient.getPreferredGender().toString().substring(0, 1);
        attr[10] = patient.getPreferredName();
        attr[11] = String.valueOf(patient.getHeight() * 100);
        attr[12] = String.valueOf(patient.getWeight());
        if(patient.getBloodGroup() != null) {
            attr[13] = patient.getBloodGroup().toString();
        }
        attr[14] = String.join(",", patient.getDonations().toString())
                .replaceAll("\\[", "").replaceAll("\\]", "");
        attr[15] = String.join(",", patient.getRequiredOrgans().toString())
                .replaceAll("\\[", "").replaceAll("\\]", "");
        return attr;
    }

    /**
     * Gets a patient's contact details and stores them in a String array
     *
     * @param patient Patient to get attributes from
     * @return String[] contact attributes
     */
    private String[] getPatientContactAttributes(Patient patient) {
        String[] contactAttr = new String[16];
        contactAttr[0] = patient.getNhiNumber();
        contactAttr[1] = patient.getStreet1();
        contactAttr[2] = patient.getStreet2();
        contactAttr[3] = patient.getSuburb();
        if(patient.getRegion() != null) {
            contactAttr[4] = patient.getRegion().toString();
        }
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
     *
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
     *
     * @param patient    Patient who is taking or used to take the medication
     * @param medication The medication used by the patient
     * @return String[] medication attributes
     */
    private String[] getMedicationAttributes(Patient patient, Medication medication, boolean isCurrent) {
        String[] medAttr = new String[3];
        medAttr[0] = patient.getNhiNumber();
        medAttr[1] = medication.getMedicationName();
        if (isCurrent) {
            medAttr[2] = "0";
        } else {
            medAttr[2] = "1";
        }
        return medAttr;
    }

    /**
     * Gets all attributes for a procedure object
     *
     * @param patient Patient with disease
     * @param procedure Procedure to get attributes from
     * @return String[] procedure attributes
     */
    private String[] getProcedureAttributes(Patient patient, Procedure procedure) {
        String[] procedureAttr = new String[5];
        procedureAttr[0] = patient.getNhiNumber();
        procedureAttr[1] = procedure.getSummary();
        procedureAttr[2] = procedure.getDescription();
        procedureAttr[3] = procedure.getDate().toString();
        procedureAttr[4] = String.join(",", procedure.getAffectedDonations().toString())
                .replaceAll("\\[", "").replaceAll("\\]", "");
        return procedureAttr;
    }

    /**
     * Gets all attributes for a disease object
     *
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
            case "chronic":
                diseaseAttr[3] = "1";
                break;
            case "cured":
                diseaseAttr[3] = "2";
                break;
            default:
                diseaseAttr[3] = "0";
        }
        return diseaseAttr;
    }

    private String[] getLogAttributes(Patient patient, PatientActionRecord record) {
        String[] recordAttr = new String[5];
        recordAttr[0] = patient.getNhiNumber();
        recordAttr[1] = record.getTimestamp().toString();
        recordAttr[2] = record.getLevel().toString();
        recordAttr[3] = record.getMessage();
        recordAttr[4] = record.getAction();
        return recordAttr;
    }

    /**
     * Gets all attributes for a transplant request
     * @param request
     * @return
     */
    private String[] getTransplantRequestAttributes(OrganWaitlist.OrganRequest request) {
        String[] attr = new String[4];
        attr[0] = request.getReceiverNhi();
        attr[1] = request.getRequestDate().toString();
        attr[2] = request.getRequestedOrgan().toString();
        attr[3] = request.getRequestRegion().toString();
        return attr;
    }

    
    /**
     * Update an object in the database. If it does not exist in the database it will create it.
     * @param object The object to be updated in the database.
     * @return True if the object was update or created, false otherwise.
     */
    public boolean update(Object object) {
        if (object instanceof Patient) {
			return updatePatient((Patient) object);
        } else if (object instanceof Clinician) {
        	return updateClinician((Clinician) object);
        }
        return false;
    }

    /**
     * Updates a patient in the database.
     * @param patient The patient to update.
     * @return True if patient updated in database, false otherwise.
     */
    private boolean updatePatient(Patient patient) {
        //Query to add base patient attributes to database 
        String[] attr = getPatientAttributes(patient);
        String query = UPDATEPATIENTQUERYSTRING;
        //Query to add patient contact details to database                
        query += ";" + UPDATEPATIENTCONTACTQUERYSTRING;
        attr = ArrayUtils.addAll(attr, getPatientContactAttributes(patient));
        //Queries to add patient medication details to database 
        for (Medication medication : patient.getCurrentMedications()) {
            attr = ArrayUtils.addAll(attr, getMedicationAttributes(patient, medication, true));
            query += ";" + UPDATEPATIENTMEDICATIONQUERYSTRING;
        }

        for (Medication medication : patient.getMedicationHistory()) {
            attr = ArrayUtils.addAll(attr, getMedicationAttributes(patient, medication, false));
            query += ";" + UPDATEPATIENTMEDICATIONQUERYSTRING;
        }
        //Queries to add patient diseases details to database 
        ArrayList<Disease> allDiseases = patient.getCurrentDiseases();
        allDiseases.addAll(patient.getPastDiseases());
        for (Disease disease : allDiseases) {
            attr = ArrayUtils.addAll(attr, getDiseaseAttributes(patient, disease));
            query += ";" + UPDATEPATIENTDISEASESQUERYSTRING;
         }
        //Queries to add patient logs details to database
        for (PatientActionRecord record : patient.getUserActionsList()) {
            attr = ArrayUtils.addAll(attr, getLogAttributes(patient, record));
            query += ";" + UPDATEPATIENTLOGQUERYSTRING;
        }
        //Queries to add patient procedures details to database
        for(Procedure procedure : patient.getProcedures()) {
            attr = ArrayUtils.addAll(attr, getProcedureAttributes(patient, procedure));
            query += ";" + UPDATEPATIENTPROCEDURESQUERYSTRING;
        }
        //Run all queries
        try {
			runQuery(query, attr);
			userActions.log(Level.INFO, "Updated patient attributes in database.", "Attempted to update patient attributes in database.");
			return true;
		} catch (SQLException e) {
			userActions.log(Level.SEVERE, "Couldn't query database " + e.getMessage(), "Attempted to update patient in database.");
		}
        return false;
    }

    /**
     * Updates a clinician in the database.
     * @param clinician The clinician to update.
     */
    private boolean updateClinician(Clinician clinician) {
    	String[] attr = getClinicianAttributes(clinician);
    	try {
			runQuery(UPDATECLINICIANQUERYSTRING, attr);
			userActions.log(Level.INFO, "Updated patient attributes in database.", "Attempted to update clinician attributes in database.");
			return true;
		} catch (SQLException e) {
			userActions.log(Level.SEVERE, "Couldn't query database " + e.getMessage(), "Attempted to update clinician in database.");
		}
    	return false;
    }

    /**
     * Checks if the given NHI exists in the database.
     * @param nhi The NHI to check.
     * @return True if NHI in database, false otherwise.
     */
    public boolean nhiInDatabase(String nhi) {
    	String query = "SELECT * FROM tblPatients WHERE Nhi = ?";
    	String[] attr = {nhi};
    	try {
			ArrayList<String[]> results = runQuery(query, attr);
			if (results.size() > 0) {
				return true;
			}
		} catch (SQLException e) {
            userActions.log(Level.SEVERE, "Failed to query database " + e.getMessage(), "Attempted to check if NHI exists in database.");
		}
    	return false;
    }
    
    /**
     * Adds a patient to the database
     *
     * @param newPatient the new patient to add
     * @return True if added patient to database, false otherwise.
     * @throws IllegalArgumentException Throws if NHI already in use.
     */
    private boolean addPatient(Patient newPatient) throws IllegalArgumentException {
        if (inDatabase(newPatient)) {
            userActions.log(Level.SEVERE, "Failed to add patient to database, NHI already exisits.", "Attempted to add new patient to database.");
            throw new IllegalArgumentException("NHI " + newPatient.getNhiNumber() + " already in use.");
        } else {
        	//Adds Patient to the database
            if (updatePatient(newPatient)) {
                //Add Patient to application
                patients.add(newPatient);
                //Add Patient to search index
                SearchPatients.addIndex(newPatient);
            	userActions.log(Level.INFO, "Successfully added patient " + newPatient.getNhiNumber(), "Attempted to add a patient");
            	return true;
            }
            userActions.log(Level.SEVERE, "Failed to add patient " + newPatient.getNhiNumber(), "Attempted to add a patient");
            return false;
        }
    }

    /**
     * Adds a clinician to the database.
     * @param newClinician the new clinician to add.
     * @return True if the clinician was added, false otherwise.
	 * @throws IllegalArgumentException Throws if Staff ID already in use.
     */
    private boolean addClinician(Clinician newClinician) throws IllegalArgumentException {
    	if (inDatabase(newClinician)) {
            userActions.log(Level.SEVERE, "Failed to add clinician to database, staff ID already exisits.", "Attempted to add new clinician to database.");
    		throw new IllegalArgumentException("StaffID " + String.valueOf(newClinician.getStaffID()) + "already in use.");
    	} else {
			if (updateClinician(newClinician)) {
				clinicians.add(newClinician);
				userActions.log(Level.INFO, "Successfully added clinician " + newClinician.getStaffID(), "Attempted to add a clinician");
				return true;
			}
			userActions.log(Level.SEVERE, "Failed added clinician " + newClinician.getStaffID(), "Attempted to add a clinician");				
			return false;
    	}
    }

    /**
     * Checks if patient exists in the database.
     * @param patient The patient to check.
     * @return True if the patient is found, false otherwise.
     * @throws SQLException Throws if there is an error querying the database.
     */
    private boolean patientInDatabase(Patient patient) throws SQLException {
    	String query = "SELECT * FROM tblPatients WHERE Nhi = ?";
    	String[] param = {patient.getNhiNumber()};
    	if (runQuery(query, param).size() > 0) {
    		return true;
    	}
    	return false;
    }
    
    /**
     * Checks if clinician exists in the database.
     * @param clinician The clinician to check.
     * @return True if the clinician is found, false otherwise.
     * @throws SQLException Throws if there is an error querying the database.
     */
    private boolean clinicianInDatabase(Clinician clinician) throws SQLException {
    	String query = "SELECT * FROM tblClinicians WHERE StaffID = ?";
    	String[] param = {String.valueOf(clinician.getStaffID())};
    	if (runQuery(query, param).size() > 0) {
    		return true;
    	}
    	return false;
    }
    
    /**
     * Checks if an object exists in the database.
     * @param object The object to check.
     * @return True if the object is found, false otherwise.
     */
    public boolean inDatabase(Object object) {
    	try {
    		if (object instanceof Patient) {
				return patientInDatabase((Patient) object);
    		} else if (object instanceof Clinician) {
    			return clinicianInDatabase((Clinician) object);
    		}
		} catch (SQLException e) {
			userActions.log(Level.WARNING, "Couldn't query database", "Attempted to check if object existed in database");
		}
    	return false;
    }
    
    //TODO shouldnt be used
    /**
     * Saves a transplant request into the database.
     * @param request The transplant request to save.
     * @return True if saved, false otherwise.
     */
    public boolean saveTransplantRequest(OrganWaitlist.OrganRequest request) {
        String[] attr = getTransplantRequestAttributes(request);
        String query = "INSERT INTO tblTransplantWaitList " +
                "VALUES (?, ?, ?, ?)";
        try {
            runQuery(query, attr);
			userActions.log(Level.INFO, "Successfully added transplant request to database.", "Attempted to add a transplant request to database.");
            return true;
        } catch (SQLException e) {
			userActions.log(Level.SEVERE, "Failure to add transplant request to database " + e.getMessage(), "Attempted to add a transplant request.");
        }
        return false;
    }

    /**
     * loads all data from database into application.
     */
    public void loadAll() {
        loadAllPatients();
        loadAllClinicians();
        loadTransplantWaitingList();
    }

    /**
     * Loads all organs for a patient and stores them in an ArrayList.
     * @param organs String of the patients organs.
     * @return ArrayList of the patients organs.
     */
    private ArrayList<GlobalEnums.Organ> loadOrgans(String organs) {
        ArrayList<GlobalEnums.Organ> organArrayList = new ArrayList<>();
        if (organs.equals("")) {
    		userActions.log(Level.INFO, "Patient had no organs to load.", "Attempted load all organs for patient.");
    		return organArrayList;
        }
        String[] organArray = organs.split(",");
        for (String organ : organArray) {
            organArrayList.add(GlobalEnums.Organ.valueOf(organ));
        }
		userActions.log(Level.INFO, "Successfully loaded all organs for patient.", "Attempted load all organs for patient.");
        return organArrayList;
    }

    /**
     * Loads user action logs for a patient.
     * @param nhi Patients NHI.
     * @return ArrayList of all logs for the patient.
     */
    private ArrayList<PatientActionRecord> loadPatientLogs(String nhi) {
        ArrayList<PatientActionRecord> patientLogs = new ArrayList<>();
        try {
            String[] nhiArray = {nhi};
            ArrayList<String[]> logsRaw = runQuery("SELECT * FROM tblPatientLogs WHERE Patient = ?", nhiArray);
            for (String[] attr : logsRaw) {
                patientLogs.add(parseLog(attr));
            }
			userActions.log(Level.INFO, "Successfully loaded all logs for patient " + nhi, "Attempted to load all logs for patient " + nhi);
        } catch (SQLException e) {
			userActions.log(Level.SEVERE, "Failed to load all logs for patient " + nhi, "Attempted to load all logs for patient " + nhi);
        }
        return patientLogs;
    }

    /**
     * Parses a user log from a string array of attributes from the database
     * @param attr log attributes from database
     * @return UserActionRecord user action log entry
     */
    private PatientActionRecord parseLog(String[] attr) {
        Timestamp timestamp = Timestamp.valueOf(attr[1]);
        Level level = Level.parse(attr[2]);
        String message = attr[3];
        String action = attr[4];
        return new PatientActionRecord(timestamp, level, message, action);
    }

    /**
     * Gets the contact details for a patient from the database and returns all attributes except the patient's NHI in a
     * String array
     * @param nhi Patient's NHI number
     * @return String array of patient contacts
     * @throws SQLException returned if SELECT operation from database fails
     */
    private String[] parsePatientContacts(String nhi) {
    	String query = "SELECT * FROM tblPatientContact WHERE Patient = ?";
    	String[] param = {nhi};
        String[] contactsRaw;
		try {
			contactsRaw = runQuery(query, param).get(0);
			return Arrays.copyOfRange(contactsRaw, 1, contactsRaw.length);
		} catch (SQLException e) {
			userActions.log(Level.SEVERE, "Couldn't query database " + e.getMessage(), "Attempted to read patient contact attributes.");
		}
        return new String[14];
    }

    //TODO Talk to Maree
    /**
     * Creates a patient object from a string array of attributes retrieved from the server
     * @param attr String array of attributes
     * @return Patient parsed patient
     */
    private Patient parsePatient(String[] attr) {
        String nhi = attr[0];
        String fName = attr[1];
        ArrayList<String> mNames = new ArrayList<>();
        mNames.addAll(Arrays.asList(attr[2].split(" ")));
        String lName = attr[3];
        LocalDate birth = LocalDate.parse(attr[4]);
        Timestamp created = Timestamp.valueOf(attr[5]);
        Timestamp modified = Timestamp.valueOf(attr[6]);
        LocalDate death = null;
        if (attr[7] != null) {
        	death = LocalDate.parse(attr[7]);
        }
        GlobalEnums.BirthGender gender;
        switch (String.valueOf((Object)attr[8])) {
            case "M":
                gender = GlobalEnums.BirthGender.MALE;
                break;
            default:
                gender = GlobalEnums.BirthGender.FEMALE;
                break;
        }
        GlobalEnums.PreferredGender preferredGender;
        switch(String.valueOf((Object)attr[9])) {
            case "M":
                preferredGender = GlobalEnums.PreferredGender.MAN;
                break;
            case "F":
                preferredGender = GlobalEnums.PreferredGender.WOMAN;
                break;
            default:
                preferredGender = GlobalEnums.PreferredGender.NONBINARY;
                break;
        }
        String prefName = attr[10];
        double height = Double.parseDouble(attr[11]) / 100;
        double weight = Double.parseDouble(attr[12]);
        GlobalEnums.BloodGroup bloodType = null;
        if (attr[13] != null) {
        	bloodType = GlobalEnums.BloodGroup.valueOf(attr[13]);
        }
        ArrayList<GlobalEnums.Organ> donations = loadOrgans(attr[14]);
        ArrayList<GlobalEnums.Organ> requested = loadOrgans(attr[15]);
//TODO assign logs to the patient
        ArrayList<PatientActionRecord> records = loadPatientLogs(nhi);

        String[] contactAttr = new String[15];
        //TODO rewrite method to do medication history and current assigning within load method
        contactAttr = parsePatientContacts(nhi);
        GlobalEnums.Region region = null;
        if (contactAttr[3] != null) {
        	region = GlobalEnums.Region.valueOf(contactAttr[3]);
        }
        int zip = Integer.parseInt(contactAttr[4]);
        ArrayList<Medication>[] meds = loadMedications(nhi);
        ArrayList<Medication> currentMeds = meds[0];
        ArrayList<Medication> medHistory = meds[1];
        ArrayList<Disease>[] diseases = loadDiseases(birth, nhi);
        ArrayList<Disease> currentDiseases = diseases[0];
        ArrayList<Disease> pastDiseases = diseases[1];
        List<Procedure> procedures = loadProcedures(nhi);
		userActions.log(Level.INFO, "Successfully loaded patient " + nhi + " from the database.", "Attempted to load a patient from the database.");	
        return new Patient(nhi, fName, mNames, lName, birth, created, modified, death, gender, preferredGender, prefName, height, weight,
                bloodType, donations, requested, contactAttr[0], contactAttr[1], contactAttr[2], region, zip,
                contactAttr[5], contactAttr[6], contactAttr[7], contactAttr[8], contactAttr[9], contactAttr[10],
                contactAttr[11], contactAttr[12], contactAttr[13], contactAttr[14], records, currentDiseases,
                pastDiseases, currentMeds, medHistory, procedures);
    }

    /**
     * Gets all diseases for a patient and sorts them into two ArrayLists of past and current medications.
     * Returns an ArrayList array of current and past medications.
     * @param nhi Patient NHI.
     * @return ArrayList array of current and past medications.
     */
    private ArrayList<Medication>[] loadMedications(String nhi) {
        ArrayList<String[]> medicationsRaw;
        ArrayList<Medication> currentMedications = new ArrayList<>();
        ArrayList<Medication> medicationHistory = new ArrayList<>();
        @SuppressWarnings("unchecked")
		ArrayList<Medication>[] medArray = new ArrayList[2];
        medArray[0] = currentMedications;
        medArray[1] = medicationHistory;
		try {
			String query = "SELECT * FROM tblMedications WHERE Patient = ?";
			String[] param = {nhi};
			medicationsRaw = runQuery(query, param);
	        for (String[] attr : medicationsRaw) {
	            medArray = parseMedication(attr, medArray);
	        }
		} catch (SQLException e) {
			userActions.log(Level.SEVERE, "Couldn't query database " + e.getMessage(), "Attempted to read all medication for patient " + nhi + " from database.");
		}
        return medArray;
    }

    /**
     * Loads all procedures for a patient.
     * @param nhi The NHI of the patient.
     * @return A List of the procedures.
     */
    private List<Procedure> loadProcedures(String nhi) {
        List<Procedure> procedureList = new ArrayList<>();
        ArrayList<String[]> proceduresRaw;
		try {
			String query = "SELECT * FROM tblProcedures WHERE Patient = ?";
			String[] param = {nhi};
			proceduresRaw = runQuery(query, param);
	        for(String[] attr : proceduresRaw) {
	            procedureList.add(parseProcedure(attr));
	        }
		} catch (SQLException e) {
			userActions.log(Level.SEVERE, "Couldn't query database " + e.getMessage(), "Attempted to read all procedures for patient " + nhi + " from database.");
		}
        return procedureList;
    }

    /**
     * Parses a String of a procedure into a Procedure object.
     * @param attr The String representation of the procedure.
     * @return The Procedure object.
     */
    private Procedure parseProcedure(String[] attr) {
        Procedure procedure = new Procedure(null, null, null, null);
        procedure.setSummary(attr[1]);
        procedure.setDescription(attr[2]);
        procedure.setDate(LocalDate.parse(attr[3]));
        String[] organArray = attr[4].split(",");
        Set<GlobalEnums.Organ> organSet = new HashSet<>();
        for (String organ : organArray) {
            organSet.add(GlobalEnums.Organ.valueOf(organ));
        }
        procedure.setAffectedDonations(organSet);
        return procedure;
    }

    /**
     * Gets all diseases for a patient and sorts them into two ArrayLists of past and current diseases.
     * Returns an ArrayList array of current and past diseases.
     * @param birth Patient birth date.
     * @param nhi Patient NHI.
     * @return ArrayList array of current and past diseases.
     */
    private ArrayList<Disease>[] loadDiseases(LocalDate birth, String nhi) {
        ArrayList<String[]> diseasesRaw;
        ArrayList<Disease> patientDiseases = new ArrayList<>();
        @SuppressWarnings("unchecked")
		ArrayList<Disease>[] diseaseArray = new ArrayList[2];
        ArrayList<Disease> pastDiseases = new ArrayList<>();
        ArrayList<Disease> currentDiseases = new ArrayList<>();
		try {
			String query = "SELECT * FROM tblDiseases WHERE Patient = ?";
			String[] param = {nhi};
			diseasesRaw = runQuery(query, param);
	        for (String[] attr : diseasesRaw) {
	            try {
					patientDiseases.add(parseDisease(attr, birth));
				} catch (InvalidObjectException e) {
					userActions.log(Level.SEVERE, "Couldn't load disease. " + e.getMessage(), "Attempted load disease for patient " + nhi);
				}
	        }
	        for (Disease disease : patientDiseases) {
	            if (disease.getDiseaseState() == GlobalEnums.DiseaseState.CURED) {
	                pastDiseases.add(disease);
	            } else {
	                currentDiseases.add(disease);
	            }
	        }
		} catch (SQLException e) {
			userActions.log(Level.SEVERE, "Couldn't query database " + e.getMessage(), "Attempted to read all diseases for patient " + nhi + " from the database.");
		}
        diseaseArray[0] = currentDiseases;
        diseaseArray[1] = pastDiseases;
        return diseaseArray;
    }

    /**
     * Adds a medication to the current or past medications for a patient.
     * @param attr String array of attributes of a medication.
     * @param meds ArrayList array of current and past medication.
     * @return The updated ArrayList arry of current and past medication.
     */
    private ArrayList<Medication>[] parseMedication(String[] attr, ArrayList<Medication>[] meds) {
        switch (attr[2]) {
            case "0":
                meds[0].add(new Medication(attr[1]));
                break;
            case "1":
                meds[1].add(new Medication(attr[1]));
                break;
        }
        return meds;
    }

    /**
     * Adds a disease to the current or past diseases for a patient.
     * @param attr String array of attributes of a disease.
     * @param birthDate birth date of the patient.
     * @return The parsed disease.
     * @throws InvalidObjectException 
     */
    private Disease parseDisease(String[] attr, LocalDate birthDate) throws InvalidObjectException {
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
     *
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
     * @return True if all patients load successfully, false otherwise.
     */
    private boolean loadAllPatients() {
        try {
        	String query = "SELECT * FROM tblPatients";
            ArrayList<String[]> patientsRaw = runQuery(query, new String[0]);
            for (String[] attr : patientsRaw) {
                patients.add(parsePatient(attr));
            }
			userActions.log(Level.INFO, "Successfully imported all patients from the database.", "Attempted to read all patients from database.");	
            return true;
        } catch (SQLException e) {
			userActions.log(Level.SEVERE, "Failed to read all patients from the database.", "Attempted to read all patients from database.");	
        }
        return false;
    }

    /**
     * Loads all clinicians into the application from the remote database.
     * @return True if successfully loads all clinicians, false otherwise.
     */
    private boolean loadAllClinicians() {
        try {
        	String query = "SELECT * FROM tblClincians";
            ArrayList<String[]> clinicianRaw = runQuery(query, new String[0]);
            for (String[] attr : clinicianRaw) {
                clinicians.add(parseClinician(attr));
            }
            return true;
        } catch (SQLException e) {
			userActions.log(Level.SEVERE, "Failed to read all clinicians from the database.", "Attempted to read all clinicians from the database.");
        }
        return false;
    }


    /**
     * Loads the transplant waiting list from the database.
     * @return True if successfully loads all transplant requests successfully, false otherwise.
     */
    private boolean loadTransplantWaitingList() {
        try {
        	String query = "SELECT * FROM tblTransplantWaitList";
            ArrayList<String[]> waitlistRaw = runQuery(query, new String[0]);
            for (String[] attr : waitlistRaw) {
                String nhi = attr[0];
                LocalDate date = LocalDate.parse(attr[1]);
                GlobalEnums.Organ organ = GlobalEnums.Organ.valueOf(attr[2]);
                GlobalEnums.Region region = GlobalEnums.Region.valueOf(attr[3]);
                Patient patient = getPatientByNhi(nhi);
                if (patient != null) {
                	String name = patient.getNameConcatenated();
                	organWaitingList.add(name, organ, date, region, nhi);
                } else {
        			userActions.log(Level.SEVERE, "Failed to create transplant request. No patient found with NHI " + nhi, "Attempted to create transplant request.");
                }             
            }
            return true;
        } catch (SQLException e) {
			userActions.log(Level.SEVERE, "Failed to read transplant waiting list from the database.", "Attempted to read the transplant waiting list from the database.");
        }
        return false;
    }

    /**
     * Deletes an object from the application and database.
     * @param object The object to delete.
     * @return True if the object was successfully deleted, false otherwise.
     */
    public boolean delete(Object object) {
    	if (object instanceof Patient) {
    		return deletePatient((Patient) object);
    	} else if (object instanceof Clinician) {
    		return deleteClinician((Clinician) object);
    	}
    	return false;
    }
    
    /**
     * Deletes a clinician from the database and application.
     * @param clinician The clinician to delete.
     * @return True if successfully deletes clinician, otherwise false.
     */
    private boolean deleteClinician(Clinician clinician) {
    	int staffID = clinician.getStaffID();
    	String query = "DELETE FROM tblClinicians WHERE StaffID = ?";
    	String[] param = {String.valueOf(staffID)};
    	try {
			runQuery(query, param);
			clinicians.remove(clinician);
			return true;
		} catch (SQLException e) {
			userActions.log(Level.SEVERE, "Couldn't delete clinician " + String.valueOf(staffID) + " from the database. " + e.getMessage(), "Attempted to delete clinician " + staffID + " from the database.");
		}
    	return false;
    }
    
    /**
     * Deletes a patient from the database and application.
     * @param nhi The NHI of the patient to remove.
     * @return True if the patient was removed, false otherwise.
     */
    private boolean deletePatient(Patient patient) {
    	String nhi = patient.getNhiNumber();
    	String query = "";
    	String[] params = new String[6];
    	String[] tables = {"tblTransplantWaitList", "tblProcedures", "tblMedications", 
    			"tblDiseases", "tblPatientLogs", "tblPatientContact", "tblPatient"};
    	int counter = 0;
    	while (counter < tables.length - 1) {
    		query += "DELETE FROM " + tables[counter] + " WHERE Patient = ?;";
    		params[counter] = nhi;
    		counter += 1;
    	}
    	query += "DELETE FROM " + tables[counter] + " WHERE Nhi = ?";
    	params[counter] = nhi;
    	try {
			runQuery(query, params);
			SearchPatients.removeIndex(patient);
			patients.remove(patient);
			userActions.log(Level.INFO, "Deleted patient " + nhi, "Attempted to delete patient " + nhi);
			return true;
		} catch (SQLException e) {
			userActions.log(Level.SEVERE, "Couldn't delete patient " + nhi + " from the database." + e.getMessage(), "Attempted delete patient " + nhi + " from the database.");
		}
    	return false;
    }

//TODO Really want to redo this cause its not the best really. Can actually use the patient name search for this.
    /**
     * Searches patients by NHI
     *
     * @param nhi The NHI to search patients by.
     * @return The Patient object with the NHI or null if patient not found.
     */
    public Patient getPatientByNhi(String nhi) {
        for (Patient p : getPatients()) {
            if (p.getNhiNumber().equals(nhi.toUpperCase())) {
                return p;
            }
        }
        return null;
    }

    //TODO same said for this
    /**
     * Searches for a clinician by staffID.
     * @param staffID The staff ID of the clinician searching for.
     * @return The clinician with the staff ID entered or null if no clinician found.
     */
    public Clinician getClinicianByID(int staffID) {
        for (Clinician c : getClinicians()) {
            if (c.getStaffID() == staffID) {
                return c;
            }
        }
        return null;
    }

    //TODO This is not a good idea and did it ever actually work properly?
    /**
     * Returns the next valid staffID based on IDs in the clinician list
     *
     * @return the valid id
     */
    public int getNextStaffID() {
        if (clinicians.size() == 0) {
            return 0;
        } else {
            int currentID = clinicians.stream()
                    .max(Comparator.comparing(Clinician::getStaffID))
                    .get()
                    .getStaffID();
            return currentID + 1;
        }
    }

    //TODO change this to save to remote database
    /**
     * Calls all sub-methods to save data to disk
     */
    public void saveToDisk() {
        try {
            saveToDiskPatients();
//            saveToDiskWaitlist();
            saveToDiskClinicians();
        } catch (IOException e) {
            userActions.log(Level.SEVERE, e.getMessage(), "attempted to save to disk");
        }
    }
//TODO change
//    /**
//     * Saves the organ waitlist to the file waitlist.json
//     * @throws IOException the file cannot be found or created
//     */
//    private static void saveToDiskWaitlist() throws IOException {
//        Gson gson = new Gson();
//        String json = gson.toJson(organWaitingList);
//
//        String PatientPath = "./";
//        Writer writer = new FileWriter(new File(PatientPath, "waitlist.json"));
//        writer.write(json);
//        writer.close();
//    }
//TODO change
    /**
     * Writes database patients to file on disk
     *
     * @throws IOException when the file cannot be found nor created
     */
    private void saveToDiskPatients() throws IOException {
        Gson gson = new Gson();
        String json = gson.toJson(patients);

        String patientPath = "./";
        Writer writer = new FileWriter(new File(patientPath, "patient.json"));
        writer.write(json);
        writer.close();
    }
//TODO change
    /**
     * Writes database clinicians to file on diskreturn null;
     *
     * @throws IOException when the file cannot be found nor created
     */
    private void saveToDiskClinicians() throws IOException {
        Gson gson = new Gson();
        String json = gson.toJson(clinicians);

        String clinicianPath = "./";
        Writer writer = new FileWriter(new File(clinicianPath, "clinician.json"));
        writer.write(json);
        writer.close();
    }
//TODO what to we do with this?
    /**
     * Reads patient data from disk
     *
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
                } catch (IllegalArgumentException e) {
                    userActions.log(Level.WARNING, "Error importing donor from file", "Attempted to import donor from file");
                }
            }
        } catch (FileNotFoundException e) {
            userActions.log(Level.WARNING, "Patient import file not found", "Attempted to read patient file");
        }
    }
//TODO what do we do with this?
//    /**
//     * Imports the organ waitlist from the selected directory
//     * @param filename file to import from
//     */
//    public static void importFromDiskWaitlist(String filename) {
//        Gson gson = new Gson();
//
//            InputStream in = ClassLoader.class.getResourceAsStream(filename);
//            BufferedReader br = new BufferedReader(new InputStreamReader(in));
//            organWaitingList = gson.fromJson(br, OrganWaitlist.class);
//
//    }
//TODO What do we do with this
    /**
     * Reads clinician data from disk
     *
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
        } catch (FileNotFoundException e) {
            userActions.log(Level.WARNING, "Failed to import clinicians", "Attempted to import clinicians");
        }
    }

    /**
     * Clears the database of all patients
     */
    public void resetLocalDatabase() {
        patients = new HashSet<>();
        clinicians = new HashSet<>();
        organWaitingList = new OrganWaitlist();
    }


    public Set<Patient> getPatients() {
        return patients;
    }


    public Set<Clinician> getClinicians() {
        return clinicians;
    }
//TODO what is this used for?
    public int showPatients() throws SQLException {
    	String query = "SELECT * FROM tblPatients";
    	ArrayList<String[]> results = runQuery(query, new String[0]);
    	System.out.println("Patients: ");
    	for (String[] result : results) {
    		for (String param : result) {
    			System.out.print(param + ", ");
    		}	
    		System.out.println();
    	}
     	return results.size();
    }
//TODO What is this used for?
    public int showClinicians() throws SQLException {
    	String query = "SELECT * FROM tblClinicians";
    	ArrayList<String[]> results = runQuery(query, new String[0]);
    	System.out.println("Clinicians: ");
    	for (String[] result : results) {
    		for (String param : result) {
    			System.out.print(param + ", ");
    		}	
    		System.out.println();
    	}
    	return results.size();
    }
    
    /**
     * Checks if the Staff ID is already in the database
     * @param staffID Staff ID to check
     * @return True if Staff ID found, false otherwise.
     */
    public boolean staffIDInDatabase(int staffID) {
    	String query = "SELECT * FROM tblClinicians WHERE StaffID = ?";
    	String[] param = {String.valueOf(staffID)};
    	try {
			if (runQuery(query, param).size() > 0) {
				return true;
			}	
		} catch (SQLException e) {
			userActions.log(Level.SEVERE, "Couldn't query database" + e.getMessage(), "Attempted to check if Staff ID existed in database");
		}
    	return false;
    }
    
    //TODO for testing only
    public static void main(String[] argv) {
        try {
            Database test = Database.getDatabase();
            Clinician clin = new Clinician(11, "First", new ArrayList<String>(), "LastlyButSecond", Region.CANTERBURY);
            Patient pat = new Patient("YGH4562", "Pat", new ArrayList<String>(), "Laffety", LocalDate.of(1998, 1, 8), new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()), null, GlobalEnums.BirthGender.MALE, GlobalEnums.PreferredGender.MAN, "Pat", 1.7, 56.0, GlobalEnums.BloodGroup.O_POSITIVE, new ArrayList<Organ>(), new ArrayList<Organ>(), "Street 1", "Street 2", "Suburb", GlobalEnums.Region.CANTERBURY, 7020, "035441143", "0", "0220918384", "plaffey@mail.com", "EC", "Relationship", "0", "0", "0", "Email@email.com", new ArrayList<PatientActionRecord>(), new ArrayList<Disease>(), new ArrayList<Disease>(), new ArrayList<Medication>(), new ArrayList<Medication>(), new ArrayList<Procedure>());

            int patients = test.showPatients();
            int clinicians = test.showClinicians();
            
            System.out.println("\nNumber of patients: " + String.valueOf(patients));
            System.out.println("Number of clinicians: " + String.valueOf(clinicians));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}