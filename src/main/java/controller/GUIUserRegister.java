package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import model.Administrator;
import model.Clinician;
import model.Patient;
import service.Database;
import utility.GlobalEnums.Region;
import utility.GlobalEnums.UIRegex;

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

public class GUIUserRegister {

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
    private GridPane userRegisterPane;

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    private UserControl userControl = new UserControl();
    
    private Database database = Database.getDatabase();

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
            userRegisterPane.getRowConstraints().get(0).setMaxHeight(0);
        } else {
            patientButton.setDisable(true);
            patientButton.setVisible(false);
            clinicianButton.setDisable(true);
            clinicianButton.setVisible(false);
            administratorButton.setDisable(true);
            administratorButton.setVisible(false);
        }

        // Enter key
        userRegisterPane.setOnKeyPressed(e -> {
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
        passwordTxt.clear();
        verifyPasswordTxt.clear();

        setValid(userIdRegister);
        setValid(firstnameRegister);
        setValid(lastnameRegister);
        setValid(middlenameRegister);
        setValid(birthRegister);
        setValid(passwordTxt);
        setValid(verifyPasswordTxt);
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
     * @return The error messages if there are any issues with fields.
     */
    private String validateNames() {
        String error = "";
        // first name
        if (!Pattern.matches(UIRegex.FNAME.getValue(), firstnameRegister.getText())) {
            setInvalid(firstnameRegister);
            error += "Invalid first name. Must only use letters and hyphens with a max length of 35.\n";
        } else {
            setValid(firstnameRegister);
        }
        if (!Pattern.matches(UIRegex.MNAME.getValue(), middlenameRegister.getText())) {
        	setInvalid(middlenameRegister);
        	error += "Invalid middle names. Must only use letters, spaces and hyphens with a max length of 70.\n";
        } else {
        	setValid(middlenameRegister);
        }
        // last name
        if (!Pattern.matches(UIRegex.LNAME.getValue(), lastnameRegister.getText())) {
            setInvalid(lastnameRegister);
            error += "Invalid last name. Must only use letters and hyphens with a max length of 35.\n";
        } else {
            setValid(lastnameRegister);
        }
        return error;
    }

    /**
     * Validates the input fields for a new patient account
     *
     * @return Whether the fields are valid
     */
    private boolean validatePatient() {
        String error = validateNames();
        // nhi
        if (!Pattern.matches(UIRegex.NHI.getValue(), userIdRegister.getText().toUpperCase())) {
            setInvalid(userIdRegister);
            error += "NHI must be 3 letters followed by 4 numbers.\n";
        } else if (database.nhiInDatabase((userIdRegister.getText()))) {
            // checks to see if nhi already in use
        	setInvalid(userIdRegister);
            error += "Patient with the given NHI already exists.\n";
        } else {
            setValid(userIdRegister);
        }
        // date of birth
        if (birthRegister.getValue() != null) {
            if (birthRegister.getValue().isAfter(LocalDate.now())) {
                setInvalid(birthRegister);
                error += "Date of birth must be before the current date.\n";
            } else {
                setValid(birthRegister);
            }
        } else {
        	setInvalid(birthRegister);
            error += "Date of birth must be before the current date.\n";
        }
        if (error.equals("")) {
        	return true;
        }
        new Alert(Alert.AlertType.ERROR, error).showAndWait();
        return false;
    }

    /**
     * Validates the input fields for a new clinician account
     *
     * @return Whether the fields are valid
     */
    private boolean validateClinician() {
        String error = validateNames();
        if (regionRegister.getValue() != null) {
            setValid(regionRegister);
        } else {
            setInvalid(regionRegister);
            error += "A region must be selected.\n";
        }
        if (error.equals("")){
        	return true;
        }
        new Alert(Alert.AlertType.ERROR, error).showAndWait();
        return false;
    }

    /**
     * Validates the input fields for a new administrator account
     *
     * @return Whether the fields are valid
     */
    private boolean validateAdministrator() {
        String error = validateNames();
        if (!Pattern.matches(UIRegex.USERNAME.getValue(), userIdRegister.getText().toUpperCase())) {
            setInvalid(userIdRegister);
            error += "Invalid username. May only contain letters, numbers, hyphens and underscores with a max length of 30.\n";
        } else if (database.administratorInDb(userIdRegister.getText().toUpperCase())) {
            setInvalid(userIdRegister);
            error += "Invalid username. Username already in use.\n";
        } else {
            setValid(userIdRegister);
        }
        if (passwordTxt.getText().length() < 6) {
            setInvalid(passwordTxt);
            error += "Invalid password. Password must be 6 or more characters.\n";
        } else {
            setValid(passwordTxt);
        }
        if (!verifyPasswordTxt.getText().equals(passwordTxt.getText())) {
            setInvalid(verifyPasswordTxt);
            if (passwordTxt.getText().length() >= 6) {
                error += "Passwords do not match.\n";
            }
        } else {
            setValid(verifyPasswordTxt);
        }
        if (error.equals("")) {
        	return true;
        }
        new Alert(Alert.AlertType.ERROR, error).showAndWait();
        return false;
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
            database.add(new Patient(id, firstName, middles, lastName, birth));
            userActions.log(Level.INFO, "Successfully registered patient profile", "Attempted to register patient profile");
            alertMsg = "Successfully registered patient with NHI " + id;
        } else if (clinicianButton.isSelected()) {
            String region = regionRegister.getValue().toString();
            int staffID = database.nextStaffID();
            database.add(new Clinician(staffID, firstName, middles, lastName, (Region) Region.getEnumFromString(region)));
            userActions.log(Level.INFO, "Successfully registered clinician profile", "Attempted to register clinician profile");
            alertMsg = "Successfully registered clinician with staff ID " + staffID;
        } else {
            try {
                database.add(new Administrator(id, firstName, middles, lastName, password));
                userActions.log(Level.INFO, "Successfully registered administrator profile", "Attempted to register administrator profile");
                alertMsg = "Successfully registered administrator with username " + id;
            } catch (IllegalArgumentException e) {
                userActions.log(Level.SEVERE, "Couldn't register administrator profile due to invalid field", "Attempted to register administrator profile");
                alertMsg = "Couldn't register administrator, this username is already in use";
            }
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
            screenControl.show(Main.getUuid(), FXMLLoader.load(getClass().getResource("/scene/login.fxml")));
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
