package controller;

import api.APIHelper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.Donor;
import model.Medication;
import service.TextWatcher;
import service.Database;

import java.awt.*;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.sql.Timestamp;
import java.util.*;
import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

public class GUIDonorMedications {

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
    public Button reviewMed;
    public Button clearMed;

    @FXML
    private TextField newMedication; // Medications are entered for adding to the currentMedications ArrayList and listView

    @FXML
    private ListView<String> currentMedications; // A listView for showing the current medications

    @FXML
    private ListView<String> pastMedications; // A listView for showing the past medications

    @FXML
    private ListView<String> medicineInformation; // A listView for showing medicine ingredients and interactions

    @FXML
    public void undo() {
        System.out.print( "undo" );  // To be completed by Story 12 and 13 responsible's
    }

    @FXML
    public void redo() {
        System.out.print( "redo" );  // To be completed by Story 12 and 13 responsible's
    }

    @FXML
    /*
     * Retrieves selected medicines when review medicine button is activated, joins and sorts them for reviewing
     */
    public void reviewMedicine() {
        ArrayList<String> selections = new ArrayList <>( pastMedications.getSelectionModel().getSelectedItems());
        selections.addAll(currentMedications.getSelectionModel().getSelectedItems() );
        Collections.sort(selections);
        removeMedication( selections );  // Lists the selected medicines for reviewing the ingredients
    }

    @FXML
    /*
     * Removes a medication from the history or current ArrayList and listView
     */
    public void deleteMedication() {
        ArrayList<String> selections = new ArrayList <>( pastMedications.getSelectionModel().getSelectedItems());
        selections.addAll(currentMedications.getSelectionModel().getSelectedItems() );
        removeMedication( selections );
    }

    @FXML
    /*
     * Saves the current state of the history and current medications ArrayLists
     */
    public void saveMedication() {
        Database.saveToDisk(); // Save to .json the changes made to medications
        currentMedications.getSelectionModel().clearSelection();
        pastMedications.getSelectionModel().clearSelection();
    }

    @FXML
    /*
     * Swaps a medication in history to current ArrayList and listView
     */
    public void makeCurrent() {
        if (pastMedications.getFocusModel().getFocusedItem() == null) {
            currentMedications.getSelectionModel().clearSelection();
        } else {
            moveToCurrent( new ArrayList <>( pastMedications.getSelectionModel().getSelectedItems() ) );
        }
    }

    @FXML
    /*
     * Swaps a medication in current to history ArrayList and listView
     */
    public void makeHistory() {
        if (currentMedications.getFocusModel().getFocusedItem() == null) {
            pastMedications.getSelectionModel().clearSelection();
        } else {
            moveToHistory( new ArrayList <>( currentMedications.getSelectionModel().getSelectedItems() ) );
        }
    }

    @FXML
    /*
     * Adds a newly entered medication to the current medications array and the listView for the current medications
     */
    public void registerMedication() {
        addMedication(newMedication.getText());
        newMedication.clear();
    }

    private ListProperty<String> currentListProperty = new SimpleListProperty<>();
    private ListProperty<String> historyListProperty = new SimpleListProperty<>();
    private ArrayList<String> current;
    private ArrayList<String> history;
    private Donor target;
    private JsonObject suggestions;

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
            addActionListeners();
        } catch (InvalidObjectException e) {
            userActions.log(Level.SEVERE, "Error loading logged in user", "attempted to manage the medications for logged in user");
            e.printStackTrace();
        }
    }


    /**
     * Adds an actionListener to the text property of the medication search field and passes text
     * to getDrugSuggestions
     */
    private void addActionListeners(){
        TextWatcher textWatcher = new TextWatcher();
        newMedication.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)){
                textWatcher.onTextChange(); //reset timer
            }
            try {
                textWatcher.afterTextChange(GUIDonorMedications.class.getMethod("parseAction"),
                        this); //start timer
            } catch (NoSuchMethodException e) {
                userActions.log(Level.SEVERE, e.getMessage());
            }
        });
    }

    /**
     *
     */
    public void parseAction(){
        getDrugSuggestions(newMedication.getText());
        displayDrugSuggestions();
    }

    /**
     * Sets a list of suggestions given a partially matching string
     * @param query - text to match drugs against
     */
    private void getDrugSuggestions(String query){
        APIHelper apiHelper = new APIHelper();
        try {
            suggestions =  apiHelper.getMapiDrugSuggestions(query);
        } catch (IOException exception) {
            suggestions = null;
        }
    }

    //TODO
    private void displayDrugSuggestions(){
        Stage stage = new Stage();
        Scene scene = new Scene(new Group(), 450, 250);

        System.out.println(suggestions.get("suggestions"));

        String[] sug = suggestions.get("suggestions").toString().split(" ");

        ContextMenu contextMenu = new ContextMenu();

        for (String suggestion : sug) {
            MenuItem item = new MenuItem(suggestion);
            contextMenu.getItems().add(item);
        }

        newMedication.setContextMenu(contextMenu);

        GridPane grid = new GridPane();
        grid.setVgap(4);
        grid.setHgap(10);
        //grid.setPadding(new Insets(5, 5, 5, 5));
        grid.add(new Label("To: "), 0, 0);
        grid.add(newMedication, 1, 0);

        Group root = (Group) scene.getRoot();
        root.getChildren().add(grid);
        stage.setScene(scene);
        stage.show();
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
        if (!medication.equals( "Enter a medication" ) && !medication.equals( "" ) && !medication.substring(0, 1).equals(" ")) {
            medication = medication.substring(0, 1).toUpperCase() + medication.substring(1).toLowerCase();

            if (!(current.contains(medication) || history.contains(medication))) {
                target.getCurrentMedications().add( new Medication(medication));
                userActions.log(Level.INFO, "Successfully registered a medication", "Registered a new medication for a donor");
                viewCurrentMedications();
                newMedication.clear();
            } else if (history.contains(medication) && !current.contains(medication)) {
                moveToCurrent(new ArrayList<>(Collections.singleton( medication ) ));
                newMedication.clear();
            } else {
                Alert err = new Alert(Alert.AlertType.ERROR, "'" + medication + "' is already registered");
                err.show();
            }
        } else {
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
            Optional <ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                if (history.contains( medication )) {
                    target.getMedicationHistory().remove( history.indexOf( medication ) );
                    userActions.log( Level.INFO, "Successfully deleted a medication", "Deleted a past medication for a donor" );
                    viewPastMedications();
                } else if (current.contains( medication )) {
                    target.getCurrentMedications().remove( current.indexOf( medication ) );
                    userActions.log( Level.INFO, "Successfully deleted a medication", "Deleted a current medication for a donor" );
                    viewCurrentMedications();
                }
            }
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
                userActions.log(Level.INFO, "Successfully moved a medication", "Re-added a current medication for a donor");
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
                userActions.log(Level.INFO, "Successfully moved a medication", "Removed a past medication for a donor");
                viewCurrentMedications();
            }
        }
    }

    /*
     * Navigates from the Medication panel to the home panel after 'back' is selected, saves medication log
     */
    @FXML
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

    /*
     * Clears each currently selected medication from being selected
     */
    @FXML
    public void clearSelections() {
        pastMedications.getSelectionModel().clearSelection();
        currentMedications.getSelectionModel().clearSelection();
    }
}
