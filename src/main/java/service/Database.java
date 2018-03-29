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
    private static HashSet<Clinician> clinicians = new HashSet<>();

    public static HashSet<Donor> getDonors() {
        return donors;
    }
    public static HashSet<Clinician> getClinicians() { return clinicians; }

    /**
     * Adds a donor to the database
     * @param newDonor the new donor to add
     */
    public static void addDonor(Donor newDonor) {
        try {
            newDonor.ensureValidNhi();
            newDonor.ensureUniqueNhi();
            donors.add(newDonor);
            userActions.log(Level.INFO,"Successfully added donor " + newDonor.getNhiNumber(), "attempted to add a donor");
        } catch (IllegalArgumentException o) {
            throw new IllegalArgumentException(o.getMessage());
        }
    }

    /**
     * Removes a donor from the database
     *
     * @param nhi the nhi to search donors by
     * @throws InvalidObjectException when the object cannot be found
     */
    public static void removeDonor(String nhi) throws InvalidObjectException {
        donors.remove(Database.getDonorByNhi(nhi));
        userActions.log(Level.INFO,"Successfully removed donor " + nhi, "attempted to remove a donor");
    }

    /**
     * Searches donors by nhi
     *
     * @param nhi the nhi to search donors by
     * @return Donor object
     * @throws InvalidObjectException when the object cannot be found
     */
    public static Donor getDonorByNhi(String nhi) throws InvalidObjectException {
        for (Donor d : getDonors()) {
            if (d.getNhiNumber().equals(nhi.toUpperCase())) {
                return d;
            }
        }
        throw new InvalidObjectException("Donor with NHI number " + nhi + " does not exist.");
    }

    public static void addClinician(int staffID, String firstName, ArrayList<String> middleNames, String lastName, GlobalEnums.Region region) throws IllegalArgumentException {
        Database.addClinician(staffID, firstName, middleNames, lastName, null, null, null, region);
    }

    public static void addClinician(int staffID, String firstName, ArrayList<String> middleNames, String lastName, String street1, String street2, String suburb, GlobalEnums.Region region) throws IllegalArgumentException {
        if (!isUniqueStaffID(staffID)) throw new IllegalArgumentException("Clinician with staff ID " + staffID + " already exists");
        if (!Pattern.matches("^[-a-zA-Z]+$", firstName)) throw new IllegalArgumentException("Invalid first name");
        if (!Pattern.matches("^[-a-zA-Z]+$", lastName)) throw new IllegalArgumentException("Invalid last name");
        if (street1 != null && !Pattern.matches("^[- a-zA-Z0-9]+$", street1)) throw new IllegalArgumentException("Invalid street address");

        clinicians.add(new Clinician(staffID, firstName, middleNames, lastName, street1, street2, suburb, region));
    }

    private static boolean isUniqueStaffID(int id) {
        for (Clinician clinician : clinicians) {
            if (clinician.getStaffID() == id) {
                return false;
            }
        }
        return true;
    }

    /**
     * Calls all sub-methods to save data to disk
     */
    public static void saveToDisk() {
        try {
            saveToDiskDonors();
        } catch (IOException e) {
            userActions.log(Level.SEVERE, e.getMessage(), "attempted to save to disk");
        }
    }

    /**
     * Writes database donors to file on disk
     *
     * @throws IOException when the file cannot be found nor created
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
     * Calls importFromDisk and handles any errors
     */
    public static void importFromDisk(String fileName) {
        try {
            importFromDiskDonors(fileName);
        } catch (IOException e) {
            userActions.log(Level.SEVERE, e.getMessage(), "attempted to import from disk");
        }
    }

    /**
     * Reads donor data from disk
     *
     * @throws IOException when the file cannot be found
     */
    private static void importFromDiskDonors(String fileName) throws IOException {
        Gson gson = new Gson();
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        Donor[] donor = gson.fromJson(br, Donor[].class);
        for (Donor d : donor) Database.addDonor(d);
    }

    /**
     *
     */
    public static void resetDatabase(){
        donors = new HashSet<>();
    }

}
