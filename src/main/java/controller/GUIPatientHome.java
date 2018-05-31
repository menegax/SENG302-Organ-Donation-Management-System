package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import service.Database;

import java.io.IOException;

import static java.util.logging.Level.SEVERE;
import static utility.UserActionHistory.userActions;

public class GUIPatientHome {

    @FXML
    public AnchorPane homePane;

    public Button profileButton;

    public Button historyButton;

    public Button logOutButton;

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    /**
     * Navigates to the patient profile screen
     */
    @FXML
    public void goToProfile() {
        try {
            screenControl.show(homePane, "/scene/patientProfile.fxml");
        } catch (IOException e) {
            new Alert((Alert.AlertType.ERROR), "Unable to load patient profile").show();
            userActions.log(SEVERE, "Failed to load patient profile", "Attempted to load patient profile");
        }
    }

    /**
     * Navigates to the patient history screen
     */
    @FXML
    public void goToHistory() {
        try {
            screenControl.show(homePane, "/scene/patientHistory.fxml");
        }
        catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Unable load patient history").show();
            userActions.log(SEVERE, "Failed to load patient history", "Attempted to load patient history");
        }
    }

    /**
     * Logs out the current patient and closes the patient window
     */
    @FXML
    public void logOut() {
        UserControl userControl = new UserControl();
        userControl.clearCache();
        screenControl.closeStage(homePane);
    }

    /**
     * Saves changes to the patient to the database
     */
    @FXML
    public void save() {
        Database.saveToDisk();
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Successfully saved!");
        alert.show();
    }
}
