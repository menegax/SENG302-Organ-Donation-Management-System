package utility_test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.Test;

import model.Donor;
import service.Database;
import utility.Search;

public class SearchTest {

//	@Test
//	public void testCreateFullIndex() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testAddIndex() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testCloseIndex() {
//		fail("Not yet implemented");
//	}

	@Test
	public void testSearchByName() {
    	Donor d1 = new Donor("abc1234", "Pat", null, "Laff", LocalDate.now());
    	Donor d2 = new Donor("def1234", "Patik", null, "Laffey", LocalDate.now());
    	Donor d3 = new Donor("ghi1234", "George", null, "Romera", LocalDate.now());
    	Database.addDonor(d3);
    	Database.addDonor(d2);
    	Database.addDonor(d1);
    	try {
			Search.createFullIndex();
			ArrayList<Donor> results = Search.searchByName("Pati Laffe");
			if (results.contains(d1)) {
				results.remove(d1);
				if (results.contains(d2)) {
					results.remove(d2);
					if (!results.isEmpty()) {
						fail();
					}
				} else {
					fail();
				}
			} else {
				fail();
			}
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
	}

}
