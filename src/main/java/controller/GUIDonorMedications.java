/**
 * Controller Class for'donorMedications.fxml'
 */

package controller;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import model.Donor;
import model.Medication;
import service.Database;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.sql.Timestamp;

import static utility.UserActionHistory.userActions;

public class GUIDonorMedications {

    @FXML
    private TextField newMedication; // The textField that new medications are entered into for adding to the currentMedications ArrayList and listView

    @FXML
    private ListView<String> currentMedications; // A listView for showing the current medications

    @FXML
    private ListView<String> pastMedications; // A listView for showing the past medications

    @FXML
    public void deleteMedication() { // Removes a medication from the history ArrayList and listView
        removeMedication(pastMedications.getSelectionModel().getSelectedItem());

    }

    @FXML
    public void makeCurrent() { // Swaps a medication in history to current ArrayList and listView
        moveToCurrent(pastMedications.getSelectionModel().getSelectedItem());
    }

    @FXML
    public void makeHistory() { // Swaps a medication in current to history ArrayList and listView
        moveToHistory(currentMedications.getSelectionModel().getSelectedItem());
    }

    @FXML
    public void registerMedication() {
        addMedication(newMedication.getText());
    } // Adds a newly entered medication to the current medications array and the listView for the current medications

    private ListProperty<String> currentListProperty = new SimpleListProperty<>();
    private ListProperty<String> historyListProperty = new SimpleListProperty<>();
    private ArrayList<String> current = new ArrayList<>();
    private ArrayList<String> history = new ArrayList<>();
    private Timestamp time;
    private Donor target;

    @FXML
    public void initialize() {
        try {
            target = Database.getDonorByNhi(ScreenControl.getLoggedInDonor().getNhiNumber());

            if(target.getCurrentMedications() == null) {
                target.setCurrentMedications(new ArrayList<>());
            }
            viewCurrentMedications();

            if(target.getMedicationHistory() == null) {
                target.setMedicationHistory(new ArrayList<>());
            }
            viewPastMedications();
        } catch (InvalidObjectException e) {
            userActions.log(Level.SEVERE, "Error loading logged in user", "attempted to manage the medications for logged in user");
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the medications stored in the currentMedications ArrayList.
     * Displays the retrieved medications to the currentMedications listView.
     */
    private void viewCurrentMedications() {
        target.getCurrentMedications().forEach((med) -> current.add(String.valueOf(med)));
        currentListProperty.set( FXCollections.observableArrayList(current));
        currentMedications.itemsProperty().bind(currentListProperty);
    }

    /**
     * Retrieves the medications stored in the medicationHistory ArrayList
     * Displays the retrieved medications to the pastMedications listView
     */
    private void viewPastMedications() {
        target.getMedicationHistory().forEach((med) -> history.add(String.valueOf(med)));
        historyListProperty.set( FXCollections.observableArrayList(history));
        pastMedications.itemsProperty().bind(historyListProperty);
    }

    /**
     * Adds a new medication to the currentMedications ArrayList
     * Resets the currentMedications ListView to display the new medication
     *
     * @param medication The selected medication being added to the current ArrayList and listView
     */
    private void addMedication(String medication) {
        if (!medication.equals("Enter a medication") && !medication.equals("") && !medication.equals(" ")) { // This could maybe do with some more thought
            if (!current.contains(medication) && !history.contains(medication)) {
                target.getCurrentMedications().add(new Medication(medication));
                viewCurrentMedications();
                time = new Timestamp(System.currentTimeMillis());

                if (!target.getMedicationLog().containsKey(medication)) {
                    ArrayList<String> newMedication = new ArrayList<>();
                    newMedication.add(time + " - registered to current: " + medication);
                    target.getMedicationLog().put(medication, newMedication);
                } else {
                    target.getMedicationLog().get(medication).add(time + " - registered to current: " + medication);
                }
            }
        }
    }

    /**
     * Removes a selected medication from the medicationHistory ArrayList
     * Resets the pastMedications ListView to display medicationHistory after the medication is removed
     *
     * @param medication The selected medication being removed from the history ArrayList and listView
     */
    private void removeMedication(String medication) {
        if (history.contains(medication)) {
            target.getMedicationHistory().remove(history.indexOf(medication));
            viewPastMedications();
            time = new Timestamp(System.currentTimeMillis());
            target.getMedicationLog().get(medication).add(time + " - deleted from history: " + medication);
        }
    }

    /**
     * Removes a selected medication from currentMedications ArrayList and adds the medication to medicationHistory ArrayList
     * Updates the listViews for each of current and past medications to match the changes in the respective ArrayLists
     *
     * @param medication The selected medication being moved from history to current ArrayLists and listViews
     */
    private void moveToCurrent(String medication) {
        if (history.contains(medication)) {
            Medication.transferMedication(target.getMedicationHistory(), target.getCurrentMedications(),
                    new Medication(medication), history.indexOf(medication));
            viewPastMedications();
            viewCurrentMedications();
            time = new Timestamp(System.currentTimeMillis());
            target.getMedicationLog().get(medication).add(time + " - moved to current: " + medication);
        }
    }

    /**
     * Removes a selected medication from medicationHistory ArrayList and adds the medication to currentMedications ArrayList
     * Updates the listViews for each of past and current medications to match the changes in the respective ArrayLists
     *
     * @param medication The selected medication being moved from current to history ArrayLists and listViews
     */
    private void moveToHistory(String medication) {
        if (current.contains(medication)) {
            Medication.transferMedication(target.getCurrentMedications(), target.getMedicationHistory(),
                    new Medication(medication), current.indexOf(medication));
            viewCurrentMedications();
            viewPastMedications();
            time = new Timestamp(System.currentTimeMillis());
            target.getMedicationLog().get(medication).add(time + " - moved to history: " + medication);
        }
    }

    public void goToProfile() {
        ScreenControl.removeScreen("donorProfile");
        try {
            ScreenControl.addScreen("donorProfile", FXMLLoader.load(getClass().getResource("/scene/donorProfile.fxml")));
            ScreenControl.activate("donorProfile");
        }catch (IOException e) {
            userActions.log(Level.SEVERE, "Error loading profile screen", "attempted to navigate from the medication page to the profile page");
            new Alert(Alert.AlertType.WARNING, "ERROR loading profile page", ButtonType.OK).showAndWait();
            e.printStackTrace();
        }
    }
}
