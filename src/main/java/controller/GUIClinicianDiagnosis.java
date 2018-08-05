package controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import model.Disease;
import model.Patient;
import utility.GlobalEnums;
import utility.undoRedo.Action;
import utility.undoRedo.StatesHistoryScreen;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

/**
 * Controller class for clinician viewing and editing of a patient's diagnoses.
 */
public class GUIClinicianDiagnosis extends UndoableController{

    @FXML
    public GridPane clinicianDiagnosesPane;

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


    /**
     * Patient being viewed
     */
    private static Patient target;

    private static Patient targetClone;

    /**
     * Patient's current diseases
     */
    private ArrayList<Disease> currentDiseases;

    /**
     * Patient's past diseases
     */
    private ArrayList<Disease> pastDiseases;


    /**
     * Boolean to show if changes have been made
     */
    private static boolean changed = false; // Boolean for if there are any un-saved edits when leaving pane

    private UserControl userControl;


    private ScreenControl screenControl = ScreenControl.getScreenControl();


    /**
     * Sets if the patient's diagnoses have been altered at all.
     * @param bool altered
     */
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
        userControl = new UserControl();
        if(userControl.getLoggedInUser() instanceof Patient) {
            target = (Patient) userControl.getLoggedInUser();
            targetClone = (Patient) target.deepClone();
            addDiagnosisButton.setVisible(false);
            addDiagnosisButton.setDisable(true);
            deleteButton.setVisible(false);
            deleteButton.setDisable(true);
        } else {
            target = (Patient) userControl.getTargetUser();
            targetClone = (Patient) target.deepClone();
            addDiagnosisButton.setVisible(true);
            addDiagnosisButton.setDisable(false);
            deleteButton.setVisible(true);
            deleteButton.setDisable(false);
        }
        if(targetClone.getCurrentDiseases() == null) {
            targetClone.setCurrentDiseases(new ArrayList<>());
        }
        if(targetClone.getPastDiseases() == null) {
            targetClone.setPastDiseases(new ArrayList<>());
        }
        currentDiseases = targetClone.getCurrentDiseases();
        pastDiseases = targetClone.getPastDiseases();
        updateDiagnosesLists();
        if(!(userControl.getLoggedInUser() instanceof Patient)) {
            setUpDoubleClickEdit(currentDiagnosesView);
            setUpDoubleClickEdit(pastDiagnosesView);
        }
        controls = new ArrayList<Control>(){{
            add(currentDiagnosesView);
            add(pastDiagnosesView);
        }};
        statesHistoryScreen = new StatesHistoryScreen(controls, GlobalEnums.UndoableScreen.CLINICIANDIAGNOSIS);
    }

    /**
     * Adds a newly entered diagnosis as a String to the current disease class and updates the table
     */
    @FXML
    public void registerDiagnosis() {
        addDiagnosis();
    }

    /**
     * Sets up double click action of opening full disease edit window.
     * Opens an update window to edit the selected disease in the table, and marks the window as being for
     * an update.
     */
    private void setUpDoubleClickEdit(TableView<Disease> tableView) {
        tableView.setOnMouseClicked(click -> {
            if (click.getClickCount() == 2 && tableView.getSelectionModel().getSelectedItem() != null) {
                GUIPatientUpdateDiagnosis.setDisease(tableView.getSelectionModel().getSelectedItem());
                GUIPatientUpdateDiagnosis.setIsAdd(false);
                //stage.setOnHiding(event -> Platform.runLater(this::tableRefresh)); todo implement
                screenControl.show("/scene/patientUpdateDiagnosis.fxml");
            }

        });
    }


    /**
     * Opens the Patient update screen for the purpose of adding a diagnosis. Sets the update window to handle
     * an addition of a disease rather than an update
     */
    private void addDiagnosis() {
        GUIPatientUpdateDiagnosis.setIsAdd(true);
        screenControl.show("/scene/patientUpdateDiagnosis.fxml");
        //stage.setOnHiding(event -> Platform.runLater(this::tableRefresh)); todo implement
    }

    /**
     * Refreshes the tables of past and current diseases
     */
    private void tableRefresh() {
        targetClone = (Patient) target.deepClone();
        loadCurrentDiseases();
        loadPastDiseases();
    }

    /**
     * Loads the current diseases table, fills the table with disease values and sets up the sorting behaviour
     * or the table to be in descending date order
     */
    private void loadCurrentDiseases() {
        ObservableList<Disease> observableCurrentDiseases = FXCollections.observableArrayList(targetClone.getCurrentDiseases());
        setCellValues(currentDateCol, currentDiagnosisCol, currentTagsCol);
        currentDiagnosesView.setItems(observableCurrentDiseases);
        currentDiagnosesView.refresh();
        setUpChronicSortBehaviour();

        //Set default date sorting
        currentDateCol.setSortType(TableColumn.SortType.DESCENDING);
        currentDiagnosesView.getSortOrder().add(currentDateCol);

        currentTagsCol.setSortable(false);
    }

    /**
     * Loads the past diseases table, fills the table with disease values and sets up the sorting behaviour
     * or the table to be in descending date order
     */
    private void loadPastDiseases() {
        ObservableList <Disease> observablePastDiseases = FXCollections.observableArrayList(targetClone.getPastDiseases() );
        setCellValues(pastDateCol, pastDiagnosisCol, pastTagsCol);
        pastDiagnosesView.setItems(observablePastDiseases);
        pastDiagnosesView.refresh();

        //Set default date sorting
        pastDateCol.setSortType(TableColumn.SortType.DESCENDING);
        pastDiagnosesView.getSortOrder().add(pastDateCol);

        pastTagsCol.setSortable(false);
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
     * Sets up sorting so that the diseases with the chronic tag remain at the top of the current table no matter what the table
     * is currently being sorting on. Diseases that both have the chronic tag will still be sorted accordingly
     */
    private void setUpChronicSortBehaviour() {
        currentDiagnosesView.sortPolicyProperty().set(param -> {
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
            FXCollections.sort(currentDiagnosesView.getItems(), comparator);
            return true;
        });
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
            if(disease.getDiseaseState() != GlobalEnums.DiseaseState.CURED) {
                pastDiseases.remove(disease);
                currentDiseases.add(disease);
            }
        }
        highlightChronic();
        loadCurrentDiseases();
        loadPastDiseases();
    }

    /**
     * Highlights a viewed diagnosis in red if the diagnosis is chronic. No background colour set otherwise.
     */
    private void highlightChronic() {
        currentDiagnosesView.setRowFactory(row -> new TableRow<Disease>() {

            @Override
            public void updateItem(Disease item, boolean empty) {
                if(item != null) {
                    super.updateItem(item, empty);
                    if (item.getDiseaseState() != null && item.getDiseaseState() ==
                            GlobalEnums.DiseaseState.CHRONIC) {
                        setStyle("-fx-background-color: #e6b3b3");
                    } else {
                        setStyle("");
                    }
                }
            }

        });
    }

    /**
     * Deletes the selected diagnosis from the selected diagnoses list and updates the table.
     */
    @FXML
    public void deleteDiagnoses() {
        if (pastDiagnosesView.getSelectionModel().getSelectedItem() != null) {
            changed = true;
            pastDiseases.remove(pastDiagnosesView.getSelectionModel().getSelectedItem());
            statesHistoryScreen.addAction(new Action(target, targetClone));
            loadPastDiseases();
            userActions.log(Level.FINE, "Successfully deleted a disease",  pastDiagnosesView.getSelectionModel().getSelectedItem() + " is successfully deleted");
        } else if (currentDiagnosesView.getSelectionModel().getSelectedItem() != null) {
            changed = true;
            currentDiseases.remove(currentDiagnosesView.getSelectionModel().getSelectedItem());
            statesHistoryScreen.addAction(new Action(target, targetClone));
            loadCurrentDiseases();
            userActions.log(Level.WARNING, "Successfully deleted a disease", currentDiagnosesView.getSelectionModel().getSelectedItem() + " is successfully deleted");
        } else {
            userActions.log(Level.WARNING, "No diagnosis selected to delete", "disease failed to be deleted");
        }
        updateDiagnosesLists();
    }
}
