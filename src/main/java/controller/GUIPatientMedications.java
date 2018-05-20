package controller;

import api.APIHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import model.Clinician;
import model.Patient;
import model.DrugInteraction;
import model.Medication;
import model.Patient;
import service.Database;
import service.TextWatcher;
import utility.UserActionRecord;
import utility.undoRedo.StatesHistoryScreen;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.sql.Timestamp;
import java.util.*;
import java.util.logging.Level;

import static utility.UserActionHistory.userActions;
//import static utility.UserActionRecord.logHistory;

public class GUIPatientMedications {

    private ListProperty<String> currentListProperty = new SimpleListProperty<>();
    private ListProperty<String> historyListProperty = new SimpleListProperty<>();
    private ObservableList<UserActionRecord> medLog = FXCollections.observableArrayList();
    private ListProperty<String> informationListProperty = new SimpleListProperty<>();
    private StatesHistoryScreen stateHistoryScreen;
    private ArrayList<String> ingredients;
    private ArrayList<String> current;
    private ArrayList<String> history;
    private Timestamp time;
    private Patient target;

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
    public Button compareMeds;
    public Button clearMed;
    public ContextMenu contextMenu;

    private JsonObject suggestions;
    private boolean itemSelected = false;

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

    private Patient viewedPatient;

    public void setViewedPatient(Patient patient) {
        viewedPatient = patient;
        loadProfile(viewedPatient.getNhiNumber());
    }

    /**
     * Goes back one edit if any editing has been conducted
     */
    @FXML
    public void undo() {
        stateHistoryScreen.undo();
    }

    /**
     * Goes forward one edit if editing had been undone at least once
     */
    @FXML
    public void redo() {
        stateHistoryScreen.redo();
    }

    /**
     * Removes a medication from the history or current ArrayList and listView
     */
    @FXML
    public void deleteMedication() {
        ArrayList<String> selections = new ArrayList<>(pastMedications.getSelectionModel().getSelectedItems());
        selections.addAll(currentMedications.getSelectionModel().getSelectedItems());
        removeMedication(selections);
    }

    /**
     * Saves the current state of the history and current medications ArrayLists
     */
    @FXML
    public void saveMedication() {
        time = new Timestamp(System.currentTimeMillis());
//        medLog.add(new UserActionRecord(String.valueOf(time), Level.FINE.toString(), "Medications are now saved", "Medications has been saved"));
//        logHistory.add(medLog.get(medLog.size() - 1));
        Alert save = new Alert(Alert.AlertType.INFORMATION, "Medication(s) have been successfully saved");
        final Button dialogOK = (Button) save.getDialogPane().lookupButton(ButtonType.OK);
//        dialogOK.addEventFilter(ActionEvent.ACTION, event -> saveToDisk());// Save to .json the changes made to medications
        save.show();
        clearSelections();
    }

//    /**
//     * Saves the current state of the logged-in patient data to the patient.json file
//     */
//    private void saveToDisk() {
//        if (target.getPatientLog() != null) {
//            ObservableList<UserActionRecord> log = target.getPatientLog();
//            log.addAll(medLog);
//            target.setMedicationLog(log);
//        } else {
//            target.setMedicationLog( medLog );
//        }
//        Database.saveToDisk();
//        medLog = FXCollections.observableArrayList();
//    }

    /**
     * Swaps a medication in history to current ArrayList and listView
     */
    @FXML
    public void makeCurrent() {
        if (pastMedications.getFocusModel().getFocusedItem() == null) {
            currentMedications.getSelectionModel().clearSelection();
        } else {
            moveToCurrent(new ArrayList<>(pastMedications.getSelectionModel().getSelectedItems()));
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
            moveToHistory(new ArrayList<>(currentMedications.getSelectionModel().getSelectedItems()));
        }
    }

    /**
     * Adds a newly entered medication to the current medications array and the listView for the current medications
     */
    @FXML
    public void registerMedication() {
        addMedication(newMedication.getText());
    }

    private UserControl userControl;

    /**
     * Initializes the Medication GUI pane, adds any medications stored for donor to current and past listViews
     */
    @FXML
    public void initialize() {
        userControl = new UserControl();
        Object user = userControl.getLoggedInUser();
        //Register events for when an item is selected from a listView and set selection mode to multiple
        currentMedications.setOnMouseClicked(event -> onSelect(currentMedications));
        pastMedications.setOnMouseClicked(event -> onSelect(pastMedications));
        pastMedications.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        currentMedications.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        stateHistoryScreen = new StatesHistoryScreen(medicationPane, new ArrayList<Control>() {{
            add(newMedication);
        }});
        if (user instanceof Patient) {
            loadProfile(((Patient) user).getNhiNumber());
        } else if (user instanceof Clinician) {
            viewedPatient = userControl.getTargetPatient();
            loadProfile(viewedPatient.getNhiNumber());
        }
    }

