/**
 * Controller Class for'donorMedications.fxml'
 */

package controller;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import model.Donor;
import model.Medication;
import service.Database;

import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.logging.Level;

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
        removeMedication(pastMedications.getSelectionModel().getSelectedItem()); // found 'getSelectionModel().getSelectedItem() researching online. It should retrieve the mouse selected med
    }

    @FXML
    public void makeCurrent() { // Swaps a medication in history to current ArrayList and listView
        moveToCurrent(pastMedications.getSelectionModel().getSelectedItem()); // found 'getSelectionModel().getSelectedItem() researching online. It should retrieve the mouse selected med
    }

    @FXML
    public void makeHistory() { // Swaps a medication in current to history ArrayList and listView
        moveToHistory(currentMedications.getSelectionModel().getSelectedItem()); // found 'getSelectionModel().getSelectedItem() researching online. It should retrieve the mouse selected med
    }

    @FXML
    public void registerMedication() {
        addMedication(newMedication.getText());
    } // Adds a newly entered medication to the current medications array and the listView for the current medications

    private ListProperty<String> currentListProperty = new SimpleListProperty<>();
    private ListProperty<String> historyListProperty = new SimpleListProperty<>();
    private ArrayList<String> current = new ArrayList<>();
    private ArrayList<String> history = new ArrayList<>();
    private Donor target;

    @FXML
    public void initialize() {
        try {
            target = Database.getDonorByNhi(ScreenControl.getLoggedInDonor().getNhiNumber());
            viewCurrentMedications();
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
        if(target.getCurrentMedications() == null) {
            target.setCurrentMedications(new ArrayList<>());
            target.getCurrentMedications().add(new Medication("Ibuprofen"));
        }
        current = new ArrayList<>();
        target.getCurrentMedications().forEach((med) -> current.add(String.valueOf(med)));
        currentListProperty.set( FXCollections.observableArrayList(current));
        currentMedications.itemsProperty().bind(currentListProperty);
    }

    /**
     * Retrieves the medications stored in the medicationHistory ArrayList
     * Displays the retrieved medications to the pastMedications listView
     */
    private void viewPastMedications() {
        if(target.getMedicationHistory() == null) {
            target.setMedicationHistory(new ArrayList<>());
            target.getMedicationHistory().add(new Medication("Panadol"));
        }
        history = new ArrayList<>();
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
            }
        }
    }

    /**
     * Removes a selected medication from the medicationHistory ArrayList
     * Resets the pastMedications ListView to display medicationHistory after the medication is removed
     *
     * ASSUMPTION: delete only from history, as assuming a med can be clicked on in both lists, and then when pressing delete both will be deleted.
     *
     * @param medication The selected medication being removed from the history ArrayList and listView
     */
    private void removeMedication(String medication) {
        if (history.contains(medication)) {
            target.getMedicationHistory().add(new Medication(medication));
            viewPastMedications();
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
            if (current.contains(medication)) {
                // although this should not happen, remove the medication from current listView prior to swapping
            }
            Medication.transferMedication(target.getMedicationHistory(), target.getCurrentMedications(),
                    new Medication(medication), history.indexOf(medication));
            viewPastMedications();
            viewCurrentMedications();
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
            if (history.contains(medication)) {
                // although this should not happen, remove the from medication from history listView prior to swapping
            }
            Medication.transferMedication(target.getCurrentMedications(), target.getMedicationHistory(),
                    new Medication(medication), current.indexOf(medication));
            viewCurrentMedications();
            viewPastMedications();
        }
    }
}
