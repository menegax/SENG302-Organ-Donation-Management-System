/**
 * Controller Class for'donorMedications.fxml'
 */

package controller;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.InvalidObjectException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.Donor;
import model.Medication;
import service.Database;

import javax.swing.text.html.ListView;
import java.util.ArrayList;
import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

public class GUIDonorMedications {

    @FXML
    private TextField newMedication;

    @FXML
    private ListView currentMedications;

    @FXML
    private ListView pastMedications;

    @FXML
    void deleteMedication(ActionEvent event) {
        removeMedication(pastMedications.getSelectionModel().getSelectedItem().getText()); // found 'getSelectionModel().getSelectedItem() researching online. getText() is a guess
    }

    @FXML
    void makeCurrent(ActionEvent event) {
        moveToCurrent(pastMedications.getSelectionModel().getSelectedItem().getText()); // found 'getSelectionModel().getSelectedItem() researching online. getText() is a guess
    }

    @FXML
    void makeHistory(ActionEvent event) {
        moveToHistory(currentMedications.getSelectionModel().getSelectedItem().getText()); // found 'getSelectionModel().getSelectedItem() researching online. getText() is a guess
    }

    @FXML
    void registerMedication(ActionEvent event) {
        addMedication(newMedication.getText());
    }

    private ListProperty<String> currentListProperty = new SimpleListProperty<>(); // Not tested
    private ListProperty<String> historyListProperty = new SimpleListProperty<>(); // Not tested
    private ArrayList<Medication> current = new ArrayList<>();
    private ArrayList<Medication> history = new ArrayList<>();
    private Donor target;

    @FXML
    public void initialize() {
        try {
            target = Database.getDonorByNhi(ScreenControl.getLoggedInDonor().getNhiNumber());
            current = target.getCurrentMedications();
            viewCurrentMedications();
            history = target.getMedicationHistory();
            viewPastMedications();
        } catch (InvalidObjectException e) {
            userActions.log(Level.SEVERE, "Error loading logged in user", "attempted to manage the donations for logged in user");
            e.printStackTrace();
        }
    }

    private void viewCurrentMedications() {
        currentListProperty.set(FXCollections.observableArrayList(current)); // Not tested
        currentMedications.itemsProperty().bind(currentListProperty); // Not tested
        target.setCurrentMedications(current);
    }

    private void viewPastMedications() {
        historyListProperty.set(FXCollections.observableArrayList(history)); // Not tested
        pastMedications.itemsProperty().bind(historyListProperty); // Not tested
        target.setMedicationHistory(history);
    }

    private void addMedication(String medication) {
        if (!medication.equals("Enter a medication") && !medication.equals("")) {
            ; //if (mediation NOT IN current or pastMedications) { add new med to current meds Array list}
        }
        viewCurrentMedications();
    }

    private void removeMedication(String medication) {
        ; // remove the medication from the history array list. Can only be deleted from history is my assumption
        viewPastMedications();
    }

    private void moveToCurrent(String medication) {
        ; // remove the med from history, and add to current
        viewPastMedications();
        viewCurrentMedications();
    }

    private void moveToHistory(String medication) {
        ; // remove the med from current, and add to history
        viewPastMedications();
        viewCurrentMedications();
    }
}