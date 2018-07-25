package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.Administrator;
import model.Clinician;
import service.Database;
import utility.GlobalEnums;
import utility.StatusObservable;
import utility.undoRedo.Action;
import utility.undoRedo.StatesHistoryScreen;
import utility.undoRedo.UndoableStage;

import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

public class GUIClinicianProfile {
    @FXML
    private GridPane clinicianProfilePane;

    @FXML
    private Label idTxt;

    @FXML
    private Label nameTxt;

    @FXML
    private Label street1Txt;

    @FXML
    private Label street2Txt;

    @FXML
    private Label suburbTxt;

    @FXML
    private Label regionTxt;

    @FXML
    private Button deleteButton;

    private UserControl userControl = new UserControl();
    
    private Database database = Database.getDatabase();

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    /**
     * Initializes the clinician profile view screen by loading the logged in clinician's profile
     */
    public void initialize() {
        if (userControl.getLoggedInUser() instanceof Clinician) {
            deleteButton.setVisible(false);
            deleteButton.setDisable(true);
            loadProfile(((Clinician) userControl.getLoggedInUser()));
        } else if (userControl.getLoggedInUser() instanceof Administrator) {
            loadProfile(((Clinician) userControl.getTargetUser()));
            if (((Clinician) userControl.getTargetUser()).getStaffID() == 0) {
                deleteButton.setVisible(false);
                deleteButton.setDisable(true);
            }
        }
    }

    /**
     * Loads clinician attributes to display in the clinician profile screen
     *
     * @param clinician clinician logged in
     */
    private void loadProfile(Clinician clinician) {
        idTxt.setText(String.valueOf(clinician.getStaffID()));
        nameTxt.setText(clinician.getNameConcatenated());
        if (clinician.getStreet1() != null && clinician.getStreet1().length() > 0) {
            street1Txt.setText(clinician.getStreet1());
        } else {
            street1Txt.setText("Not Set");
        }
        if (clinician.getStreet2() != null && clinician.getStreet2().length() > 0) {
            street2Txt.setText(clinician.getStreet2());
        } else {
            street2Txt.setText("Not Set");
        }
        if (clinician.getSuburb() != null && clinician.getSuburb().length() > 0) {
            suburbTxt.setText(clinician.getSuburb());
        } else {
            suburbTxt.setText("Not Set");
        }
        if (clinician.getRegion() != null) regionTxt.setText(clinician.getRegion().getValue());
    }

    /**
     * Deletes the current profile from the HashSet in Database, not from disk, not until saved
     */
    public void deleteProfile() {
        Clinician clinician = (Clinician) userControl.getTargetUser();
        if (clinician.getStaffID() != 0) {
            Action action = new Action(clinician, null);
            for (Stage stage : screenControl.getUsersStages(userControl.getLoggedInUser())) {
                if (stage instanceof UndoableStage) {
                    for (StatesHistoryScreen statesHistoryScreen : ((UndoableStage) stage).getStatesHistoryScreens()) {
                        if (statesHistoryScreen.getUndoableScreen().equals(GlobalEnums.UndoableScreen.ADMINISTRATORSEARCHUSERS)) {
                            statesHistoryScreen.addAction(action);
                        }
                    }
                }
            }
            userActions.log(Level.INFO, "Successfully deleted clinician profile", new String[]{"Attempted to delete clinician profile", String.valueOf(clinician.getStaffID())});
            database.delete(clinician);
            ((Stage) clinicianProfilePane.getScene().getWindow()).close();
        }
    }
}
