package BuisnessRules;

import DataAccess.interfaces.IPatientDataAccess;
import model.Patient;

public class BRPatient {

    private IPatientDataAccess patientDataAccess;

    public BRPatient(IPatientDataAccess patientDataAccess) {
        this.patientDataAccess = patientDataAccess;
    }

    public Patient getPatientByNhi(String nhi) {
        return patientDataAccess.getPatientByNhi(nhi);
    }
}
