package controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.Disease;
import model.DrugInteraction;
import model.Patient;
import service.Database;
import utility.GlobalEnums;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

/**
 * Controller class for clinician viewing and editing of a patient's diagnoses.
 */
public class GUIClinicianDiagnosis implements IPopupable {

    @FXML
    public AnchorPane clinicianDiagnosesPane;

    @FXML
    public TableView<Disease> pastDiagnosesView;

    @FXML
    public TableColumn<Disease, LocalDate> pastDateCol;

    @FXML
    public TableColumn<Disease, String> pastDiagnosisCol;

    @FXML
    public TableColumn<Disease, GlobalEnums.DiseaseState> pastTagsCol;

    @FXML
    public TableView<Disease> currentDiagnosesView;

    @FXML
    public TableColumn<Disease, LocalDate> currentDateCol;

    @FXML
    public TableColumn<Disease, String> currentDiagnosisCol;

    @FXML
    public TableColumn<Disease, GlobalEnums.DiseaseState> currentTagsCol;

    @FXML
    public Button saveButton;

    @FXML
    public Button deleteButton;

    @FXML
    public Button addDiagnosisButton;

    @FXML
    private TextField newDiagnosis;

    /**
     * Patient being viewed
     */
    private static Patient target;

    /**
     * Deleted past diseases
     */
    private ArrayList<Disease> deletedPast = new ArrayList<Disease>();

    /**
     * Deleted current diseases
     */
    private ArrayList<Disease> deletedCurrent = new ArrayList<Disease>();

    /**
     * Patient's current diseases
     */
    private ArrayList<Disease> currentDiseases;

    /**
     * Patient's past diseases
     */
    private ArrayList<Disease> pastDiseases;

    /**
     * Selected disease
     */
    private Disease chosen;

    /**
     * Boolean to show if changes have been made
     */
    private static boolean changed = false; // Boolean for if there are any un-saved edits when leaving pane

    /**
     * Sets the viewed patient being viewed and edited
     * @param patient viewed patient
     */
    public void setViewedPatient(Patient patient) {
        target = patient;
    }

    /**
     * Statically sets the patient being viewed and sets the target patient to the corresponding target by the
     * same NHI number in the database
     * @param patient viewed patient
     */
    public static void staticSetPatient(Patient patient) {
        target = patient;
        try {
            Patient p = Database.getPatientByNhi(target.getNhiNumber());
            target = p;
        } catch(InvalidObjectException e) {
            userActions.log(Level.SEVERE, "Error loading user",
                    "attempted to manage the diagnoses for logged in user");
        }
    }

    public static void setChanged(boolean bool) { changed = bool;}

    /**
     * Initializes the clinician diagnoses view window.
     * Sets the target patient's current and past diseases to new ArrayLists of Disease objects if they have not been
     * set before.
     * Current diseases and past diseases are set as the target's current and past diseases, and these lists are
     * loaded into the ListViews.
     * Double click functions to update a diagnosis are added for both current and past diseases
     */
    @FXML
    public void initialize() {
        if(target.getCurrentDiseases() == null) {
            target.setCurrentDiseases(new ArrayList<>());
        }
        if(target.getPastDiseases() == null) {
            target.setPastDiseases(new ArrayList<>());
        }
        currentDiseases = target.getCurrentDiseases();
        pastDiseases = target.getPastDiseases();
        loadCurrentDiseases();
        loadPastDiseases();
        setUpRightClickTags();
        setUpDoubleClickEdit(pastDiagnosesView);
        setUpDoubleClickEdit(currentDiagnosesView);
    }

    /**
     * Adds a newly entered diagnosis as a String to the current disease class and updates the table
     */
    @FXML
    public void registerDiagnosis() {
        addDiagnosis(newDiagnosis.getText());
    }

