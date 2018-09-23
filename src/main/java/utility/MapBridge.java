package utility;

import controller.GUIHome;
import controller.GUIMap;
import controller.ScreenControl;
import javafx.scene.control.ProgressBar;
import model.Patient;
import model.PatientOrgan;
import service.PatientDataService;
import service.interfaces.IPatientDataService;

import java.util.Random;

/**
 * Provides the map javascript access to the java codebase
 */
public class MapBridge {

    private ProgressTask radiiTask;

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
    private int LENGTHOFNZ = 1600000;

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

        Random rand = new Random();
        int value = rand.nextInt(LENGTHOFNZ);

        PatientOrgan targetPatientOrgan = new PatientOrgan(patient, organ);
        targetPatientOrgan.startTask();
        targetPatientOrgan.getProgressTask().setProgressBar(new ProgressBar()); //dummy progress task
        radiiTask = targetPatientOrgan.getProgressTask();
        CachedThreadPool.getCachedThreadPool().getThreadService().submit(radiiTask);
        String remainingTime = radiiTask.getMessage();
        double remaining = 0;
        if (!(remainingTime.equals("Cannot calculate")) && !remainingTime.equals("")) {
            String[] times = remainingTime.split(":");

            remaining += Integer.parseInt(times[0]) * 3600;
            remaining += Integer.parseInt(times[1]) * 60;
            remaining += Integer.parseInt(times[2]);

            remaining = remaining - organLoadTime - organUnloadtime;
            radius = remaining * heloTravelSpeedMps;
            System.out.println(organ);
            GUIMap.getJSBridge().call("createMarkerRadii", radius, radiiTask.getColor(), organ.toString());
        } else {
            GUIMap.getJSBridge().call("createMarkerRadii", radius, "#008000", organ.toString());
        }
        radiiTask.messageProperty().addListener((observable, oldValue, newValue) -> {
            if (!oldValue.equals("")) { // first circle always gives green
                double rem = 0;
                double rad;
                String[] time = newValue.split(":");
                rem += Integer.parseInt(time[0]) * 3600;
                rem += Integer.parseInt(time[1]) * 60;
                rem += Integer.parseInt(time[2]);

                rem = rem - organLoadTime - organUnloadtime;
                rad = rem * heloTravelSpeedMps;

                if (rad > LENGTHOFNZ) {
                    rad = LENGTHOFNZ;
                }
//                System.out.println(rad);
                String color = targetPatientOrgan.getProgressTask().getColor();
                GUIMap.getJSBridge().call("updateMarkerRadii", rad, color, organ.toString());
            }
        });
    }

    /**
     * Sets donations in map.js
     */
//    @SuppressWarnings("unused") // used in corrresponding javascript
//    public void loadCircles(String patientNhi) {
//        Patient patient = patientDataService.getPatientByNhi(patientNhi);
//        GUIMap.getJSBridge().setMember("currentOrgan", patient.getDonations().get(0).toString().toLowerCase());
//        for (GlobalEnums.Organ organ: patient.getDonations()) {
//            GUIMap.getJSBridge().call("createOrganButtons", organ.toString(), patient.getDonations().size());
//            updateMarkerRadii(patient, organ);
//        }
//        GUIMap.getJSBridge().call("createOpenPatientButton", patient.getDonations().size());
//    }

    public void loadCircle(String patientNhi, String organStr) {
        if (radiiTask != null) {
            radiiTask.setInterrupted();
        }
        Patient patient = patientDataService.getPatientByNhi(patientNhi);
        GlobalEnums.Organ organ = GlobalEnums.Organ.getEnumFromString(organStr);
        if (patient.getDonations().contains(organ)) {
            updateMarkerRadii(patient, organ);
        }
    }
}