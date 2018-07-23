package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import model.Patient;
import service.Database;
import utility.GlobalEnums;
import utility.StatusObservable;
import utility.undoRedo.StatesHistoryScreen;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.logging.Level;

import static java.util.logging.Level.SEVERE;
import static utility.UserActionHistory.userActions;

/**
 * Controller class for handling GUI application contact detail viewing and editing for a Patient.
 * Contact fields are editable and are pre-filled with the Patient's existing contact details.
 * Details are saved when the Save button is selected, and the user is returned to the patient profile view screen.
 *
 * @author Maree Palmer
 */
public class GUIPatientUpdateContacts extends UndoableController {

    @FXML
    public GridPane patientContactsPane;

    @FXML
    private TextField homePhoneField;

    @FXML
    private TextField mobilePhoneField;

    @FXML
    private TextField workPhoneField;

    @FXML
    private TextField emailAddressField;

    @FXML
    private TextField contactRelationshipField;

    @FXML
    private TextField contactHomePhoneField;

    @FXML
    private TextField contactMobilePhoneField;

    @FXML
    private TextField contactWorkPhoneField;

    @FXML
    private TextField contactEmailAddressField;

    @FXML
    private TextField contactNameField;

    /**
     * Patient that is currently logged in
     */
    private Patient target;

    private UserControl userControl;

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    /**
     * Saves changes to a patient's contact details by calling the Database saving method.
     */
    @FXML
    public void saveContactDetails() {
        boolean valid = setPatientContactDetails();
        if (valid) {
            screenControl.setIsSaved(false);
            new Alert(Alert.AlertType.INFORMATION, "Local changes have been made", ButtonType.OK).show();
        } else {
            new Alert(Alert.AlertType.WARNING, "Invalid fields", ButtonType.OK).show();
        }
    }


