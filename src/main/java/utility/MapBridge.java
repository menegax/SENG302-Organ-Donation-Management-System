package utility;

import controller.GUIHome;
import controller.GUIMap;
import controller.ScreenControl;
import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
import model.Patient;
import model.PatientOrgan;
import netscape.javascript.JSObject;
import service.PatientDataService;
import service.interfaces.IPatientDataService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    private PatientOrgan targetPatientOrgan;
    private IPatientDataService patientDataService = new PatientDataService();

    /**
     * Calculates marker radii
     */
    @SuppressWarnings("unused") // used in corrresponding javascript
    public void updateMarkerRadii(String patientNhi, GlobalEnums.Organ organ) {
        //constants using kilometers and seconds
        long organLoadTime = 1800;
        long organUnloadtime = 1800;
        long heloTravelSpeedKmh = 222; // cruise speed of westpac helicopter
        double metersPerSec = 1000 / (double) 3600;
        double heloTravelSpeedMps = heloTravelSpeedKmh * metersPerSec;
        double radius = 0;

        targetPatientOrgan = new PatientOrgan(patientDataService.getPatientByNhi(patientNhi), organ);
        targetPatientOrgan.startTask();
        targetPatientOrgan.getProgressTask().setProgressBar(new ProgressBar()); //dummy progress task
        CachedThreadPool.getCachedThreadPool().getThreadService().submit(targetPatientOrgan.getProgressTask());
        String remainingTime = targetPatientOrgan.getProgressTask().getMessage();
        double remaining = 0;
        if (!(remainingTime.equals("Cannot calculate")) && !remainingTime.equals("")) {
            String[] times = remainingTime.split(":");

            remaining += Integer.parseInt(times[0]) * 3600;
            remaining += Integer.parseInt(times[1]) * 60;
            remaining += Integer.parseInt(times[2]);

            remaining = remaining - organLoadTime - organUnloadtime;
            radius = remaining * heloTravelSpeedMps;
            System.out.println(radius);

            GUIMap.getJSBridge().call("updateMarkerRadii", radius);
        } else {
            radius = 100000;
            GUIMap.getJSBridge().call("updateMarkerRadii", radius);
        }
    }
}