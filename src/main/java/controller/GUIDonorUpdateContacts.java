package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import model.Donor;
import service.Database;
import utility.undoRedo.StatesHistoryScreen;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

/**
 * Controller class for handling GUI application contact detail viewing and editing for a Donor.
 * Contact fields are editable and are pre-filled with the Donor's existing contact details.
 * Details are saved when the Save button is selected, and the user is returned to the donor profile view screen.
 * @author Maree Palmer
 */
public class GUIDonorUpdateContacts implements IPopupable {

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

    private StatesHistoryScreen statesHistoryScreen;

    @FXML
    private void redo() {
        statesHistoryScreen.redo();
    }


    @FXML
    private void undo() {
        statesHistoryScreen.undo();
    }


    public void setViewedDonor(Donor donor) {
        target = donor;
        loadProfile(target.getNhiNumber());
        setContactFields();
    }

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
        if (ScreenControl.getLoggedInDonor() != null) {
            loadProfile(ScreenControl.getLoggedInDonor().getNhiNumber());
            setContactFields();
        }

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
     *
     * @param nhi The nhi of the donor to load
     */
    private void loadProfile(String nhi) {
        try {
            target = Database.getDonorByNhi(nhi);

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
            statesHistoryScreen = new StatesHistoryScreen(donorContactsPane, controls);
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
        if (!(emailAddressField.getText().equals("")) && emailAddressField.getText().matches("[0-9a-zA-Z.]+[@][a-z]+[.][a-z][a-z|.]+")) {
            target.setEmailAddress(emailAddressField.getText());
            setValid(emailAddressField);
        } else if(emailAddressField.getText().equals("")) {
            target.setEmailAddress(null);
            setValid(emailAddressField);
        } else {
            valid = setInvalid(emailAddressField);
        }
        if (!(contactRelationshipField.getText().equals(""))) {
            target.setContactRelationship(contactRelationshipField.getText());
            setValid(contactRelationshipField);
        } else if(contactRelationshipField.getText().equals("")) {
            target.setContactRelationship(null);
            setValid(contactRelationshipField);
        } else {
            valid = setInvalid(contactRelationshipField);
        }
        if (!(contactNameField.getText().equals(""))) {
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
        if (!(contactEmailAddressField.getText().equals("") && emailAddressField.getText().matches("[0-9a-zA-Z.]+[@][a-z]+[.][a-z][a-z|.]+"))) {
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
        if (ScreenControl.getLoggedInDonor() != null) {
            ScreenControl.removeScreen("donorProfile");
            try {
                ScreenControl.addScreen("donorProfile", FXMLLoader.load(getClass().getResource("/scene/donorProfile.fxml")));
                ScreenControl.activate("donorProfile");
            } catch (IOException e) {
                userActions.log(Level.SEVERE, "Error returning to profile screen", "attempted to navigate from the donation page to the profile page");
                new Alert(Alert.AlertType.WARNING, "Error loading profile page", ButtonType.OK).show();
            }
        } else {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scene/donorProfile.fxml"));
            try {
                ScreenControl.loadPopUpPane(donorContactsPane.getScene(), fxmlLoader, target);
            } catch (IOException e) {
                userActions.log(Level.SEVERE, "Error returning to profile screen in popup", "attempted to navigate from the donation page to the profile page in popup");
                new Alert(Alert.AlertType.WARNING, "Error loading profile page", ButtonType.OK).show();
            }
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
            goToProfile();
        } else {
            new Alert(Alert.AlertType.WARNING, "Invalid fields", ButtonType.OK).show();
        }
    }
}
