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

    /**
     * Finds the index of the patient with matching nhi in the patients list.
     * - If the patient exists, the old patient object
     * is replaced with the new patient object.
     * - If not, the new patient is added to the list
     * @param patient - The updated/new patient object
     */
    public void storePatient(Patient patient) {
        Integer index = null;
        for (int i=0; index == null && i<patients.size(); i++) {
            if (patients.get(i).getNhiNumber().toLowerCase().equals(patient.getNhiNumber().toLowerCase())) {
                index = i;
            }
        }
        if (index != null) {
            patients.set(index, patient);
        } else {
            patients.add(patient);
        }
    }

    /**
     * Finds the index of the clinician with matching staff id in the clinicians list.
     * - If the clinician exists, the old clinician object
     * is replaced with the new clinician object.
     * - If not, the new clinician is added to the list
     * @param clinician - The updated/new clinician object
     */
    public void storeClinician(Clinician clinician) {
        Integer index = null;
        for (int i=0; index == null && i<=clinicians.size(); i++) {
            if (clinicians.get(i).getStaffID() == clinician.getStaffID()) {
                index = i;
            }
        }
        if (index != null) {
            clinicians.set(index, clinician);
        } else {
            clinicians.add(clinician);
        }
    }

    /**
     * Finds the index of the administrator with matching username in the administrators list.
     * - If the administrator exists, the old administrator object
     * is replaced with the new administrator object.
     * - If not, the new administrator is added to the list
     * @param administrator - The updated/new administrator object
     */
    public void storeAdministrator(Administrator administrator) {
        Integer index = null;
        for (int i=0; index == null && i<=administrators.size(); i++) {
            if (administrators.get(i).getUsername().toLowerCase().equals(administrator.getUsername().toLowerCase())) {
                index = i;
            }
        }
        if (index != null) {
            administrators.set(index, administrator);
        } else {
            administrators.add(administrator);
        }
    }
}
