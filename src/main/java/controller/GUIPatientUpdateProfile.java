package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.AnchorPane;
import model.Patient;
import service.Database;
import utility.GlobalEnums;
import utility.undoRedo.StatesHistoryScreen;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Pattern;

import static utility.UserActionHistory.userActions;

public class GUIPatientUpdateProfile extends UndoableController implements IPopupable {

    @FXML
    private AnchorPane patientUpdateAnchorPane;

    @FXML
    private Label lastModifiedLbl;

    @FXML
    private TextField nhiTxt;

    @FXML
    private TextField firstnameTxt;

    @FXML
    private TextField lastnameTxt;

    @FXML
    private TextField middlenameTxt;

    @FXML
    private RadioButton genderMaleRadio;

    @FXML
    private RadioButton genderFemaleRadio;

    @FXML
    private RadioButton genderOtherRadio;

    @FXML
    private DatePicker dobDate;

    @FXML
    private DatePicker dateOfDeath;

    @FXML
    private TextField street1Txt;

    @FXML
    private TextField street2Txt;

    @FXML
    private TextField suburbTxt;

    @FXML
    private ChoiceBox<String> regionDD;

    @FXML
    private TextField zipTxt;

    @FXML
    private TextField weightTxt;

    @FXML
    private TextField heightTxt;

    @FXML
    private ChoiceBox<String> bloodGroupDD;

    private Patient target;

    public void setViewedPatient(Patient patient) {
        target = patient;
        loadProfile(target.getNhiNumber());
    }

