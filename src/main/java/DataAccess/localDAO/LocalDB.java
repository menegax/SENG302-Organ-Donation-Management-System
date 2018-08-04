package DataAccess.localDAO;

import model.Administrator;
import model.Clinician;
import model.Patient;
import utility.Searcher;

import java.util.HashSet;
import java.util.Set;

public class LocalDB {
    private static LocalDB instance;

    private Set<Patient> patients;
    private Set<Clinician> clinicians;
    private Set<Administrator> administrators;
    private Set<Patient> deleted;

    private LocalDB() {
        patients = new HashSet<>();
        clinicians = new HashSet<>();
        administrators = new HashSet<>();
    }

    public static LocalDB getInstance() {
        if (instance == null) {
            instance = new LocalDB();
        }
        return instance;
    }

    public Set<Patient> getPatients() {
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

    public Set<Clinician> getClinicians() {
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

    public Set<Administrator> getAdministrators() {
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

    /**
     * Finds the index of the patient with matching nhi in the patients list.
     * - If the patient exists, the old patient object
     * is replaced with the new patient object.
     * - If not, the new patient is added to the list
     * @param patient - The updated/new patient object
     */
    public void storePatient(Patient patient) {
        Searcher.getSearcher().removeIndex(patient);
        patients.remove(patient);
        Searcher.getSearcher().addIndex(patient);
        patients.add(patient);
    }

    /**
     * Finds the index of the clinician with matching staff id in the clinicians list.
     * - If the clinician exists, the old clinician object
     * is replaced with the new clinician object.
     * - If not, the new clinician is added to the list
     * @param clinician - The updated/new clinician object
     */
    public void storeClinician(Clinician clinician) {
        clinicians.remove(clinician);
        clinicians.add(clinician);
    }

    /**
     * Finds the index of the administrator with matching username in the administrators list.
     * - If the administrator exists, the old administrator object
     * is replaced with the new administrator object.
     * - If not, the new administrator is added to the list
     * @param administrator - The updated/new administrator object
     */
    public void storeAdministrator(Administrator administrator) {
        administrators.remove(administrator);
        administrators.add(administrator);
    }
}
