package utility;

import controller.*;
import javafx.collections.ObservableList;
import javafx.scene.control.ProgressBar;
import model.Clinician;
import model.Patient;
import model.PatientOrgan;
import org.mockito.internal.matchers.Or;
import service.OrganWaitlist;
import service.PatientDataService;
import service.interfaces.IPatientDataService;
import javafx.geometry.Point2D;
import utility.undoRedo.IAction;
import utility.undoRedo.MultiAction;

import java.util.*;
import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

/**
 * Provides the map javascript access to the java codebase
 */
public class MapBridge {

    private ProgressTask radiiTask;

    private IPatientDataService patientDataService = new PatientDataService();

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    private UserControl userControl = UserControl.getUserControl();

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

    private int LENGTHOFNZ = 1500000;

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
            if (remaining < 0) {
                remaining = 0;
            }
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
                if (rem < 0) {
                    rem = 0;
                }
                rad = rem * heloTravelSpeedMps;

                if (rad > LENGTHOFNZ) {
                    rad = LENGTHOFNZ;
                }
                String color = targetPatientOrgan.getProgressTask().getColor();
                GUIMap.getJSBridge().call("updateMarkerRadii", rad, color, organ.toString());
            }
        });
    }

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
    public void getAvailableOrgans() {
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
        GUIMap.getJSBridge().call("setPatients",patients);
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

    public void getPotentialMatches(String patientNhi, String organStr) {
        GlobalEnums.Organ organ = GlobalEnums.Organ.getEnumFromString(organStr);
        PotentialMatchFinder potentialMatchFinder = new PotentialMatchFinder();
        Patient patient = patientDataService.getPatientByNhi(patientNhi);
        PatientOrgan patientOrgan = new PatientOrgan(patient, organ);
        ObservableList<OrganWaitlist.OrganRequest> allRequests = potentialMatchFinder.matchOrgan(patientOrgan);
        List<OrganWaitlist.OrganRequest> requests = new ArrayList<OrganWaitlist.OrganRequest>(allRequests);
        List<Patient> patients = new ArrayList<>();
        for(OrganWaitlist.OrganRequest request : requests) {
            patients.add(request.getReceiver());
        }
        if (patients.size() > 0) {
            GUIMap.getJSBridge().setMember("patients", patients);
            GUIMap.getJSBridge().setMember("potentialMatches", patients);
            GUIMap.getJSBridge().setMember("potentialMatchesCopy", patients);
            GUIMap.getJSBridge().setMember("donorPatientNhi", patient.getNhiNumber());
            GUIMap.getJSBridge().call("populatePotentialMatches", patientNhi, patient);
        } else {
            GUIMap.getJSBridge().call("noPotentialMatchesFound", patients.size());
        }
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

    @SuppressWarnings("unused")
    public void assignOrgan(String donorNhiStr, String receiverNhiStr, String organStr) {
        GlobalEnums.Organ organ = GlobalEnums.Organ.getEnumFromString(organStr);
        Patient donor = patientDataService.getPatientByNhi(donorNhiStr);
        Patient receiver = patientDataService.getPatientByNhi(receiverNhiStr);

        Patient after1 = (Patient) donor.deepClone();
        Patient after2 = (Patient) receiver.deepClone();
        after1.getDonations().put(organ, after2.getNhiNumber());
        after2.getRequiredOrgans().get(organ).setDonorNhi(after1.getNhiNumber());
        IAction action = new MultiAction(donor, after1, receiver, after2);
        GlobalEnums.UndoableScreen undoableScreen;
        if (userControl.getLoggedInUser() instanceof Clinician) {
            undoableScreen = GlobalEnums.UndoableScreen.CLINICIANPROFILE;
        } else {
            undoableScreen = GlobalEnums.UndoableScreen.ADMINISTRATORPROFILE;
        }
        UndoRedoControl.getUndoRedoControl().addAction(action, undoableScreen);
        userActions.log(Level.INFO, "Assigned organ (" + organ + ") to patient " + receiver.getNhiNumber(), "Attempted to assign organ to patient");
    }
}