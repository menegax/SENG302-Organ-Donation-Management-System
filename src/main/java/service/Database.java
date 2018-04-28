package service;

import com.google.gson.Gson;
import model.Clinician;
import model.Donor;
import utility.GlobalEnums;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.regex.Pattern;

import static utility.UserActionHistory.userActions;

public class Database {

    private static HashSet<Donor> donors = new HashSet<>();
    private static ArrayList<Clinician> clinicians = new ArrayList<>();

    public static HashSet<Donor> getDonors() {
        return donors;
    }
    public static ArrayList<Clinician> getClinicians() { return clinicians; }

    /**
     * Adds a donor to the database
     *
     * @param newDonor the new donor to add
     */
    public static void addDonor(Donor newDonor) {
        try {
            newDonor.ensureValidNhi();
            newDonor.ensureUniqueNhi();
            donors.add(newDonor);
            userActions.log(Level.INFO, "Successfully added donor " + newDonor.getNhiNumber(), "attempted to add a donor");
        } catch (IllegalArgumentException o) {
            throw new IllegalArgumentException(o.getMessage());
        }
    }

    /**
     * Removes a donor from the database
     *
     * @param nhi the nhi to search donors by
     * @exception InvalidObjectException when the object cannot be found
     */
    public static void removeDonor(String nhi) throws InvalidObjectException {
        donors.remove(Database.getDonorByNhi(nhi));
        userActions.log(Level.INFO, "Successfully removed donor " + nhi, "attempted to remove a donor");
    }


    /**
     * Searches donors by nhi
     *
     * @param nhi the nhi to search donors by
     * @return Donor object
     *
     * @exception InvalidObjectException when the object cannot be found
     */
    public static Donor getDonorByNhi(String nhi) throws InvalidObjectException {
        for (Donor d : getDonors()) {
            if (d.getNhiNumber()
                    .equals(nhi.toUpperCase())) {
                return d;
            }
        }
        throw new InvalidObjectException("Donor with NHI number " + nhi + " does not exist.");
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

            if (newClinician.getStaffID() == Database.getNextStaffID()) {
                clinicians.add(newClinician);
                userActions.log(Level.INFO, "Successfully added clinician " + newClinician.getStaffID(), "attempted to add a clinician");
            } else {
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
            saveToDiskDonors();
            saveToDiskClinicians();
        }
        catch (IOException e) {
            userActions.log(Level.SEVERE, e.getMessage(), "attempted to save to disk");
        }
    }


    /**
     * Writes database donors to file on disk
     *
     * @exception IOException when the file cannot be found nor created
     */
    private static void saveToDiskDonors() throws IOException {
        Gson gson = new Gson();
        String json = gson.toJson(donors);

        String donorPath = "./";
        Writer writer = new FileWriter(new File(donorPath, "donor.json"));
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
     *
     * @param fileName the filename of the file to import
     */
    public static void importFromDisk(String fileName) {
        try {
            donors = new HashSet<>();
            importFromDiskDonors(fileName);
        }
        catch (IOException e) {
            userActions.log(Level.SEVERE, e.getMessage(), "attempted to import from disk");
        }
    }


    /**
     * Reads donor data from disk
     *
     * @exception IOException when the file cannot be found
     */
    private static void importFromDiskDonors(String fileName) throws IOException {
        Gson gson = new Gson();
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        Donor[] donor = gson.fromJson(br, Donor[].class);
        for (Donor d : donor) {
            Database.addDonor(d);
        }
    }


    /**
     * Reads clinician data from disk
     *
     * @throws IOException when the file cannot be found
     */
    public static void importFromDiskClinicians(String fileName) throws IOException {
        Gson gson = new Gson();
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        Clinician[] clinician = gson.fromJson(br, Clinician[].class);
        for (Clinician c : clinician) {
            try {
                Database.addClinician(c);
            } catch (IllegalArgumentException e) {
                userActions.log(Level.WARNING, "Error importing clinician from file");
            }
        }
    }

    /**
     *
     */
    public static void resetDatabase() {
        donors = new HashSet<>();
    }

}
