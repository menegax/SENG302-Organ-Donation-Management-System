package controller_component_test;

import static java.util.logging.Level.OFF;
import static org.assertj.core.api.Assertions.assertThat;
import static utility.UserActionHistory.userActions;

import controller.Main;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Patient;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import service.Database;

import java.io.InvalidObjectException;
import java.time.LocalDate;
import java.util.ArrayList;

@Ignore //todo
public class GUIPatientUpdateProfileTest extends ApplicationTest {

    private Main main = new Main();

    private int patientUpdateProfileTabIndex = 1;

    private Patient existingPatient1 = new Patient("TFX9999", "Joe", null, "Bloggs", LocalDate.of(1990, 2, 9));

    private String existingNhi1 = existingPatient1.getNhiNumber();


    @Override
    public void start(Stage stage) throws Exception {
        main.start(stage);
    }

    @Before
    public void setUp() {
        Database.resetDatabase();
        Database.addPatient(existingPatient1);

        //Log in and navigate to profile update
        interact(() -> {
            lookup("#clinicianToggle").queryAs(CheckBox.class)
                    .setSelected(false);
            lookup("#nhiLogin").queryAs(TextField.class)
                    .setText(String.valueOf(existingNhi1));
            lookup("#loginButton").queryButton()
                    .fire();
            lookup("#horizontalTabPane").queryAs(TabPane.class)
                    .getSelectionModel()
                    .clearAndSelect(patientUpdateProfileTabIndex);
        });
    }

    /**
     * Turn off logging
     */
    @BeforeClass
    public static void setUpClass() {
        Database.resetDatabase();
        userActions.setLevel(OFF);
    }


    /**
     * Checks that a patients NHI cannot be updated to one with an invalid format
     */
    @Test
    public void testInvalidNhi() throws InvalidObjectException {
        givenNhi("999abcd");
        whenSave();
        thenDBPatientHasNhi(existingNhi1);
    }


    /**
     * Checks that a patients NHI cannot be updated to an existing one
     */
    @Test
    public void testDuplicateNhi() throws InvalidObjectException {

        // add second patient to db
        Database.addPatient(new Patient("TFX9998", "Jane", null, "ZEreraeDA", LocalDate.of(1876, 12, 31)));

        givenNhi("TFX998");
        whenSave();
        thenDBPatientHasNhi(existingNhi1);

    }

    @Test
    public void testValidNhi() throws InvalidObjectException {
        givenNhi("TFX1111");
        whenSave();
        thenDBPatientHasNhi("TFX1111");
    }

    @Test
    public void testValidFirstName() throws InvalidObjectException {
        givenFirstName("Andrew");
        whenSave();
        thenDBPatientHasFirstName("Andrew");

        givenFirstName("AndreW ");
        whenSave();
        thenDBPatientHasFirstName("Andrew");
    }

    @Test
    public void testInvalidFirstName() throws InvalidObjectException {
        givenFirstName("!");
        whenSave();
        thenDBPatientHasFirstName(existingPatient1.getFirstName());

        givenFirstName("@");
        whenSave();
        thenDBPatientHasFirstName(existingPatient1.getFirstName());

        givenFirstName("#");
        whenSave();
        thenDBPatientHasFirstName(existingPatient1.getFirstName());

        givenFirstName("$");
        whenSave();
        thenDBPatientHasFirstName(existingPatient1.getFirstName());

        givenFirstName("%");
        whenSave();
        thenDBPatientHasFirstName(existingPatient1.getFirstName());

        givenFirstName("^");
        whenSave();
        thenDBPatientHasFirstName(existingPatient1.getFirstName());

        givenFirstName("&");
        whenSave();
        thenDBPatientHasFirstName(existingPatient1.getFirstName());

        givenFirstName("*");
        whenSave();
        thenDBPatientHasFirstName(existingPatient1.getFirstName());
    }

    @Test
    public void testValidMiddleNames() throws InvalidObjectException {

        ArrayList<String> newMiddles = new ArrayList<>();
        newMiddles.add("Phil");
        newMiddles.add("Schiller");

        givenMiddleNames("Phil Schiller");
        whenSave();
        thenDBPatientHasMiddleNames(newMiddles);
    }

    @Test
    public void testInvalidMiddleNames() throws InvalidObjectException {

        ArrayList<String> existingMiddles = existingPatient1.getMiddleNames();

        givenMiddleNames(" @     $");
        whenSave();
        thenDBPatientHasMiddleNames(existingMiddles);
    }

