package utility;

import com.google.maps.model.LatLng;
import controller.GUIHome;
import controller.GUIMap;
import controller.ScreenControl;
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


    @SuppressWarnings("unused")
    public void filterArea(ArrayList<Patient> patients, float filterStartLat, float filterStartLng, float filterEndLat, float filterEndLng) {
        ArrayList patientsFilteredByArea = filterPatientsByArea(patients, filterStartLat, filterStartLng, filterEndLat, filterEndLng);
//        GUIMap.getJSBridge().call("setFilteredPatients", patientsFilteredByArea); // todo
        System.out.println("final filtered patients: " + patientsFilteredByArea); //todo rm
    }


    private ArrayList<Patient> filterPatientsByArea(ArrayList<Patient> patients, float filterStartLat, float filterStartLng, float filterEndLat, float filterEndLng) {

        ArrayList<Patient> patientsFilteredByArea = new ArrayList<>();

        System.out.println("globalPatients to be filtered");

        for (Patient patient : patients) {
            System.out.println( "patient being filtered: " + patient.getNhiNumber());
            if (isPatientInArea(patient, filterStartLat, filterStartLng, filterEndLat, filterEndLng)) {
                patientsFilteredByArea.add(patient);
                System.out.println("Patient " + patient.getNhiNumber() + " is within bounds.");
            }
            else {
                System.out.println( "Patient " + patient.getNhiNumber() + " is outside bounds.");
            }
        }

        return patientsFilteredByArea;
    }


    private boolean isPatientInArea(Patient patient, float filterStartLat, float filterStartLng, float filterEndLat, float filterEndLng) {
        System.out.println("figuring out if patient in area boolean");

        double minLng, minLat, maxLng, maxLat;
        LatLng current;

        // if can't get location, it's not in the area
        try {
            current = patient.getCurrentLocation();
            System.out.println("yo");
        } catch (Exception e) {
            System.out.println("failed to geocode in area");
            return false;
        }

        System.out.println("ya");
//        minLng = Math.min(filterStart.lng, filterEnd.lng);
//        minLat = Math.min(filterStart.lat, filterEnd.lat);
//        maxLng = Math.max(filterStart.lng, filterEnd.lng);
//        maxLat = Math.max(filterStart.lat, filterEnd.lat);
        System.out.println("filterEnd.lng: " + filterEndLng);
        minLng = filterEndLng;
        minLat = filterEndLat;
        maxLng = filterEndLng;
        maxLat = filterEndLat;
        System.out.println("yang");

        System.out.println("Finding if patient is in area: " + minLng + " " + minLat + " " + maxLng + " " + maxLat);

        if (current.lng < minLng) {
            return false;
        }
        else if (current.lat < minLat) {
            return false;
        }
        else if (current.lng > maxLng) {
            return false;
        }
        else if (current.lat > maxLat) {
            return false;
        }

        return true;
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
}
