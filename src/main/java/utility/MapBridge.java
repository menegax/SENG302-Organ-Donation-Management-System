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
import utility.ProgressTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

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

    private IPatientDataService patientDataService = new PatientDataService();
    private int LENGTHOFNZ = 1500000;

    /**
     * Calculates marker radii
     */
    @SuppressWarnings("unused") // used in corrresponding javascript
    public void updateMarkerRadii(Patient patient, GlobalEnums.Organ organ) {
        //constants using kilometers and seconds
        long organLoadTime = 1800;
        long organUnloadtime = 1800;
        long heloTravelSpeedKmh = 222; // cruise speed of westpac helicopter
        double metersPerSec = 1000 / (double) 3600;
        double heloTravelSpeedMps = heloTravelSpeedKmh * metersPerSec;
        double radius = 0;

        PatientOrgan targetPatientOrgan = new PatientOrgan(patient, organ);
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
            GUIMap.getJSBridge().call("createMarkerRadii", radius, targetPatientOrgan.getProgressTask().getColor(), organ.toString());
        } else {
            Random rand = new Random();
            int value = rand.nextInt(LENGTHOFNZ);
            GUIMap.getJSBridge().call("createMarkerRadii", value, "#008000", organ.toString());
        }
        targetPatientOrgan.getProgressTask().messageProperty().addListener((observable, oldValue, newValue) -> {
            if (!oldValue.equals("")) { // first circle always gives green
                Random rand = new Random();
                int value = rand.nextInt(LENGTHOFNZ);
                String color = targetPatientOrgan.getProgressTask().getColor();
                GUIMap.getJSBridge().call("updateMarkerRadii", value, color, organ.toString());
            }
        });
    }

    /**
     * Sets donations in map.js
     */
    public void loadCircles(String patientNhi) {
        Patient patient = patientDataService.getPatientByNhi(patientNhi);
        for (GlobalEnums.Organ organ: patient.getDonations()) {
            updateMarkerRadii(patient, organ);
        }
        jsBridge.setMember("donations", patient.getDonations());
    }
}