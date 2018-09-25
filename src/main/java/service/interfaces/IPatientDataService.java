package service.interfaces;

import model.Patient;

import java.util.List;

public interface IPatientDataService {

    /**
     * Gets the patient, firstly try from local, if fail get from remote
     * @param nhi - nhi
     * @return - patient from implementation of db
     */
    public Patient getPatientByNhi(String nhi);

    /**
     * Save patient to local db
     * @param patient - patient to save
     */
    public void save(Patient patient);

    /**
     * Save a list of globalPatients to db
     * @param patientList - list of globalPatients to save
     */
    public void save(List<Patient> patientList);

    public List<Patient> getDeadDonors();

}
