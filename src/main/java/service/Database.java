package service;

import java.util.HashSet;

import model_test.Donor;

public class Database {

    private static HashSet<Donor> donors = new HashSet<>();

    public static HashSet<Donor> getDonors() {
        return donors;
    }

    public static void addDonor(Donor newDonor) {
        donors.add(newDonor);
    }

    public static void removeDonor(Donor donor) {
        donors.remove(donor);
    }

    public static void updateDonor(Donor newDonor) {
        removeDonor(newDonor);
        addDonor(newDonor);
    }

}