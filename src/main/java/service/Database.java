package service;

import java.io.InvalidObjectException;
import java.util.HashSet;

import model.Donor;
import org.omg.CORBA.DynAnyPackage.Invalid;

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
        throw new InvalidObjectException("Donor with ird " + ird + " does not exist");
    }


}
