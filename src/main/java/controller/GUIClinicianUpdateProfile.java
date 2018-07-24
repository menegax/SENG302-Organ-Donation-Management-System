package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import model.Clinician;
import model.User;
import service.Database;
import utility.GlobalEnums;
import utility.GlobalEnums.Region;
import utility.StatusObservable;
import utility.undoRedo.StatesHistoryScreen;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Pattern;

import static java.util.logging.Level.SEVERE;
import static utility.UserActionHistory.userActions;

/**
 * Controller class to control GUI Clinician updating screen.
 */
public class GUIClinicianUpdateProfile extends UndoableController {

    @FXML
    public AnchorPane clinicianUpdateAnchorPane;

    @FXML
    private Label lastModifiedLbl;

    @FXML
    private TextField staffId;

    @FXML
    private TextField firstnameUpdateTxt;

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
    private ChoiceBox regionDD;

    private Clinician target;

    private ScreenControl screenControl = ScreenControl.getScreenControl();


    /**
     * Initializes the clinician editing screen.
     * Populates the Region drop down menu using region enums.
     * Calls to load the clinician profile and calls to set up undo/redo functionality
     */
    public void initialize() {
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
        UserControl userControl = new UserControl();
        User loggedIn = userControl.getLoggedInUser();
        if (loggedIn instanceof Clinician) {
            target = (Clinician) loggedIn;
        }
        else {
            target = (Clinician) userControl.getTargetUser();
        }
        loadProfile(target.getStaffID());
        setUpStateHistory();
    }


    /**
     * Loads the currently logged in clinician from the Database and populates the tables using the logged
     * in clinician's attributes.
     *
     * @param staffId ID of clinician to load
     */
    private void loadProfile(int staffId) {
        try {
            Clinician clinician = Database.getClinicianByID(staffId);
            target = clinician;
            populateForm(clinician);
        }
        catch (InvalidObjectException e) {
            userActions.log(Level.SEVERE, "Error loading logged in user", "attempted to edit the logged in user");
        }
    }


    /**
     * Creates a list of control elements using all editable nodes on the update screen and initializes
     * the StateHistoryScreen used to undo or redo actions using the control elements
     */
    private void setUpStateHistory() {
        controls = new ArrayList<Control>() {{
            add(staffId);
            add(firstnameUpdateTxt);
            add(lastnameTxt);
            add(middlenameTxt);
            add(street1Txt);
            add(street2Txt);
            add(suburbTxt);
            add(regionDD);
        }};
        statesHistoryScreen = new StatesHistoryScreen(controls, GlobalEnums.UndoableScreen.CLINICIANPROFILEUPDATE);
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
        firstnameUpdateTxt.setText(clinician.getFirstName());
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
        if (!Pattern.matches("[0-9]{1,3}", staffId.getText())) {
            valid = false;
            setInvalid(staffId);
        }
        String newName = firstnameUpdateTxt.getText();
        if (firstnameUpdateTxt.getText()
                .length() == 0 || !Pattern.matches("[a-z|A-Z]{1,20}", newName)) {
            valid = false;
            setInvalid(firstnameUpdateTxt);
        }
        else {
            setValid(firstnameUpdateTxt);
        }
        if (lastnameTxt.getText()
                .length() == 0 || !Pattern.matches("[a-z|A-Z]{1,20}", lastnameTxt.getText())) {
            valid = false;
            setInvalid(lastnameTxt);
        }
        else {
            setValid(lastnameTxt);
        }
        if (!Pattern.matches("[a-z|A-Z ]{0,50}", middlenameTxt.getText())) {
            valid = false;
            setInvalid(middlenameTxt);
        }
        else {
            setValid(middlenameTxt);
        }
        if (!Pattern.matches("[0-9|a-z|A-Z ]{0,50}", street1Txt.getText())) {
            valid = false;
            setInvalid(street1Txt);
        }
        else {
            setValid(street1Txt);
        }
        if (!Pattern.matches("[0-9|a-z|A-Z ]{0,50}", street2Txt.getText())) {
            valid = false;
            setInvalid(street2Txt);
        }
        else {
            setValid(street2Txt);
        }
        if (!Pattern.matches("[a-z|A-Z ]{0,50}", suburbTxt.getText())) {
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
            target.setStaffID(Integer.parseInt(staffId.getText()));
            target.setFirstName(firstnameUpdateTxt.getText());
            target.setLastName(lastnameTxt.getText());
            List<String> middlenames = Arrays.asList(middlenameTxt.getText()
                    .split(" "));
            ArrayList<String> middles = new ArrayList<>(middlenames);
            target.setMiddleNames(middles);

            target.setStreet1(street1Txt.getText());
            target.setStreet2(street2Txt.getText());
            target.setSuburb(suburbTxt.getText());
            target.setRegion((Region) Region.getEnumFromString(regionDD.getSelectionModel()
                    .getSelectedItem()
                    .toString()));
            userActions.log(Level.INFO,
                    "Successfully updated clinician profile",
                    new String[] { "Attempted to update clinician profile", String.valueOf(target.getStaffID()) });
            target.userModified();
            screenControl.setIsSaved(false);
        }
        else {
            userActions.log(Level.WARNING, "Invalid fields", "Attempted to update clinician profile with invalid fields");
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
