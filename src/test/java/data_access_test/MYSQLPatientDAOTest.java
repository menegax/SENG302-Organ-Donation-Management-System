package data_access_test;

import DataAccess.DBHelper;
import DataAccess.factories.DAOFactory;
import model.Disease;
import model.Medication;
import model.Patient;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import utility.GlobalEnums.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MYSQLPatientDAOTest {
    private static DAOFactory daoFactory;
    private static DBHelper dbHelper;
    private Patient patient;

    @BeforeClass
    public static void setUp() {
        System.setProperty("connection_type", DbType.TEST.getValue());
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
        List<Organ> required = new ArrayList<>();
        required.add(Organ.CORNEA);
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


    @AfterClass
    public static void reset() {
        dbHelper.reset();
    }
}
