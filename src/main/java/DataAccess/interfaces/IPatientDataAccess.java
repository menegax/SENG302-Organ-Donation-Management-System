package DataAccess.interfaces;

import model.Patient;
import utility.GlobalEnums;

import java.util.List;
import java.util.Map;

public interface IPatientDataAccess {

    public int savePatients (List<Patient> patient);

    public  boolean addPatientsBatch (List<Patient> patient);

    public List<Patient> getPatients ();

    public Patient getPatientByNhi(String nhi);

    List<Patient> searchPatient(String searchTerm, Map<GlobalEnums.FilterOption, String> filters, int numResults);
}