    /**
     * Sets up double click action of opening full disease edit window
     */
    private void setUpDoubleClickEdit(TableView<Disease> tableView) {
        tableView.setOnMouseClicked(click -> {
            if (click.getClickCount() == 2 && tableView.getSelectionModel()
                    .getSelectedItem() != null) {
                try {
                    GUIPatientUpdateDiagnosis.setDisease(tableView.getSelectionModel().getSelectedItem());
                    GUIPatientUpdateDiagnosis.setPatient(target);

                    Stage popUpStage = new Stage();

                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scene/patientUpdateDiagnosis.fxml"));
                    Scene scene = new Scene(fxmlLoader.load());
                    popUpStage.setHeight(285);
                    popUpStage.setWidth(420);
                    popUpStage.setScene(scene);

                    // When pop up is closed, refresh the table
                    popUpStage.setOnHiding(event -> Platform.runLater(this::tableRefresh));

                    popUpStage.showAndWait();

                    updateDiagnosesLists();
                }
                catch (Exception e) {
                    userActions.log(Level.SEVERE,
                            "Failed to open diagnosis update window from the diagnoses page",
                            "attempted to open diagnosis update window from the diagnoses page");
                    new Alert(Alert.AlertType.ERROR, "Unable to open diagnosis update window", ButtonType.OK).show();
                }
            }

        });
    }

    /**
     * Sets up right click tags to change disease tag, and mark a disease as chronic, cured or remove tags
     */
    private void setUpRightClickTags() {
        pastDiagnosesView.setOnMouseClicked(click -> {
            if (click.getButton() == MouseButton.SECONDARY && pastDiagnosesView.getSelectionModel().getSelectedItem() != null) {
                Disease selected = pastDiseases.get( pastDiseases.indexOf( pastDiagnosesView.getSelectionModel().getSelectedItem() ) );
                ContextMenu rightClickPast = new ContextMenu();
                MenuItem makeChronicAction = new MenuItem( "Tag chronic" );
                MenuItem makeCuredAction = new MenuItem( "Tag cured" );
                MenuItem makeNullAction = new MenuItem( "Remove tags" );
                makeChronicAction.setOnAction( event -> {
                    updatePastStatus( selected, "chronic" );
                } );
                makeCuredAction.setOnAction( event -> {
                    updatePastStatus( selected, "cured" );
                } );
                makeNullAction.setOnAction( event -> {
                    updatePastStatus( selected, "" );
                } );
                rightClickPast.getItems().addAll( makeChronicAction, makeCuredAction, makeNullAction );
                rightClickPast.show( pastDiagnosesView.getSelectionModel().getTableView(), click.getScreenX(), click.getScreenY() );
            } else if (click.getButton() == MouseButton.PRIMARY && pastDiagnosesView.getSelectionModel().getSelectedItem() != null) {
                chosen = pastDiagnosesView.getSelectionModel().getSelectedItem();
            }
        });

        currentDiagnosesView.setOnMouseClicked(click -> {
            if (click.getButton() == MouseButton.SECONDARY && currentDiagnosesView.getSelectionModel().getSelectedItem() != null) {
                Disease selected = currentDiseases.get( currentDiseases.indexOf( currentDiagnosesView.getSelectionModel().getSelectedItem() ) );
                ContextMenu rightClickCurrent = new ContextMenu();
                MenuItem makeChronicAction = new MenuItem( "Mark as chronic" );
                MenuItem makeCuredAction = new MenuItem( "Mark as cured" );
                MenuItem makeNullAction = new MenuItem( "Mark as null" );
                makeChronicAction.setOnAction( event -> {
                    updateCurrentStatus( selected, "chronic" );
                } );
                makeCuredAction.setOnAction( event -> {
                    updateCurrentStatus( selected, "cured" );
                } );
                makeNullAction.setOnAction( event -> {
                    updateCurrentStatus( selected, "" );
                } );
                rightClickCurrent.getItems().addAll( makeChronicAction, makeCuredAction, makeNullAction );
                rightClickCurrent.show( currentDiagnosesView.getSelectionModel().getTableView(), click.getScreenX(), click.getScreenY() );
            } else if (click.getButton() == MouseButton.PRIMARY && currentDiagnosesView.getSelectionModel().getSelectedItem() != null) {
                chosen = currentDiagnosesView.getSelectionModel().getSelectedItem();
            }
        });
    }

