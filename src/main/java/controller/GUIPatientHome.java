package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import service.Database;
import utility.undoRedo.UndoableStage;

import java.io.IOException;

public class GUIPatientHome {

    @FXML
    public AnchorPane homePane;

    public Button profileButton;

    public Button historyButton;

    public Button saveButton;

    public Button logOutButton;

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    @FXML
    public void goToProfile() throws IOException {
        screenControl.show(((UndoableStage) homePane.getScene().getWindow()).getUUID(),FXMLLoader.load(getClass().getResource("/scene/patientProfile.fxml")));
    }


    @FXML
    public void goToHistory() {
        try {
            ScreenControl.addTabToHome("patientHistory", FXMLLoader.load(getClass().getResource("/scene/patientHistory.fxml")));
        }
        catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Unable load patient history").show();
        }
        ScreenControl.activate("patientHistory");
    }


    @FXML
    public void logOut() {
        UserControl userControl = new UserControl();
        userControl.clearCahce();
        ScreenControl.activate("login");
    }


    @FXML
    public void save() {
        Database.saveToDisk();
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Successfully saved!");
        alert.show();
    }
}
