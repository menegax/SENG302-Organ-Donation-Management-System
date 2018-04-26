package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import model.Clinician;
import service.Database;
import utility.GlobalEnums.Region;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Pattern;

import static utility.UserActionHistory.userActions;

public class GUIClinicianProfileUpdate {
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
    private ChoiceBox regionDD;

    private Clinician target;

    public void initialize() {
        // Populate region dropdown with values from the Regions enum
        List<String> regions = new ArrayList<>();
        for (Region region : Region.values()) {
            regions.add(region.getValue());
        }
        ObservableList<String> regionsOL = FXCollections.observableList(regions);
        regionDD.setItems(regionsOL);

        // Registering a change event to clear the invalid class
        regionDD.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> setValid(regionDD));

        loadProfile(ScreenControl.getLoggedInClinician().getStaffID());
    }

    private void loadProfile(int staffId) {
        try {
            Clinician clinician = Database.getClinicianByID(staffId);
            target = clinician;
            populateForm(clinician);
        } catch (InvalidObjectException e) {
            userActions.log(Level.SEVERE, "Error loading logged in user", "attempted to edit the logged in user");
            e.printStackTrace();
        }
    }

    private void populateForm(Clinician clinician) {
//        lastModifiedLbl.setText("Last Modified: " + clinician.getModified());
        staffId.setText(Integer.toString(clinician.getStaffID()));
        firstnameTxt.setText(clinician.getFirstName());
        lastnameTxt.setText(clinician.getLastName());
        for (String name : clinician.getMiddleNames()) {
            middlenameTxt.setText(middlenameTxt.getText() + name + " ");
        }
        if (clinician.getStreet1() != null) street1Txt.setText(clinician.getStreet1());
        if (clinician.getStreet2() != null) street2Txt.setText(clinician.getStreet2());
        if (clinician.getSuburb() != null) suburbTxt.setText(clinician.getSuburb());
        if (clinician.getRegion() != null) {
            regionDD.getSelectionModel().select(clinician.getRegion().getValue());
        }
    }

    public void saveProfile() {
        Boolean valid = true;
        if (!Pattern.matches("[0-9]{1,3}", staffId.getText())) {
            valid = false;
            setInvalid(staffId);
        }
        if (firstnameTxt.getText().length() == 0 || !Pattern.matches("[a-z|A-Z]{1,20}", firstnameTxt.getText())) {
            valid = false;
            setInvalid(firstnameTxt);
        }
        if (lastnameTxt.getText().length() == 0 || !Pattern.matches("[a-z|A-Z]{1,20}", lastnameTxt.getText())) {
            valid = false;
            setInvalid(lastnameTxt);
        }
        if (!Pattern.matches("[a-z|A-Z ]{0,50}", middlenameTxt.getText())) {
            valid = false;
            setInvalid(middlenameTxt);
        }
        if (!Pattern.matches("[a-z|A-Z ]{0,50}", street1Txt.getText())) {
            valid = false;
            setInvalid(street1Txt);
        }
        if (!Pattern.matches("[a-z|A-Z ]{0,50}", street2Txt.getText())) {
            valid = false;
            setInvalid(street2Txt);
        }
        if (!Pattern.matches("[a-z|A-Z ]{0,50}", suburbTxt.getText())) {
            valid = false;
            setInvalid(suburbTxt);
        }
        if (regionDD.getSelectionModel().getSelectedIndex() == -1) { // If the selected item is nothing
            valid = false;
            setInvalid(regionDD);
        }
        // If all the fields are entered correctly
        if (valid) {
            target.setStaffID(Integer.parseInt(staffId.getText()));
            target.setFirstName(firstnameTxt.getText());
            target.setLastName(lastnameTxt.getText());
            List<String> middlenames = Arrays.asList(middlenameTxt.getText().split(" "));
            ArrayList middles = new ArrayList();
            middles.addAll(middlenames);
            target.setMiddleNames(middles);

            if (street1Txt.getText().length() > 0) target.setStreet1(street1Txt.getText());
            if (street2Txt.getText().length() > 0) target.setStreet2(street2Txt.getText());
            if (suburbTxt.getText().length() > 0) target.setSuburb(suburbTxt.getText());
            target.setRegion((Region) Region.getEnumFromString(regionDD.getSelectionModel().getSelectedItem().toString()));
            new Alert(Alert.AlertType.CONFIRMATION, "Donor successfully updated", ButtonType.OK).show();
            goBackToProfile();
        } else {
            new Alert(Alert.AlertType.WARNING, "Invalid fields", ButtonType.OK).show();
        }
    }

    /***
     * Applies the invalid class to the target control
     * @param target The target to add the class to
     */
    private void setInvalid(Control target) {
        target.getStyleClass().add("invalid");
    }

    /**
     * Removes the invalid class from the target control if it has it
     *
     * @param target The target to remove the class from
     */
    private void setValid(Control target) {
        if (target.getStyleClass().contains("invalid")) {
            target.getStyleClass().remove("invalid");
        }
    }

    /**
     * Checks if the keyevent target was a textfield. If so, if the target has the invalid class, it is removed.
     *
     * @param e The KeyEvent instance
     */
    public void onKeyReleased(KeyEvent e) {
        if (e.getTarget().getClass() == TextField.class) {
            TextField target = (TextField) e.getTarget();
            setValid(target);
        }
    }

    /**
     * Navigates back to the profile window
     */
    public void goBackToProfile() {
        ScreenControl.removeScreen("clinicianProfile");
        try {
            ScreenControl.addScreen("clinicianProfile", FXMLLoader.load(getClass().getResource("/scene/clinicianProfile.fxml")));
            ScreenControl.activate("clinicianProfile");
        } catch (IOException e) {
            userActions.log(Level.SEVERE, "Error loading profile screen", "attempted to navigate from the edit page to the profile page");
            new Alert(Alert.AlertType.WARNING, "ERROR loading profile page", ButtonType.OK).showAndWait();
            e.printStackTrace();
        }
    }
}
