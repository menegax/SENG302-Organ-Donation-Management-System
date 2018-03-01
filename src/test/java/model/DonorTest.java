package model;

import org.joda.time.LocalDate;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class DonorTest {

    @Test
    public void testDonorConstructor() {
        Donor donor = givenDonor();

        thenDonorHasAttributes(donor);
    }

    private void thenDonorHasAttributes(Donor donor) {
        assertTrue(donor.getCREATED() != null);
        assertEquals(donor.getFirstName(), "Bob");
        //TODO add other attributes
    }

    private Donor givenDonor() {
        return new Donor("Bob", null, "Wallace", new LocalDate(1995, 12, 31));
    }

}