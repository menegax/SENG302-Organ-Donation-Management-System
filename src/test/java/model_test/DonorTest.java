package model_test;

import java.time.LocalDate;

import model.Donor;
import org.junit.Test;

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
        return new Donor("Bob", null, "Wallace", LocalDate.of(1995, 12, 31));
    }

}