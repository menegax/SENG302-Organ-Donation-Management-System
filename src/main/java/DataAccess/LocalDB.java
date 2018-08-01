package DataAccess;

import model.Administrator;
import model.Clinician;
import model.Patient;

import java.util.ArrayList;
import java.util.List;

public class LocalDB {
    private static LocalDB instance;

    private List<Patient> patients;
    private List<Clinician> clinicians;
    private List<Administrator> administrators;

    private LocalDB() {
        patients = new ArrayList<>();
        clinicians = new ArrayList<>();
        administrators = new ArrayList<>();
    }

    public static LocalDB getInstance() {
        if (instance == null) {
            instance = new LocalDB();
        }
        return instance;
    }

    public List<Patient> getPatients() {
        return patients;
    }

    public Patient getPatientByNHI(String nhi) {
        for (Patient patient : patients) {
            if (patient.getNhiNumber().equals(nhi)) {
                return patient;
            }
        }
        return null;
    }

    public List<Clinician> getClinicians() {
        return clinicians;
    }

    public Clinician getClinicianByStaffID(int id) {
        for (Clinician clinician : clinicians) {
            if (clinician.getStaffID() == id) {
                return clinician;
            }
        }
        return null;
    }

    public List<Administrator> getAdministrators() {
        return administrators;
    }

    public Administrator getAdministratorByUsername(String username) {
        for (Administrator administrator : administrators) {
            if (administrator.getUsername().equals(username)) {
                return administrator;
            }
        }
        return null;
    }

    public void addPatient(Patient patient) {
        patients.add(patient);
    }

    public void addClinician(Clinician clinician) {
        clinicians.add(clinician);
    }

    public void addAdministrator(Administrator administrator) {
        administrators.add(administrator);
    }
}
