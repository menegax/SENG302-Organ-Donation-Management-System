package utility_test;

import model.Patient;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import utility.GlobalEnums.*;
import utility.parsing.ParseCSV;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

public class ParseCSVTest {

    private static ParseCSV parseCSV = new ParseCSV();
    private static String header = "nhi,first_names,last_names,date_of_birth,date_of_death,birth_gender,gender,blood_type," +
            "height,weight,street_number,street_name,neighborhood,city,region,zip_code," +
            "country,birth_country,home_number,mobile_number,email";
    private static Patient patient;


    @BeforeClass
    public static void setup() {
        userActions.setLevel(Level.OFF);
        String patientRow = "\nAAG3309,Rosette,Sivior,08/27/1995,,Female,Female,A+,152,71,306,Kipling," +
                "Santana de Parnaa,Whangarei,Northland,2163,NZ,NZ,03 759 5999,029 260 0739,rsivior0@biglobe.ne.jp\n";

        Map res = parseCSV.parse(new StringReader(header + patientRow));
        Assert.assertEquals(1, ((List)res.get(ParseCSV.Result.SUCCESS)).size()); //check we get a result. Makes easier to debug in regression
        patient = ((Patient)((List)res.get(ParseCSV.Result.SUCCESS)).get(0));
    }

    /**
     * Test exception is thrown when file location is not valid
     * @throws FileNotFoundException - file location invalid
     */
    @Test(expected = FileNotFoundException.class)
    public void testInvalidFilePath() throws FileNotFoundException {
        parseCSV.parse(new FileReader(""));
    }

    /**
     * Verify the number of errors from the read
     * @throws FileNotFoundException - file location invalid
     */
    @Test
    public void testNumberErrors() throws FileNotFoundException {
        Map results = parseCSV.parse(new FileReader("src/test/resources/parsingTestCases/Test_Case_01.csv"));
        Assert.assertEquals(7, ((List) results.get(ParseCSV.Result.FAIL)).size());
    }

    /**
     * Verify the number of success from the read
     * @throws FileNotFoundException - file location invalid
     */
    @Test
    public void testNumberValid() throws FileNotFoundException {
        Map results = parseCSV.parse(new FileReader("src/test/resources/parsingTestCases/Test_Case_01.csv"));
        Assert.assertEquals(22, ((List) results.get(ParseCSV.Result.SUCCESS)).size());
    }

    /**
     * Check enums conversion of a valid row
     */
    @Test
    public void testValidRowEnums(){
        String patientRow = "\nAAG3309,Rosette,Sivior,08/27/1995,,Female,Female,A+,152,71,306,Kipling," +
                "Santana de Parnaa,Whangarei,Northland,2163,NZ,NZ,03 759 5999,029 260 0739,rsivior0@biglobe.ne.jp\n";

        Map res = parseCSV.parse(new StringReader(header + patientRow));
        Assert.assertEquals(BloodGroup.A_POSITIVE, ((Patient)((List)res.get(ParseCSV.Result.SUCCESS)).get(0)).getBloodGroup());
        Assert.assertEquals(Region.NORTHLAND, ((Patient)((List)res.get(ParseCSV.Result.SUCCESS)).get(0)).getRegion());
        Assert.assertEquals(BirthGender.FEMALE, ((Patient)((List)res.get(ParseCSV.Result.SUCCESS)).get(0)).getBirthGender());
        Assert.assertEquals(PreferredGender.WOMAN, ((Patient)((List)res.get(ParseCSV.Result.SUCCESS)).get(0)).getPreferredGender());
    }

    /**
     * Check that a row with invalid blood type is rejected
     */
    @Test
    public void testInvalidBloodType(){
        String patientRow = "\nAAG3309,Rosette,Sivior,08/27/1995,,Female,Female,NOT VALID,152,71,306,Kipling," +
                "Santana de ParnaÃa,Whangarei,Northland,2163,NZ,NZ,03 759 5999,029 260 0739,rsivior0@biglobe.ne.jp\n";

        Map res = parseCSV.parse(new StringReader(header + patientRow));
        hasBeenRejected(res);
    }