    @Test
    public void testValidLastName() throws InvalidObjectException {
        givenLastName("Spearman");
        whenSave();
        thenDBPatientHasLastName("Spearman");
    }

    @Test
    public void testInvalidLastName() throws InvalidObjectException {

        givenLastName("!");
        whenSave();
        thenDBPatientHasLastName(existingPatient1.getLastName());

        givenLastName("@");
        whenSave();
        thenDBPatientHasLastName(existingPatient1.getLastName());

        givenLastName("#");
        whenSave();
        thenDBPatientHasLastName(existingPatient1.getLastName());

        givenLastName("$");
        whenSave();
        thenDBPatientHasLastName(existingPatient1.getLastName());

        givenLastName("%");
        whenSave();
        thenDBPatientHasLastName(existingPatient1.getLastName());

        givenLastName("^");
        whenSave();
        thenDBPatientHasLastName(existingPatient1.getLastName());

        givenLastName("&");
        whenSave();
        thenDBPatientHasLastName(existingPatient1.getLastName());

        givenLastName("*");
        whenSave();
        thenDBPatientHasLastName(existingPatient1.getLastName());

        givenLastName("()");
        whenSave();
        thenDBPatientHasLastName(existingPatient1.getLastName());

        givenLastName("_+{}-=[]\\|';:\"/.,<>?");
        whenSave();
        thenDBPatientHasLastName(existingPatient1.getLastName());
    }

    @Test
    public void testValidDeath() throws InvalidObjectException {
        givenDeath(LocalDate.now().minusDays(3));
        whenSave();
        thenDBPatientHasDeath(LocalDate.now().minusDays(3));
    }

    @Test
    public void testInvalidDeath() throws InvalidObjectException {

        LocalDate existingDeath = existingPatient1.getDeath();

        givenDeath(LocalDate.now().plusDays(1));
        whenSave();
        thenDBPatientHasDeath(existingDeath);

        givenDeath(LocalDate.now().plusYears(200));
        whenSave();
        thenDBPatientHasDeath(existingDeath);
    }

    @Test
    public void testValidBirth() throws InvalidObjectException {
        givenBirth(LocalDate.now());
        whenSave();
        thenDBPatientHasBirth(LocalDate.now());
    }

    @Test
    public void testInvalidBirth() throws InvalidObjectException {

        LocalDate existingBirth = existingPatient1.getBirth();

        givenBirth(LocalDate.now().plusDays(1));
        whenSave();
        thenDBPatientHasBirth(existingBirth);

        givenBirth(LocalDate.now().plusMonths(9));
        whenSave();
        thenDBPatientHasBirth(existingBirth);

        givenBirth(LocalDate.now().plusYears(12));
        whenSave();
        thenDBPatientHasBirth(existingBirth);
    }

    @Test
    public void testValidPreferredName() throws InvalidObjectException {
        givenPreferredName("Andy");
        whenSave();
        thenDBPatientHasPreferredtName("Andy");

        givenPreferredName("AnD y ");
        whenSave();
        thenDBPatientHasPreferredtName("Andy");

        givenPreferredName("SummEr");
        whenSave();
        thenDBPatientHasPreferredtName("Summer");
    }

    @Test
    public void testInvalidPreferredName() throws InvalidObjectException {

        String existingPreferredName = existingPatient1.getPreferredName();

        givenPreferredName("!");
        whenSave();
        thenDBPatientHasFirstName(existingPreferredName);

        givenPreferredName("@");
        whenSave();
        thenDBPatientHasFirstName(existingPreferredName);

        givenPreferredName("#");
        whenSave();
        thenDBPatientHasFirstName(existingPreferredName);

        givenPreferredName("$");
        whenSave();
        thenDBPatientHasFirstName(existingPreferredName);

        givenPreferredName("%");
        whenSave();
        thenDBPatientHasFirstName(existingPreferredName);

        givenPreferredName("^");
        whenSave();
        thenDBPatientHasFirstName(existingPreferredName);

        givenPreferredName("&");
        whenSave();
        thenDBPatientHasFirstName(existingPreferredName);

        givenPreferredName("*");
        whenSave();
        thenDBPatientHasFirstName(existingPreferredName);
    }

    @Test
    public void testValidStreet1() throws InvalidObjectException {
        givenStreet1("Waireiki Streeteders");
        whenSave();
        thenDBPatientHasStreet1("Waireiki Streeteders");

        givenStreet1("122 Rd");
        whenSave();
        thenDBPatientHasStreet1("122 Rd");
    }

