package service;

import com.google.gson.Gson;
import model.Administrator;
import model.Clinician;
import model.Patient;
import utility.Searcher;

import java.io.*;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Pattern;

import static utility.SystemLogger.systemLogger;
import static utility.UserActionHistory.userActions;

public class Database {

    private static OrganWaitlist organWaitingList = new OrganWaitlist();

    public static OrganWaitlist getWaitingList() {
    	return organWaitingList;
    }

    private static Set<Patient> patients = new HashSet<>();

    private static Set<Clinician> clinicians = new HashSet<>();

    private static Set<Administrator> administrators = new HashSet<>();

    private static Searcher searcher = Searcher.getSearcher();

    /**
     * Adds a patient to the database
     *
     * @param newPatient the new patient to add
     */
    public static void addPatient(Patient newPatient) throws IllegalArgumentException {
        try {
            newPatient.ensureValidNhi();
            newPatient.ensureUniqueNhi();
            patients.add(newPatient);
            searcher.addIndex(newPatient);
            userActions.log(Level.INFO, "Successfully added patient " + newPatient.getNhiNumber(), "Attempted to add a patient");
        }
        catch (IllegalArgumentException o) {
            userActions.log(Level.WARNING, "Failed to add patient " + newPatient.getNhiNumber(), "Attempted to add a patient");
            throw new IllegalArgumentException(o.getMessage());
        }
    }

