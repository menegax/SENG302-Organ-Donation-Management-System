package service;

import java.util.HashSet;

import model.Donor;
//TODO: need to decide if this will be static..

public class Database {

    private HashSet<Donor> donors;

    public Database(){
        donors = new HashSet<>();
    }


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
