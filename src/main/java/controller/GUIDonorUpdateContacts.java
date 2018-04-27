package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import model.Donor;
import service.Database;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

/**
 * Controller class for handling GUI application contact detail viewing and editing for a Donor.
 * Contact fields are editable and are pre-filled with the Donor's existing contact details.
 * Details are saved when the Save button is selected, and the user is returned to the donor profile view screen.
 * @author Maree Palmer
 */
public class GUIDonorUpdateContacts {

    @FXML
    public AnchorPane donorContactsPane;

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
     * Donor that is currently logged in
     */
    private Donor target;


    /**
     * Saves changes to a donor's contact details by calling the Database saving method.
     */
    @FXML
    public void saveContactDetails() {
        saveToDisk();
    }


    /**
     * Initializes the contact details screen. Loads in the current donor and sets the text fields to
     * display current contact attributes.
     */
    public void initialize() {
        loadProfile();
        setContactFields();

        // Enter key triggers log in
        donorContactsPane.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                saveContactDetails();
            }
        });
    }


    /**
     * Sets initial contact text fields to a donor's existing contact details.
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
     * Sets the target donor to the currently logged in donor.
     * Throws an InvalidObjectException if the logged in donor can not be retrieved
     */
    private void loadProfile() {
        try {
            target = Database.getDonorByNhi(ScreenControl.getLoggedInDonor()
                    .getNhiNumber());
        }
        catch (InvalidObjectException e) {
            userActions.log(Level.SEVERE, "Error loading logged in user", "attempted to manage the contacts for logged in user");
        }
    }


    /**
     * Sets a donor's contact details to the text entered in the text fields for each individual
     * attribute. If the text field for an attribute is empty, the contact detail is not updated.
     */
    private boolean setDonorContactDetails() {
        boolean valid = true;
        if (!(homePhoneField.getText().equals("")) && homePhoneField.getText().matches("[0-9]+")) {
            target.setHomePhone(homePhoneField.getText());
        } else if(homePhoneField.getText().equals("")) {
            target.setHomePhone(null);
        } else {
            valid = false;
        }
        if (!(mobilePhoneField.getText().equals("")) && mobilePhoneField.getText().matches("[0-9]+")) {
            target.setMobilePhone(mobilePhoneField.getText());
        } else if(mobilePhoneField.getText().equals("")) {
            target.setMobilePhone(null);
        } else {
            valid = false;
        }
        if (!(workPhoneField.getText().equals("")) && workPhoneField.getText().matches("[0-9]+")) {
            target.setWorkPhone(workPhoneField.getText());
        } else if(workPhoneField.getText().equals("")) {
            target.setWorkPhone(null);
        } else {
            valid = false;
        }
        if (!(emailAddressField.getText().equals("")) && emailAddressField.getText().matches("[0-9a-zA-Z.]+[@][a-z]+[.][a-z][a-z|.]+")) {
            target.setEmailAddress(emailAddressField.getText());
        } else if(emailAddressField.getText().equals("")) {
            target.setEmailAddress(null);
        } else {
            valid = false;
        }
        if (!(contactRelationshipField.getText().equals(""))) {
            target.setContactRelationship(contactRelationshipField.getText());
        } else if(contactRelationshipField.getText().equals("")) {
            target.setContactRelationship(null);
        } else {
            valid = false;
        }
        if (!(contactNameField.getText().equals(""))) {
            target.setContactName(contactNameField.getText());
        } else if(contactNameField.getText().equals("")) {
            target.setContactName(null);
        } else {
            valid = false;
        }
        if (!(contactHomePhoneField.getText().equals("")) && contactHomePhoneField.getText().matches("[0-9]+")) {
            target.setContactHomePhone(contactHomePhoneField.getText());
        } else if(contactHomePhoneField.getText().equals("")) {
            target.setContactHomePhone(null);
        } else {
            valid = false;
        }
        if (!(contactMobilePhoneField.getText().equals("")) && contactMobilePhoneField.getText().matches("[0-9]+")) {
            target.setContactMobilePhone(contactMobilePhoneField.getText());
        } else if(contactMobilePhoneField.getText().equals("")) {
            target.setContactMobilePhone(null);
        } else {
            valid = false;
        }
        if (!(contactWorkPhoneField.getText().equals("")) && contactWorkPhoneField.getText().matches("[0-9]+")) {
            target.setContactWorkPhone(contactWorkPhoneField.getText());
        } else if(contactWorkPhoneField.getText().equals("")) {
            target.setContactWorkPhone(null);
        } else {
            valid = false;
        }
        if (!(contactEmailAddressField.getText().equals("") && emailAddressField.getText().matches("[0-9a-zA-Z.]+[@][a-z]+[.][a-z][a-z|.]+"))) {
            target.setContactEmailAddress(contactEmailAddressField.getText());
        } else if(contactEmailAddressField.getText().equals("")) {
            target.setContactEmailAddress(null);
        } else {
            valid = false;
        }
        return valid;
    }


    /**
     * Closes the contact details screen and returns the user to the profile window without saving changes.
     */
    public void goToProfile() {
        ScreenControl.removeScreen("donorProfile");
        try {
            ScreenControl.addScreen("donorProfile", FXMLLoader.load(getClass().getResource("/scene/donorProfile.fxml")));
            ScreenControl.activate("donorProfile");
        }
        catch (IOException e) {
            userActions.log(Level.SEVERE, "Error loading profile screen", "attempted to navigate from the contacts page to the profile page");
            new Alert(Alert.AlertType.WARNING, "ERROR loading profile page", ButtonType.OK).showAndWait();
        }
    }


    /**
     * Sets the donor's contact details to the values specified in the GUI, and runs the save operation from
     * the application database. An alert is then shown to inform the user of a successful save, and the donor
     * profile window is shown.
     */
    private void saveToDisk() {
        boolean valid = setDonorContactDetails();
        if(valid) {
            Database.saveToDisk();
            new Alert(Alert.AlertType.CONFIRMATION, "Contact details saved successfully", ButtonType.OK).show();
            goToProfile();
        } else {
            new Alert(Alert.AlertType.CONFIRMATION, "Invalid fields", ButtonType.OK).show();
        }
    }
}
