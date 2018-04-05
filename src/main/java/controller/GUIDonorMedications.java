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
import java.awt.event.ActionEvent;
import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.Arrays;
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
    void deleteMedication(ActionEvent event) {
        removeMedication(pastMedications.getSelectionModel().getSelectedItem()); // found 'getSelectionModel().getSelectedItem() researching online. It should retrieve the mouse selected med
    }

    @FXML
    void makeCurrent(ActionEvent event) {
        moveToCurrent(pastMedications.getSelectionModel().getSelectedItem()); // found 'getSelectionModel().getSelectedItem() researching online. It should retrieve the mouse selected med
    }

    @FXML
    void makeHistory(ActionEvent event) {
        moveToHistory(currentMedications.getSelectionModel().getSelectedItem()); // found 'getSelectionModel().getSelectedItem() researching online. It should retrieve the mouse selected med
    }

    @FXML
    void registerMedication(ActionEvent event) {
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
            //current = target.getCurrentMedications();
            target.getCurrentMedications().forEach((med) -> current.add(String.valueOf(med)));
            viewCurrentMedications();
            //history = target.getMedicationHistory();
            target.getMedicationHistory().forEach((med) -> history.add(String.valueOf(med)));
            viewPastMedications();
        } catch (InvalidObjectException e) {
            userActions.log(Level.SEVERE, "Error loading logged in user", "attempted to manage the medications for logged in user");
            e.printStackTrace();
        }
    }

    public void saveMedications() { // CURRENTLY STILL RESEARCHING THE CORRECT METHOD(S) FOR THIS
        // on mouse click of registerMedication button
        //registerMedication(event);

        // on mouse click of deleteMedication button
        //deleteMedication(event);

        // on mouse click of makeCurrent button
        //makeCurrent(event);

        // on mouse click of makeHistory button
        //makeHistory(event);
    }

    /**
     * Retrieves the medications stored in the currentMedications ArrayList.
     * Displays the retrieved medications to the currentMedications listView.
     */
    private void viewCurrentMedications() {
        currentListProperty.set( FXCollections.observableArrayList(current));
        currentMedications.itemsProperty().bind(currentListProperty);
        target.setCurrentMedications(current); // REQUIRES CONVERTING ARRAYLIST TYPE STRING TO TYPE MEDICATION - NOT COMPLETED
    }

    /**
     * Retrieves the medications stored in the medicationHistory ArrayList
     * Displays the retrieved medications to the pastMedications listView
     */
    private void viewPastMedications() {
        historyListProperty.set( FXCollections.observableArrayList(history));
        pastMedications.itemsProperty().bind(historyListProperty);
        target.setMedicationHistory(new ArrayList<Medication>(history)); // REQUIRES CONVERTING ARRAYLIST TYPE STRING TO TYPE MEDICATION - NOT COMPLETED
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
                current.add(medication);
                viewCurrentMedications();
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
            history.remove( medication );
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
            history.remove( medication );

            if (!current.contains(medication)) {
                current.add( medication );
                viewCurrentMedications();
            }
            viewPastMedications();
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
            current.remove( medication );

            if (!history.contains(medication)) {
                history.add( medication );
                viewPastMedications();
            }
            viewCurrentMedications();
        }
    }
}
