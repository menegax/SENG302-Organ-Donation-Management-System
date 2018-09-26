package controller;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import model.Clinician;
import service.ClinicianDataService;
import utility.GlobalEnums;
import utility.GlobalEnums.Region;
import utility.GlobalEnums.UIRegex;
import utility.TouchComboBoxSkin;
import utility.undoRedo.IAction;
import utility.undoRedo.SingleAction;
import utility.undoRedo.StatesHistoryScreen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Pattern;

import static utility.UserActionHistory.userActions;

/**
 * Controller class to control GUI Clinician updating screen.
 */
public class GUIClinicianUpdateProfile extends UndoableController {

    @FXML
    private GridPane clinicianUpdateProfile;

    @FXML
    private Label lastModifiedLbl;

    @FXML
    private TextField staffId;

    @FXML
    private TextField firstnameTxt;

    @FXML
    private TextField lastnameTxt;

    @FXML
    private TextField middlenameTxt;

    @FXML
    private TextField street1Txt;

    @FXML
    private TextField street2Txt;

    @FXML
    private TextField suburbTxt;

    @FXML
    private ComboBox regionDD;

    private ScreenControl screenControl = ScreenControl.getScreenControl();


    /**
     * Initializes the clinician editing screen.
     * Populates the Region drop down menu using region enums.
     * Calls to loadController the clinician profile and calls to set up undo/redo functionality
     */
    public void loadController() {
        // Populate region dropdown with values from the Regions enum
        List<String> regions = new ArrayList<>();
        for (Region region : Region.values()) {
            regions.add(region.getValue());
        }
        ObservableList<String> regionsOL = FXCollections.observableList(regions);
        regionDD.setItems(regionsOL);

        // Registering a change event to clear the invalid class
        regionDD.getSelectionModel()
                .selectedIndexProperty()
                .addListener((observable, oldValue, newValue) -> setValid(regionDD));
        loadProfile(((Clinician) target).getStaffID());
        setUpStateHistory();
        if (screenControl.isTouch()) {
            new TouchComboBoxSkin(regionDD, (Pane) screenControl.getTouchParent(clinicianUpdateProfile));
        }
    }


    /**
     * Loads the currently logged in clinician from the Database and populates the tables using the logged
     * in clinician's attributes.
     *
     * @param staffId ID of clinician to loadController
     */
    private void loadProfile(int staffId) {
        ClinicianDataService dataService = new ClinicianDataService();
        Clinician clinician = dataService.getClinician(staffId); //loadController from db
        if (clinician != null) {
            target = clinician;
            populateForm(clinician);
        } else {
            userActions.log(Level.SEVERE, "Error loading clinician profile", new String[]{"Attempted to loadController clinician profile", String.valueOf(((Clinician) target).getStaffID())});
        }
    }


    /**
     * Creates a list of control elements using all editable nodes on the update screen and initializes
     * the StateHistoryScreen used to undo or redo actions using the control elements
     */
    private void setUpStateHistory() {
        controls = new ArrayList<Control>() {{
            add(staffId);
            add(firstnameTxt);
            add(lastnameTxt);
            add(middlenameTxt);
            add(street1Txt);
            add(street2Txt);
            add(suburbTxt);
            add(regionDD);
        }};
        statesHistoryScreen = new StatesHistoryScreen(controls, GlobalEnums.UndoableScreen.CLINICIANPROFILEUPDATE, target);
    }


    /**
     * Populates the update screen using the current clinician attributes
     *
     * @param clinician logged in clinician
     */
    private void populateForm(Clinician clinician) {
        //adds last modified date only if clinician has been edited before
        if (target.getModified() != null) {
            lastModifiedLbl.setText("Last Updated: " + clinician.getModified()
                    .toString());
        }
        else {
            lastModifiedLbl.setText("Last Updated: n/a");
        }
        staffId.setText(Integer.toString(clinician.getStaffID()));
        firstnameTxt.setText(clinician.getFirstName());
        lastnameTxt.setText(clinician.getLastName());
        for (String name : clinician.getMiddleNames()) {
            middlenameTxt.setText(middlenameTxt.getText() + name + " ");
        }
        if (clinician.getStreet1() != null) {
            street1Txt.setText(clinician.getStreet1());
        }
        if (clinician.getStreet2() != null) {
            street2Txt.setText(clinician.getStreet2());
        }
        if (clinician.getSuburb() != null) {
            suburbTxt.setText(clinician.getSuburb());
        }
        if (clinician.getRegion() != null) {
            regionDD.getSelectionModel()
                    .select(clinician.getRegion()
                            .getValue());
        }
    }


