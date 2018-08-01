package DataAccess.interfaces;

import model.Patient;

import java.util.List;

public interface IPatientDataAccess {

    public int savePatients (List<Patient> patient);

    public  boolean addPatientsBatch (List<Patient> patient);

    public List<Patient> getPatients ();

    public Patient getPatientByNhi(String nhi);

    public List<Patient> searchPatient(String searchTerm);

    public void deletePatientByNhi(String nhi);
}
