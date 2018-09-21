package utility;

import controller.GUIHome;
import controller.ScreenControl;
import javafx.application.Platform;
import model.Patient;
import netscape.javascript.JSObject;
import service.PatientDataService;

import java.util.ArrayList;

/**
 * Provides the map javascript access to the java codebase
 */
public class MapBridge {

    private JSObject jsBridge;

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

    /**
     *
     */
    public void updateMarkerRadii(Patient patient) {
        //constants using kilometers and seconds
        long organLoadTime = 1800;
        long organUnloadtime = 1800;
        long refuelTime = 1800;
        long maxTravelDistanceStatuteKilometers = 460; //on one tank of gas
        long heloTravelSpeedKmh = 260;
        double metersPerKm = 1000 / (double) 3600;
        double radius = 0.0;
        jsBridge.call("updateMarkerRadii", radius);
    }
}
