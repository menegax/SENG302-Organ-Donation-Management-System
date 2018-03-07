package service;

import model.Donor;
import org.junit.Before;
import org.junit.Test;

import javax.xml.crypto.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

public class DatabaseTest {

//    @Before
//    public void setup() {
//        Database.addDonor(new Donor(1381421476, "Bob", null, "Wallace", LocalDate.of(1995, 12, 31)));
//        Database.addDonor(new Donor(1298325983, "Sheldon", new ArrayList<>(Arrays.asList("The,Heroic".split(" , "))), "Carpenter", LocalDate.of(1900, 1, 1)));
//        Database.addDonor(new Donor(0, "Bill", null, "Jobs", LocalDate.of(1995, 12, 31)));
//    }

    @Test
    public void testSaveToDisk() {
        // todo
    }

    @Test
    public void testSaveToDiskDonors() {
        Database.addDonor(new Donor(1381421476, "Bob", null, "Wallace", LocalDate.of(1995, 12, 31)));
        Database.addDonor(new Donor(1298325983, "Sheldon", new ArrayList<>(Arrays.asList("The,Heroic".split(" , "))), "Carpenter", LocalDate.of(1900, 1, 1)));
        Database.addDonor(new Donor(0, "Bill", null, "Jobs", LocalDate.of(1995, 12, 31)));
        Database.saveToDisk();
        // todo add read JSON method to see what was written
    }

    @Test
    public void getDonorByIrd() {
        //todo
    }

    @Test
    public void testImportFromDisk() {
        // todo
    }

    @Test
    public void testImportFromDiskDonors() {
        // todo
    }
}