    @Test
    public void testInvalidStreet1() throws InvalidObjectException {
        givenStreet1("122@ Rd");
        whenSave();
        thenDBPatientHasStreet1(existingPatient1.getStreet1());

        givenStreet1("Waireiki!@#$%^&*()_+{}|:\"?><-=[]\';./,' Streeteders");
        whenSave();
        thenDBPatientHasStreet1(existingPatient1.getStreet1());
    }

    @Test
    public void testValidStreet2() throws InvalidObjectException {
        givenStreet2("122 Rd");
        whenSave();
        thenDBPatientHasStreet2("122 Rd");

        givenStreet2("Waireiki Streeteders");
        whenSave();
        thenDBPatientHasStreet2("Waireiki Streeteders");
    }

    @Test
    public void testInvalidStreet2() throws InvalidObjectException {
        givenStreet2("122@ Rd");
        whenSave();
        thenDBPatientHasStreet2(existingPatient1.getStreet2());

        givenStreet2("Waireiki!@#$%^&*()_+{}|:\"?><-=[]\';./,' Streeteders");
        whenSave();
        thenDBPatientHasStreet2(existingPatient1.getStreet2());
    }

    @Test
    public void testValidSuburb() throws InvalidObjectException {
        givenSuburb("St. Albans");
        whenSave();
        thenDBPatientHasSuburb("St. Albans");

        givenSuburb("Rangiora");
        whenSave();
        thenDBPatientHasSuburb("Rangiora");
    }

    @Test
    public void testInvalidSuburb() throws InvalidObjectException {
        givenSuburb("!@#$%^&*()");
        whenSave();
        thenDBPatientHasSuburb(existingPatient1.getSuburb());

        givenSuburb("_+-={}|:\"<>?[]\\;',./'");
        whenSave();
        thenDBPatientHasSuburb(existingPatient1.getSuburb());
    }

    @Test
    public void testValidZip() throws InvalidObjectException {
        givenZip("0001");
        whenSave();
        thenDBPatientHasZip("0001");

        givenZip("");
        whenSave();
        thenDBPatientHasZip("");

        givenZip("0000");
        whenSave();
        thenDBPatientHasZip("0000");

        givenZip("1");
        whenSave();
        thenDBPatientHasZip("0001");

        givenZip("9000");
        whenSave();
        thenDBPatientHasZip("9000");

        givenZip("8754");
        whenSave();
        thenDBPatientHasZip("8754");
    }

    @Test
    public void testInvalidZip() throws InvalidObjectException {
        givenZip("12345");
        whenSave();
        thenDBPatientHasZip(String.valueOf(existingPatient1.getZip()));
    }

    @Test
    public void testValidWeight() throws InvalidObjectException {
        givenWeight("0");
        whenSave();
        thenDBPatientHasWeight(0.0);

        givenWeight("-0");
        whenSave();
        thenDBPatientHasWeight(0.0);

        givenWeight("0.0");
        whenSave();
        thenDBPatientHasWeight(0.0);

        givenWeight("-0.0");
        whenSave();
        thenDBPatientHasWeight(0.0);

        givenWeight("65.3");
        whenSave();
        thenDBPatientHasWeight(65.3);

        givenWeight("65.32345");
        whenSave();
        thenDBPatientHasWeight(65.3);
    }

    @Test
    public void testInvalidWeight() throws InvalidObjectException {
        givenWeight("-0.1");
        whenSave();
        thenDBPatientHasWeight(existingPatient1.getWeight());

        givenWeight("-3");
        whenSave();
        thenDBPatientHasWeight(existingPatient1.getWeight());
    }

    @Test
    public void testValidHeight() throws InvalidObjectException {
        givenHeight("0");
        whenSave();
        thenDBPatientHasHeight(0.0);

        givenHeight("-0");
        whenSave();
        thenDBPatientHasHeight(0.0);

        givenHeight("0.0");
        whenSave();
        thenDBPatientHasHeight(0.0);

        givenHeight("-0.0");
        whenSave();
        thenDBPatientHasHeight(0.0);

        givenHeight("65.3");
        whenSave();
        thenDBPatientHasHeight(65.3);

        givenHeight("65.32345");
        whenSave();
        thenDBPatientHasHeight(65.3);
    }

    @Test
    public void testInvalidHeight() throws InvalidObjectException {
        givenHeight("-0.1");
        whenSave();
        thenDBPatientHasHeight(existingPatient1.getWeight());

        givenHeight("-3");
        whenSave();
        thenDBPatientHasHeight(existingPatient1.getWeight());
    }

