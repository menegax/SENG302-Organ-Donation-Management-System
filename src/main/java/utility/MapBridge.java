package utility;

import controller.GUIHome;
import controller.ScreenControl;
import model.Patient;
import service.PatientDataService;

import java.util.ArrayList;

/**
 * Provides the map javascript access to the java codebase
 */
public class MapBridge {

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    /**
     * Opens the patient profile in a new window
     * @param patientNhi The NHI of the patient for  the new GUIHome scene to use
     */
    @SuppressWarnings("unused") // used in corresponding javascript
    public void openPatientProfile(String patientNhi) {
        Patient patient = new PatientDataService().getPatientByNhi(patientNhi);
        GUIHome controller = (GUIHome) screenControl.show("/scene/home.fxml", true, null, patient);
        controller.setTarget(patient);
    }


}
