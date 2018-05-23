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

    public void setViewedPatient(Patient patient) {
        target = patient;
        loadProfile(target.getNhiNumber());
        setContactFields();
    }

    /**
     * Saves changes to a patient's contact details by calling the Database saving method.
     */
    @FXML
    public void saveContactDetails() {
        boolean valid = setPatientContactDetails();
        if(valid) {
            Database.saveToDisk();
            goToProfile();
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
        if (userControl.getTargetPatient() != null) {
            loadProfile((userControl.getTargetPatient()).getNhiNumber());
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
            statesHistoryScreen = new StatesHistoryScreen(patientContactsPane, controls);
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
        } else if(homePhoneField.getText().equals("")) {
            target.setHomePhone(null);
            setValid(homePhoneField);
        } else {
            valid = setInvalid(homePhoneField);
        }
        if (!(mobilePhoneField.getText().equals("")) && mobilePhoneField.getText().matches("[0-9]+")) {
            target.setMobilePhone(mobilePhoneField.getText());
            setValid(mobilePhoneField);
        } else if(mobilePhoneField.getText().equals("")) {
            target.setMobilePhone(null);
            setValid(mobilePhoneField);
        } else {
            valid = setInvalid(mobilePhoneField);
        }
        if (!(workPhoneField.getText().equals("")) && workPhoneField.getText().matches("[0-9]+")) {
            target.setWorkPhone(workPhoneField.getText());
            setValid(workPhoneField);
        } else if(workPhoneField.getText().equals("")) {
            target.setWorkPhone(null);
            setValid(workPhoneField);
        } else {
            valid = setInvalid(workPhoneField);
        }
        if (emailAddressField.getText().matches("[0-9a-zA-Z.]+[@][a-z]+[.][a-z][a-z|.]+")) {
            target.setEmailAddress(emailAddressField.getText());
            setValid(emailAddressField);
        } else if(emailAddressField.getText().equals("")) {
            target.setEmailAddress(null);
            setValid(emailAddressField);
        } else {
            valid = setInvalid(emailAddressField);
        }
        if (contactRelationshipField.getText().matches("([A-Za-z]+[\\s]*)*")) {
            target.setContactRelationship(contactRelationshipField.getText());
            setValid(contactRelationshipField);
        } else if(contactRelationshipField.getText().equals("")) {
            target.setContactRelationship(null);
            setValid(contactRelationshipField);
        } else {
            valid = setInvalid(contactRelationshipField);
        }
        if (contactNameField.getText().matches("([A-Za-z]+[.]*[-]*[\\s]*)*")) {
            target.setContactName(contactNameField.getText());
            setValid(contactNameField);
        } else if(contactNameField.getText().equals("")) {
            target.setContactName(null);
            setValid(contactNameField);
        } else {
            valid = setInvalid(contactNameField);
        }
        if (!(contactHomePhoneField.getText().equals("")) && contactHomePhoneField.getText().matches("[0-9]+")) {
            target.setContactHomePhone(contactHomePhoneField.getText());
            setValid(contactHomePhoneField);
        } else if(contactHomePhoneField.getText().equals("")) {
            target.setContactHomePhone(null);
            setValid(contactHomePhoneField);
        } else {
            valid = setInvalid(contactHomePhoneField);
        }
        if (!(contactMobilePhoneField.getText().equals("")) && contactMobilePhoneField.getText().matches("[0-9]+")) {
            target.setContactMobilePhone(contactMobilePhoneField.getText());
            setValid(contactMobilePhoneField);
        } else if(contactMobilePhoneField.getText().equals("")) {
            target.setContactMobilePhone(null);
            setValid(contactMobilePhoneField);
        } else {
            valid = setInvalid(contactMobilePhoneField);
        }
        if (!(contactWorkPhoneField.getText().equals("")) && contactWorkPhoneField.getText().matches("[0-9]+")) {
            target.setContactWorkPhone(contactWorkPhoneField.getText());
            setValid(contactWorkPhoneField);
        } else if(contactWorkPhoneField.getText().equals("")) {
            target.setContactWorkPhone(null);
            setValid(contactWorkPhoneField);
        } else {
            valid = setInvalid(contactWorkPhoneField);
        }
        if (contactEmailAddressField.getText().matches("[0-9a-zA-Z.]+[@][a-z]+[.][a-z][a-z|.]+")) {
            target.setContactEmailAddress(contactEmailAddressField.getText());
            setValid(contactEmailAddressField);
        } else if(contactEmailAddressField.getText().equals("")) {
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


    /**
     * Closes the contact details screen and returns the user to the profile window without saving changes.
     */
    public void goToProfile() {
        if (userControl.getLoggedInUser() instanceof Patient) {
            try {
                screenControl.show(patientContactsPane, "/scene/patientProfile.fxml");
            } catch (IOException e) {
                new Alert((Alert.AlertType.ERROR), "Unable to patient profile").show();
                userActions.log(SEVERE, "Failed to load patient profile", "Attempted to load patient profile");
            }
        } else {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scene/patientProfile.fxml"));
            try {
                ScreenControl.loadPopUpPane(patientContactsPane.getScene(), fxmlLoader);
            } catch (IOException e) {
                userActions.log(Level.SEVERE, "Error returning to profile screen in popup", "attempted to navigate from the donation page to the profile page in popup");
                new Alert(Alert.AlertType.WARNING, "Error loading profile page", ButtonType.OK).show();
            }
        }
    }
    /**
     * Sets the patient's contact details to the values specified in the GUI, and runs the save operation from
     * the application database. An alert is then shown to inform the user of a successful save, and the patient
     * profile window is shown.
     */
    private void saveToDisk() {
        boolean valid = setPatientContactDetails();
        if(valid) {
            Database.saveToDisk();
            goToProfile();
        } else {
            new Alert(Alert.AlertType.WARNING, "Invalid fields", ButtonType.OK).show();
        }
    }
}
