package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.Administrator;
import model.Clinician;
import service.AdministratorDataService;
import service.ClinicianDataService;
import service.interfaces.IClinicianDataService;
import utility.GlobalEnums;
import utility.undoRedo.Action;
import utility.undoRedo.StatesHistoryScreen;
import utility.undoRedo.UndoableWrapper;

import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

public class GUIClinicianProfile extends TargetedController {
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

    private UserControl userControl = UserControl.getUserControl();

    private UndoRedoControl undoRedoControl = UndoRedoControl.getUndoRedoControl();

    private IClinicianDataService clinicianDataService = new ClinicianDataService();

    /**
     * Initializes the clinician profile view screen by loading the logged in clinician's profile
     */
    public void load() {
        Clinician clinicianToLoad = clinicianDataService.getClinician(((Clinician) target).getStaffID());
        if (userControl.getLoggedInUser() instanceof Clinician) {
            deleteButton.setVisible(false);
            deleteButton.setDisable(true);
            loadProfile(clinicianToLoad);
        } else if (userControl.getLoggedInUser() instanceof Administrator) {
            loadProfile(clinicianToLoad);
            if (((Clinician) target).getStaffID() == 0) {
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
        if (((Clinician) target).getStaffID() != 0) {
            Action action = new Action((target), null);
            new AdministratorDataService().deleteUser(target);
            undoRedoControl.addAction(action, GlobalEnums.UndoableScreen.ADMINISTRATORSEARCHUSERS);
            userActions.log(Level.INFO, "Successfully deleted clinician profile", new String[]{"Attempted to delete clinician profile", String.valueOf(((Clinician) target).getStaffID())});
            ((Stage) clinicianProfilePane.getScene().getWindow()).close();
        }
    }
}
