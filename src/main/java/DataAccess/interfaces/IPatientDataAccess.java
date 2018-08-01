package DataAccess.interfaces;

import model.Patient;

import java.util.List;

public interface IPatientDataAccess {

    public int updatePatient (List<Patient> patient);

    public boolean addPatient (Patient patient);

    public  boolean addPatients (List<Patient> patient);

    public List<Patient> getPatients ();

    public Patient getPatientByNhi(String nhi);

    List<Patient> searchPatient(String searchTerm);
}
