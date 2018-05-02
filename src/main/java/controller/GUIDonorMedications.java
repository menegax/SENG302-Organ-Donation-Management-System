package controller;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import model.Donor;
import model.Medication;
import service.Database;
import utility.UserActionRecord;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;

import static utility.UserActionHistory.userActions;
import static utility.UserActionRecord.logHistory;

public class GUIDonorMedications implements IPopupable {

    private ListProperty<String> currentListProperty = new SimpleListProperty<>();
    private ListProperty<String> historyListProperty = new SimpleListProperty<>();
    private ObservableList<UserActionRecord> medLog = FXCollections.observableArrayList();
    private ArrayList<String> current;
    private ArrayList<String> history;
    private Timestamp time;
    private Donor target;

    @FXML
    public AnchorPane medicationPane;
    public Button registerMed;
    public Button removeMed;
    public Button addMed;
    public Button deleteMed;
    public Button saveMed;
    public Button undoEdit;
    public Button redoEdit;
    public Button goBack;
    public Button clearMed;

    /*
     * Textfield for entering medications for adding to the currentMedications ArrayList and listView
     */
    @FXML
    private TextField newMedication;

    /*
     * A listView for showing the current medications
     */
    @FXML
    private ListView<String> currentMedications;

    /*
     * A listView for showing the past medications
     */
    @FXML
    private ListView<String> pastMedications;

    /*
     * A listView for showing medicine ingredients and interactions
     */
    @FXML
    private ListView<String> medicineInformation;

    private Donor viewedDonor;

    public void setViewedDonor(Donor donor) {
        viewedDonor = donor;
        loadProfile(viewedDonor.getNhiNumber());
    }

    @FXML
    public void undo() {
        System.out.print( "undo" );  // To be completed by Story 12 and 13 responsible's
    }

    @FXML
    public void redo() {
        System.out.print( "redo" );  // To be completed by Story 12 and 13 responsible's
    }

    /**
     * Removes a medication from the history or current ArrayList and listView
     */
    @FXML
    public void deleteMedication() {
        ArrayList<String> selections = new ArrayList <>( pastMedications.getSelectionModel().getSelectedItems());
        selections.addAll(currentMedications.getSelectionModel().getSelectedItems() );
        removeMedication( selections );
    }

    /**
     * Saves the current state of the history and current medications ArrayLists
     */
    @FXML
    public void saveMedication() {
        medLog.add(new UserActionRecord(String.valueOf(time), Level.FINE.toString(), "Medications are now saved", "Medications has been saved"));
        logHistory.add(medLog.get(medLog.size() - 1));
        Alert save = new Alert(Alert.AlertType.CONFIRMATION, "Medication(s) have been successfully saved");
        final Button dialogOK = (Button) save.getDialogPane().lookupButton(ButtonType.OK);
        dialogOK.addEventFilter(ActionEvent.ACTION, event -> saveToDisk());// Save to .json the changes made to medications
        save.show();
        clearSelections();
    }

    private void saveToDisk() {
        if (target.getMedicationLog() != null) {
            ObservableList<UserActionRecord> log = target.getMedicationLog();
            log.addAll(medLog);
            target.setMedicationLog(log);
        } else {
            target.setMedicationLog( medLog );
        }
        Database.saveToDisk();
        medLog = FXCollections.observableArrayList();
    }

    /**
     * Swaps a medication in history to current ArrayList and listView
     */
    @FXML
    public void makeCurrent() {
        if (pastMedications.getFocusModel().getFocusedItem() == null) {
            currentMedications.getSelectionModel().clearSelection();
        } else {
            moveToCurrent( new ArrayList <>( pastMedications.getSelectionModel().getSelectedItems() ) );
        }
    }

