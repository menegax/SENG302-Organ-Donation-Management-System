package service_test;

import DataAccess.DBHelper;
import DataAccess.factories.DAOFactory;
import DataAccess.localDAO.LocalDB;
import model.Patient;
import org.junit.After;
import org.junit.Test;
import service.PatientDataService;
import utility.GlobalEnums;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;

public class PatientDataServiceTest {

    private static  LocalDB localDB = LocalDB.getInstance();
    private DAOFactory daoFactory = DAOFactory.getDAOFactory(GlobalEnums.FactoryType.MYSQL);
    private Patient patient;
    private static DBHelper dbHelper = new DBHelper();
    private PatientDataService patientDataService = new PatientDataService();


    /**
     * Check that the patient can be found in remote db if its not in local
     */
    @Test
    public void testGetPatientByNhiNoLocal(){
        givenNoPatientsInLocal();
        Patient patient = patientDataService.getPatientByNhi("ZLH0909");
        assert patient.equals(this.patient);
    }


    @Test
    public void testGetPatientByNhiFromLocal() {
        givenPatientsInLocal();
        Patient patient = patientDataService.getPatientByNhi("ZLH0909");
        assert  patient.equals(this.patient);
    }

    @Test
    public void testLocalSave() {
        givenNoPatientsInLocal();
        whenPatientSavedToLocalDB();
        thenOnlyOnePatientInLocal();
    }


    /**
     * Makes sure that local db is empty and the patient can be found in remote
     */
    private void givenNoPatientsInLocal() {
        localDB.clear();
        if (patient == null) {
            patient = new Patient("ZLH0909", "Henry", new ArrayList<>(), "Smith", LocalDate.of(1950, 2, 2));
            daoFactory.getPatientDataAccess().savePatients(new HashSet<Patient>(){{add(patient);}});
        }
    }


    /**
     * Ensures that the patient can be found in local db
     */
    private void givenPatientsInLocal() {
        patient = new Patient("ZLH0909", "Henry", new ArrayList<>(), "Smith", LocalDate.of(1950, 2, 2));
        localDB.storePatient(patient);
    }

    /**
     * Save patients to local database
     */
    private void whenPatientSavedToLocalDB() {
        patient = new Patient("ZLH0909", "Henry", new ArrayList<>(), "Smith", LocalDate.of(1950, 2, 2));
        patientDataService.save(new ArrayList<Patient>() {{add(patient);}});
        patientDataService.save(patient);
    }


    /**
     * Check that only one patient is in local db
     */
    private void thenOnlyOnePatientInLocal() {
        assert localDB.getPatients().size() == 1;
        assert localDB.getPatients().contains(patient);
    }

    @After
    public void reset() {
        localDB.clear();
        dbHelper.reset();
    }

}
