package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;
import model.Administrator;
import model.Clinician;
import model.Patient;
import service.Database;
import utility.GlobalEnums.Region;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Pattern;

import static java.util.logging.Level.SEVERE;
import static utility.UserActionHistory.userActions;

public class GUIPatientRegister {

    public Button doneButton;

    @FXML
    private Label backButton;

    @FXML
    private TextField firstnameRegister;

    @FXML
    private TextField lastnameRegister;

    @FXML
    private TextField middlenameRegister;

    @FXML
    private DatePicker birthRegister;

    @FXML
    private TextField userIdRegister;

    @FXML
    private ChoiceBox regionRegister;

    @FXML
    private PasswordField passwordTxt;

    @FXML
    private PasswordField verifyPasswordTxt;

    @FXML
    private RadioButton patientButton;

    @FXML
    private RadioButton clinicianButton;

    @FXML
    private RadioButton administratorButton;

    @FXML
    private AnchorPane patientRegisterAnchorPane;

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    private UserControl userControl = new UserControl();

    /**
     * Sets up register page GUI elements
     */
    public void initialize() {
        firstnameRegister.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, Event::consume);
        lastnameRegister.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, Event::consume);
        middlenameRegister.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, Event::consume);
        userIdRegister.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, Event::consume);
        setDateConverter();
        birthRegister.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, Event::consume);
        regionRegister.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, Event::consume);
        if (userControl.getLoggedInUser() instanceof Administrator) {
            ObservableList<String> regions = FXCollections.observableArrayList();
            for (Region region : Region.values()) {
                regions.add(region.getValue());
            }
            regionRegister.setItems(regions);
            backButton.setVisible(false);
            backButton.setDisable(true);
        } else {
            patientButton.setDisable(true);
            patientButton.setVisible(false);
            clinicianButton.setDisable(true);
            clinicianButton.setVisible(false);
            administratorButton.setDisable(true);
            administratorButton.setVisible(false);
        }

        // Enter key
        patientRegisterAnchorPane.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                register();
            }
        });
    }

    /**
     * Sets the registry fields for registering a patient
     */
    @FXML
    public void onSelectPatient() {
        clearFields();
        userIdRegister.setPromptText("NHI Number");
        regionRegister.setVisible(false);
        regionRegister.setDisable(true);
        birthRegister.setVisible(true);
        birthRegister.setDisable(false);
        passwordTxt.setVisible(false);
        passwordTxt.setDisable(true);
        verifyPasswordTxt.setVisible(false);
        verifyPasswordTxt.setDisable(true);
        userIdRegister.setVisible(true);
        userIdRegister.setDisable(false);
    }

    /**
     * Sets the registry fields for registering a clinician
     */
    @FXML
    public void onSelectClinician() {
        clearFields();
        userIdRegister.setPromptText("Staff ID");
        regionRegister.setVisible(true);
        regionRegister.setDisable(false);
        birthRegister.setVisible(false);
        birthRegister.setDisable(true);
        passwordTxt.setVisible(false);
        passwordTxt.setDisable(true);
        verifyPasswordTxt.setVisible(false);
        verifyPasswordTxt.setDisable(true);
        userIdRegister.setVisible(false);
        userIdRegister.setDisable(true);
    }

    /**
     * Sets the registry fields for registering an administrator
     */
    @FXML
    public void onSelectAdministrator() {
        clearFields();
        userIdRegister.setPromptText("Username");
        regionRegister.setVisible(false);
        regionRegister.setDisable(true);
        birthRegister.setVisible(false);
        birthRegister.setDisable(true);
        passwordTxt.setVisible(true);
        passwordTxt.setDisable(false);
        verifyPasswordTxt.setVisible(true);
        verifyPasswordTxt.setDisable(false);
        userIdRegister.setVisible(true);
        userIdRegister.setDisable(false);
    }

    /**
     * Back button listener to switch to the login screen
     */
    @FXML
    public void goBackToLogin() {
        clearFields();
        returnToPreviousPage();
    }


    /**
     * Clears the data in the fields of the GUI
     */
    private void clearFields() {
        userIdRegister.clear();
        firstnameRegister.clear();
        lastnameRegister.clear();
        middlenameRegister.clear();
        birthRegister.getEditor().clear();
        regionRegister.valueProperty().set(null);

        setValid(userIdRegister);
        setValid(firstnameRegister);
        setValid(lastnameRegister);
        setValid(middlenameRegister);
        setValid(birthRegister);
    }

    /**
     * Sets the date picker format to be yyyy-MM-dd
     */
    private void setDateConverter() {
        StringConverter<LocalDate> dateConverter = new StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");


            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }


            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        };
        birthRegister.setConverter(dateConverter);
    }

    /**
     * Validates the name fields that are common to each account type
     *
     * @return Whether the fields are valid
     */
    private boolean validateNames() {
        boolean valid = true;
        // first name
        if (!firstnameRegister.getText()
                .matches("([A-Za-z]+[.]*[-]*[\\s]*)+")) {
            valid = setInvalid(firstnameRegister);
        } else {
            setValid(firstnameRegister);
        }
        // last name
        if (!lastnameRegister.getText()
                .matches("([A-Za-z]+[.]*[-]*[\\s]*)+")) {
            valid = setInvalid(lastnameRegister);
        } else {
            setValid(lastnameRegister);
        }
        //middle names
        if (!middlenameRegister.getText()
                .matches("([A-Za-z]+[.]*[-]*[\\s]*)*")) {
            valid = setInvalid(middlenameRegister);
        } else {
            setValid(middlenameRegister);
        }
        return valid;
    }

    /**
     * Validates the input fields for a new patient account
     *
     * @return Whether the fields are valid
     */
    private boolean validatePatient() {
        boolean valid = true;
        // nhi
        if (!Pattern.matches("[A-Za-z]{3}[0-9]{4}", userIdRegister.getText().toUpperCase())) {
            valid = setInvalid(userIdRegister);
            new Alert(Alert.AlertType.ERROR, "NHI must be 3 characters followed by 4 numbers").showAndWait();
        } else if (Database.isPatientInDb(userIdRegister.getText())) {
            // checks to see if nhi already in use
            valid = setInvalid(userIdRegister);
            new Alert(Alert.AlertType.ERROR, "Patient with the given NHI already exists").showAndWait();
        } else {
            setValid(userIdRegister);
        }
        // date of birth
        if (birthRegister.getValue() != null) {
            if (birthRegister.getValue()
                    .isAfter(LocalDate.now())) {
                valid = setInvalid(birthRegister);
            } else {
                setValid(birthRegister);
            }
        } else {
            valid = setInvalid(birthRegister);
        }
        return valid;
    }

    /**
     * Validates the input fields for a new clinician account
     *
     * @return Whether the fields are valid
     */
    private boolean validateClinician() {
        boolean valid = true;
        if (regionRegister.getValue() != null) {
            setValid(birthRegister);
        } else {
            valid = setInvalid(regionRegister);
        }
        return valid;
    }

    /**
     * Validates the input fields for a new administrator account
     *
     * @return Whether the fields are valid
     */
    private boolean validateAdministrator() {
        boolean valid = validateNames();
        if (!userIdRegister.getText()
                .matches("([A-Za-z0-9]+[-]*[_]*)+")) {
            valid = setInvalid(userIdRegister);
        } else {
            setValid(userIdRegister);
        }
        if (passwordTxt.getText().length() < 6) {
            valid = setInvalid(passwordTxt);
            new Alert(Alert.AlertType.ERROR, "Password must be 6 or more characters").showAndWait();
        } else {
            setValid(passwordTxt);
        }
        if (!verifyPasswordTxt.getText().equals(passwordTxt.getText())) {
            valid = setInvalid(verifyPasswordTxt);
            if (passwordTxt.getText().length() >= 6) {
                new Alert(Alert.AlertType.ERROR, "Passwords do not match").showAndWait();
            }
        } else {
            setValid(verifyPasswordTxt);
        }
        return valid;
    }

    /**
     * Check users inputs for validity and registers the user patient profile
     */
    @FXML
    public void register() {
        if (patientButton.isSelected()) {
            if (!validatePatient()) {
                userActions.log(Level.WARNING, "Failed to register patient profile due to invalid fields", "Attempted to register patient profile");
                return;
            }
        } else if (clinicianButton.isSelected()) {
            if (!validateClinician()) {
                userActions.log(Level.WARNING, "Failed to register clinician profile due to invalid fields", "Attempted to register clinician profile");
                return;
            }
        } else {
            if (!validateAdministrator()) {
                userActions.log(Level.WARNING, "Failed to register administrator profile due to invalid fields", "Attempted to register administrator profile");
                return;
            }
        }

        // if all are valid
        String id = userIdRegister.getText();
        String firstName = firstnameRegister.getText();
        String lastName = lastnameRegister.getText();
        String password = passwordTxt.getText();
        ArrayList<String> middles = new ArrayList<>();
        String alertMsg;
        if (!middlenameRegister.getText().equals("")) {
            List<String> middleNames = Arrays.asList(middlenameRegister.getText().split(" "));
            middles = new ArrayList<>(middleNames);
        }
        if (patientButton.isSelected()) {
            LocalDate birth = birthRegister.getValue();
            Database.addPatient(new Patient(id, firstName, middles, lastName, birth));
            userActions.log(Level.INFO, "Successfully registered patient profile", "Attempted to register patient profile");
            alertMsg = "Successfully registered patient with NHI " + id;
        } else if (clinicianButton.isSelected()) {
            String region = regionRegister.getValue().toString();
            int staffID = Database.getNextStaffID();
            Database.addClinician(new Clinician(staffID, firstName, middles, lastName, (Region) Region.getEnumFromString(region)));
            userActions.log(Level.INFO, "Successfully registered clinician profile", "Attempted to register clinician profile");
            alertMsg = "Successfully registered clinician with staff ID " + staffID;
        } else {
            Database.addAdministrator(new Administrator(id, firstName, middles, lastName, password));
            userActions.log(Level.INFO, "Successfully registered administrator profile", "Attempted to register administrator profile");
            alertMsg = "Successfully registered administrator with username " + id;
        }
        clearFields();
        new Alert(Alert.AlertType.INFORMATION, alertMsg).showAndWait();
        if (userControl.getLoggedInUser() == null) {
            returnToPreviousPage();
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

    private void returnToPreviousPage() {
        try {
            if (userControl.getLoggedInUser() != null && userControl.getLoggedInUser() instanceof Administrator) {
                screenControl.show(patientRegisterAnchorPane, "/scene/administratorHome.fxml");
            } else {
                screenControl.show(Main.getUuid(), FXMLLoader.load(getClass().getResource("/scene/login.fxml")));
            }
        } catch (IOException e) {
            new Alert((Alert.AlertType.ERROR), "Unable to load login").show();
            userActions.log(SEVERE, "Failed to load login", "Attempted to load login");
        }
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
