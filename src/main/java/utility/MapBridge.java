package utility;

import controller.GUIHome;
import controller.GUIMap;
import controller.ScreenControl;
import javafx.collections.ObservableList;
import model.Patient;
import model.PatientOrgan;
import org.mockito.internal.matchers.Or;
import service.OrganWaitlist;
import service.PatientDataService;
import service.interfaces.IPatientDataService;
import javafx.geometry.Point2D;

import java.util.*;
import java.util.logging.Level;

/**
 * Provides the map javascript access to the java codebase
 */
public class MapBridge {

    private IPatientDataService patientDataService = new PatientDataService();

    private ScreenControl screenControl = ScreenControl.getScreenControl();


    /**
     * Opens the patient profile in a new window
     * @param patientNhi The NHI of the patient for  the new GUIHome scene to use
     */
    @SuppressWarnings("unused") // used in corresponding javascript
    public void openPatientProfile(String patientNhi) {
        Patient patient = new PatientDataService().getPatientByNhi(patientNhi);
        GUIHome controller = (GUIHome) screenControl.show("/scene/home.fxml", true, null, patient, null);
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

    public Patient getPatientByNhi(String nhi) {
        return patientDataService.getPatientByNhi(nhi);
    }

    public void getPotentialMatches(String patientNhi, GlobalEnums.Organ organ) {
        PotentialMatchFinder potentialMatchFinder = new PotentialMatchFinder();
        Patient patient = patientDataService.getPatientByNhi(patientNhi);
        PatientOrgan patientOrgan = new PatientOrgan(patient, organ);
        ObservableList<OrganWaitlist.OrganRequest> allRequests = potentialMatchFinder.matchOrgan(patientOrgan);
        List<OrganWaitlist.OrganRequest> requests = new ArrayList<OrganWaitlist.OrganRequest>(allRequests);
        List<Patient> patients = new ArrayList<>();
        for(OrganWaitlist.OrganRequest request : requests) {
            patients.add(request.getReceiver());
        }
        GUIMap.getJSBridge().setMember("potentialMatches", patients);
        GUIMap.getJSBridge().call("populatePotentialMatches", patientNhi, patient);

    }
}
