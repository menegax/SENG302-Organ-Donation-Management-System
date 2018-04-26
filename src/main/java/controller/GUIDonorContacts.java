package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import model.Donor;
import service.Database;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

public class GUIDonorContacts {

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


    @FXML
    public void saveContactDetails() {
        saveToDisk();
    }


    private Donor target;


    /**
     * Initializes the contact details screen. Loads in the current donor and sets the text fields to
     * display current contact attributes.
     */
    public void initialize() {
        loadProfile();
        setContactFields();
    }


    /**
     *
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


    private void loadProfile() {
        try {
            target = Database.getDonorByNhi(ScreenControl.getLoggedInDonor()
                    .getNhiNumber());

        }
        catch (InvalidObjectException e) {
            userActions.log(Level.SEVERE, "Error loading logged in user", "attempted to manage the contacts for logged in user");
            e.printStackTrace();
        }
    }


    private void setDonorContactDetails() {
        if (!(homePhoneField.getText()
                .equals(""))) {
            target.setHomePhone(homePhoneField.getText());
        }
        if (!(mobilePhoneField.getText()
                .equals(""))) {
            target.setMobilePhone(mobilePhoneField.getText());
        }
        if (!(workPhoneField.getText()
                .equals(""))) {
            target.setWorkPhone(workPhoneField.getText());
        }
        if (!(emailAddressField.getText()
                .equals(""))) {
            target.setEmailAddress(emailAddressField.getText());
        }
        if (!(contactRelationshipField.getText()
                .equals(""))) {
            target.setContactRelationship(contactRelationshipField.getText());
        }
        if (!(contactNameField.getText()
                .equals(""))) {
            target.setContactName(contactNameField.getText());
        }
        if (!(contactHomePhoneField.getText()
                .equals(""))) {
            target.setContactHomePhone(contactHomePhoneField.getText());
        }
        if (!(contactMobilePhoneField.getText()
                .equals(""))) {
            target.setContactMobilePhone(contactMobilePhoneField.getText());
        }
        if (!(contactWorkPhoneField.getText()
                .equals(""))) {
            target.setContactWorkPhone(contactWorkPhoneField.getText());
        }
        if (!(contactEmailAddressField.getText()
                .equals(""))) {
            target.setContactEmailAddress(contactEmailAddressField.getText());
        }
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
            //e.printStackTrace();
        }
    }


    private void saveToDisk() {
        setDonorContactDetails();
        Database.saveToDisk();
        new Alert(Alert.AlertType.CONFIRMATION, "Contact details saved successfully", ButtonType.OK).showAndWait();
        goToProfile();
    }
}
