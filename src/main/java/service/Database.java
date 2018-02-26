package service;

import java.util.HashSet;

import model.Donor;

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

//    public static int getMaxDonorId(){
//        int maxId = 0;
//        if (getDonors().isEmpty()) return 0; // no donors yet,
//        for (Donor d : getDonors()){
//            if (d.getDonorId() > maxId){
//                maxId = d.getDonorId();
//            }
//        }
//        return maxId;
//    }
//
//    public static Donor getDonorById(int id) throws DonorNotFoundException {
//        for (Donor d : getDonors()){
//            if (d.getDonorId() == id){
//                return d;
//            }
//        }
//        throw new DonorNotFoundException();
//    }

}