    /**
     * Registers a new diagnosis entry for a patient when 'Add diagnosis' is activated
     *
     * @param diagnosis The entered diagnosis to textField for registration
     */
    private void addDiagnosis(String diagnosis) {
        if (!diagnosis.equals("Enter a diagnosis") && !diagnosis.equals("") && !diagnosis.substring(0, 1).equals(" ")
                && diagnosis.matches("[A-Z|a-z0-9.]{3,75}")) {
            diagnosis = diagnosis.substring(0, 1).toUpperCase() + diagnosis.substring(1).toLowerCase();
            Boolean unique = true;

            for (Disease current : currentDiseases) {
                if (current.getDiseaseName().equals( diagnosis )) {
                    userActions.log( Level.WARNING, "Failed to register a disease", diagnosis + " is already registered" );
                    Alert err = new Alert( Alert.AlertType.ERROR, "'" + diagnosis + "' is already registered" );
                    err.show();
                    unique = false;
                }
            }

            for (Disease past : pastDiseases) {
                if (past.getDiseaseName().equals( diagnosis )) {
                    if (past.getDiseaseState() == null) {
                        moveFromPastToCurrent( past, null );
                        unique = false;
                        changed = true;
                        break;
                    } else {
                        userActions.log( Level.WARNING, "Failed to register a disease", diagnosis + " is already registered" );
                        Alert err = new Alert( Alert.AlertType.ERROR, "'" + diagnosis + "' is already registered" );
                        err.show();
                        unique = false;
                    }
                }
            }

            if (unique) {
                changed = true;
                currentDiseases.add( new Disease( diagnosis, null ) );
                userActions.log( Level.FINE, "Successfully registered a disease", "Registered a new disease for a patient" );
                loadCurrentDiseases();
                newDiagnosis.clear();
            }
        } else {
            userActions.log(Level.WARNING, "Failed to register a disease", diagnosis + " is invalid for registration");
            Alert err = new Alert(Alert.AlertType.ERROR, "'" + diagnosis + "' is invalid for registration");
            err.show();
        }
    }

    /**
     * Loads the current diseases table
     */
    private void loadCurrentDiseases() {
        ObservableList<Disease> observableCurrentDiseases = FXCollections.observableArrayList(currentDiseases);
        setCellValues(currentDateCol, currentDiagnosisCol, currentTagsCol);
        currentDiagnosesView.setItems(observableCurrentDiseases);
        currentDiagnosesView.refresh();
        setUpSortBehaviour(currentDiagnosesView, currentTagsCol);
    }

    /**
     * Loads the past diseases table
     */
    private void loadPastDiseases() {
        ObservableList <Disease> observablePastDiseases = FXCollections.observableArrayList( pastDiseases );
        setCellValues(pastDateCol, pastDiagnosisCol, pastTagsCol);
        pastDiagnosesView.setItems(observablePastDiseases);
        pastDiagnosesView.refresh();
        setUpSortBehaviour(pastDiagnosesView, pastTagsCol);
    }

    /**
     * Sets the values to fetch for each column based on it's type
     *
     * @param dateCol      The date of diagnosis column
     * @param diagnosisCol The disease name/diagnosis column
     * @param tagCol       The diseases tag column
     */
    private void setCellValues(TableColumn<Disease, LocalDate> dateCol, TableColumn<Disease, String> diagnosisCol, TableColumn<Disease, GlobalEnums.DiseaseState> tagCol) {
        dateCol.setCellValueFactory(new PropertyValueFactory<>("dateDiagnosed"));
        diagnosisCol.setCellValueFactory(new PropertyValueFactory<>("diseaseName"));
        tagCol.setCellValueFactory(new PropertyValueFactory<>("diseaseState"));
    }

