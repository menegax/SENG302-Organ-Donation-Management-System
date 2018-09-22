package utility;

import com.google.maps.errors.ApiException;
import controller.GUIHome;
import controller.GUIMap;
import controller.ScreenControl;
import model.Patient;
import netscape.javascript.JSObject;
import service.PatientDataService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Provides the map javascript access to the java codebase
 */
public class MapBridge {

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

    public void checkOrganMatch(double[] geolocation, String patientNhi) throws InterruptedException, ApiException, IOException {
        Patient patient = patientDataService.getPatientByNhi(patientNhi);
        Set<GlobalEnums.Organ> donations = patient.getDonations().keySet();
        if (donations.size() > 0) {
            for (GlobalEnums.Organ organ : donations) {
                String recipientNhi = patient.getDonations().get(organ);
                if (recipientNhi != null) {
                    Patient recipient = patientDataService.getPatientByNhi(recipientNhi);
                    GUIMap.getJsBridge().call("matchedOrgan", patient.getCurrentLocation(), recipient.getCurrentLocation());
                }
            }
        }

    }

}
