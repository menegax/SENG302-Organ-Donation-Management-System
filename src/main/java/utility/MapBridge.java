package utility;

import com.google.maps.errors.ApiException;
import controller.*;
import javafx.collections.ObservableList;
import com.google.maps.errors.ApiException;
import controller.GUIHome;
import controller.GUIMap;
import controller.ScreenControl;
import javafx.scene.control.ProgressBar;
import model.Clinician;
import model.OrganReceival;
import model.Patient;
import model.PatientOrgan;
import org.mockito.internal.matchers.Or;
import service.APIGoogleMaps;
import service.OrganWaitlist;
import service.PatientDataService;
import service.interfaces.IPatientDataService;
import javafx.geometry.Point2D;
import utility.undoRedo.IAction;
import utility.undoRedo.MultiAction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static utility.MathUtilityMethods.deg2rad;
import static utility.UserActionHistory.userActions;

/**
 * Provides the map javascript access to the java codebase
 */
public class MapBridge {

    private ProgressTask radiiTask;

    private double rad;

    private IPatientDataService patientDataService = new PatientDataService();

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    private UserControl userControl = UserControl.getUserControl();
    private int LENGTHOFNZ = 1500000;

    private Logger systemLogger = SystemLogger.systemLogger;

    public void populateLastSetOfPatients() {
        List lastSetOfPatients = GUIMap.getLastSetOfPatientsParsed();
        GUIMap.getJSBridge().call("setPatients", lastSetOfPatients);
    }

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
        String organStr = organ.toString().substring(0,1).toUpperCase() + organ.toString().substring(1);
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
            rad = radius;
            GUIMap.getJSBridge().call("createMarkerRadii", radius, radiiTask.getColor(), organStr);
        } else {
            GUIMap.getJSBridge().call("createMarkerRadii", radius, "#008000", organStr);
        }
        radiiTask.messageProperty().addListener((observable, oldValue, newValue) -> {
            if (!oldValue.equals("")) { // first circle always gives green
                double rem = 0;
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
                GUIMap.getJSBridge().call("updateMarkerRadii", rad, color, organStr);
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
                for (GlobalEnums.Organ organ : patient.getDonations()
                        .keySet()) {
                    if (patient.getDonations()
                            .get(organ) == null) {
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
        GUIMap.setLastSetOfPatientsParsed(new ArrayList<>(uniqueSetOfPatients));
        return new ArrayList<>(uniqueSetOfPatients);
    }


    /**
     * Refreshes the patient's info window
     * @param patient the patient to refresh
     */
    public void updateInfoWindow(Patient patient) {
        if (GUIMap.getJSBridge() != null) {
            GUIMap.getJSBridge()
                    .call("reloadInfoWindow", patient);
        }
        else {
            SystemLogger.systemLogger.log(Level.WARNING, "GUIMAP not instantiated", this);
        }
    }

    /**
     * Checks if the donor patient has organs that have been matched to a recipient patient, if so, triggers js method to
     * create a line between two markers
     * @param patientNhi - patient nhi
     * @throws InterruptedException -  if geocode fails
     * @throws ApiException - if geocode fails
     * @throws IOException - if geocode fails
     */
    /*public void checkOrganMatch(String patientNhi) throws InterruptedException, ApiException, IOException {
        Patient patient = patientDataService.getPatientByNhi(patientNhi);
        Set<GlobalEnums.Organ> donations = patient.getDonations()
                .keySet();

        if (donations.size() > 0) {
            for (GlobalEnums.Organ organ : donations) {
                String recipientNhi = patient.getDonations()
                        .get(organ);
                if (recipientNhi != null) {
                    Patient recipient = patientDataService.getPatientByNhi(recipientNhi);
                    PatientOrgan targetPatientOrgan = new PatientOrgan(patient, organ);
                    targetPatientOrgan.startTask();
                    targetPatientOrgan.getProgressTask()
                            .setProgressBar(new ProgressBar()); //dummy progress task
                    CachedThreadPool.getCachedThreadPool()
                            .getThreadService()
                            .submit(targetPatientOrgan.getProgressTask());

                    GUIMap.getJSBridge()
                            .call("createMatchedOrganArrow",
                                    patient.getCurrentLocation(),
                                    recipient.getCurrentLocation(),
                                    patientNhi,
                                    recipient.getNhiNumber(),
                                    organ.toString());

                }
            }
        }

    }*/

    /**
     * Gets a patient by the nhi
     * @param nhi the nhi to get
     * @return the patient
     */
    public Patient getPatientByNhi(String nhi) {
        return patientDataService.getPatientByNhi(nhi);
    }

    /**
     * Uses the donor nhi number to find potential matches to the organ parsed through in the parameter to trigger either
     * a populatePotentialMatches method or noPotentialMatchesFound method for assigning organs via map
     * @param patientNhi - string format of donor nhi to get the donor Patient object
     * @param organStr - String format of the organ to potentially match
     */
    public void getPotentialMatches(String patientNhi, String organStr) {
        GlobalEnums.Organ organ = GlobalEnums.Organ.getEnumFromString(organStr);
        PotentialMatchFinder potentialMatchFinder = new PotentialMatchFinder();
        Patient patient = patientDataService.getPatientByNhi(patientNhi);
        PatientOrgan patientOrgan = new PatientOrgan(patient, organ);
        ObservableList<OrganWaitlist.OrganRequest> allRequests = potentialMatchFinder.matchOrgan(patientOrgan);
        List<OrganWaitlist.OrganRequest> requests = new ArrayList<OrganWaitlist.OrganRequest>(allRequests);
        List<Patient> patients = new ArrayList<>();
        for(OrganWaitlist.OrganRequest request : requests) {
            if (patientWithinCircle(patient, request.getReceiver())) {
                patients.add(request.getReceiver());
            }
        }
        System.out.println(patients.size());
        if (patients.size() > 0) {
            GUIMap.getJSBridge().setMember("patients", patients);
            GUIMap.getJSBridge().setMember("potentialMatches", patients);
            GUIMap.getJSBridge().setMember("potentialMatchesCopy", patients);
            GUIMap.getJSBridge().setMember("donorPatientNhi", patient.getNhiNumber());
            GUIMap.getJSBridge().call("populatePotentialMatches", patientNhi, patient);
        } else {
            GUIMap.getJSBridge().call("noPotentialMatchesFound");
        }
    }

    /**
     * Gets whether the current receiver is within the circle radius currently displayed on the map
     * @param donor the donor at the center of the circle
     * @param receiver the receiver to calculate the distance to
     * @return whether the receiver is within the circle or not
     */
    private boolean patientWithinCircle(Patient donor, Patient receiver) {
        try {
            double R = 6371; // Radius of the earth in km
            double distanceLat = deg2rad(donor.getCurrentLocation().lat - receiver.getCurrentLocation().lat);
            double distanceLon = deg2rad(donor.getCurrentLocation().lng - receiver.getCurrentLocation().lng);
            double a = Math.sin(distanceLat / 2) * Math.sin(distanceLat / 2) +
                            Math.cos(deg2rad(donor.getCurrentLocation().lat)) * Math.cos(deg2rad(receiver.getCurrentLocation().lat)) *
                                    Math.sin(distanceLon / 2) * Math.sin(distanceLon / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            double d = R * c * 1000; // Distance in m

            return (d <= rad);
        } catch (InterruptedException | ApiException | IOException e) {
            systemLogger.log(Level.WARNING, "Unable to geolocate patient", "Attempted to geolocate patient while calculating distances");
            return false;
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

    public void getAssignmentsFromNhi(String patientNhi) {
        List<Patient> patients = new ArrayList<>();
        Patient patient = patientDataService.getPatientByNhi(patientNhi);
        Map<GlobalEnums.Organ, String> donatingOrgans = patient.getDonations();
        for (String nhi : donatingOrgans.values()) {
            if (nhi != null) {
                patients.add(patientDataService.getPatientByNhi(nhi));
            }
        }
        if (!patient.isDead()) {
            Map<GlobalEnums.Organ, OrganReceival> receivingOrgans = patient.getRequiredOrgans();
            for (OrganReceival organReceival : receivingOrgans.values()) {
                if (organReceival.getDonorNhi() != null) {
                    patients.add(patientDataService.getPatientByNhi(organReceival.getDonorNhi()));
                }
            }
        }
        GUIMap.getJSBridge().call("showAssignments", patients);
    }

    /**
     * Method to assign an organ to a receiver from a donor. To match the donor/receiver pair via the organ
     * @param donorNhiStr - string format of the donor nhi
     * @param receiverNhiStr - string format of the receiver nhi
     * @param organStr - string format of the organ
     */
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
        UndoRedoControl.getUndoRedoControl().addAction(action, undoableScreen, userControl.getLoggedInUser());
        userActions.log(Level.INFO, "Assigned organ (" + organ + ") to patient " + receiver.getNhiNumber(), "Attempted to assign organ to patient");
    }
}