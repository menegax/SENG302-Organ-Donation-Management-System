package data_access_test;

import data_access.DBHelper;
import data_access.factories.DAOFactory;
import model.Disease;
import model.Medication;
import model.Patient;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.cglib.core.Local;
import utility.GlobalEnums.*;
import utility.GlobalEnums;
import utility.SystemLogger;

import java.time.LocalDate;
import java.util.*;

import static java.util.logging.Level.OFF;
import static utility.UserActionHistory.userActions;

public class MYSQLPatientDAOTest {
    private static DAOFactory daoFactory;
    private static DBHelper dbHelper;
    private Patient patient;
    private Map<Integer, List<Patient>> searchResults;


    @BeforeClass
    @SuppressWarnings("Duplicates")
    public static void setUp() {
        userActions.setLevel(OFF);
        SystemLogger.systemLogger.setLevel(OFF);
        System.setProperty("connection_type", GlobalEnums.DbType.STORY44.getValue());
        daoFactory = DAOFactory.getDAOFactory(FactoryType.MYSQL);
        dbHelper = new DBHelper();
    }

    private void givenPatient() {
        patient = new Patient("ZLH0909", "Henry", new ArrayList<>(), "Smith", LocalDate.of(1950, 2, 2));
    }

    private void givenPatientMedications() {
        List<Medication> pastMeds = new ArrayList<>();
        List<Medication> currentMeds = new ArrayList<>();
        pastMeds.add(new Medication("codeine", MedicationStatus.HISTORY));
        currentMeds.add(new Medication("panadol", MedicationStatus.CURRENT));
        patient.setMedicationHistory(pastMeds);
        patient.setCurrentMedications(currentMeds);
    }

    private void givenPatientContacts() {
        patient.setRegion(Region.AUCKLAND);
        patient.setContactEmailAddress("Henry.smith@uc.nz");
        patient.setHomePhone("0211020102");
        patient.setContactName("Henry sr");
    }

    private void givenPatientDiseases() {
        List<Disease> pastDiseases = new ArrayList<>();
        List<Disease> currentDiseases = new ArrayList<>();
        pastDiseases.add(new Disease("Headache", DiseaseState.CURED));
        currentDiseases.add(new Disease("Dead", DiseaseState.CHRONIC));
        patient.setPastDiseases(pastDiseases);
        patient.setCurrentDiseases(currentDiseases);
    }

    private void givenPatientDonations() {
        List<Organ> donations = new ArrayList<>();
        donations.add(Organ.LIVER);
        patient.setDonations(donations);
    }

    private void givenPatientRequired() {
        Map<Organ, LocalDate> required = new HashMap<>();
        required.put(Organ.CORNEA, LocalDate.now());
        patient.setRequiredOrgans(required);
    }

    private void whenPatientsSaved() {
        daoFactory.getPatientDataAccess().savePatients(new HashSet<Patient>() {{
            add(patient);
        }});
    }

    private void thenPatientIsInDB() {
        Patient parsed = daoFactory.getPatientDataAccess().getPatientByNhi("ZLH0909");
        assert parsed.getFirstName().equals("Henry");
        assert parsed.getMedicationHistory().get(0).getMedicationName().equals("codeine");
        assert parsed.getCurrentMedications().get(0).getMedicationName().equals("panadol");
    }

    @Test
    public void testPatientSave() {
        givenPatient();
        givenPatientMedications();
        givenPatientContacts();
        givenPatientDiseases();
        givenPatientDonations();
        givenPatientRequired();
        whenPatientsSaved();
        thenPatientIsInDB();
    }

    private void whenSearched(String searchTerm) {
        searchResults = daoFactory.getPatientDataAccess().searchPatients(searchTerm, null, 50);
    }

    private void thenSearchHasResults() {
        boolean found = false;
        for (List<Patient> patList : searchResults.values()) {
            if (patList.contains(patient)) {
                found = true;
            }
        }
        assert found;
    }

    @Test
    public void testPatientMatchingSearch() {
        givenPatient();
        whenPatientsSaved();
        whenSearched("henr");
        thenSearchHasResults();
    }

    private void thenSearchHasNoResults() {
        boolean found = false;
        for (List<Patient> patList : searchResults.values()) {
            if (patList.contains(patient)) {
                found = true;
            }
        }
        assert !found;
    }

    @Test
    public void testPatientNonMatchingSearch() {
        givenPatient();
        whenPatientsSaved();
        whenSearched("zxcvbnm");
        thenSearchHasNoResults();
    }

    @Test
    public void testPatientNHISearch() {
        givenPatient();
        whenPatientsSaved();
        whenSearched("ZLH0909");
        thenSearchHasResults();
    }

    @AfterClass
    public static void reset() {
        dbHelper.reset();
    }
}
