package service;

import java.io.*;
import java.nio.file.Path;
import java.io.*;
import java.util.HashSet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Donor;

public class Database {

    private static String donorPath = "./";

    private static HashSet<Donor> donors = new HashSet<>();

    public static HashSet<Donor> getDonors() {
        return donors;
    }

    public static void addDonor(Donor newDonor) {
        donors.add(newDonor);
    }

    public static void removeDonor(int ird) throws InvalidObjectException {
        donors.remove(Database.getDonorByIrd(ird));
    }

    public static Donor getDonorByIrd(int ird) throws InvalidObjectException {
        for (Donor d : getDonors()) {
            if (d.getIrdNumber() == ird) {
                return d;
            }
        }
        throw new InvalidObjectException("Donor with IRD number " + ird + " does not exist.");
    }

    /**
     * Calls all sub-methods to save data to disk
     */
    public static void saveToDisk() {
        try {
            saveToDiskDonors();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes database donors to file on disk
     *
     * @throws IOException
     */
    private static void saveToDiskDonors() throws IOException {
        Gson gson = new Gson();
        String json = gson.toJson(donors);

        Writer writer = new FileWriter(new File(donorPath, "donor.json")); //todo change path to /team-27/data/
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
            System.out.println(e.getMessage());
        }
    }

    /**
     * Reads donor data from disk
     *
     * @throws IOException
     */
    private static void importFromDiskDonors(String fileName) throws IOException {
        Gson gson = new Gson();
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            Donor[] donor = gson.fromJson(br, Donor[].class);
            for (Donor d : donor) Database.addDonor(d);
        } catch (IOException exception) {
            throw exception;
        }


    }
}