    /**
     * Check that a row with an invalid nhi is rejected
     */
    @Test
    public void testInvalidNHI(){
        String patientRow = "\nZZZZZZ,Rosette,Sivior,08/27/1995,,Female,Female,A+,152,71,306,Kipling," +
                "Santana de ParnaÃa,Whangarei,Northland,2163,NZ,NZ,03 759 5999,029 260 0739,rsivior0@biglobe.ne.jp\n";

        Map res = parseCSV.parse(new StringReader(header + patientRow));
        hasBeenRejected(res);
    }

    /**
     * Check that a row with an invalid date of birth is rejected
     */
    @Test
    public void testDateOfBirth(){
        String patientRow = "\nAAG3309,Rosette,Sivior,08/27/1995215515,,Female,Female,A+,152,71,306,Kipling," +
                "Santana de ParnaÃa,Whangarei,Northland,2163,NZ,NZ,03 759 5999,029 260 0739,rsivior0@biglobe.ne.jp\n";

        Map res = parseCSV.parse(new StringReader(header + patientRow));
        hasBeenRejected(res);
    }

    /**
     * Check that a row with an invalid date of death is rejected
     */
    @Test
    public void testDateOfDeath(){
        String patientRow = "\nAAG3309,Rosette,Sivior,08/27/1995, 08-124-1212,Female,Female,A+,152,71,306,Kipling," +
                "Santana de ParnaÃa,Whangarei,Northland,2163,NZ,NZ,03 759 5999,029 260 0739,rsivior0@biglobe.ne.jp\n";

        Map res = parseCSV.parse(new StringReader(header + patientRow));
        hasBeenRejected(res);
    }

    /**
     * Check that a row has all valid contact information set onto patient object
     */
    @Test
    public void testContactDetailsParsed(){
        Assert.assertEquals("03 759 5999", patient.getHomePhone());
        Assert.assertEquals("029 260 0739", patient.getMobilePhone());
        Assert.assertEquals("rsivior0@biglobe.ne.jp", patient.getEmailAddress());
    }

    /**
     * Check that a row has all valid information set onto patient object
     */
    @Test
    public void testAddressParsed() {
        String address = String.format("%s %s %s %s %s", patient.getStreetNumber(), patient.getStreetName(), patient.getCity(), patient.getSuburb(), patient.getRegion());
        Assert.assertEquals("306 Kipling Whangarei Santana de Parnaa Northland", address);
    }

    /**
     * Check that the nhi has been set correctly
     */
    @Test
    public void testNhiParsed() {
        Assert.assertEquals("AAG3309", patient.getNhiNumber());
    }

    /**
     * Check that the names have been set correctly
     */
    @Test
    public void testNameParsed() {
        Assert.assertEquals("Rosette", patient.getFirstName());
        Assert.assertEquals("Sivior", patient.getLastName());
    }

    /**
     * Check that the dates have been set correctly
     */
    @Test
    public void testDatesParsed() {
        Assert.assertEquals("1995-08-27", patient.getBirth().toString());
        Assert.assertNull(patient.getDeath());
    }

    /**
     * Check that the height and weight have been set correctly
     */
    @Test
    public void testPhysicalAttributesParsed() {
        Assert.assertEquals(152, patient.getHeight(), 0);
        Assert.assertEquals(71, patient.getWeight(), 0);
    }

    /**
     * Check that there is one failure/rejected row
     * @param res - results of the parsing
     */
    private void hasBeenRejected(Map res) {
        Assert.assertEquals(0, ((List)res.get(ParseCSV.Result.SUCCESS)).size());
        Assert.assertEquals(1, ((List)res.get(ParseCSV.Result.FAIL)).size());
    }
}
