package service;

import com.google.gson.Gson;
import model.Clinician;
import model.Patient;
import utility.GlobalEnums;
import utility.SearchPatients;

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


    //ToDo change to real database before submittion
    private void initializeConnection() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://mysql2.csse.canterbury.ac.nz:3306/seng302-2018-team800-test",
                    "seng302-team800", "ScornsGammas5531");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //ToDO Complete
    public ArrayList<String[]> runQuery(String query, String[] params) {
        try {
            String type = query.split(" ")[0].toUpperCase();
            PreparedStatement stmt = setupQuery(query, params);
            if (type.equals("SELECT")) {
                return runSelectQuery(stmt);
            }
            else if (type.equals("UPDATE")) {
                runUpdateQuery(stmt);
            }
//        else if (type.equals("INSERT")) {
//
//        }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //ToDo Complete
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

    //ToDo Complete
    public void add(Object object) {
        if (object instanceof Patient) {
            addPatient((Patient) object);
        }
        else if (object instanceof Clinician) {

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
            PreparedStatement stmt = setupQuery(query, attr);
            runUpdateQuery(stmt);
            userActions.log(Level.INFO, "Successfully added patient " + newPatient.getNhiNumber(), "Attempted to add a patient");
        }
        catch (SQLException o) {
            userActions.log(Level.WARNING, "Failed to add patient " + newPatient.getNhiNumber(), "Attempted to add a patient");
            throw new IllegalArgumentException(o.getMessage());
        }
    }

    public void loadAll() {
        loadAllPatients();
        loadAllClinicians();
        loadWaitingList();
    }

    private ArrayList<GlobalEnums.Organ> loadOrgans(String organs) {
        String[] organArray = organs.split(",");
        ArrayList<GlobalEnums.Organ> organArrayList = new ArrayList<>();
        for(String organ : organArray) {
            organArrayList.add(GlobalEnums.Organ.valueOf(organ));
        }
        return organArrayList;
    }

    private Patient parsePatient(String[] attr) {
        String nhi = attr[0];
        String fName = attr[1];
        ArrayList<String> middleNames = new ArrayList<>();
        middleNames.addAll(Arrays.asList(attr[2].split(" ")));
        String lName = attr[3];
        LocalDate birth = LocalDate.parse(attr[4]);
        Patient newPatient = new Patient(nhi, fName, middleNames, lName, birth);
        newPatient.setCREATED(Timestamp.valueOf(attr[5]));
        Timestamp modified = Timestamp.valueOf(attr[6]);
        newPatient.setDeath(LocalDate.parse(attr[7]));
        switch (attr[8]) {
            case "M": newPatient.setGender(GlobalEnums.Gender.MALE);break;
            case "F": newPatient.setGender(GlobalEnums.Gender.FEMALE);break;
            default: newPatient.setGender(GlobalEnums.Gender.OTHER);break;
        }
        //todo: set pref gender and name here after story 29 is in
        newPatient.setHeight(Double.parseDouble(attr[11]) / 100);
        newPatient.setWeight(Double.parseDouble(attr[12]));
        newPatient.setBloodGroup(GlobalEnums.BloodGroup.valueOf(attr[13]));
        newPatient.setDonations(loadOrgans(attr[14]));
        newPatient.setRequiredOrgans(loadOrgans(attr[15]));
        newPatient.setModified(modified);
        
        return newPatient;
    }

    private void loadAllPatients() {
        ArrayList<String[]> patientsRaw = runQuery("SELECT * FROM tblPatients",null);
        for (String[] attr : patientsRaw) {
            patients.add(parsePatient(attr));
        }
    }

    private void loadAllClinicians() {

    }

    private void loadWaitingList() {

    }

////ToDo Local version remove?????
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
     * Adds a clinician to the database
     *
     * @param newClinician the new clinician to add
     */
    public void addClinician(Clinician newClinician) throws IllegalArgumentException {
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
            userActions.log(Level.INFO, "Successfully added clinician " + newClinician.getStaffID(), "Attempted to add a clinician");
        }

        else {
            userActions.log(Level.WARNING, "Couldn't add clinician due to invalid field staffID", "Attempted to add a clinician");
            throw new IllegalArgumentException("staffID");
        }
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
        Database test = Database.getDatabase();
        String stmt = "SELECT * FROM tblPatients WHERE LName = ?";
        String[] params = {"Joeson"};
        ArrayList<String[]> results = test.runQuery(stmt, params);
        for (String[] col: results) {
            System.out.println(String.join(" ", col));
        }
    }
}
