package service;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;

import com.google.gson.Gson;
import model.Donor;

public class Database {

    private static HashSet<Donor> donors = new HashSet<>();

    public static HashSet<Donor> getDonors() {
        return donors;
    }

    public static void addDonor(Donor newDonor) {
        donors.add(newDonor);
    }

    public static Donor getDonorByIrd(int ird) throws InvalidObjectException {
        for (Donor d : getDonors()) {
            if (d.getIrdNumber() == ird) {
                return d;
            }
        }
        throw new InvalidObjectException("Donor with IRD number " + ird + " does not exist.");
    }

    public static void saveToDisk() {
        try {
            saveToDiskDonors();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveToDiskDonors() throws IOException {
        // todo add tests
        // todo ensure human readability of .txt file
        // todo change file location

        String json = new Gson().toJson(donors);

        String text = "Text to save to file";
        Files.write(Paths.get("./donor.txt"), text.getBytes());
    }

}
