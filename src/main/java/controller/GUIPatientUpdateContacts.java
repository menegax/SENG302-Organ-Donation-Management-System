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
import utility.undoRedo.Action;
import utility.undoRedo.StatesHistoryScreen;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.regex.Pattern;

import utility.GlobalEnums.UIRegex;

import static java.util.logging.Level.INFO;
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

    private StatesHistoryScreen statesHistoryScreen;

    Database database = Database.getDatabase();

    @FXML
    private void redo() {
        statesHistoryScreen.redo();
    }


    @FXML
    private void undo() {
        statesHistoryScreen.undo();
    }

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    /**
     * Saves changes to a patient's contact details by calling the Database saving method.
     */
    @FXML
    public void saveContactDetails() {
        boolean valid = setPatientContactDetails();
        if (valid) {
            database.updateDatabase();
            screenControl.setIsSaved(false);
            userActions.log(INFO, "Successfully saved contact details", "Attempted to set invalid contact details");
        } else {
            userActions.log(Level.WARNING,"Failed to save contact details due to invalid fields", "Attempted to set invalid contact details");
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
            target = database.getPatientByNhi(nhi);
        }
        catch (NullPointerException e) {
            userActions.log(Level.SEVERE, "Error loading logged in user", "attempted to manage the contacts for logged in user");
        }
    }


    /**
     * Sets a patient's contact details to the text entered in the text fields for each individual
     * attribute. If the text field for an attribute is empty, the contact detail is not updated.
     */
    private boolean setPatientContactDetails() {
        boolean valid = true;

        Patient after = (Patient) target.deepClone();
        if (Pattern.matches(UIRegex.HOMEPHONE.getValue(), homePhoneField.getText())) {
            after.setHomePhone(homePhoneField.getText());
            setValid(homePhoneField);
        } else if (homePhoneField.getText().equals("")) {
            after.setHomePhone(null);
            setValid(homePhoneField);
        } else {
            valid = setInvalid(homePhoneField);
        }
        if (Pattern.matches(UIRegex.MOBILEPHONE.getValue(), mobilePhoneField.getText())) {
            after.setMobilePhone(mobilePhoneField.getText());
            setValid(mobilePhoneField);
        } else if (mobilePhoneField.getText().equals("")) {
            after.setMobilePhone(null);
            setValid(mobilePhoneField);
        } else {
            valid = setInvalid(mobilePhoneField);
        }
        if (Pattern.matches(UIRegex.WORKPHONE.getValue(), workPhoneField.getText())) {
            after.setWorkPhone(workPhoneField.getText());
            setValid(workPhoneField);
        } else if (workPhoneField.getText().equals("")) {
            after.setWorkPhone(null);
            setValid(workPhoneField);
        } else {
            valid = setInvalid(workPhoneField);
        }
        if (Pattern.matches(UIRegex.EMAIL.getValue(), emailAddressField.getText())) {
            after.setEmailAddress(emailAddressField.getText());
            setValid(emailAddressField);
        } else if (emailAddressField.getText().equals("")) {
            after.setEmailAddress(null);
            setValid(emailAddressField);
        } else {
            valid = setInvalid(emailAddressField);
        }
        if (Pattern.matches(UIRegex.RELATIONSHIP.getValue(), contactRelationshipField.getText())) {
            after.setContactRelationship(contactRelationshipField.getText());
            setValid(contactRelationshipField);
        } else if (contactRelationshipField.getText().equals("")) {
            after.setContactRelationship(null);
            setValid(contactRelationshipField);
        } else {
            valid = setInvalid(contactRelationshipField);
        }
        if (Pattern.matches(UIRegex.MNAME.getValue(), contactNameField.getText())) {
            after.setContactName(contactNameField.getText());
            setValid(contactNameField);
        } else if (contactNameField.getText().equals("")) {
            after.setContactName(null);
            setValid(contactNameField);
        } else {
            valid = setInvalid(contactNameField);
        }
        if (Pattern.matches(UIRegex.HOMEPHONE.getValue(), contactHomePhoneField.getText())) {
            after.setContactHomePhone(contactHomePhoneField.getText());
            setValid(contactHomePhoneField);
        } else if (contactHomePhoneField.getText().equals("")) {
            after.setContactHomePhone(null);
            setValid(contactHomePhoneField);
        } else {
            valid = setInvalid(contactHomePhoneField);
        }
        if (Pattern.matches(UIRegex.MOBILEPHONE.getValue(), contactMobilePhoneField.getText())) {
            after.setContactMobilePhone(contactMobilePhoneField.getText());
            setValid(contactMobilePhoneField);
        } else if (contactMobilePhoneField.getText().equals("")) {
            after.setContactMobilePhone(null);
            setValid(contactMobilePhoneField);
        } else {
            valid = setInvalid(contactMobilePhoneField);
        }
        if (Pattern.matches(UIRegex.WORKPHONE.getValue(), contactWorkPhoneField.getText())) {
            after.setContactWorkPhone(contactWorkPhoneField.getText());
            setValid(contactWorkPhoneField);
        } else if (contactWorkPhoneField.getText().equals("")) {
            after.setContactWorkPhone(null);
            setValid(contactWorkPhoneField);
        } else {
            valid = setInvalid(contactWorkPhoneField);
        }
        if (Pattern.matches(UIRegex.EMAIL.getValue(), contactEmailAddressField.getText())) {
            after.setContactEmailAddress(contactEmailAddressField.getText());
            setValid(contactEmailAddressField);
        } else if (contactEmailAddressField.getText().equals("")) {
            after.setContactEmailAddress(null);
            setValid(contactEmailAddressField);
        } else {
            valid = setInvalid(contactEmailAddressField);
        }
        if (valid) {
            Action action = new Action(target, after);
            statesHistoryScreen.addAction(action);
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