    /**
     * Sets up sorting so that the diseases with the chronic tag remain at the top of the table no matter what the table
     * is currently being sorting on. Diseases that both have the chronic tag will still be sorted accordingly
     *
     * @param table  The table to apply the sort behaviour to
     * @param tagCol The column to disable sorting on - This will be the column that displays disease tags
     */
    private void setUpSortBehaviour(TableView<Disease> table, TableColumn<Disease, GlobalEnums.DiseaseState> tagCol) {
        tagCol.setSortable(false); //Disables sorting on the tag column
        table.sortPolicyProperty().set(param -> {
            Comparator<Disease> comparator = (o1, o2) -> {
                if (o1.getDiseaseState() == GlobalEnums.DiseaseState.CHRONIC) {
                    if (o2.getDiseaseState() == GlobalEnums.DiseaseState.CHRONIC) {
                        if (param.getComparator() == null) return 0;
                        return param.getComparator().compare(o1, o2);
                    }
                    return -1;
                } else if (o2.getDiseaseState() == GlobalEnums.DiseaseState.CHRONIC) return 1;
                if (param.getComparator() == null) {
                    return 0;
                } else {
                    return param.getComparator().compare(o1, o2);
                }
            };
            FXCollections.sort(table.getItems(), comparator);
            return true;
        });
    }

