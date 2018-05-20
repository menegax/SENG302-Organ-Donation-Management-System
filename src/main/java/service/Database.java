package service;

import com.google.gson.Gson;
import model.Clinician;
import model.Patient;
import org.omg.CORBA.DynAnyPackage.Invalid;
import utility.GlobalEnums;
import utility.SearchPatients;

import java.io.*;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Pattern;


import static utility.UserActionHistory.userActions;

public class Database {

    private Set<Patient> patients;

    private Set<Clinician> clinicians;

    private OrganWaitlist organWaitingList;

    private static Database database = null;

    private Database() {
        patients = new HashSet<>();
        clinicians = new HashSet<>();
        organWaitingList = new OrganWaitlist();
    }

    public static Database getDatabase() {
        if (database == null) {
            database = new Database();
        }
        return database;
    }

    //ToDO Complete
    public void runQuery(String query) {
        String type = query.split(" ")[0].toUpperCase();
        if (type.equals("SELECT")) {

        }
        else if (type.equals("UPDATE")) {

        }
//        else if (type.equals("INSERT")) {
//
//        }
    }

    //ToDo Complete
    private void runSelectQuery(String query) {

    }

    //ToDo Complete
    public void add(Object object) {
        if (object instanceof Patient) {
            addPatient((Patient) object);
        }
        else if (object instanceof Clinician) {

        }
    }

    private void runInsertQuery(String query, String[] params) {

    }

    public OrganWaitlist getWaitingList() {
        return organWaitingList;
    }

    private String[] getPatientAttributes(Patient patient) {
        String[] attr = new String[16];
        attr[0] = patient.getNhiNumber();
        attr[1] = patient.getFirstName();
        attr[2] = String.join(" ", patient.getMiddleNames());
        attr[3] = patient.getLastName();
        attr[4] = patient.getBirth().toString();
        attr[5] = patient.getCREATED().toString();
        attr[5] = patient.getModified().toString();
        attr[5] = patient.getDeath().toString();
        attr[5] = patient.getGender().toString().substring(0,1);
        attr[5] = null;
        attr[5] = null;
        attr[5] = String.valueOf(patient.getHeight());
        attr[5] = String.valueOf(patient.getWeight());
        attr[5] = patient.getBloodGroup().toString();


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
            String query = "INSERT INTO tblPatients" +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            runInsertQuery(query, attr);
            userActions.log(Level.INFO, "Successfully added patient " + newPatient.getNhiNumber(), "Attempted to add a patient");
        }
        catch (IllegalArgumentException o) {
            userActions.log(Level.WARNING, "Failed to add patient " + newPatient.getNhiNumber(), "Attempted to add a patient");
            throw new IllegalArgumentException(o.getMessage());
        }
    }

//ToDo Local version remove?????
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
     * Writes database clinicians to file on disk
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
}
