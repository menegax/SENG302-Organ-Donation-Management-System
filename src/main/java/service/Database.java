package service;

import com.google.gson.Gson;
import model.Clinician;
import model.Donor;
import model.Human;
import model.Receiver;
import utility.GlobalEnums;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.regex.Pattern;

import static utility.UserActionHistory.userActions;

public class Database {

    private static HashSet<Human> donors = new HashSet<>();
    private static HashSet<Human> receivers = new HashSet<>();
    private static ArrayList<Clinician> clinicians = new ArrayList<>();


    public static HashSet<Human> getDonors() {
        return donors;
    }
    public static HashSet<Human> getReceivers() {
        return receivers;
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
        }
        catch (IllegalArgumentException o) {
            throw new IllegalArgumentException(o.getMessage());
        }
    }

    /**
     * Adds a receiver to the database
     *
     * @param newReceiver the new receiver to add
     */
    public static void addReceiver(Receiver newReceiver) {
        try {
            newReceiver.ensureValidNhi();
            newReceiver.ensureUniqueNhi();
            receivers.add(newReceiver);
            userActions.log(Level.INFO, "Successfully added receiver " + newReceiver.getNhiNumber(), "attempted to add a receiver");
        }
        catch (IllegalArgumentException o) {
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
    public static Human getDonorByNhi(String nhi) throws InvalidObjectException {
        for (Human d : getDonors()) {
            if (d.getNhiNumber()
                    .equals(nhi.toUpperCase())) {
                return d;
            }
        }
        for (Human r : getReceivers()) {
            if (r.getNhiNumber()
                    .equals(nhi.toUpperCase())) {
                return r;
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
            saveToDiskHumans(donors, "donor.json");
            saveToDiskHumans(receivers, "receiver.json");
        }
        catch (IOException e) {
            userActions.log(Level.SEVERE, e.getMessage(), "attempted to save to disk");
        }
    }

    /**
     * Writes database receivers or donors to file on disk
     *
     * @exception IOException when the file cannot be found nor created
     */
    private static void saveToDiskHumans(HashSet<Human>  human, String path) throws IOException {
        Gson gson = new Gson();
        String json = gson.toJson(human);

        String humanPath = "./";
        Writer writer = new FileWriter(new File(humanPath, path));
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
            if (fileName.toLowerCase().contains("donor")) {
                donors = new HashSet<>();
                importFromDiskDonors(fileName);
            } else {
                receivers = new HashSet<>();
                importFromDiskReceivers(fileName);
            }
        }
        catch (IOException e) {
            userActions.log(Level.SEVERE, e.getMessage(), "attempted to import from disk");
        }
    }

    /**
     * Reads receiver data from disk
     *
     * @exception IOException when the file cannot be found
     */
    private static void importFromDiskReceivers(String fileName) throws IOException {
        Gson gson = new Gson();
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        Receiver[] receiver = gson.fromJson(br, Receiver[].class);
        for (Receiver r : receiver) {
            Database.addReceiver(r);
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
     *
     */
    public static void resetDatabase() {
        donors = new HashSet<>();
        receivers = new HashSet<>();
    }

}
