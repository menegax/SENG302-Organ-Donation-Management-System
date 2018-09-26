package utility;

import controller.GUIHome;
import controller.GUIMap;
import controller.ScreenControl;
import javafx.scene.control.ProgressBar;
import model.Patient;
import model.PatientOrgan;
import service.PatientDataService;
import service.interfaces.IPatientDataService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

/**
 * Provides the map javascript access to the java codebase
 */
public class MapBridge {

    private ProgressTask radiiTask;

    private IPatientDataService patientDataService = new PatientDataService();

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    private int LENGTHOFNZ = 1500000;


    /**
     * Opens the patient profile in a new window
     *
     * @param patientNhi The NHI of the patient for  the new GUIHome scene to use
     */
    @SuppressWarnings("unused") // used in corresponding javascript
    public void openPatientProfile(String patientNhi) {
        Patient patient = new PatientDataService().getPatientByNhi(patientNhi);
        GUIHome controller = (GUIHome) screenControl.show("/scene/home.fxml", true, null, patient, null);
        controller.setTarget(patient);
    }

    /**
     * Calculates marker radii
     */
    private void updateMarkerRadii(Patient patient, GlobalEnums.Organ organ) {
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
                String color = targetPatientOrgan.getProgressTask().getColor();
                GUIMap.getJSBridge().call("updateMarkerRadii", rad, color, organ.toString());
            }
        });
    }


    /**
     * Retrieves the active donations of the given patient and adds it to the master data
     * @param nhi the NHI of the patient
     */
    public void getPatientActiveDonations(String nhi) {
        List<PatientOrgan> masterData = new ArrayList<>();
        List<Patient> deadPatients = patientDataService.getDeadDonors();
        for (Patient patient : deadPatients) {
            if (patient.getDeathDate() != null && patient.getNhiNumber().equals(nhi)) {
                for (GlobalEnums.Organ organ : patient.getDonations().keySet()) {
                    if (patient.getDonations().get(organ) == null) {
                        PatientOrgan patientOrgan = new PatientOrgan(patient, organ);
                        if (!masterData.contains(patientOrgan)) {
                            if (patientOrgan.timeRemaining() < 0) {
                                masterData.add(patientOrgan);
                            }
                        }
                    }
                }
            }
        }
        GUIMap.getJSBridge().call("loadActiveDonations", masterData);
    }

    /**
     * Collects the patient list from available organs list
     */
    @SuppressWarnings("unused")
    public List getAvailableOrgans() {
        List<PatientOrgan> masterData = new ArrayList<PatientOrgan>();
        List<Patient> deadPatients = patientDataService.getDeadDonors();
        for (Patient patient : deadPatients) {
            if (patient.getDeathDate() != null) {
                for (GlobalEnums.Organ organ : patient.getDonations().keySet()) {
                    if (patient.getDonations().get(organ) == null) {
                        PatientOrgan patientOrgan = new PatientOrgan(patient, organ);
                        if (!masterData.contains(patientOrgan)) {
                            if (patientOrgan.timeRemaining() < 0) {
                                masterData.add(patientOrgan);
                            }
                        }
                    }
                }
            }
        }

        Set<Patient> uniqueSetOfPatients = new HashSet<Patient>();

        for (PatientOrgan aMasterData : masterData) {
            uniqueSetOfPatients.add(aMasterData.getPatient());
        }
        return new ArrayList<>(uniqueSetOfPatients);
    }


    /**
     * Refreshes the patient's info window
     * @param patient the patient to refresh
     */
    public void updateInfoWindow(Patient patient){
        if (GUIMap.getJSBridge() != null) {
            GUIMap.getJSBridge().call("reloadInfoWindow", patient);
        } else {
            SystemLogger.systemLogger.log(Level.WARNING, "GuiMap not instantiated", this);
        }
    }


    /**
     * Gets a patient by the nhi
     * @param nhi the nhi to get
     * @return the patient
     */
    public Patient getPatientByNhi(String nhi) {
        return patientDataService.getPatientByNhi(nhi);
    }

    /**
     * Interrupts thread and stops the task if there is one already created
     * Triggers method to updateMarker's radius to create/update the circle on the map
     * @param patientNhi the nhi of the currently selected patient on the map
     * @param organStr the string representation of the selected organ
     */
    @SuppressWarnings("unused")
    public void loadCircle(String patientNhi, String organStr) {
        if (radiiTask != null) {
            radiiTask.setInterrupted();
        }
        Patient patient = patientDataService.getPatientByNhi(patientNhi);
        GlobalEnums.Organ organ = GlobalEnums.Organ.getEnumFromString(organStr);
        if (patient.getDonations().containsKey(organ)) {
            updateMarkerRadii(patient, organ);
        }
    }
}