    /**
     * Swaps a medication in current to history ArrayList and listView
     */
    @FXML
    public void makeHistory() {
        if (currentMedications.getFocusModel().getFocusedItem() == null) {
            pastMedications.getSelectionModel().clearSelection();
        } else {
            moveToHistory( new ArrayList <>( currentMedications.getSelectionModel().getSelectedItems() ) );
        }
    }

    /**
     * Adds a newly entered medication to the current medications array and the listView for the current medications
     */
    @FXML
    public void registerMedication() {
        addMedication(newMedication.getText());
    }

    /**
     * Initializes the Medication GUI pane, adds any medications stored for donor to current and past listViews
     */
    @FXML
    public void initialize() {
        pastMedications.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        currentMedications.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        if (ScreenControl.getLoggedInDonor() != null) {
            loadProfile(ScreenControl.getLoggedInDonor().getNhiNumber());
        }
    }

    public void loadProfile(String nhi) {
        try {
            target = Database.getDonorByNhi(nhi);

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
        clearSelections();
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
        clearSelections();
        history = new ArrayList<>();
        target.getMedicationHistory().forEach((med) -> history.add(String.valueOf(med)));
        historyListProperty.set( FXCollections.observableArrayList(history));
        pastMedications.itemsProperty().bind(historyListProperty);
    }

    /**
     * Adds a new medication to the currentMedications ArrayList
     * Resets the currentMedications ListView to display the new medication
     * @param medication The selected medication being added to the current ArrayList and listView
     */
    private void addMedication(String medication) {
        if (!medication.equals( "Enter medicine" ) && !medication.equals( "" ) && !medication.substring(0, 1).equals(" ")) {
            medication = medication.substring(0, 1).toUpperCase() + medication.substring(1).toLowerCase();

            if (!(current.contains(medication) || history.contains(medication))) {
                target.getCurrentMedications().add( new Medication(medication));
                time = new Timestamp(System.currentTimeMillis());
                medLog.add(new UserActionRecord(String.valueOf(time), Level.FINE.toString(), medication + " is now registered as current", medication + " has successfully been registered"));
                logHistory.add(medLog.get(medLog.size() - 1));
                viewCurrentMedications();
                newMedication.clear();
            } else if (history.contains(medication) && !current.contains(medication)) {
                moveToCurrent(new ArrayList<>(Collections.singleton( medication ) ));
                newMedication.clear();
            } else {
                time = new Timestamp(System.currentTimeMillis());
                medLog.add(new UserActionRecord(String.valueOf(time), Level.WARNING.toString(), medication + " is already registered", medication + " has failed registration"));
                logHistory.add(medLog.get(medLog.size() - 1));
                Alert err = new Alert(Alert.AlertType.ERROR, "'" + medication + "' is already registered");
                err.show();
            }
        } else {
            time = new Timestamp(System.currentTimeMillis());
            medLog.add(new UserActionRecord(String.valueOf(time), Level.WARNING.toString(), medication + " is invalid for registration", medication + " has failed registration"));
            logHistory.add(medLog.get(medLog.size() - 1));
            Alert err = new Alert(Alert.AlertType.ERROR, "'" + medication + "' is invalid for registration");
            err.show();
        }
    }

    /**
     * Removes a selected medication from the medicationHistory ArrayList
     * Resets the pastMedications ListView to display medicationHistory after the medication is removed
     * @param medications The selected medications being removed from the history ArrayList and listView
     */
    private void removeMedication(ArrayList<String> medications) {
        for (String medication : medications) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Confirm deletion of " + medication + "?");
            final Button dialogOK = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
            dialogOK.addEventFilter(ActionEvent.ACTION, event -> performDelete(medication));
            alert.show();
        }
    }