    /**
     * Loads the donor medication GUI pane with all logged-in patient medication data listed in listViews
     *
     * @param nhi The NHI number of the logged-in patient
     */
    private void loadProfile(String nhi) {
        try {
            target = Database.getPatientByNhi(nhi);

            if (target.getCurrentMedications() == null) {
                target.setCurrentMedications(new ArrayList<>());
            }
            viewCurrentMedications();

            if (target.getMedicationHistory() == null) {
                target.setMedicationHistory(new ArrayList<>());
            }
            viewPastMedications();
            refreshReview();
            addActionListeners();
            refreshReview();
        } catch (InvalidObjectException e) {
            userActions.log(Level.SEVERE, "Error loading logged in user", "attempted to manage the medications for logged in user");
        }
    }

    /**
     * Sets a list of suggestions given a partially matching string
     * Adds an action listener to the text property of the medication search field and passes text
     * to getDrugSuggestions
     */
    private void addActionListeners() {
        TextWatcher textWatcher = new TextWatcher();
        newMedication.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                textWatcher.onTextChange(); //reset timer, user hasn't finished typing yet
            }
            if (itemSelected) {
                itemSelected = false; //reset
            }
            if (!newMedication.getText().equals("") && !itemSelected) { //don't make an empty api call. will crash FX thread()) {
                try {
                    textWatcher.afterTextChange(GUIPatientMedications.class.getMethod("autoComplete"), this); //start timer

                } catch (NoSuchMethodException e) {
                    userActions.log(Level.SEVERE, e.getMessage(), "");
                }
            }
        });
    }

    /**
     * Runs the updating of UI elements and API call
     */
    @SuppressWarnings("WeakerAccess")
    public void autoComplete() {
        Platform.runLater(() -> { // run this on the FX thread (next available)
            getDrugSuggestions(newMedication.getText().trim()); //possibly able to run this on the timer thread
            displayDrugSuggestions();//UPDATE UI
        });
    }

    /**
     * Sets a list of suggestions given a partially matching string
     *
     * @param query - text to match drugs against
     */
    private void getDrugSuggestions(String query) {
        APIHelper apiHelper = new APIHelper();
        try {
            suggestions = apiHelper.getMapiDrugSuggestions(query);
        } catch (IOException exception) {
            suggestions = null;
            userActions.log(Level.WARNING, "Illegal characters in query");
        }
    }

    /**
     * Display the drug suggestions in the context menu
     */
    private void displayDrugSuggestions() {
        contextMenu.getItems().clear();
        List<String> suggestionArray = new ArrayList<>();
        suggestions.get("suggestions").getAsJsonArray().forEach((suggestion) -> suggestionArray.add(suggestion.getAsString()));
        Collections.shuffle(suggestionArray); //shuffle array so each time results are slightly different (to combat not being able to view all)
        for (int i = 0; i < 10 && i < suggestionArray.size(); i++) {
            MenuItem item = createMenuItem(suggestionArray.get(i));
            contextMenu.getItems().add(item);
        }
        contextMenu.show(newMedication, Side.BOTTOM, 0, 0);
    }

    /**
     * Create menu items to display inside the context menu
     *
     * @param suggestion - string to display inside the context menu
     * @return - menu item to be placed into the context menu
     */
    private MenuItem createMenuItem(String suggestion) {
        Label menuLabel = new Label(suggestion);//create label with suggestion
        menuLabel.setPrefWidth(newMedication.getPrefWidth() - 30);
        menuLabel.setWrapText(true);
        MenuItem item = new MenuItem();
        item.setGraphic(menuLabel);
        item.setOnAction((ae) -> {
            selectFromAutoComplete(menuLabel.getText());
        });
        return item;
    }

    /**
     * Sets the text field to the medication selected
     *
     * @param selectedItem - string of the selected medication
     */
    private void selectFromAutoComplete(String selectedItem) {
        newMedication.setText(selectedItem);
        newMedication.requestFocus();
        newMedication.end(); //place cursor at end of text
        itemSelected = true; //indicate that an API does not need to be made
    }

    /**
     * Retrieves the medications stored in the currentMedications ArrayList.
     * Displays the retrieved medications to the currentMedications listView.
     */
    private void viewCurrentMedications() {
        clearSelections();
        current = new ArrayList<>();
        target.getCurrentMedications().forEach((med) -> current.add(String.valueOf(med)));
        currentListProperty.set(FXCollections.observableArrayList(current));
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
        historyListProperty.set(FXCollections.observableArrayList(history));
        pastMedications.itemsProperty().bind(historyListProperty);
    }

    /**
     * Adds a new medication to the currentMedications ArrayList
     * Resets the currentMedications ListView to display the new medication
     *
     * @param medication The selected medication being added to the current ArrayList and listView
     */
    private void addMedication(String medication) {
        if (!medication.equals("Enter medicine") && !medication.equals("") && !medication.substring(0, 1).equals(" ")) {
            medication = medication.substring(0, 1).toUpperCase() + medication.substring(1).toLowerCase();

            if (!(current.contains(medication) || history.contains(medication))) {
                target.getCurrentMedications().add(new Medication(medication));
                time = new Timestamp(System.currentTimeMillis());
//                medLog.add(new UserActionRecord(String.valueOf(time), Level.FINE.toString(), medication + " is now registered as current", medication + " has successfully been registered"));
//                logHistory.add(medLog.get(medLog.size() - 1));
                viewCurrentMedications();
                newMedication.clear();
            } else if (history.contains(medication) && !current.contains(medication)) {
                moveToCurrent(new ArrayList<>(Collections.singleton(medication)));
                newMedication.clear();
            } else {
                time = new Timestamp(System.currentTimeMillis());
//                medLog.add(new UserActionRecord(String.valueOf(time), Level.WARNING.toString(), medication + " is already registered", medication + " has failed registration"));
//                logHistory.add(medLog.get(medLog.size() - 1));
                Alert err = new Alert(Alert.AlertType.ERROR, "'" + medication + "' is already registered");
                err.show();
            }
        } else {
            time = new Timestamp(System.currentTimeMillis());
//            medLog.add(new UserActionRecord(String.valueOf(time), Level.WARNING.toString(), medication + " is invalid for registration", medication + " has failed registration"));
//            logHistory.add(medLog.get(medLog.size() - 1));
            Alert err = new Alert(Alert.AlertType.ERROR, "'" + medication + "' is invalid for registration");
            err.show();
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
        if (history.contains(medication)) {
            target.getMedicationHistory().remove(history.indexOf(medication));
            time = new Timestamp(System.currentTimeMillis());
//            medLog.add(new UserActionRecord(String.valueOf(time), Level.FINE.toString(), medication + " is now removed", medication + " is deleted from history list"));
//            logHistory.add(medLog.get(medLog.size() - 1));
            viewPastMedications();
        } else if (current.contains(medication)) {
            target.getCurrentMedications().remove(current.indexOf(medication));
            time = new Timestamp(System.currentTimeMillis());
//            medLog.add(new UserActionRecord(String.valueOf(time), Level.FINE.toString(), medication + " is now removed", medication + " is deleted from current list"));
//            logHistory.add(medLog.get(medLog.size() - 1));
            viewCurrentMedications();
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
            if (history.contains(medication)) {
                target.getMedicationHistory().remove(history.indexOf(medication));

                if (!current.contains(medication)) {
                    target.getCurrentMedications().add(new Medication(medication));
                    viewCurrentMedications();
                }
                time = new Timestamp(System.currentTimeMillis());
//                medLog.add(new UserActionRecord(String.valueOf(time), Level.FINE.toString(), medication + " is now current", medication + " moved from history to current list"));
//                logHistory.add(medLog.get(medLog.size() - 1));
                viewPastMedications();
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
            if (current.contains(medication)) {
                target.getCurrentMedications().remove(current.indexOf(medication));

                if (!history.contains(medication)) {
                    target.getMedicationHistory().add(new Medication(medication));
                    viewPastMedications();
                }
                time = new Timestamp(System.currentTimeMillis());
//                medLog.add(new UserActionRecord(String.valueOf(time), Level.FINE.toString(), medication + " is now history", medication + " moved from current to history list"));
//                logHistory.add(medLog.get(medLog.size() - 1));
                viewCurrentMedications();
            }
        }
    }

    /**
     * Runs when an item is selected within a listView
     * If there is only one item, the function to load the ingredients for the selected medication is called
     *
     * @param listView The listView of the selected item
     */
    private void onSelect(ListView listView) {
        if (listView.getSelectionModel().getSelectedItems().size() >= 1) {
            for (Object item : listView.getSelectionModel().getSelectedItems()) {
                if (item != null) {
                    loadMedicationIngredients(item.toString());
                }
            }
        }
    }

    /**
     * Shifts an already existing ingredient/interaction listing entry to the top of the list
     *
     * @param index The current index in the list of the already existing entry
     */
    private void moveToTopInformationList(int index) {
        String entry;

        for (int i = index; index < ingredients.size(); i++) {
            entry = ingredients.get(i);
            ingredients.remove(i);
            ingredients.add(i - index, entry);

            if (entry.equals("")) {
                break;
            }
        }
    }

    /**
     * Fetches the ingredients from the APIHelper, then converts the results into a temporary list.
     * The list, after having a header included, is then added to existing listing of other medicine
     * ingredients and medicine interactions and passed to be bound to the listView
     *
     * @param medication The medication to fetch the ingredients for
     */
    private void loadMedicationIngredients(String medication) {
        APIHelper helper = new APIHelper();
        ArrayList<String> newIngredients = new ArrayList<>();
        Boolean hasIngredients = false;

        if (!ingredients.contains("Ingredients for '" + medication + "': ")) {
            newIngredients.add("Ingredients for '" + medication + "': ");
            try {
                if (medication.length() == 1) {
                    getDrugSuggestions(medication);
                } else {
                    getDrugSuggestions(Collections.max(new ArrayList<>(Arrays.asList(medication.split(" ")))));
                }

                if (suggestions.get("suggestions").toString().contains(medication)) {
                    JsonArray response = helper.getMapiDrugIngredients(medication);
                    response.forEach((element) -> newIngredients.add(element.getAsString()));
                    hasIngredients = true;
                }
            } catch (IOException e) {
                hasIngredients = false;
            }

            if (!hasIngredients) {
                newIngredients.add("There are no recorded ingredients for '" + medication + "'");
            }
            newIngredients.add("");
            ingredients.addAll(0, newIngredients);
        } else {
            int index = ingredients.indexOf("Ingredients for '" + medication + "': ");
            moveToTopInformationList(index);
        }
        displayIngredients(ingredients);
    }

    /**
     * When activated displays the interactions between the two most recently selected medications
     */
    private void displayInteractions(HashMap<String, Set<String>> interactions, String drugOne, String drugTwo) {
        Set<String> keys = interactions.keySet();
        ingredients.clear();
        ingredients.add("Interactions for " + drugOne + " & " + drugTwo);
        ingredients.add("");
        for (String key : keys) {
            interactions.get(key).forEach((interaction) ->
                    ingredients.add(interaction.replace("\"", "") + "  (" + key + ")"));
        }
        displayIngredients(ingredients);
    }

    /**
     * When activated displays the interactions between the two most recently selected medications
     */
    @FXML
    public void reviewInteractions() {
        Alert alert = new Alert(Alert.AlertType.WARNING, "Drug interactions not available. Please select 2 medications.");
        ArrayList<String> selectedMedications = new ArrayList<String>() {{
            addAll(currentMedications.getSelectionModel().getSelectedItems());
            addAll(pastMedications.getSelectionModel().getSelectedItems());
        }};
        if (selectedMedications.size() == 2) { //if two are selected
            try {
                DrugInteraction interaction = new DrugInteraction(selectedMedications.get(0), selectedMedications.get(1), viewedPatient);
                displayInteractions(interaction.getInteractionsWithDurations(), selectedMedications.get(0), selectedMedications.get(1));
            } catch (IOException e) {
                alert.setContentText("Drug interactions not available, either this study has not been completed or" +
                        " drugs provided don't exist.");
                alert.show();
            }
        } else {
            alert.show();
        }
    }

    /**
     * Takes a string list of ingredients, and binds it to the medicineInformation listView to be displayed
     *
     * @param ingredients The List of ingredients
     */
    private void displayIngredients(List<String> ingredients) {
        informationListProperty.set(FXCollections.observableList(ingredients));
        medicineInformation.itemsProperty().bind(informationListProperty);
    }

    /**
     * Button for clearing each currently selected medication from being selected on activation
     * Navigates from the Medication panel to the home panel after 'back' is selected, saves medication log
     */
    @FXML
    public void goToProfile() {
        if (userControl.getLoggedInUser() instanceof Patient ) {
            ScreenControl.removeScreen("patientProfile");
            try {
                ScreenControl.addScreen("patientProfile", FXMLLoader.load(getClass().getResource("/scene/patientProfile.fxml")));
                ScreenControl.activate("patientProfile");
            } catch (IOException e) {
                userActions.log(Level.SEVERE, "Error loading profile screen", "attempted to navigate from the medication page to the profile page");
                new Alert(Alert.AlertType.WARNING, "ERROR loading profile page", ButtonType.OK).showAndWait();
            }
        } else {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scene/patientProfile.fxml"));
            try {
                ScreenControl.loadPopUpPane(medicationPane.getScene(), fxmlLoader);
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
        medicineInformation.getSelectionModel().clearSelection();
    }

    /**
     * Button for clearing the information being currently displayed on the medicine information ListView on activation
     */
    @FXML
    private void refreshReview() {
        ingredients = new ArrayList<>();
        displayIngredients(ingredients);
    }
}