    public void initialize() {

        populateDropdowns();

        if (ScreenControl.getLoggedInPatient() != null) {
            loadProfile(ScreenControl.getLoggedInPatient()
                    .getNhiNumber());
        }

        // Enter key
        patientUpdateAnchorPane.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                saveProfile();
            }
        });
    }

    /**
     * Populates drop down menus that represent enum data
     */
    private void populateDropdowns() {
        // Populate blood group drop down with values from the Blood groups enum
        List<String> bloodGroups = new ArrayList<>();
        for (GlobalEnums.BloodGroup bloodgroup : GlobalEnums.BloodGroup.values()) {
            bloodGroups.add(bloodgroup.getValue());
        }
        ObservableList<String> bloodGroupsOL = FXCollections.observableList(bloodGroups);
        bloodGroupDD.setItems(bloodGroupsOL);

        // Populate region drop down with values from the Regions enum
        List<String> regions = new ArrayList<>();
        for (GlobalEnums.Region region : GlobalEnums.Region.values()) {
            regions.add(region.getValue());
        }
        ObservableList<String> regionsOL = FXCollections.observableList(regions);
        regionDD.setItems(regionsOL);

    }


    /**
     * Loads the patient's profile into the gui
     *
     * @param nhi the NHI of the patient to load
     */
    private void loadProfile(String nhi) {
        try {
            Patient patient = Database.getPatientByNhi(nhi);
            target = patient;
            populateForm(patient);

            controls = new ArrayList<Control>() {{
                add(nhiTxt);
                add(firstnameTxt);
                add(lastnameTxt);
                add(middlenameTxt);
                add(bloodGroupDD);
                add(regionDD);
                add(dobDate);
                add(dateOfDeath);
                add(genderMaleRadio);
                add(genderFemaleRadio);
                add(genderOtherRadio);
                add(street1Txt);
                add(street2Txt);
                add(suburbTxt);
                add(weightTxt);
                add(heightTxt);
                add(zipTxt);
            }};
            statesHistoryScreen = new StatesHistoryScreen(controls, GlobalEnums.UndoableScreen.PATIENTUPDATEPROFILE);

        }
        catch (InvalidObjectException e) {
            userActions.log(Level.SEVERE, "Error loading logged in user", "attempted to edit the logged in user");
        }
    }


    /**
     * Populates the scene controls with values from the patient object
     *
     * @param patient the patient object whose attributes are used to load into the form
     */
    private void populateForm(Patient patient) {
        lastModifiedLbl.setText("Last Modified: " + patient.getModified());
        nhiTxt.setText(patient.getNhiNumber());
        firstnameTxt.setText(patient.getFirstName());
        lastnameTxt.setText(patient.getLastName());
        for (String name : patient.getMiddleNames()) {
            middlenameTxt.setText(middlenameTxt.getText() + name + " ");
        }
        if (patient.getGender() != null) {
            switch (patient.getGender()
                    .getValue()) {
                case "male":
                    genderMaleRadio.setSelected(true);
                    break;
                case "female":
                    genderFemaleRadio.setSelected(true);
                    break;
                case "other":
                    genderOtherRadio.setSelected(true);
                    break;
            }
        }
        dobDate.setValue(patient.getBirth());
        dateOfDeath.setValue(patient.getDeath());
        if (patient.getStreet1() != null) {
            street1Txt.setText(patient.getStreet1());
        }
        if (patient.getStreet2() != null) {
            street2Txt.setText(patient.getStreet2());
        }
        if (patient.getSuburb() != null) {
            suburbTxt.setText(patient.getSuburb());
        }
        if (patient.getRegion() != null) {
            regionDD.setValue(patient.getRegion()
                    .getValue());
        }
        if (patient.getZip() != 0) {
            zipTxt.setText(String.valueOf(patient.getZip()));
            while (zipTxt.getText()
                    .length() < 4) {
                zipTxt.setText("0" + zipTxt.getText());
            }
        }
        weightTxt.setText(String.valueOf(patient.getWeight()));
        heightTxt.setText(String.valueOf(patient.getHeight()));
        if (patient.getBloodGroup() != null) {
            bloodGroupDD.setValue(patient.getBloodGroup()
                    .getValue());
        }
    }


    /**
     * Checks for invalidity of a double used for height or weight.
     * Returns true if input is not a valid double or the input is a valid double with a value of less than 0.
     *
     * @param input String input from text field
     * @return boolean is invalid
     */
    private boolean isInvalidDouble(String input) {
        try {
            double value = Double.parseDouble(input);
            return (value < 0);
        }
        catch (NumberFormatException e) {
            return true;
        }
    }


    /**
     * Saves profile changes after checking each field for validity
     */
    public void saveProfile() {
        Boolean valid = true;

        // nhi
        if (!Pattern.matches("[A-Z]{3}[0-9]{4}",
                nhiTxt.getText()
                        .toUpperCase())) {
            valid = setInvalid(nhiTxt);
        }
        else {
            setValid(nhiTxt);
        }

        // first name
        if (!firstnameTxt.getText()
                .matches("([A-Za-z]+[.]*[-]*[\\s]*)+")) {
            valid = setInvalid(firstnameTxt);
        }
        else {
            setValid(firstnameTxt);
        }

        // last name
        if (!lastnameTxt.getText()
                .matches("([A-Za-z]+[.]*[-]*[\\s]*)+")) {
            valid = setInvalid(lastnameTxt);
        }
        else {
            setValid(lastnameTxt);
        }

        //middle names
        if (!middlenameTxt.getText()
                .matches("([A-Za-z]+[.]*[-]*[\\s]*)*")) {
            valid = setInvalid(middlenameTxt);
        }
        else {
            setValid(middlenameTxt);
        }

        // region
        if (regionDD.getSelectionModel()
                .getSelectedIndex() != -1) {
            Enum region = GlobalEnums.Region.getEnumFromString(regionDD.getSelectionModel()
                    .getSelectedItem());
            if (region == null) {
                valid = setInvalid(regionDD);
            }
            else {
                setValid(regionDD);
            }
        }
        else {
            setValid(regionDD);
        }

        // zip
        if (!zipTxt.getText()
                .equals("")) {
            try {
                if (zipTxt.getText()
                        .length() != 4 && !(zipTxt.getText()
                        .equals(""))) {
                    valid = setInvalid(zipTxt);
                }
                else {
                    Integer.parseInt(zipTxt.getText());
                    setValid(zipTxt);
                }
            }
            catch (NumberFormatException e) {
                valid = setInvalid(zipTxt);
            }
        }
        else {
            setValid(zipTxt);
        }

        // weight
        if (weightTxt.getText() != null) {
            if (isInvalidDouble(weightTxt.getText())) {
                valid = setInvalid(weightTxt);
            }
            else {
                setValid(weightTxt);
            }
        }
        else {
            setValid(weightTxt);
        }

        // height
        if (heightTxt.getText() != null) {
            if (isInvalidDouble(heightTxt.getText())) {
                valid = setInvalid(heightTxt);
            }
            else {
                setValid(heightTxt);
            }
        }
        else {
            setValid(heightTxt);
        }

        // blood group
        if (bloodGroupDD.getValue() != null) {
            String bgStr = bloodGroupDD.getValue()
                    .replace(' ', '_');
            Enum bloodgroup = GlobalEnums.BloodGroup.getEnumFromString(bgStr);
            if (bloodgroup == null) {
                valid = setInvalid(bloodGroupDD);
            }
            else {
                setValid(bloodGroupDD);
            }
        }
        else {
            setValid(bloodGroupDD);
        }

        // date of birth
        if (dobDate.getValue() != null) {
            if (dobDate.getValue()
                    .isAfter(LocalDate.now())) {
                valid = setInvalid(dobDate);
            }
            else {
                setValid(dobDate);
            }
        }
        else {
            valid = setInvalid(dobDate);
        }

        // date of death
        if (dateOfDeath.getValue() != null) {
            if ((dobDate.getValue() != null && dateOfDeath.getValue()
                    .isBefore(dobDate.getValue())) || dateOfDeath.getValue()
                    .isAfter(LocalDate.now())) {
                valid = setInvalid(dateOfDeath);
            }
            else {
                setValid(dateOfDeath);
            }
        }
        else {
            setValid(dateOfDeath);
        }

        // if all are valid
        if (valid) {
            target.setNhiNumber(nhiTxt.getText());
            target.setFirstName(firstnameTxt.getText());
            target.setLastName(lastnameTxt.getText());
            if (middlenameTxt.getText()
                    .equals("")) {
                target.setMiddleNames(new ArrayList<>());
            }
            else {
                List<String> middlenames = Arrays.asList(middlenameTxt.getText()
                        .split(" "));
                ArrayList<String> middles = new ArrayList<>(middlenames);
                target.setMiddleNames(middles);
            }
            if (genderMaleRadio.isSelected()) {
                target.setGender((GlobalEnums.Gender) GlobalEnums.Gender.getEnumFromString("male"));
            }
            if (genderFemaleRadio.isSelected()) {
                target.setGender((GlobalEnums.Gender) GlobalEnums.Gender.getEnumFromString("female"));
            }
            if (genderOtherRadio.isSelected()) {
                target.setGender((GlobalEnums.Gender) GlobalEnums.Gender.getEnumFromString("other"));
            }
            if (dobDate.getValue() != null) {
                target.setBirth(dobDate.getValue());
            }
            if (dateOfDeath.getValue() != null) {
                target.setDeath(dateOfDeath.getValue());
            }
            if (street1Txt.getText()
                    .length() > 0) {
                target.setStreet1(street1Txt.getText());
            }
            if (street2Txt.getText()
                    .length() > 0) {
                target.setStreet2(street2Txt.getText());
            }
            if (suburbTxt.getText()
                    .length() > 0) {
                target.setSuburb(suburbTxt.getText());
            }
            if (regionDD.getValue() != null) {
                target.setRegion((GlobalEnums.Region) GlobalEnums.Region.getEnumFromString(regionDD.getSelectionModel()
                        .getSelectedItem()));
            }
            if (zipTxt.getText() != null) {
                target.setZip(zipTxt.getText()
                        .equals("") ? 0 : Integer.parseInt(zipTxt.getText()));
            }
            if (weightTxt.getText() != null) {
                target.setWeight(Double.parseDouble(weightTxt.getText()));
            }
            if (heightTxt.getText() != null) {
                target.setHeight(Double.parseDouble(heightTxt.getText()));
            }
            if (bloodGroupDD.getValue() != null) {
                target.setBloodGroup((GlobalEnums.BloodGroup) GlobalEnums.BloodGroup.getEnumFromString(bloodGroupDD.getSelectionModel()
                        .getSelectedItem()));
            }
            userActions.log(Level.INFO, "Successfully updated patient profile", "Attempted to update patient profile");
            Database.saveToDisk();
            goBackToProfile();
        }
        else {
            userActions.log(Level.WARNING, "Failed to update patient profile", "Attempted to update patient profile");
            new Alert(Alert.AlertType.WARNING, "Invalid fields", ButtonType.OK).show();
        }
    }


    /***
     * Applies the invalid class to the target control
     * @param target The target to add the class to
     */
    private boolean setInvalid(Control target) {

        target.getStyleClass()
                .add("invalid");
        return false;
    }


    /**
     * Removes the invalid class from the target control if it has it
     *
     * @param target The target to remove the class from
     */
    private void setValid(Control target) {
        if (target.getStyleClass()
                .contains("invalid")) {
            target.getStyleClass()
                    .remove("invalid");
        }
    }


    /**
     * Returns to patient profile screen
     */
    public void goBackToProfile() {
        if (ScreenControl.getLoggedInPatient() != null) {
            ScreenControl.removeScreen("patientProfile");
            try {
                ScreenControl.addScreen("patientProfile", FXMLLoader.load(getClass().getResource("/scene/patientProfile.fxml")));
                ScreenControl.activate("patientProfile");
            }
            catch (IOException e) {
                userActions.log(Level.SEVERE, "Error loading profile screen", "attempted to navigate from the edit page to the profile page");
                new Alert(Alert.AlertType.ERROR, "Error loading profile page", ButtonType.OK).show();
            }
        }
        else {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scene/patientProfile.fxml"));
            try {
                ScreenControl.loadPopUpPane(patientUpdateAnchorPane.getScene(), fxmlLoader, target);
            }
            catch (IOException e) {
                userActions.log(Level.SEVERE,
                        "Error loading profile screen in popup",
                        "attempted to navigate from the edit page to the profile page in popup");
                new Alert(Alert.AlertType.ERROR, "Error loading profile page", ButtonType.OK).show();
            }
        }
    }

}
