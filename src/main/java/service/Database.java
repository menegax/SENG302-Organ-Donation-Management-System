package service;

import com.google.gson.Gson;
import model.Donor;

import java.io.*;
import java.util.HashSet;
import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

public class Database {

    private static HashSet<Donor> donors = new HashSet<>();

    public static HashSet<Donor> getDonors() {
        return donors;
    }

    /**
     * Adds a donor to the database
     * @param newDonor the new donor to add
     */
    public static void addDonor(Donor newDonor) { //TODO: needs to throw to another layer
        try {
            newDonor.ensureValidNhi();
            newDonor.ensureUniqueNhi();
            donors.add(newDonor);
            userActions.log(Level.INFO,"Successfully added " + newDonor);
        } catch (IllegalArgumentException o) {
            userActions.log(Level.SEVERE, o.getMessage());
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

    /**
     * Calls all sub-methods to save data to disk
     */
    public static void saveToDisk() {
        try {
            saveToDiskDonors();
        } catch (IOException e) {
            userActions.log(Level.SEVERE, e.getMessage());
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
            userActions.log(Level.SEVERE, e.getMessage());
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