    /**
     * Checks fields for validity before setting clinician's updated attributes and returning to profile.
     * Changes are saved for the session, but are only persistently saved by calling save from the home page
     */
    public void saveProfile() {
        Boolean valid = true;
        if (!Pattern.matches(UIRegex.STAFFID.getValue(), staffId.getText())) {
            valid = false;
            setInvalid(staffId);
        }
        if (!Pattern.matches(UIRegex.FNAME.getValue(), firstnameTxt.getText())) {
            valid = false;
            setInvalid(firstnameTxt);
        }
        if (!Pattern.matches(UIRegex.LNAME.getValue(), lastnameTxt.getText())) {
            valid = false;
            setInvalid(lastnameTxt);
        } else {
            setValid(lastnameTxt);
        }
        if (!Pattern.matches(UIRegex.MNAME.getValue(), middlenameTxt.getText())) {
            valid = false;
            setInvalid(middlenameTxt);
        } else {
            setValid(middlenameTxt);
        }
        if (!Pattern.matches(UIRegex.STREET.getValue(), street1Txt.getText())) {
            valid = false;
            setInvalid(street1Txt);
        }
        else {
            setValid(street1Txt);
        }
        if (!Pattern.matches(UIRegex.STREET.getValue(), street2Txt.getText())) {
            valid = false;
            setInvalid(street2Txt);
        } else {
            setValid(street2Txt);
        }
        if (!Pattern.matches(UIRegex.SUBURB.getValue(), suburbTxt.getText())) {
            valid = false;
            setInvalid(suburbTxt);
        }
        else {
            setValid(suburbTxt);
        }
        if (regionDD.getSelectionModel()
                .getSelectedIndex() == -1) { // If the selected item is nothing
            valid = false;
            setInvalid(regionDD);
        }
        else {
            setValid(regionDD);
        }
        // If all the fields are entered correctly
        if (valid) {
            Clinician after = (Clinician) target.deepClone();
            after.setStaffID(Integer.parseInt(staffId.getText()));
            after.setFirstName(firstnameTxt.getText());
            after.setLastName(lastnameTxt.getText());
            List<String> middlenames = Arrays.asList(middlenameTxt.getText()
                    .split(" "));
            ArrayList<String> middles = new ArrayList<>(middlenames);
            after.setMiddleNames(middles);

            after.setStreet1(street1Txt.getText());
            after.setStreet2(street2Txt.getText());
            after.setSuburb(suburbTxt.getText());
            after.setRegion((Region) Region.getEnumFromString(regionDD.getSelectionModel()
                    .getSelectedItem()
                    .toString()));
            userActions.log(Level.INFO,
                    "Successfully updated clinician profile",
                    new String[] { "Attempted to update clinician profile", String.valueOf(after.getStaffID()) });
            after.userModified();

            IAction action = new SingleAction(target, after);
            new ClinicianDataService().save(after);
            statesHistoryScreen.addAction(action);
        }
        else {
            userActions.log(Level.WARNING, "Invalid fields", new String[]{"Attempted to update clinician profile with invalid fields", String.valueOf(((Clinician) target).getStaffID())});
        }
    }


    /***
     * Applies the invalid class to the target control
     * @param target The target to add the class to
     */
    private void setInvalid(Control target) {
        target.getStyleClass()
                .add("invalid");
    }


    /**
     * Removes the invalid class from the target control if it has it
     *
     * @param target The target to remove the class from
     */
    private void setValid(Control target) {
        target.getStyleClass()
                .remove("invalid");
    }
}
