package DataAccess.interfaces;

import model.Patient;

import java.util.List;

public interface IPatientDataAccess {

    public int savePatients (List<Patient> patient);

    public  boolean addPatientsBatch (List<Patient> patient);

    public List<Patient> getPatients ();

    public Patient getPatientByNhi(String nhi);

    List<Patient> searchPatient(String searchTerm);

    public boolean deletePatient(Patient patient);
}
