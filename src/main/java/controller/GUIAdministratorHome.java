package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import service.Database;

import java.io.IOException;

import static java.util.logging.Level.SEVERE;
import static utility.UserActionHistory.userActions;

public class GUIAdministratorHome {
    @FXML
    private Button searchUsers;
    @FXML
    private AnchorPane administratorHomePane;
    @FXML
    private Button importDataButton;
    @FXML
    private Button waitingListButton;
    @FXML
    private Button registerButton;
    @FXML
    private Button profileButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Button history;
    @FXML
    private Button importPatientsButton;
    @FXML
    private Button importCliniciansButton;
    @FXML
    private Button importWaitListButton;

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    private UserControl userControl = new UserControl();

    /**
     * Opens the administrator profile screen
     */
    @FXML
    public void goToAdministratorProfile() {
        try {
            screenControl.show(administratorHomePane, "/scene/administratorProfile.fxml");
        } catch (IOException e) {
            new Alert((Alert.AlertType.ERROR), "Unable to load administrator profile").show();
            userActions.log(SEVERE, "Failed to load administrator profile", "Attempted to load administrator profile");
        }
    }

    /**
     * Opens the register user profile screen
     */
    @FXML
    public void registerNewUser() {
        try {
            screenControl.show(administratorHomePane, "/scene/administratorCreateUser.fxml");
        } catch (IOException e) {
            new Alert((Alert.AlertType.ERROR), "Unable to load user register").show();
            userActions.log(SEVERE, "Failed to load user register", "Attempted to load user register");
        }
    }

    /**
     * Opens the user search screen
     */
    @FXML
    public void goToSearchUsers() {
        try {
            //todo
            screenControl.show(administratorHomePane, "/scene/administratorSearchClinicians.fxml");
        } catch (IOException e) {
            new Alert((Alert.AlertType.ERROR), "Unable to load search users").show();
            userActions.log(SEVERE, "Failed to load search users", "Attempted to load search users");
        }
    }

    /**
     * Logs out the administrator and closes the open window for the logged in administrator
     */
    @FXML
    public void logOutAdministrator() {
        screenControl.closeStage(administratorHomePane);
        userControl.setTargetPatient(null);
    }

    /**
     * Saves changes made by the administrator to the database
     */
    @FXML
    public void saveAdministrator() {
        Database.saveToDisk();
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Successfully Saved!");
        alert.show();
    }

    /**
     * Opens the administrator history screen
     */
    @FXML
    public void goToHistory() {
        try {
            screenControl.show(administratorHomePane, "/scene/administratorHome.fxml");
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Unable load administrator history").show();
            userActions.log(SEVERE, "Failed to load administrator history", "Attempted to load administrator history");
        }
    }

    /**
     * Opens the receiver waiting list screen
     */
    public void goToClinicianWaitingList() {
        try {
            screenControl.show(administratorHomePane, "/scene/clinicianWaitingList.fxml");
        } catch (IOException e) {
            new Alert((Alert.AlertType.ERROR), "ERROR loading organ waiting list page").show();
            userActions.log(SEVERE, "Error loading organ waiting list screen", "attempted to navigate from the " +
                    "home page to the waiting list page");
        }
    }

    /**
     * Imports patient data from disk
     */
    public void importPatients() {
        try {
            Database.importFromDiskPatients("./patient.json");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Imports clinician data from disk
     */
    public void importClinicians() {
        try {
            Database.importFromDiskClinicians("./clinician.json");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Imports wait list data from disk
     */
    public void importWaitList() {
        try {
            Database.importFromDiskWaitlist("/waitlist.json");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Imports data from disk
     */
    public void importData() {
        importPatients();
        importClinicians();
        importWaitList();
    }
}

