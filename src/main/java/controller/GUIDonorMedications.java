/**
 * Controller Class for'donorMedications.fxml'
 */

package controller;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import model.Donor;
import model.Medication;
import service.Database;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.sql.Timestamp;
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
    public void deleteMedication() { // Removes a medication from either the history or current ArrayList and listView
        if (pastMedications.getSelectionModel().getSelectedIndex() != -1) {
            removeMedication(new ArrayList<>(pastMedications.getSelectionModel().getSelectedItems()));
        } else if (currentMedications.getSelectionModel().getSelectedIndex() != -1) {
            removeMedication(new ArrayList<>(currentMedications.getSelectionModel().getSelectedItems()));
        }
        pastMedications.getSelectionModel().clearSelection();
        currentMedications.getSelectionModel().clearSelection();
    }

    @FXML
    public void makeCurrent() { // Swaps a medication in history to current ArrayList and listView
        moveToCurrent(new ArrayList<>(pastMedications.getSelectionModel().getSelectedItems()));
        pastMedications.getSelectionModel().clearSelection();
        currentMedications.getSelectionModel().clearSelection();
    }

    @FXML
    public void makeHistory() { // Swaps a medication in current to history ArrayList and listView
        moveToHistory(new ArrayList<>(currentMedications.getSelectionModel().getSelectedItems()));
        pastMedications.getSelectionModel().clearSelection();
        currentMedications.getSelectionModel().clearSelection();
    }

    @FXML
    public void registerMedication() {
        addMedication(newMedication.getText());
    } // Adds a newly entered medication to the current medications array and the listView for the current medications

    private ListProperty<String> currentListProperty = new SimpleListProperty<>();
    private ListProperty<String> historyListProperty = new SimpleListProperty<>();
    private ArrayList<String> current;
    private ArrayList<String> history;
    private Timestamp time;
    private Donor target;

    @FXML
    public void initialize() {
        try {
            pastMedications.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            currentMedications.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
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
            if (!(current.contains(medication) || history.contains(medication))) {
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
     * @param medications The selected medications being removed from the history ArrayList and listView
     */
    private void removeMedication(ArrayList<String> medications) {
        for (String medication : medications) {
            if (history.contains( medication )) {
                target.getMedicationHistory().remove( history.indexOf( medication ) );
                viewPastMedications();
                time = new Timestamp(System.currentTimeMillis());
                target.getMedicationLog().get(medication).add(time + " - deleted from history: " + medication);
            } else if (current.contains( medication )) {
                target.getCurrentMedications().remove( current.indexOf( medication ) );
                viewCurrentMedications();
                time = new Timestamp(System.currentTimeMillis());
                target.getMedicationLog().get(medication).add(time + " - deleted from current: " + medication);
            }
        }
    }

    /**
     * Removes a selected medication from currentMedications ArrayList and adds the medication to medicationHistory ArrayList
     * Updates the listViews for each of current and past medications to match the changes in the respective ArrayLists
     *
     * @param medications The selected medications being moved from history to current ArrayLists and listViews
     */
    private void moveToCurrent(ArrayList<String> medications) {
        for (String medication : medications) {
            if (history.contains( medication )) {
                //Medication.transferMedication(target.getMedicationHistory(), target.getCurrentMedications(),
                //      new Medication(medication), history.indexOf(medication));
                target.getMedicationHistory().remove( history.indexOf( medication ) );
                viewPastMedications();

                if (!current.contains( medication )) {
                    target.getCurrentMedications().add( new Medication( medication ) );
                    viewCurrentMedications();
                }
                time = new Timestamp(System.currentTimeMillis());
                target.getMedicationLog().get(medication).add(time + " - moved to current: " + medication);
            }
        }
    }

    /**
     * Removes a selected medication from medicationHistory ArrayList and adds the medication to currentMedications ArrayList
     * Updates the listViews for each of past and current medications to match the changes in the respective ArrayLists
     *
     * @param medications The selected medications being moved from current to history ArrayLists and listViews
     */
    private void moveToHistory(ArrayList<String> medications) {
        for (String medication : medications) {
            if (current.contains( medication )) {
                //Medication.transferMedication(target.getCurrentMedications(), target.getMedicationHistory(),
                //      new Medication(medication), current.indexOf(medication));
                target.getCurrentMedications().remove( current.indexOf( medication ) );
                viewCurrentMedications();

                if (!history.contains( medication )) {
                    target.getMedicationHistory().add( new Medication( medication ) );
                    viewPastMedications();
                }
                time = new Timestamp(System.currentTimeMillis());
                target.getMedicationLog().get(medication).add(time + " - moved to history: " + medication);
            }
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