    /**
     * Removes a patient from the database
     *
     * @param nhi the nhi to search patients by
     * @exception InvalidObjectException when the object cannot be found
     */
    public static void removePatient(String nhi) throws InvalidObjectException {
        patients.remove(Database.getPatientByNhi(nhi));
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
    public static Patient getPatientByNhi(String nhi) throws InvalidObjectException {
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
    public static boolean isPatientInDb(String nhi) {
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
    public static boolean isClinicianInDb(int staffID) {
        for (Clinician c : getClinicians()) {
            if (c.getStaffID() == staffID) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if an administrator with the given username exists in the database
     *
     * @param username the username of the administrator to search for
     * @return true if exists else false
     */
    public static boolean isAdministratorInDb(String username) {
        for (Administrator a : getAdministrators()) {
            if (a.getUsername().equals(username.toUpperCase())) {
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
     * @throws InvalidObjectException when the object cannot be found
     */
    public static Clinician getClinicianByID(int staffID) throws InvalidObjectException {
        for (Clinician c : getClinicians()) {
            if (c.getStaffID() == staffID) {
                return c;
            }
        }
        throw new InvalidObjectException("Clinician with staff ID number " + staffID + " does not exist.");
    }

    public static Administrator getAdministratorByUsername(String username) throws InvalidObjectException {
        for (Administrator a : getAdministrators()) {
            if (a.getUsername().equals(username)) {
                return a;
            }
        }
        throw new InvalidObjectException("Administrator with username " + " does not exist");
    }

    /**
     * Adds a clinician to the database
     *
     * @param newClinician the new clinician to add
     */
    public static void addClinician(Clinician newClinician) throws IllegalArgumentException {
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

        if (newClinician.getStaffID() == Database.getNextStaffID()) {
            clinicians.add(newClinician);
            searcher.addIndex(newClinician);
            userActions.log(Level.INFO, "Successfully added clinician " + newClinician.getStaffID(), "Attempted to add a clinician");
        }

        else {
            userActions.log(Level.WARNING, "Couldn't add clinician due to invalid field staffID", "Attempted to add a clinician");
            throw new IllegalArgumentException("staffID");
        }
    }

    public static void addAdministrator(Administrator administrator) throws IllegalArgumentException {
        if (!Pattern.matches("^[-a-zA-Z]+$", administrator.getFirstName())) {
            userActions.log(Level.WARNING, "Couldn't add administrator due to invalid field: first name", "Attempted to add a administrator");
            throw new IllegalArgumentException("firstname");
        }

        if (!Pattern.matches("^[-a-zA-Z]+$", administrator.getLastName())) {
            userActions.log(Level.WARNING, "Couldn't add administrator due to invalid field: last name", "Attempted to add an administrator");
            throw new IllegalArgumentException("lastname");
        }

        for (Administrator admin : administrators) {
            if (admin.getUsername().toLowerCase().equals(administrator.getUsername().toLowerCase())) {
                userActions.log(Level.WARNING, "Couldn't add administrator due to invalid field username", "Attempted to add an administrator");
                throw new IllegalArgumentException("admin username");
            }
        }
        administrators.add(administrator);
        userActions.log(Level.INFO, "Successfully added administrator " + administrator.getUsername(), "Attempted to add an administrator");
    }

    /**
     * Returns the next valid staffID based on IDs in the clinician list
     *
     * @return the valid id
     */
    public static int getNextStaffID() {
        if (clinicians.size() == 0) {
            return 0;
        } else {
            int currentID = clinicians.stream()
                    .max( Comparator.comparing( Clinician::getStaffID ) )
                    .get()
                    .getStaffID();
            return currentID + 1;
        }
    }

    public static boolean usernameUsed(String username) {
    	username = username.toUpperCase();
    	boolean exisits = false;
    	for (Administrator admin: getAdministrators()) {
    		if (admin.getUsername().equals(username)) {
    			exisits = true;
    		}
    	}
    	return exisits;
    }
    
    /**
     * Calls all sub-methods to save data to disk
     */
    public static void saveToDisk() {
        try {
            saveToDiskPatients();
            saveToDiskWaitlist();
            saveToDiskClinicians();
            saveToDiskAdministrators();
        } catch (IOException e) {
            userActions.log(Level.SEVERE, e.getMessage(), "attempted to save to disk");
        }
    }

    /**
     * Saves the organ waitlist to the file waitlist.json
     * @throws IOException the file cannot be found or created
     */
    private static void saveToDiskWaitlist() throws IOException {
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
    private static void saveToDiskPatients() throws IOException {
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
     * @throws IOException when the file cannot be found nor created
     */
    private static void saveToDiskClinicians() throws IOException {
        Gson gson = new Gson();
        String json = gson.toJson(clinicians);

        String clinicianPath = "./";
        Writer writer = new FileWriter(new File(clinicianPath, "clinician.json"));
        writer.write(json);
        writer.close();
    }

    /**
     * Writes database administrators to file on disk
     *
     * @throws IOException when the file cannot be found nor created
     */
    private static void saveToDiskAdministrators() throws IOException {
        Gson gson = new Gson();
        String json = gson.toJson(administrators);

        String adminPath = "./";
        Writer writer = new FileWriter(new File(adminPath, "administrator.json"));
        writer.write(json);
        writer.close();
    }

    /**
     * Reads patient data from disk
     * @param fileName file to import from
     */
    public static void importFromDiskPatients(String fileName) {
        Gson gson = new Gson();
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(fileName));
            Patient[] patient = gson.fromJson(br, Patient[].class);
            for (Patient d : patient) {
                try {
                    Database.addPatient(d);
                }
                catch (IllegalArgumentException e) {
                    userActions.log(Level.WARNING, "Error importing donor from file", "Attempted to import donor from file");
                }
            }
            systemLogger.log(Level.INFO, "Successfully imported patients from file");
        }
        catch (FileNotFoundException e) {
            userActions.log(Level.WARNING, "Patient import file not found", "Attempted to read patient file");
        }
        catch (Exception e) {
            userActions.log(Level.WARNING, "Failed to import patients from file", "Attempted to read patient file");
        }

    }

    /**
     * Reads clinician data from disk
     * @param fileName file to import from
     */
    public static void importFromDiskClinicians(String fileName) {
        Gson gson = new Gson();
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(fileName));
            Clinician[] clinician = gson.fromJson(br, Clinician[].class);
            for (Clinician c : clinician) {
                try {
                    Database.addClinician(c);
                } catch (IllegalArgumentException e) {
                    userActions.log(Level.WARNING, "Error importing clinician from file", "Attempted to import clinician from file");
                }
            }
            systemLogger.log(Level.INFO, "Successfully imported clinician from file");
        }
        catch (FileNotFoundException e) {
            userActions.log(Level.WARNING, "Clinician import file not found", "Attempted to read clinician file");
        }
        catch (Exception e) {
            userActions.log(Level.WARNING, "Failed to import clinicians from file", "Attempted to read clinician file");
        }

    }

    /**
     * Reads administrator data from disk
     * @param fileName file to import from
     */
    public static void importFromDiskAdministrators(String fileName) {
        Gson gson = new Gson();
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(fileName));
            Administrator[] administrators = gson.fromJson(br, Administrator[].class);
            for (Administrator a : administrators) {
                try {
                    Database.addAdministrator(a);
                } catch (IllegalArgumentException e) {
                    userActions.log(Level.WARNING, "Error importing administrator from file", "Attempted to import administrator from file");
                }
            }
        }
        catch (FileNotFoundException e) {
            userActions.log(Level.WARNING, "Administrator import file not found", "Attempted to read administrator file");
        }
        catch (Exception e) {
            userActions.log(Level.WARNING, "Failed to import administrators from file", "Attempted to read administrator file");
        }
    }

    /**
     * Imports the organ waitlist from the selected directory
     * @param filename file to import from
     */
    public static void importFromDiskWaitlist(String filename) {
        Gson gson = new Gson();
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(filename));
            organWaitingList = gson.fromJson(br, OrganWaitlist.class);
            systemLogger.log(Level.INFO, "Successfully imported organ waiting list from file");
        }
        catch (FileNotFoundException e) {
            userActions.log(Level.WARNING, "Waitlist import file not found", "Attempted to read waitlist file");
        }
        catch (Exception e) {
            userActions.log(Level.WARNING, "Failed to import from waitlist file", "Attempted to read watilist file");
        }

    }

    /**
     * Removes from the clinicians HashSet the given clinician
     * @param clinician The clinician being removed from set
     */
    public static void deleteClinician(Clinician clinician) {
        if (clinician.getStaffID() != 0) {
            searcher.removeIndex(clinician);
            clinicians.remove(clinician);
        }
    }

    /**
     * Removes from the patients HashSet the given patient
     * @param patient The patient being removed from set
     */
    public static void deletePatient(Patient patient) {
        searcher.removeIndex(patient);
        patients.remove( patient );
    }

    /**
     * Removes from the administrators HashSet the given administrator
     * @param administrator The administrator being removed from set
     */
    public static void deleteAdministrator(Administrator administrator) {
        if (!administrator.getUsername().toLowerCase().equals("admin")) {
            searcher.removeIndex(administrator);
            administrators.remove(administrator);
        }
    }

    /**
     * Clears the database of all patients
     */
    public static void resetDatabase() {
        patients = new HashSet<>();
        clinicians = new HashSet<>();
    }



    public static Set<Patient> getPatients() {
        return patients;
    }

    public static Set<Clinician> getClinicians() {
        return clinicians;
    }

    public static Set<Administrator> getAdministrators() { return administrators; }
}
