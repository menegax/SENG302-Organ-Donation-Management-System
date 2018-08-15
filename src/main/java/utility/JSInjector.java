package utility;

import controller.GUIHome;
import controller.ScreenControl;
import model.Patient;
import service.PatientDataService;

public class JSInjector {

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    @SuppressWarnings("unused") // used in js
    public void openPatientProfile(String patientNhi) {
        Patient patient = new PatientDataService().getPatientByNhi(patientNhi);
        GUIHome controller = (GUIHome) screenControl.show("/scene/home.fxml", true, null, patient);
        controller.setTarget(patient);
    }

}
