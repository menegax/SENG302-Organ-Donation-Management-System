package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import model.Disease;
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
public class GUIClinicianDiagnosis {

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

    Database database = Database.getDatabase();


    /**
     * Patient being viewed
     */
    private static Patient target;

    /**
     * Deleted past diseases
     */
    private ArrayList<Disease> deletedPast = new ArrayList<>();

    /**
     * Deleted current diseases
     */
    private ArrayList<Disease> deletedCurrent = new ArrayList<>();

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
            addDiagnosisButton.setVisible(false);
            addDiagnosisButton.setDisable(true);
            saveButton.setVisible(false);
            saveButton.setDisable(true);
            deleteButton.setVisible(false);
            deleteButton.setDisable(true);
        } else {
            target = userControl.getTargetPatient();
            addDiagnosisButton.setVisible(true);
            addDiagnosisButton.setDisable(false);
            saveButton.setVisible(true);
            saveButton.setDisable(false);
            deleteButton.setVisible(true);
            deleteButton.setDisable(false);
        }
        if(target.getCurrentDiseases() == null) {
            target.setCurrentDiseases(new ArrayList<>());
        }
        if(target.getPastDiseases() == null) {
            target.setPastDiseases(new ArrayList<>());
        }
        currentDiseases = target.getCurrentDiseases();
        pastDiseases = target.getPastDiseases();
        updateDiagnosesLists();
        if(!(userControl.getLoggedInUser() instanceof Patient)) {
            setUpDoubleClickEdit(currentDiagnosesView);
            setUpDoubleClickEdit(pastDiagnosesView);
        }
    }

    /**
     * Adds a newly entered diagnosis as a String to the current disease class and updates the table
     */
    @FXML
    public void registerDiagnosis() {
        addDiagnosis();
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
                    GUIPatientUpdateDiagnosis.setIsAdd(false);
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scene/patientUpdateDiagnosis.fxml"));
                    try {
                        ScreenControl.loadPopUpPane(clinicianDiagnosesPane.getScene(), fxmlLoader);
                    } catch (IOException e) {
                        userActions.log(Level.SEVERE, "Error loading update diagnoses screen in popup", "attempted to navigate from the diagnoses page to the update diagnosis page in popup");
                        new Alert(Alert.AlertType.WARNING, "Error loading update diagnoses page", ButtonType.OK).show();
                    }
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
     * Registers a new diagnosis entry for a patient when 'Add diagnosis' is activated
     */
    private void addDiagnosis() {
        try {
            GUIPatientUpdateDiagnosis.setIsAdd(true);

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scene/patientUpdateDiagnosis.fxml"));
            try {
                ScreenControl.loadPopUpPane(clinicianDiagnosesPane.getScene(), fxmlLoader);
            } catch (IOException e) {
                userActions.log(Level.SEVERE, "Error loading update diagnoses screen in popup", "attempted to navigate from the diagnoses page to the update diagnosis page in popup");
                new Alert(Alert.AlertType.WARNING, "Error loading update diagnoses page", ButtonType.OK).show();
            }
        }
        catch (Exception e) {
            userActions.log(Level.SEVERE,
                    "Failed to open diagnosis update window from the diagnoses page",
                    "attempted to open diagnosis update window from the diagnoses page");
            new Alert(Alert.AlertType.ERROR, "Unable to open diagnosis update window", ButtonType.OK).show();
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
        setUpChronicSortBehaviour();

        //Set default date sorting
        currentDateCol.setSortType(TableColumn.SortType.DESCENDING);
        currentDiagnosesView.getSortOrder().add(currentDateCol);

        currentTagsCol.setSortable(false);
    }

    /**
     * Loads the past diseases table
     */
    private void loadPastDiseases() {
        ObservableList <Disease> observablePastDiseases = FXCollections.observableArrayList( pastDiseases );
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
     * Returns to the patient profile page
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
                    ScreenControl.loadPopUpPane(clinicianDiagnosesPane.getScene(), fxmlLoader);
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
        database.saveToDisk();
        userActions.log( Level.FINE, "Successfully saved patient diseases", "Successfully saved patient " + target.getNhiNumber() + "diseases");
        new Alert(Alert.AlertType.CONFIRMATION, "Diagnosis saved successfully", ButtonType.OK).show();
        changed = false;
        goToProfile();
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
     * Deletes the selected diagnosis
     */
    @FXML
    public void deleteDiagnoses() {
        if (pastDiagnosesView.getSelectionModel().getSelectedItem() != null) {
            changed = true;
            pastDiseases.remove(pastDiagnosesView.getSelectionModel().getSelectedItem());
            deletedPast.add(pastDiagnosesView.getSelectionModel().getSelectedItem());
            loadPastDiseases();
            userActions.log(Level.FINE, "Successfully deleted a disease",  pastDiagnosesView.getSelectionModel().getSelectedItem() + " is successfully deleted");
            new Alert(Alert.AlertType.CONFIRMATION, "Diagnosis deleted successfully", ButtonType.OK).show();
        } else if (currentDiagnosesView.getSelectionModel().getSelectedItem() != null) {
            changed = true;
            currentDiseases.remove(currentDiagnosesView.getSelectionModel().getSelectedItem());
            deletedCurrent.add(currentDiagnosesView.getSelectionModel().getSelectedItem());
            loadCurrentDiseases();
            userActions.log(Level.WARNING, "Successfully deleted a disease", currentDiagnosesView.getSelectionModel().getSelectedItem() + " is successfully deleted");
            new Alert(Alert.AlertType.CONFIRMATION, "Diagnosis deleted successfully", ButtonType.OK).show();
        } else {
            userActions.log(Level.WARNING, "Failed to delete a disease", "disease failed to be deleted");
            new Alert(Alert.AlertType.WARNING, "No Diagnosis selected", ButtonType.OK).show();
        }
        updateDiagnosesLists();
    }
}
