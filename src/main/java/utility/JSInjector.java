package utility;

import controller.GUIHome;
import controller.ScreenControl;
import data_access.mysqlDAO.PatientDAO;
import model.Patient;
import service.ClinicianDataService;
import service.PatientDataService;

import java.util.ArrayList;
import java.util.List;



//todo remove before review as currently unneeded. (Leaving it in for now incase it is needed later)
public class JSInjector {

    private ScreenControl screenControl = ScreenControl.getScreenControl();

//    public List<Patient> getPatients() {
//        List<Patient> patients = new ArrayList<>(new ClinicianDataService().searchPatients("", null, 30));
//        List<Patient> results = new ArrayList<>();
//        for (Patient p : patients) {
//            results.add(new PatientDataService().getPatientByNhi(p.getNhiNumber()));
//        }
//        return results;
//    }

    public void openPatientProfile(String patientNhi) {
        System.out.println("YAY");
//        Patient patient = new PatientDataService().getPatientByNhi(patientNhi);
//        GUIHome controller = (GUIHome) screenControl.show("/scene/home.fxml", true, null, patient);
//        controller.setTarget(patient);
    }

}
