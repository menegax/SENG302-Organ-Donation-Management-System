package controller;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.control.Alert;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.util.StringConverter;
import model.Patient;
import service.Database;

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
    private TextField firstnameRegister;

    @FXML
    private TextField lastnameRegister;

    @FXML
    private TextField middlenameRegister;

    @FXML
    private DatePicker birthRegister;

    @FXML
    private TextField nhiRegister;

    @FXML
    private Pane patientRegisterAnchorPane;

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    /**
     * Sets up register page GUI elements
     */
    public void initialize() {
        setDateConverter();
        firstnameRegister.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, Event::consume);
        lastnameRegister.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, Event::consume);
        middlenameRegister.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, Event::consume);
        nhiRegister.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, Event::consume);
        birthRegister.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, Event::consume);

        // Enter key
        patientRegisterAnchorPane.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                register();
            }
        });
    }


    /**
     * Back button listener to switch to the login screen
     */
    @FXML
    public void goBackToLogin() {
        clearFields();
        try {
            screenControl.show(Main.getUuid(), FXMLLoader.load(getClass().getResource("/scene/login.fxml")));
        } catch (IOException e) {
            new Alert((Alert.AlertType.ERROR), "Unable to load login").show();
            userActions.log(SEVERE, "Failed to load login", "Attempted to load login");
        }
    }


    /**
     * Clears the data in the fields of the GUI
     */
    private void clearFields() {
        nhiRegister.clear();
        firstnameRegister.clear();
        lastnameRegister.clear();
        middlenameRegister.clear();
        birthRegister.getEditor().clear();

        setValid(nhiRegister);
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
     * Check users inputs for validity and registers the user patient profile
     */
    @FXML
    public void register() {

        Boolean valid = true;

        Alert invalidInfo = new Alert(Alert.AlertType.WARNING);
        StringBuilder invalidContent = new StringBuilder("Please fix the following errors:\n");

        // nhi
        if (!Pattern.matches("[A-Za-z]{3}[0-9]{4}", nhiRegister.getText().toUpperCase())) {
            valid = setInvalid(nhiRegister);
            invalidContent.append("NHI must be three letters followed by four numbers\n");
        } else if (Database.isPatientInDb(nhiRegister.getText())) {
            // checks to see if nhi already in use
            valid = setInvalid(nhiRegister);
            invalidContent.append("NHI is already in use\n");
        } else {
            setValid(nhiRegister);
        }

        // first name
        if (!firstnameRegister.getText()
                .matches("([A-Za-z]+[.]*[-]*[\\s]*)+")) {
            valid = setInvalid(firstnameRegister);
            invalidContent.append("First name must be letters, ., or -.\n");
        } else {
            setValid(firstnameRegister);
        }

        // last name
        if (!lastnameRegister.getText()
                .matches("([A-Za-z]+[.]*[-]*[\\s]*)+")) {
            valid = setInvalid(lastnameRegister);
            invalidContent.append("Last name must be letters, ., or -.\n");
        } else {
            setValid(lastnameRegister);
        }

        //middle names
        if (!middlenameRegister.getText()
                .matches("([A-Za-z]+[.]*[-]*[\\s]*)*")) {
            valid = setInvalid(middlenameRegister);
            invalidContent.append("Middle name(s) must be letters, ., or -.\n");
        } else {
            setValid(middlenameRegister);
        }

        // date of birth
        if (birthRegister.getValue() != null) {
            if (birthRegister.getValue()
                    .isAfter(LocalDate.now())) {
                valid = setInvalid(birthRegister);
                invalidContent.append("Date of birth must be a valid date either today or earlier.\n");
            } else {
                setValid(birthRegister);
            }
        } else {
            valid = setInvalid(birthRegister);
            invalidContent.append("Date of birth must be set.\n");
        }

        // if all are valid
        if (valid) {
            String nhi = nhiRegister.getText();
            String firstName = firstnameRegister.getText();
            String lastName = lastnameRegister.getText();
            ArrayList<String> middles = new ArrayList<>();
            if (!middlenameRegister.getText().equals("")) {
                List<String> middleNames = Arrays.asList(middlenameRegister.getText().split(" "));
                middles = new ArrayList<>(middleNames);
            }
            LocalDate birth = birthRegister.getValue();

            Database.addPatient(new Patient(nhi, firstName, middles, lastName, birth));
            userActions.log(Level.INFO, "Successfully registered patient profile", "Attempted to register patient profile");
            clearFields();
            new Alert(Alert.AlertType.INFORMATION, "Successfully registered!").show();
            try {
                screenControl.show(Main.getUuid(), FXMLLoader.load(getClass().getResource("/scene/login.fxml")));
            } catch (IOException e) {
                new Alert((Alert.AlertType.ERROR), "Unable to load login").show();
                userActions.log(SEVERE, "Failed to load login", "Attempted to load login");
            }
        } else {
            userActions.log(Level.WARNING, "Failed to register patient profile due to invalid fields", "Attempted to register patient profile");
            invalidInfo.setContentText(invalidContent.toString());
            invalidInfo.show();
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
        target.getStyleClass()
                .remove("invalid");
    }

}
