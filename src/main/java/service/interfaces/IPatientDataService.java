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

    public void save(Patient patient);

    public void save(List<Patient> patientList);

}