    /**
     * Called when the user confirms the deletion of the selected medication(s) in the alert window.
     */
    private void performDelete(String medication) {
        if (history.contains( medication )) {
            target.getMedicationHistory().remove( history.indexOf( medication ) );
            time = new Timestamp(System.currentTimeMillis());
            medLog.add(new UserActionRecord(String.valueOf(time), Level.FINE.toString(), medication + " is now removed", medication + " is deleted from history list"));
            logHistory.add(medLog.get(medLog.size() - 1));
            viewPastMedications();
        } else if (current.contains( medication )) {
            target.getCurrentMedications().remove( current.indexOf( medication ) );
            time = new Timestamp(System.currentTimeMillis());
            medLog.add(new UserActionRecord(String.valueOf(time), Level.FINE.toString(), medication + " is now removed", medication + " is deleted from current list"));
            logHistory.add(medLog.get(medLog.size() - 1));
            viewCurrentMedications();
        }
    }

    /**
     * Removes a selected medication from currentMedications ArrayList and adds the medication to medicationHistory ArrayList
     * Updates the listViews for each of current and past medications to match the changes in the respective ArrayLists
     * @param medications The selected medications being moved from history to current ArrayLists and listViews
     */
    private void moveToCurrent(ArrayList<String> medications) {
        for (String medication : medications) {
            if (history.contains( medication )) {
                target.getMedicationHistory().remove( history.indexOf( medication ));

                if (!current.contains( medication )) {
                    target.getCurrentMedications().add( new Medication( medication )  );
                    viewCurrentMedications();
                }
                time = new Timestamp(System.currentTimeMillis());
                medLog.add(new UserActionRecord(String.valueOf(time), Level.FINE.toString(), medication + " is now current", medication + " moved from history to current list"));
                logHistory.add(medLog.get(medLog.size() - 1));
                viewPastMedications();
            }
        }
    }

    /**
     * Removes a selected medication from medicationHistory ArrayList and adds the medication to currentMedications ArrayList
     * Updates the listViews for each of past and current medications to match the changes in the respective ArrayLists
     * @param medications The selected medications being moved from current to history ArrayLists and listViews
     */
    private void moveToHistory(ArrayList<String> medications) {
        for (String medication : medications) {
            if (current.contains( medication )) {
                target.getCurrentMedications().remove( current.indexOf( medication ));

                if (!history.contains( medication )) {
                    target.getMedicationHistory().add( new Medication( medication ) );
                    viewPastMedications();
                }
                time = new Timestamp(System.currentTimeMillis());
                medLog.add(new UserActionRecord(String.valueOf(time), Level.FINE.toString(), medication + " is now history", medication + " moved from current to history list"));
                logHistory.add(medLog.get(medLog.size() - 1));
                viewCurrentMedications();
            }
        }
    }

    /**
     * Navigates from the Medication panel to the home panel after 'back' is selected, saves medication log
     */
    @FXML
    public void goToProfile() {
        if (ScreenControl.getLoggedInDonor() != null ) {
            ScreenControl.removeScreen("donorProfile");
            try {
                ScreenControl.addScreen("donorProfile", FXMLLoader.load(getClass().getResource("/scene/donorProfile.fxml")));
                ScreenControl.activate("donorProfile");
            }catch (IOException e) {
                userActions.log(Level.SEVERE, "Error loading profile screen", "attempted to navigate from the medication page to the profile page");
                new Alert(Alert.AlertType.WARNING, "ERROR loading profile page", ButtonType.OK).showAndWait();
                e.printStackTrace();
            }
        } else {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scene/donorProfile.fxml"));
            try {
                ScreenControl.loadPopUpPane(medicationPane.getScene(), fxmlLoader, viewedDonor);
            } catch (IOException e) {
                userActions.log(Level.SEVERE, "Error loading profile screen in popup", "attempted to navigate from the edit page to the profile page in popup");
                new Alert(Alert.AlertType.ERROR, "Error loading profile page", ButtonType.OK).showAndWait();
            }
        }
    }

    /**
     * Clears each currently selected medication from being selected
     */
    @FXML
    public void clearSelections() {
        pastMedications.getSelectionModel().clearSelection();
        currentMedications.getSelectionModel().clearSelection();
    }
}