    private void givenFirstName(String name) {
        interact(() -> lookup("#firstnameTxt").queryAs(TextField.class).setText(name));
    }

    private void givenMiddleNames(String name) {
        interact(() -> lookup("#middlenameTxt").queryAs(TextField.class).setText(name));
    }

    private void givenLastName(String name) {
        interact(() -> lookup("#lastnameTxt").queryAs(TextField.class).setText(name));
    }

    private void givenDeath(LocalDate death) {
        interact(() -> lookup("#dateOfDeath").queryAs(DatePicker.class).setValue(death));
    }

    private void givenBirth(LocalDate birth) {
        interact(() -> lookup("#dobDate").queryAs(DatePicker.class).setValue(birth));
    }

    private void givenPreferredName(String name) {
        interact(() -> lookup("#preferrednameTxt").queryAs(TextField.class).setText(name));
    }

    private void givenStreet1(String street) {
        interact(() -> lookup("#street1Txt").queryAs(TextField.class).setText(street));
    }

    private void givenStreet2(String street) {
        interact(() -> lookup("#street2Txt").queryAs(TextField.class).setText(street));
    }

    private void givenSuburb(String suburb) {
        interact(() -> lookup("#suburbTxt").queryAs(TextField.class).setText(suburb));
    }

    private void givenZip(String zip) {
        interact(() -> lookup("#zipTxt").queryAs(TextField.class).setText(zip));
    }

    private void givenWeight(String weight) {
        interact(() -> lookup("#weightTxt").queryAs(TextField.class).setText(weight));
    }

    private void givenHeight(String height) {
        interact(() -> lookup("#heightTxt").queryAs(TextField.class).setText(height));
    }

    private void thenDBPatientHasNhi(String nhi) throws InvalidObjectException {
        assertThat(Database.getPatientByNhi(existingNhi1).getNhiNumber()).isEqualTo(nhi);
    }

    private void thenDBPatientHasFirstName(String firstName) throws InvalidObjectException {
        assertThat(Database.getPatientByNhi(existingNhi1).getFirstName()).isEqualTo(firstName);
    }

    private void thenDBPatientHasMiddleNames(ArrayList<String> middles) throws InvalidObjectException {
        assertThat(Database.getPatientByNhi(existingNhi1).getMiddleNames()).isEqualTo(middles);
    }

    private void thenDBPatientHasLastName(String lastName) throws InvalidObjectException {
        assertThat(Database.getPatientByNhi(existingNhi1).getLastName()).isEqualTo(lastName);
    }

    private void thenDBPatientHasDeath(LocalDate death) throws InvalidObjectException {
        assertThat(Database.getPatientByNhi(existingNhi1).getDeath()).isEqualTo(death);
    }

    private void thenDBPatientHasBirth(LocalDate birth) throws InvalidObjectException {
        assertThat(Database.getPatientByNhi(existingNhi1).getBirth()).isEqualTo(birth);
    }

    private void thenDBPatientHasPreferredtName(String preferredName) throws InvalidObjectException {
        assertThat(Database.getPatientByNhi(existingNhi1).getPreferredName()).isEqualTo(preferredName);
    }

    private void thenDBPatientHasStreet1(String street1) throws InvalidObjectException {
        assertThat(Database.getPatientByNhi(existingNhi1).getStreet1()).isEqualTo(street1);
    }

    private void thenDBPatientHasStreet2(String street2) throws InvalidObjectException {
        assertThat(Database.getPatientByNhi(existingNhi1).getStreet2()).isEqualTo(street2);
    }

    private void thenDBPatientHasSuburb(String suburb) throws InvalidObjectException {
        assertThat(Database.getPatientByNhi(existingNhi1).getSuburb()).isEqualTo(suburb);
    }

    private void thenDBPatientHasZip(String zip) throws InvalidObjectException {
        assertThat(String.valueOf(Database.getPatientByNhi(existingNhi1).getZip())).isEqualTo(zip);
    }

    private void thenDBPatientHasWeight(double weight) throws InvalidObjectException {
        assertThat(Database.getPatientByNhi(existingNhi1).getWeight()).isEqualTo(weight);
    }

    private void thenDBPatientHasHeight(double height) throws InvalidObjectException {
        assertThat(Database.getPatientByNhi(existingNhi1).getHeight()).isEqualTo(height);
    }

    private void whenSave() {
        interact(() -> lookup("#saveButton").queryAs(Button.class).fire());
    }


    private void givenNhi(String newNhi) {
        interact(() -> lookup("#nhiTxt").queryAs(TextField.class).setText(newNhi));
    }

}