    /**
     * Initializes the contact details screen. Loads in the current patient and sets the text fields to
     * display current contact attributes.
     */
    public void initialize() {
        userControl = new UserControl();
        Object user = userControl.getLoggedInUser();
        if (user instanceof Patient) {
            loadProfile(((Patient) user).getNhiNumber());
            setContactFields();
        }
        if (userControl.getTargetUser() != null) {
            loadProfile(((Patient)userControl.getTargetUser()).getNhiNumber());
            setContactFields();
        }
        setupUndoRedo();

        // Enter key triggers log in
        patientContactsPane.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                saveContactDetails();
            }
        });
    }

    /**
     * Sets up the variables needed for undo and redo functionality
     */
    private void setupUndoRedo() {
        controls = new ArrayList<Control>() {{
            add(homePhoneField);
            add(mobilePhoneField);
            add(workPhoneField);
            add(emailAddressField);
            add(contactNameField);
            add(contactRelationshipField);
            add(contactHomePhoneField);
            add(contactMobilePhoneField);
            add(contactWorkPhoneField);
            add(contactEmailAddressField);
        }};
        statesHistoryScreen = new StatesHistoryScreen(controls, GlobalEnums.UndoableScreen.PATIENTUPDATECONTACTS);
    }

    /**
     * Sets initial contact text fields to a patient's existing contact details.
     * The fields are left blank if no contact detail is present for that field.
     */
    private void setContactFields() {
        if (target.getHomePhone() != null) {
            homePhoneField.setText(target.getHomePhone());
        }
        if (target.getMobilePhone() != null) {
            mobilePhoneField.setText(target.getMobilePhone());
        }
        if (target.getWorkPhone() != null) {
            workPhoneField.setText(target.getWorkPhone());
        }
        if (target.getEmailAddress() != null) {
            emailAddressField.setText(target.getEmailAddress());
        }
        if (target.getContactRelationship() != null) {
            contactRelationshipField.setText(target.getContactRelationship());
        }
        if (target.getContactName() != null) {
            contactNameField.setText(target.getContactName());
        }
        if (target.getContactHomePhone() != null) {
            contactHomePhoneField.setText(target.getContactHomePhone());
        }
        if (target.getContactMobilePhone() != null) {
            contactMobilePhoneField.setText(target.getContactMobilePhone());
        }
        if (target.getContactWorkPhone() != null) {
            contactWorkPhoneField.setText(target.getContactWorkPhone());
        }
        if (target.getContactEmailAddress() != null) {
            contactEmailAddressField.setText(target.getContactEmailAddress());
        }
    }


    /**
     * Sets the target patient to the currently logged in patient.
     * Throws an InvalidObjectException if the logged in patient can not be retrieved
     *
     * @param nhi The nhi of the patient to load
     */
    private void loadProfile(String nhi) {
        try {
            target = Database.getPatientByNhi(nhi);

            ArrayList<Control> controls = new ArrayList<Control>() {{
                add(homePhoneField);
                add(mobilePhoneField);
                add(workPhoneField);
                add(emailAddressField);
                add(contactNameField);
                add(contactRelationshipField);
                add(contactHomePhoneField);
                add(contactMobilePhoneField);
                add(contactWorkPhoneField);
                add(contactEmailAddressField);
            }};
            statesHistoryScreen = new StatesHistoryScreen(controls, GlobalEnums.UndoableScreen.PATIENTUPDATECONTACTS);
        }
        catch (InvalidObjectException e) {
            userActions.log(Level.SEVERE, "Error loading logged in user", "attempted to manage the contacts for logged in user");
        }
    }


    /**
     * Sets a patient's contact details to the text entered in the text fields for each individual
     * attribute. If the text field for an attribute is empty, the contact detail is not updated.
     */
    private boolean setPatientContactDetails() {
        boolean valid = true;
        if (!(homePhoneField.getText().equals("")) && homePhoneField.getText().matches("[0-9]+")) {
            target.setHomePhone(homePhoneField.getText());
            setValid(homePhoneField);
        } else if (homePhoneField.getText().equals("")) {
            target.setHomePhone(null);
            setValid(homePhoneField);
        } else {
            valid = setInvalid(homePhoneField);
        }
        if (!(mobilePhoneField.getText().equals("")) && mobilePhoneField.getText().matches("[0-9]+")) {
            target.setMobilePhone(mobilePhoneField.getText());
            setValid(mobilePhoneField);
        } else if (mobilePhoneField.getText().equals("")) {
            target.setMobilePhone(null);
            setValid(mobilePhoneField);
        } else {
            valid = setInvalid(mobilePhoneField);
        }
        if (!(workPhoneField.getText().equals("")) && workPhoneField.getText().matches("[0-9]+")) {
            target.setWorkPhone(workPhoneField.getText());
            setValid(workPhoneField);
        } else if (workPhoneField.getText().equals("")) {
            target.setWorkPhone(null);
            setValid(workPhoneField);
        } else {
            valid = setInvalid(workPhoneField);
        }
        if (emailAddressField.getText().matches("[0-9a-zA-Z.]+[@][a-z]+[.][a-z][a-z|.]+")) {
            target.setEmailAddress(emailAddressField.getText());
            setValid(emailAddressField);
        } else if (emailAddressField.getText().equals("")) {
            target.setEmailAddress(null);
            setValid(emailAddressField);
        } else {
            valid = setInvalid(emailAddressField);
        }
        if (contactRelationshipField.getText().matches("([A-Za-z]+[\\s]*)*")) {
            target.setContactRelationship(contactRelationshipField.getText());
            setValid(contactRelationshipField);
        } else if (contactRelationshipField.getText().equals("")) {
            target.setContactRelationship(null);
            setValid(contactRelationshipField);
        } else {
            valid = setInvalid(contactRelationshipField);
        }
        if (contactNameField.getText().matches("([A-Za-z]+[.]*[-]*[\\s]*)*")) {
            target.setContactName(contactNameField.getText());
            setValid(contactNameField);
        } else if (contactNameField.getText().equals("")) {
            target.setContactName(null);
            setValid(contactNameField);
        } else {
            valid = setInvalid(contactNameField);
        }
        if (!(contactHomePhoneField.getText().equals("")) && contactHomePhoneField.getText().matches("[0-9]+")) {
            target.setContactHomePhone(contactHomePhoneField.getText());
            setValid(contactHomePhoneField);
        } else if (contactHomePhoneField.getText().equals("")) {
            target.setContactHomePhone(null);
            setValid(contactHomePhoneField);
        } else {
            valid = setInvalid(contactHomePhoneField);
        }
        if (!(contactMobilePhoneField.getText().equals("")) && contactMobilePhoneField.getText().matches("[0-9]+")) {
            target.setContactMobilePhone(contactMobilePhoneField.getText());
            setValid(contactMobilePhoneField);
        } else if (contactMobilePhoneField.getText().equals("")) {
            target.setContactMobilePhone(null);
            setValid(contactMobilePhoneField);
        } else {
            valid = setInvalid(contactMobilePhoneField);
        }
        if (!(contactWorkPhoneField.getText().equals("")) && contactWorkPhoneField.getText().matches("[0-9]+")) {
            target.setContactWorkPhone(contactWorkPhoneField.getText());
            setValid(contactWorkPhoneField);
        } else if (contactWorkPhoneField.getText().equals("")) {
            target.setContactWorkPhone(null);
            setValid(contactWorkPhoneField);
        } else {
            valid = setInvalid(contactWorkPhoneField);
        }
        if (contactEmailAddressField.getText().matches("[0-9a-zA-Z.]+[@][a-z]+[.][a-z][a-z|.]+")) {
            target.setContactEmailAddress(contactEmailAddressField.getText());
            setValid(contactEmailAddressField);
        } else if (contactEmailAddressField.getText().equals("")) {
            target.setContactEmailAddress(null);
            setValid(contactEmailAddressField);
        } else {
            valid = setInvalid(contactEmailAddressField);
        }
        return valid;
    }

    /***
     * Applies the invalid class to the target control
     * @param target The target to add the class to
     */
    private boolean setInvalid(Control target) {
        target.getStyleClass().add("invalid");
        return false;
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

}
