package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import model.Donor;
import service.Database;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

public class GUIDonorRegister {

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

    private StringConverter<LocalDate> dateConverter;


    /**
     * Back button listener to switch to the login screen
     */
    @FXML
    public void goBackToLogin(){
        ScreenControl.activate("login");
    }

    /**
     * Sets up register page GUI elements
     */
    public void initialize(){
        setDateConverter();
    }


    /**
     * Checks users have entered all REQUIRED fields
     * @return boolean - if user has entered all required fields
     */

    private boolean hasAllRequired(){
        return firstnameRegister.getText().isEmpty() || lastnameRegister.getText().isEmpty()
                || birthRegister.getValue() == null || nhiRegister.getText().isEmpty();
    }

    /**
     * Adds donor to database
     * @throws IllegalArgumentException - if entered NHI is not unique
     */

    private void addDonorGui() throws IllegalArgumentException {
        Database.addDonor(new Donor(nhiRegister.getText(), firstnameRegister.getText(),
                middlenameRegister.getText().isEmpty() ? new ArrayList<>() :
                        new ArrayList<>(Arrays.asList(middlenameRegister.getText().split("\\s*,\\s*"))),
                lastnameRegister.getText(), dateConverter.fromString(birthRegister.getValue().toString())));
    }

    /**
     * Sets the date picker format to be yyyy-MM-dd
     */
    private void setDateConverter(){
        dateConverter = new StringConverter<LocalDate>() {
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
     * Check users inputs and proceeds to the donations screen
     */
    @FXML
    public void goToDonations(){
        Alert alert = new Alert(Alert.AlertType.WARNING, "");
        if (!(hasAllRequired())) {
            try{
                addDonorGui();
                ScreenControl.activate("home"); //TODO: route to donations
            } catch (IllegalArgumentException e) {
                userActions.log(Level.SEVERE, e.getMessage());
                alert.setContentText(e.getMessage());
                alert.show();
            }
        } else {
            userActions.log(Level.WARNING, "Not all fields were filled in");
            alert.setContentText("Enter all required fields.");
            alert.show();
        }
    }


}
