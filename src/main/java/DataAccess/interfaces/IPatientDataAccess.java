package DataAccess.interfaces;

import model.Patient;
import model.User;
import utility.GlobalEnums;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

public interface IPatientDataAccess {

    public int savePatients (Set<Patient> patient);

    public  boolean addPatientsBatch (List<Patient> patient);

    public Set<Patient> getPatients();

    public Patient getPatientByNhi(String nhi);

    public boolean deletePatient(Patient patient);

    Map<Integer, SortedSet<User>> searchPatients(String searchTerm, Map<GlobalEnums.FilterOption, String> filters, int numResults);

    public void deletePatientByNhi(String nhi);

}
