package service;

import java.util.HashSet;

import model.Donor;

public class Database {

    private HashSet<Donor> donors;


    public HashSet<Donor> getDonors() {
        return donors;
    }

    public void addDonor(Donor newDonor) {
        donors.add(newDonor);
    }

    public void removeDonor(Donor donor) {
        donors.remove(donor);
    }

    public void updateDonor(Donor newDonor) {
        removeDonor(newDonor);
        addDonor(newDonor);
    }


}
