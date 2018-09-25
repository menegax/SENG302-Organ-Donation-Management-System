package utility;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Patient;
import model.PatientOrgan;
import service.ClinicianDataService;
import service.OrganWaitlist;
import service.PatientDataService;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import static java.lang.Math.abs;

public class PotentialMatchFinder {

    private List<OrganWaitlist.OrganRequest> requests;

    private ObservableList<OrganWaitlist.OrganRequest> allRequests = FXCollections.observableArrayList();

    private PatientDataService patientDataService = new PatientDataService();

    private Patient patient;

    private GlobalEnums.Organ organ;

    private ClinicianDataService clinicianDataService;

    private OrganWaitlist organWaitList;


    public ObservableList<OrganWaitlist.OrganRequest> matchOrgan(PatientOrgan patientOrgan) {
        patient = patientOrgan.getPatient();
        organ = patientOrgan.getOrgan();
        clinicianDataService = new ClinicianDataService();
        organWaitList = clinicianDataService.getOrganWaitList();
        requests = new ArrayList<>();
        for (OrganWaitlist.OrganRequest request: organWaitList) {
            if(checkMatch(request)) {
                allRequests.add(request);
            }
        }
        return allRequests;
    }

    /**
     * Checks that the provided request matches the organ being donated
     *
     * @param request the potential match
     * @return whether the match is valid or not
     */
    private boolean checkMatch(OrganWaitlist.OrganRequest request) {
        boolean match = true;
        Patient potentialReceiver = patientDataService.getPatientByNhi(request.getReceiverNhi());
        //Do not match against patients that have no address or region
        if (potentialReceiver.getRegion() == null || (potentialReceiver.getStreetNumber() == null && potentialReceiver.getStreetName() == null)) {
            return false;
        }
        long requestAge = ChronoUnit.DAYS.between(request.getBirth(), LocalDate.now());
        long targetAge = ChronoUnit.DAYS.between((patient).getBirth(), (patient).getDeathDate());
        if (request.getRequestedOrgan() != organ || request.getBloodGroup() != (patient).getBloodGroup() || request.getReceiver().getDeathDate() != null) {
            match = false;
        }
        else if ((requestAge < 4383 && targetAge > 4383) || (requestAge > 4383 && targetAge < 4383) || abs(requestAge - targetAge) > 5478.75) {
            match = false;
        }
        return match;
    }

    public List<OrganWaitlist.OrganRequest> getRequests() {
        return requests;
    }
}
