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
import utility.GlobalEnums;
import utility.GlobalEnums.Region;
import utility.undoRedo.Action;
import utility.undoRedo.StatesHistoryScreen;

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

public class GUIAdministratorUserRegister extends UndoableController {

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
            userRegisterPane.getRowConstraints().get(0).setMaxHeight(0);
        } else {
            patientButton.setVisible(false);
            clinicianButton.setVisible(false);
            administratorButton.setVisible(false);
        }
        setUpUndoRedo();
        setUpButtonListeners();

        // Enter key
        userRegisterPane.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                register();
            }
        });
    }

    /**
     * Sets up the listeners for the radio buttons to switch controls on selection
     */
    private void setUpButtonListeners() {
        patientButton.selectedProperty().addListener(((observable, oldValue, newValue) -> {
            if (!oldValue && newValue) {
                if (!statesHistoryScreen.getUndoableStage().getChangingStates()) {
                    statesHistoryScreen.getUndoableStage().setChangingStates(true);
                    clearFields();
                    statesHistoryScreen.getUndoableStage().setChangingStates(false);
                }
                onSelectPatient();
            }
        }));
        clinicianButton.selectedProperty().addListener(((observable, oldValue, newValue) -> {
            if (!oldValue && newValue) {
                if (!statesHistoryScreen.getUndoableStage().getChangingStates()) {
                    statesHistoryScreen.getUndoableStage().setChangingStates(true);
                    clearFields();
                    statesHistoryScreen.getUndoableStage().setChangingStates(false);
                }
                onSelectClinician();
            }
        }));
        administratorButton.selectedProperty().addListener(((observable, oldValue, newValue) -> {
            if (!oldValue && newValue) {
                if (!statesHistoryScreen.getUndoableStage().getChangingStates()) {
                    statesHistoryScreen.getUndoableStage().setChangingStates(true);
                    clearFields();
                    statesHistoryScreen.getUndoableStage().setChangingStates(false);
                }
                onSelectAdministrator();
            }
        }));
    }

    /**
     * Sets up undo redo for this tab
     */
    private void setUpUndoRedo() {
        controls = new ArrayList<Control>(){{
            add(firstnameRegister);
            add(lastnameRegister);
            add(middlenameRegister);
            add(birthRegister);
            add(userIdRegister);
            add(regionRegister);
            add(passwordTxt);
            add(verifyPasswordTxt);
            add(patientButton);
            add(clinicianButton);
            add(administratorButton);
        }};
        statesHistoryScreen = new StatesHistoryScreen(controls, GlobalEnums.UndoableScreen.ADMINISTRATORUSERREGISTER);
    }

    /**
     * Sets the registry fields for registering a patient
     */
    @FXML
    public void onSelectPatient() {
        userIdRegister.setPromptText("NHI Number");
        regionRegister.setVisible(false);
        birthRegister.setVisible(true);
        passwordTxt.setVisible(false);
        verifyPasswordTxt.setVisible(false);
        userIdRegister.setVisible(true);
    }

    /**
     * Sets the registry fields for registering a clinician
     */
    @FXML
    public void onSelectClinician() {
        userIdRegister.setPromptText("Staff ID");
        regionRegister.setVisible(true);
        birthRegister.setVisible(false);
        passwordTxt.setVisible(false);
        verifyPasswordTxt.setVisible(false);
        userIdRegister.setVisible(false);
    }

    /**
     * Sets the registry fields for registering an administrator
     */
    @FXML
    public void onSelectAdministrator() {
        userIdRegister.setPromptText("Username");
        regionRegister.setVisible(false);
        birthRegister.setVisible(false);
        passwordTxt.setVisible(true);
        verifyPasswordTxt.setVisible(true);
        userIdRegister.setVisible(true);
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
     * @return Whether the fields are valid
     */
    private boolean validateNames() {
        boolean valid = true;
        // first name
        if (!firstnameRegister.getText()
                .matches("^[-a-zA-Z]+$")) {
            valid = setInvalid(firstnameRegister);
        } else {
            setValid(firstnameRegister);
        }
        // last name
        if (!lastnameRegister.getText()
                .matches("^[-a-zA-Z]+$")) {
            valid = setInvalid(lastnameRegister);
        } else {
            setValid(lastnameRegister);
        }
        return valid;
    }

    /**
     * Validates the input fields for a new patient account
     *
     * @return Whether the fields are valid
     */
    private boolean validatePatient() {
        boolean valid = validateNames();
        // nhi
        if (!Pattern.matches("[A-Za-z]{3}[0-9]{4}", userIdRegister.getText().toUpperCase())) {
            valid = setInvalid(userIdRegister);
            userActions.log(Level.WARNING, "NHI must be 3 characters followed by 4 numbers", "Attempted to register new patient");
        } else if (Database.isPatientInDb(userIdRegister.getText())) {
            // checks to see if nhi already in use
            valid = setInvalid(userIdRegister);
            userActions.log(Level.WARNING, "Patient with the given NHI already exists", "Attempted to register new patient");
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
        boolean valid = validateNames();
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
        String error = "";
        if (!userIdRegister.getText()
                .matches("([A-Za-z0-9]+[-]*[_]*)+")) {
            valid = setInvalid(userIdRegister);
            error += "Invalid username.\n";
        } else if (Database.usernameUsed(userIdRegister.getText())) {
            valid = setInvalid(userIdRegister);
            error += "Username already in use.\n";
        } else {
            setValid(userIdRegister);
        }
        if (passwordTxt.getText().length() < 6) {
            valid = setInvalid(passwordTxt);
            error += "Password must be 6 or more characters.\n";
        } else {
            setValid(passwordTxt);
        }
        if (!verifyPasswordTxt.getText().equals(passwordTxt.getText())) {
            valid = setInvalid(verifyPasswordTxt);
            if (passwordTxt.getText().length() >= 6) {
                error += "Passwords do not match.\n";
            }
        } else {
            setValid(verifyPasswordTxt);
        }
        if (!valid) {
        	userActions.log(Level.WARNING, error, "Attempted to register new administrator");
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
            statesHistoryScreen.addAction(new Action(null, new Patient(id, firstName, middles, lastName, birth)));
            userActions.log(Level.INFO, "Successfully registered patient profile", "Attempted to register patient profile");
        } else if (clinicianButton.isSelected()) {
            String region = regionRegister.getValue().toString();
            int staffID = Database.getNextStaffID();
            statesHistoryScreen.addAction(new Action(null, new Clinician(staffID, firstName, middles, lastName, Region.getEnumFromString(region))));
            userActions.log(Level.INFO, "Successfully registered clinician profile", "Attempted to register clinician profile");
        } else {
            try {
                statesHistoryScreen.addAction(new Action(null, new Administrator(id, firstName, middles, lastName, password)));
                userActions.log(Level.INFO, "Successfully registered administrator profile", "Attempted to register administrator profile");
            } catch (IllegalArgumentException e) {
                userActions.log(Level.SEVERE, "Couldn't register administrator profile due to invalid field", "Attempted to register administrator profile");
            }
        }
        statesHistoryScreen.getUndoableStage().setChangingStates(true);
        clearFields();
        statesHistoryScreen.getUndoableStage().setChangingStates(false);
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