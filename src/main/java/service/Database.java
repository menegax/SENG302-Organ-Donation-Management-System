package service;

import com.google.gson.Gson;
import model.Clinician;
import model.Patient;
import org.omg.CORBA.DynAnyPackage.Invalid;
import utility.GlobalEnums;
import utility.SearchPatients;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.regex.Pattern;

import static utility.UserActionHistory.userActions;

public class Database {

    private static HashSet<Patient> patients = new HashSet<>();
    private static ArrayList<Clinician> clinicians = new ArrayList<>();

    public static HashSet<Patient> getPatients() {
        return patients;
    }
    public static ArrayList<Clinician> getClinicians() { return clinicians; }


    /**
     * Adds a patient to the database
     *
     * @param newPatient the new patient to add
     */
    public static void addPatient(Patient newPatient) {
        try {
            newPatient.ensureValidNhi();
            newPatient.ensureUniqueNhi();
            patients.add(newPatient);
            SearchPatients.addIndex(newPatient);
            userActions.log(Level.INFO,"Successfully added patient " + newPatient.getNhiNumber(), "attempted to add a patient");
        } catch (IllegalArgumentException o) {
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

    /**
     * Adds a clinician to the database
     *
     * @param newClinician the new clinician to add
     */
    public static void addClinician(Clinician newClinician) throws IllegalArgumentException {
        try {
            if (!Pattern.matches("^[-a-zA-Z]+$", newClinician.getFirstName()))
                throw new IllegalArgumentException("firstname");
            if (!Pattern.matches("^[-a-zA-Z]+$", newClinician.getLastName()))
                throw new IllegalArgumentException("lastname");

            if (newClinician.getStreet1() != null && !Pattern.matches("^[- a-zA-Z0-9]+$", newClinician.getStreet1()))
                throw new IllegalArgumentException("street1");
            try {
                clinicians.add(newClinician);
                boolean validStaffId = Database.getClinicianByID(newClinician.getStaffID()) == null;
                userActions.log(Level.INFO, "Successfully added clinician " + newClinician.getStaffID(), "attempted to add a clinician");
            } catch (Exception e) {
                throw new IllegalArgumentException("staffID");
            }
        } catch (IllegalArgumentException e) {
            userActions.log(Level.WARNING, "Couldn't add clinician due to invalid field: " + e.getMessage(), "attempted to add a clinician");
            throw new IllegalArgumentException(e.getMessage());
        }
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
            int currentID = clinicians.get(clinicians.size() - 1).getStaffID();
            return currentID + 1;
        }
    }

    /**
     * Calls all sub-methods to save data to disk
     */
    public static void saveToDisk() {
        try {
            saveToDiskPatients();
            saveToDiskClinicians();
        }
        catch (IOException e) {
            userActions.log(Level.SEVERE, e.getMessage(), "attempted to save to disk");
        }
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
     * Calls importFromDisk and handles any errors
     * @param fileName The file to import from
     */
    public static void importFromDisk(String fileName) {
        try {
            importFromDiskPatients(fileName);
            userActions.log(Level.INFO, "Imported patients from disk", "Attempted to import from disk");
            SearchPatients.createFullIndex();
        }
        catch (IOException e) {
            userActions.log(Level.WARNING, e.getMessage(), "attempted to import from disk");
        }
    }

    /**
     * Reads patient data from disk
     *
     * @exception IOException when the file cannot be found
     */
    private static void importFromDiskPatients(String fileName) throws IOException {
        Gson gson = new Gson();
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        Patient[] patient = gson.fromJson(br, Patient[].class);
        for (Patient p : patient) {
            Database.addPatient(p);
        }
    }


    /**
     * Reads clinician data from disk
     *
     * @throws IOException when the file cannot be found
     */
    public static void importFromDiskClinicians(String fileName) {
        Gson gson = new Gson();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(fileName));
            Clinician[] clinician = gson.fromJson(br, Clinician[].class);
            for (Clinician c : clinician) {
                try {
                    Database.addClinician(c);
                } catch (IllegalArgumentException e) {
                    userActions.log(Level.WARNING, "Error importing clinician from file", "Attempted to import clinicians");
                }
            }
        }
        catch (FileNotFoundException e) {
            userActions.log(Level.WARNING, "Failed to import clinicians", "Attempted to import clinicians");
        }
    }

    /**
     *
     */
    public static void resetDatabase() {
        patients = new HashSet<>();
    }

}
