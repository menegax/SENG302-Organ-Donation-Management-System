package service;

import com.google.gson.Gson;
import model.Clinician;
import model.Patient;
import utility.GlobalEnums;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.regex.Pattern;

import static utility.UserActionHistory.userActions;

public class Database {

    private static HashSet<Patient> patients = new HashSet<>();
    private static ArrayList<Clinician> clinicians = new ArrayList<>();
    private static OrganWaitlist organWaitingList = new OrganWaitlist();

    public static OrganWaitlist getWaitingList() {
    	return organWaitingList;
    }
    
    public static HashSet<Patient> getPatients() {
        return patients;
    }
    private static ArrayList<Clinician> getClinicians() { return clinicians; }



    /**
     * Adds a patient to the database
     *
     * @param newPatient the new patient to add
     */
    public static void addPatients(Patient newPatient) {
        try {
            newPatient.ensureValidNhi();
            newPatient.ensureUniqueNhi();
            patients.add(newPatient);
            userActions.log(Level.INFO, "Successfully added patient " + newPatient.getNhiNumber(), "attempted to add a patient");
        }
        catch (IllegalArgumentException o) {
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

    public static int addClinician(String firstName, ArrayList<String> middleNames, String lastName, GlobalEnums.Region region) throws IllegalArgumentException {
        return Database.addClinician(firstName, middleNames, lastName, null, null, null, region);
    }

    /**
     * Adds a new clinician to the database. Staff IDs are in increasing integer order
     * @param firstName     Clinicians first name
     * @param middleNames   Clinicians middle names - an arraylist of names
     * @param lastName      Clinicians last name
     * @param street1       Clinicians street1 address
     * @param street2       Clinicians street2 address
     * @param suburb        Clinicians suburb
     * @param region        Clinicians region - using the GlobalEnums.Region enum
     * @return  The staff id of the new clinician
     * @throws IllegalArgumentException If the first name, last name, or street address does not match its required regex
     */
    public static int addClinician(String firstName, ArrayList<String> middleNames, String lastName, String street1, String street2, String suburb, GlobalEnums.Region region) throws IllegalArgumentException {
        int staffID = getNextStaffID();
        if (!Pattern.matches("^[-a-zA-Z]+$", firstName)) throw new IllegalArgumentException("Invalid first name");
        if (!Pattern.matches("^[-a-zA-Z]+$", lastName)) throw new IllegalArgumentException("Invalid last name");
        if (street1 != null && !Pattern.matches("^[- a-zA-Z0-9]+$", street1)) throw new IllegalArgumentException("Invalid street address");

        clinicians.add(new Clinician(staffID, firstName, middleNames, lastName, street1, street2, suburb, region));
        userActions.log(Level.INFO,"Successfully added clinician with id " + staffID, "attempted to add a clinician");
        return staffID;
    }

    private static int getNextStaffID() {
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
            saveToDiskWaitlist();
        }
        catch (IOException e) {
            userActions.log(Level.SEVERE, e.getMessage(), "attempted to save to disk");
        }
    }

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

        String PatientPath = "./";
        Writer writer = new FileWriter(new File(PatientPath, "patient.json"));
        writer.write(json);
        writer.close();
    }


    /**
     * Calls importFromDisk and handles any errors
     *
     * @param fileName the filename of the file to import
     */
    public static void importFromDisk(String directory) {
        try {
            importFromDiskPatients(directory);
            userActions.log(Level.INFO, "Imported donors from disk", "Attempted to import donors from disk");
            importFromDiskWaitlist(directory);
            userActions.log(Level.INFO, "Imported waitlist from disk", "Attempted to import waitlist from disk");
        }
        catch (IOException e) {
            userActions.log(Level.SEVERE, e.getMessage(), "attempted to import from disk");
        }
    }

    private static void importFromDiskWaitlist(String directory) throws FileNotFoundException {
    	String fileName = directory + "waitlist.json";
        Gson gson = new Gson();
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        organWaitingList = gson.fromJson(br, OrganWaitlist.class);
//        Patient[] patients = gson.fromJson(br, Patient[].class);
//        for (Patient p : patients) {
//            Database.addPatients(p);
//        }
    }
    
    /**
     * Reads patient data from disk
     *
     * @exception IOException when the file cannot be found
     */
    private static void importFromDiskPatients(String directory) throws IOException {
    	String fileName = directory + "patient.json";
        Gson gson = new Gson();
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        Patient[] patients = gson.fromJson(br, Patient[].class);
        for (Patient p : patients) {
            Database.addPatients(p);
        }
    }


    /**
     *
     */
    public static void resetDatabase() {
        patients = new HashSet<>();
    }

}
