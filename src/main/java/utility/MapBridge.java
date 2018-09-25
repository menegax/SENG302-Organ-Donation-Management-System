package utility;

import controller.GUIAvailableOrgans;
import com.google.maps.errors.ApiException;
import controller.GUIHome;
import controller.GUIMap;
import controller.ScreenControl;
import javafx.scene.control.ProgressBar;
import model.Patient;
import model.PatientOrgan;
import netscape.javascript.JSObject;
import model.PatientOrgan;
import service.PatientDataService;
import service.interfaces.IPatientDataService;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.io.IOException;
import java.util.Set;

/**
 * Provides the map javascript access to the java codebase
 */
public class MapBridge {

    private IPatientDataService patientDataService = new PatientDataService();

    private ScreenControl screenControl = ScreenControl.getScreenControl();
    private PatientDataService patientDataService = new PatientDataService();


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
     * Collects the patient list from available organs list
     */
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
    /**
     * Checks if the donor patient has organs that have been matched to a recipient patient, if so, triggers js method to
     * create a line between two markers
     * @param geolocation - lat lng of donor patient
     * @param patientNhi - nhi number of donor patient
     * @throws InterruptedException
     * @throws ApiException
     * @throws IOException
     */
    @SuppressWarnings("unused") // used in corresponding javascript
    public void checkOrganMatch(double[] geolocation, String patientNhi) throws InterruptedException, ApiException, IOException {
        Patient patient = patientDataService.getPatientByNhi(patientNhi);
        Set<GlobalEnums.Organ> donations = patient.getDonations().keySet();

        if (donations.size() > 0) {
            for (GlobalEnums.Organ organ : donations) {
                String recipientNhi = patient.getDonations().get(organ);
                if (recipientNhi != null) {
                    Patient recipient = patientDataService.getPatientByNhi(recipientNhi);
                    PatientOrgan targetPatientOrgan = new PatientOrgan(patient, organ);
                    targetPatientOrgan.startTask();
                    targetPatientOrgan.getProgressTask().setProgressBar(new ProgressBar()); //dummy progress task
                    CachedThreadPool.getCachedThreadPool().getThreadService().submit(targetPatientOrgan.getProgressTask());

                    GUIMap.getJSBridge().call("matchedOrgan", patient.getCurrentLocation(),
                            recipient.getCurrentLocation(), recipient.getNhiNumber(),
                            targetPatientOrgan.getProgressTask().getColor(), organ.toString());

                    targetPatientOrgan.getProgressTask().messageProperty().addListener((observable, oldValue, newValue) -> {
                        if (!oldValue.equals("")) { // first circle always gives green
                            String color = targetPatientOrgan.getProgressTask().getColor();
                            GUIMap.getJSBridge().call("updateMatchedOrganLine", color, recipientNhi, organ.toString());
                        }
                    });
                }
            }
        }

    }

        for (int i = 0; i < masterData.size(); i++) {
            uniqueSetOfPatients.add(masterData.get(i).getPatient());
        }
        List<Patient> patients = new ArrayList<Patient>(uniqueSetOfPatients);
        return patients;
    }

    public void updateInfoWindow(Patient patient){
        if (GUIMap.getJSBridge() != null) {
            GUIMap.getJSBridge().call("reloadInfoWindow", patient);
        } else {
            SystemLogger.systemLogger.log(Level.WARNING, "GUIMAP not instantiated - soz for hacky", this);
        }
    }
}
