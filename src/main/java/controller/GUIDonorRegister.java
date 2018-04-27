package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.util.StringConverter;
import model.Donor;
import model.StatesHistoryScreen;
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

    @FXML
    private Pane donorRegisterAnchorPane;

    @FXML
    private void undo() {
        statesHistoryScreen.undo();
    }

    @FXML
    private void redo() {
        statesHistoryScreen.redo();
    }

    private StringConverter<LocalDate> dateConverter;

    private StatesHistoryScreen statesHistoryScreen;

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
        ArrayList<Control> controls = new ArrayList<Control>() {{
            add(firstnameRegister);
            add(lastnameRegister);
            add(middlenameRegister);
            add(birthRegister);
            add(nhiRegister);
        }};
        statesHistoryScreen = new StatesHistoryScreen(donorRegisterAnchorPane, controls);
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
     * Check users inputs and registers the user donor profile
     */
    @FXML
    public void register(){
        Alert alert = new Alert(Alert.AlertType.WARNING, "");
        if (!(hasAllRequired())) {
            try{
                addDonorGui();
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Successfully Registered");
                confirm.showAndWait();
                ScreenControl.activate("login");
            } catch (IllegalArgumentException e) {
                userActions.log(Level.SEVERE, e.getMessage(), "attempted to add donor from gui attributes");
                alert.setContentText(e.getMessage());
                alert.show();
            }
        } else {
            alert.setContentText("Enter all required fields.");
            alert.show();
        }
    }


}
