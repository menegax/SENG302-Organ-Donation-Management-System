package controller;

import api.APIHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import model.Donor;
import model.Medication;
import service.Database;

import java.io.IOException;
import java.io.InvalidObjectException;
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
    public Button wipeReview;
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
        Database.saveToDisk(); // Save to .json the changes made to medications
        clearSelections();
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
        newMedication.clear();
    }

    private ListProperty<String> currentListProperty = new SimpleListProperty<>();
    private ListProperty<String> historyListProperty = new SimpleListProperty<>();
    private ListProperty<String> informationListProperty = new SimpleListProperty<>();
    private ArrayList<String> current;
    private ArrayList<String> history;
    private ArrayList<String> ingredients;
    private Donor target;
    private JsonObject suggestions;

    @FXML
    public void initialize() {
        //Register events for when an item is selected from a listView and set selection mode to multiple
        currentMedications.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> onSelect(currentMedications));
        pastMedications.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> onSelect(pastMedications));
        pastMedications.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        currentMedications.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

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
            refreshReview();
        } catch (InvalidObjectException e) {
            userActions.log(Level.SEVERE, "Error loading logged in user", "attempted to manage the medications for logged in user");
            e.printStackTrace();
        }
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

    /**
     * Retrieves the medications stored in the currentMedications ArrayList.
     * Displays the retrieved medications to the currentMedications listView.
     */
    private void viewCurrentMedications() {
        current = new ArrayList<>();
        clearSelections();
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
        clearSelections();
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
        if (!medication.equals( "Enter a medication" ) && !medication.equals( "" )) {
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
                target.getMedicationHistory().remove( history.indexOf( medication ) );

                if (!current.contains( medication )) {
                    target.getCurrentMedications().add( new Medication( medication ) );
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
                target.getCurrentMedications().remove( current.indexOf( medication ) );

                if (!history.contains( medication )) {
                    target.getMedicationHistory().add( new Medication( medication ) );
                    viewPastMedications();
                }
                userActions.log(Level.INFO, "Successfully moved a medication", "Removed a past medication for a donor");
                viewCurrentMedications();
            }
        }
    }

    /**
     * Runs when an item is selected within a listView
     * If there is only one item, the function to load the ingredients for the selected medication is called
     * @param listView The listView of the selected item
     */
    private void onSelect(ListView listView) {
        if (listView.getSelectionModel().getSelectedItems().size() == 1) {
            loadMedicationIngredients(listView.getSelectionModel().getSelectedItem().toString());
        }
    }

    /**
     * Fetches the ingredients from the APIHelper, then converts the results into a temporary list.
     * The list, after having a header included, is then added to existing listing of other medicine
     * ingredients and medicine interactions and passed to be bound to the listView
     * @param medication The medication to fetch the ingredients for
     */
    private void loadMedicationIngredients(String medication) {
        APIHelper helper = new APIHelper();
        ArrayList <String> newIngredients = new ArrayList <>();
        Boolean hasIngredients = false;

        if (!ingredients.contains( "Ingredients for '" + medication + "': " )) {
            newIngredients.add( "Ingredients for '" + medication + "': " );

            try {
                if (medication.length() == 1) {
                    getDrugSuggestions( medication );
                } else {
                    getDrugSuggestions(Collections.max(new ArrayList <>(Arrays.asList( medication.split(" ")))));
                }

                if (suggestions.get( "suggestions" ).toString().contains( medication )) {
                    JsonArray response = helper.getMapiDrugIngredients( medication );
                    response.forEach( ( element ) -> newIngredients.add( element.getAsString() ) );
                    hasIngredients = true;
                }
            } catch (IOException e) {
                hasIngredients = false;
            }

            if (!hasIngredients) {
                newIngredients.add( "There are no recorded ingredients for '" + medication + "'");
            }
            newIngredients.add( "" );
            ingredients.addAll( 1, newIngredients );
        } else {
            int index = ingredients.indexOf("Ingredients for '" + medication + "': ");
            String entry;

            for (int i = index; index < ingredients.size(); i++) {
                entry = ingredients.get(i);
                ingredients.remove(i);
                ingredients.add(i - index + 1, entry);

                if (entry.equals("")) {
                    break;
                }
            }
        }
        displayIngredients( ingredients );
    }

    /**
     * Takes a string list of ingredients, and binds it to the medicineInformation listView to be displayed
     * @param ingredients The List of ingredients
     */
    private void displayIngredients(List<String> ingredients) {
        informationListProperty.set(FXCollections.observableList(ingredients));
        medicineInformation.itemsProperty().bind(informationListProperty);
    }

    /**
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

    /**
     * Button for clearing each currently selected medication from being selected on activation
     */
    @FXML
    public void clearSelections() {
        pastMedications.getSelectionModel().clearSelection();
        currentMedications.getSelectionModel().clearSelection();
        medicineInformation.getSelectionModel().clearSelection();
    }

    /**
     * Button for clearing the information being currently displayed on the medicine information ListView on activation
     */
    @FXML
    public void refreshReview() {
        ingredients = new ArrayList<>(Collections.singletonList("ACTIVE INGREDIENTS FOR MEDICINE(S):"));
        displayIngredients( ingredients );
    }
}
