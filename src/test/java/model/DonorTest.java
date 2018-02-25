package model;

import org.joda.time.LocalDate;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class DonorTest {

    @Test
    public void testDonorConstructor() {
        Donor donor = givenDonor();

        thenDonorHasAttributes(donor);
        }

    private void thenDonorHasAttributes(Donor donor) {
        assertTrue(donor.getUUID() != null);
        assertTrue(donor.getCREATED() != null);
        assertEquals(donor.getFirstName(), "Bob");
    }

    private Donor givenDonor() {
        return new Donor("Bob", new ArrayList<>(), "Wallace", new LocalDate(2014, 9, 6));
    }

}