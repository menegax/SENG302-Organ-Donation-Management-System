package service;
import java.io.InvalidObjectException;
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

    public static Donor getDonorByIrd(int ird) throws InvalidObjectException{
        for (Donor d: getDonors()){
            if (d.getIrdNumber() == ird){
                return d;
            }
        }
        throw new InvalidObjectException("Donor with IRD number " + ird + " does not exist."); //TODO: feel free to create a proper exception for this
    }


}