    /**
     * Returns to the
     */
    @FXML
    public void goToProfile() {
        boolean back = false;
        if (changed) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "You have made some changes, are you sure you want to continue?", ButtonType.YES, ButtonType.CANCEL);
            Optional<ButtonType> confirmation = alert.showAndWait();
            if (confirmation.get() == ButtonType.YES) {
                back = true;
                currentDiseases.addAll(deletedCurrent);
                pastDiseases.addAll(deletedPast);
            }
        } else {
            back = true;
        }
        if (back) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scene/patientProfile.fxml"));
                try {
                    ScreenControl.loadPopUpPane(clinicianDiagnosesPane.getScene(), fxmlLoader, target);
                } catch (IOException e) {
                    userActions.log(Level.SEVERE, "Error returning to profile screen in popup", "attempted to navigate from the diagnoses page to the profile page in popup");
                    new Alert(Alert.AlertType.WARNING, "Error loading profile page", ButtonType.OK).show();
                }
        }
    }

    /**
     * Saves the current diagnoses to the database
     */
    @FXML
    public void saveDiagnoses() {
        target.setCurrentDiseases(currentDiseases);
        target.setPastDiseases(pastDiseases);
        Database.saveToDisk();
        userActions.log( Level.FINE, "Successfully saved patient diseases", "Successfully saved patient " + target.getNhiNumber() + "diseases");
        new Alert(Alert.AlertType.CONFIRMATION, "Diagnosis saved successfully", ButtonType.OK).show();
        changed = false;
        goToProfile();
    }


    /**
     * Updates the disease state for a current diagnosis if the update is not 'cured' when the current state is still 'chronic'
     * If state is set to cured, the disease will be automatically moved to the past diseases list
     * @param disease The disease in the list of the diagnosis being updated
     * @param status The Disease status update
     */
    private void updateCurrentStatus(Disease disease, String status) {
        if (status.toLowerCase().equals( "cured" ) && disease.getDiseaseState() == null) {
            moveFromCurrentToPast( disease, status );
            changed = true;
        } else if (status.toLowerCase().equals( "cured" ) && disease.getDiseaseState().toString().toLowerCase().equals( "chronic" )) {
            new Alert(Alert.AlertType.WARNING, "Can not set disease state to 'cured' if the current state is 'chronic'", ButtonType.OK).show();
            userActions.log(Level.WARNING, "Failed to update a disease state", "Disease state could not be changed to 'cured' when it is 'chronic'");
        } else if (status.toLowerCase().equals( "chronic" )) {
            disease.setDiseaseState( GlobalEnums.DiseaseState.CHRONIC );
            loadCurrentDiseases();
            userActions.log(Level.FINE, "Updated a current disease state", "Updated a current disease state to chronic");
            changed = true;
        } else {
            disease.setDiseaseState( null );
            loadCurrentDiseases();
            userActions.log(Level.FINE, "Updated a current disease state", "Updated a current disease state to " + status);
            changed = true;
        }
    }

    /**
     * Updates the disease state for a past diagnosis
     * @param disease The disease in the list of the diagnosis being updated
     * @param status The Disease status update
     */
    private void updatePastStatus(Disease disease, String status) {
        switch (status.toLowerCase()) {
            case "chronic": moveFromPastToCurrent(disease, status); break;
            case "cured":
                disease.setDiseaseState( GlobalEnums.DiseaseState.CURED );
                loadPastDiseases();
                userActions.log(Level.FINE, "Updated a current disease state", "Updated a current disease state to cured");
                break;
            default:
                disease.setDiseaseState( null );
                loadPastDiseases();
                userActions.log(Level.FINE, "Updated a current disease state", "Updated a current disease state to " + status);
        }
        changed = true;
    }

    /**
     * Moves a diagnosis from the past diseases list to the current diseases list
     * @param disease The diseases being moved from past to current diseases list
     * @param state The state of the disease; chronic or null
     */
    private void moveFromPastToCurrent(Disease disease, String state) {
        if (state == null || state.toLowerCase().equals( "chronic" )) {
            if (state == null) {
                disease.setDiseaseState( null );
            } else {
                disease.setDiseaseState( GlobalEnums.DiseaseState.CHRONIC );
            }
            currentDiseases.add( disease );
            pastDiseases.remove( disease );
            userActions.log(Level.FINE, "Updated a current disease state", "Updated a current disease state to " + state);
            userActions.log( Level.FINE, "Moved a disease from past to current", "Moved disease " + disease.getDiseaseName() + " from past to current" );
            loadCurrentDiseases();
            loadPastDiseases();
            newDiagnosis.clear();
        }
    }

    /**
     * Moves a diagnosis from the current diseases list to the past diseases list
     * @param disease The diseases being moved from current to past diseases list
     * @param state The state of the disease; cured or null
     */
    private void moveFromCurrentToPast(Disease disease, String state) {
        if (state == null || state.toLowerCase().equals( "cured" )) {
            if (state == null) {
                disease.setDiseaseState( null );
            } else {
                disease.setDiseaseState( GlobalEnums.DiseaseState.CURED );
            }
            pastDiseases.add( disease );
            currentDiseases.remove( disease );
            userActions.log(Level.FINE, "Updated a current disease state", "Updated a current disease state to " + state);
            userActions.log( Level.FINE, "Moved a disease from current to past", "Moved disease " + disease.getDiseaseName() + " from current to past" );
            loadPastDiseases();
            loadCurrentDiseases();
        }
    }

    /**
     * Iterates through current and past diagnoses and moves cured and chronic diseases to their required lists.
     * Reloads the lists to reflect updates
     */
    private void updateDiagnosesLists() {
        for(Disease disease : new ArrayList<>(currentDiseases)) {
            if(disease.getDiseaseState() == GlobalEnums.DiseaseState.CURED) {
                currentDiseases.remove(disease);
                pastDiseases.add(disease);
            }
        }

        for(Disease disease : new ArrayList<>(pastDiseases)) {
            if(disease.getDiseaseState() == GlobalEnums.DiseaseState.CHRONIC) {
                pastDiseases.remove(disease);
                currentDiseases.add(disease);
            }
        }

        loadCurrentDiseases();
        loadPastDiseases();
    }

    /**
     * Deletes the selected diagnosis
     */
    @FXML
    public void deleteDiagnoses() {
        if (pastDiagnosesView.getSelectionModel().getSelectedItem() != null) {
            changed = true;
            pastDiseases.remove(chosen);
            deletedPast.add(chosen);
            loadPastDiseases();
            userActions.log(Level.FINE, "Successfully deleted a disease",  chosen + " is successfully deleted");
            new Alert(Alert.AlertType.CONFIRMATION, "Diagnosis deleted successfully", ButtonType.OK).show();
        } else if (currentDiagnosesView.getSelectionModel().getSelectedItem() != null) {
            changed = true;
            currentDiseases.remove(chosen);
            deletedCurrent.add(chosen);
            loadCurrentDiseases();
            userActions.log(Level.WARNING, "Failed to delete a disease", chosen + " failed to be deleted");
            new Alert(Alert.AlertType.CONFIRMATION, "Diagnosis deleted successfully", ButtonType.OK).show();
        } else {
            userActions.log(Level.WARNING, "Failed to delete a disease", chosen + " failed to be deleted");
            new Alert(Alert.AlertType.WARNING, "No Diagnosis selected", ButtonType.OK).show();
        }
    }

    /**
     * Refreshes the table data
     */
    private void tableRefresh() {
        currentDiagnosesView.refresh();
        pastDiagnosesView.refresh();
    }
}
