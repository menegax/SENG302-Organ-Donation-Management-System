package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.Administrator;
import model.Clinician;
import model.Patient;
import service.Database;
import utility.GlobalEnums;
import utility.GlobalEnums.Region;
import utility.TouchPaneController;
import utility.TouchscreenCapable;
import utility.StatusObservable;
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
import static java.util.logging.Level.WARNING;
import static utility.UserActionHistory.userActions;

public class GUIUserRegister implements TouchscreenCapable {

    public Button doneButton;

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
    private GridPane userRegisterPane;

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    private TouchPaneController registerTouchPane;

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

        // Enter key
        userRegisterPane.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                register();
            }
        });
        if(screenControl.isTouch()) {
            registerTouchPane = new TouchPaneController(userRegisterPane);
            userRegisterPane.setOnZoom(this::zoomWindow);
            userRegisterPane.setOnRotate(this::rotateWindow);
            userRegisterPane.setOnScroll(this::scrollWindow);
        }

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
                .matches("^[-a-zA-Z]+$")) {
            valid = setInvalid(firstnameRegister);
        } else {
            setValid(firstnameRegister);
        }

        // middle name
        if (!middlenameRegister.getText()
                .matches("^([-a-zA-Z]*|[ ])*$")) {
            valid = setInvalid(middlenameRegister);
        } else {
            setValid(middlenameRegister);
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
            userActions.log(Level.WARNING, "NHI must be 3 characters followed by 4 numbers", "Attempted to create patient with invalid NHI");
        } else if (Database.isPatientInDb(userIdRegister.getText())) {
            // checks to see if nhi already in use
            valid = setInvalid(userIdRegister);
            userActions.log(Level.WARNING, "Patient with the given NHI already exists", "Attempted to create patient with invalid NHI");
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
     * Check users inputs for validity and registers the user patient profile
     */
    @FXML
    public void register() {
        if (!validatePatient()) {
            userActions.log(Level.WARNING, "Failed to register patient profile due to invalid fields", "Attempted to register patient profile");
            return;
        }

        // if all are valid
        String id = userIdRegister.getText();
        String firstName = firstnameRegister.getText();
        String lastName = lastnameRegister.getText();
        ArrayList<String> middles = new ArrayList<>();
        String errorMsg = "";
        if (!middlenameRegister.getText().equals("")) {
            List<String> middleNames = Arrays.asList(middlenameRegister.getText().split(" "));
            middles = new ArrayList<>(middleNames);
        }
        LocalDate birth = birthRegister.getValue();
        Database.addPatient(new Patient(id, firstName, middles, lastName, birth));
        userActions.log(Level.INFO, "Successfully registered patient profile", "Attempted to register patient profile");
        errorMsg = "Successfully registered patient with NHI " + id;
        screenControl.setIsSaved(false);
        clearFields();
        if (!errorMsg.equals("")) {
            userActions.log(Level.INFO, errorMsg, "Attempted to register a new user");
        }
        returnToPreviousPage();
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
        screenControl.setUpNewLogin();
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

    @Override
    public void zoomWindow(ZoomEvent zoomEvent) {
        registerTouchPane.zoomPane(zoomEvent);
    }

    @Override
    public void rotateWindow(RotateEvent rotateEvent) {
        registerTouchPane.rotatePane(rotateEvent);
    }

    @Override
    public void scrollWindow(ScrollEvent scrollEvent) {
        if(scrollEvent.isDirect()) {
            registerTouchPane.scrollPane(scrollEvent);
        }
    }


}
