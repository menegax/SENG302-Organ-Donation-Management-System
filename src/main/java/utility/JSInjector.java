package utility;

import model.Patient;
import service.ClinicianDataService;
import service.PatientDataService;

import java.util.ArrayList;
import java.util.List;

//todo remove before review as currently unneeded. (Leaving it in for now incase it is needed later)
public class JSInjector {

    public List<Patient> getPatients() {
        List<Patient> patients = new ArrayList<>(new ClinicianDataService().searchPatients("", null, 30));
        List<Patient> results = new ArrayList<>();
        for (Patient p : patients) {
            results.add(new PatientDataService().getPatientByNhi(p.getNhiNumber()));
        }
        return results;
//        Patient patient = new Patient("JAM3333", "Joshua", new ArrayList<>(), "Meneghini", LocalDate.of(1997, 9, 17));
//        patient.setStreetNumber("38");
//        patient.setStreetName("Kentucky Way");
//        try {
//            patient.setSuburb("Awapuni");
//        } catch (DataFormatException e) {
//            e.printStackTrace();
//        }
//        patients.add(patient);
//        Patient patient1 = new Patient("KLA6666", "Kyle", new ArrayList<>(), "Lamb", LocalDate.of(1997, 8, 17));
//        patient1.setStreetNumber("2");
//        patient1.setStreetName("Ilam Road");
//        try {
//            patient1.setSuburb("Ilam");
//        } catch (DataFormatException e) {
//            e.printStackTrace();
//        }
//        patients.add(patient1);
//        return patients;
    }

}
