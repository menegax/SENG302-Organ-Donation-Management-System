package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import model.Disease;
import model.Donor;
import service.Database;
import utility.GlobalEnums;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

public class GUIClinicianDiagnosis {

    public AnchorPane clinicianDiagnosesPane;
    public TableView<Disease> pastDiagnosesView;
    public TableColumn<Disease, LocalDate> pastDateCol;
    public TableColumn<Disease, String> pastDiagnosisCol;
    public TableColumn<Disease, GlobalEnums.DiseaseState> pastTagsCol;
    public TableView<Disease> currentDiagnosesView;
    public TableColumn<Disease, LocalDate> currentDateCol;
    public TableColumn<Disease, String> currentDiagnosisCol;
    public TableColumn<Disease, GlobalEnums.DiseaseState> currentTagsCol;
    public Button saveButton;
    public Button deleteButton;
    public Button addDiagnosisButton;

    /*
     * Textfield for entering diagnosis for adding to the current diagnosis ArrayList and table
     */
    @FXML
    private TextField newDiagnosis;

    /**
     * Adds a newly entered diagnosis as a String to the current disease class and updates the table
     */
    @FXML
    public void registerDiagnosis() {
        addDiagnosis(newDiagnosis.getText());
    }

    private Donor currentDonor;
    private ArrayList<Disease> deletedPast = new ArrayList<Disease>();
    private ArrayList<Disease> deletedCurrent = new ArrayList<Disease>();
    private ArrayList<Disease> currentDiseases;
    private ArrayList<Disease> pastDiseases;
    private Disease chosen;
    private boolean changed = false;


    @FXML
    public void initialize() {
        currentDonor = ScreenControl.getLoggedInDonor();
        currentDiseases = currentDonor.getCurrentDiseases();
        pastDiseases = currentDonor.getPastDiseases();
        loadCurrentDiseases();
        loadPastDiseases();
        pastDiagnosesView.setOnMouseClicked(click -> {
            if (click.getButton() == MouseButton.SECONDARY && pastDiagnosesView.getSelectionModel().getSelectedItem() != null) {
                Disease selected = pastDiagnosesView.getSelectionModel().getSelectedItem();
                ContextMenu rightClickPast = new ContextMenu();
                MenuItem makeChronicAction = new MenuItem("Mark as Chronic");
                makeChronicAction.setOnAction(event -> {
                    selected.setDiseaseState(GlobalEnums.DiseaseState.CHRONIC);
                    pastDiseases.remove(selected);
                    currentDiseases.add(selected);
                    loadCurrentDiseases();
                    loadPastDiseases();
                });
                rightClickPast.getItems().addAll(makeChronicAction);
                rightClickPast.show(pastDiagnosesView.getSelectionModel().getTableView(), click.getScreenX(), click.getScreenY());
            } else if (click.getButton() == MouseButton.PRIMARY && pastDiagnosesView.getSelectionModel().getSelectedItem() != null) {
                chosen = pastDiagnosesView.getSelectionModel().getSelectedItem();
            }
        });

        currentDiagnosesView.setOnMouseClicked(click -> {
            if (click.getButton() == MouseButton.SECONDARY && currentDiagnosesView.getSelectionModel().getSelectedItem() != null) {
                Disease selected = currentDiagnosesView.getSelectionModel().getSelectedItem();
                ContextMenu rightClickCurrent = new ContextMenu();
                MenuItem makeCuredAction = new MenuItem("Mark as Cured");
                MenuItem makeChronicAction = new MenuItem("Mark as Chronic");
                makeCuredAction.setOnAction(event -> {
                    selected.setDiseaseState(GlobalEnums.DiseaseState.CURED);
                    currentDiseases.remove(selected);
                    pastDiseases.add(selected);
                    loadCurrentDiseases();
                    loadPastDiseases();
                });
                makeChronicAction.setOnAction(event -> {
                    selected.setDiseaseState(GlobalEnums.DiseaseState.CHRONIC);
                    loadCurrentDiseases();
                    loadPastDiseases();
                });
                rightClickCurrent.getItems().addAll(makeChronicAction, makeCuredAction);
                rightClickCurrent.show(currentDiagnosesView.getSelectionModel().getTableView(), click.getScreenX(), click.getScreenY());
            } else if (click.getButton() == MouseButton.PRIMARY && currentDiagnosesView.getSelectionModel().getSelectedItem() != null) {
                chosen = currentDiagnosesView.getSelectionModel().getSelectedItem();
            }
        });
    }

    /**
     * Registers a new diagnosis entry for a donor when 'Add diagnosis' is activated
     *
     * @param diagnosis The entered diagnosis to textField for registration
     */
    private void addDiagnosis(String diagnosis) {
        if (!diagnosis.equals("Enter a diagnosis") && !diagnosis.equals("") && !diagnosis.substring(0, 1).equals(" ")) {
            diagnosis = diagnosis.substring(0, 1).toUpperCase() + diagnosis.substring(1).toLowerCase();
            Boolean unique = true;

            for (Disease current : currentDiseases) {
                if (current.getDiseaseName().equals( diagnosis )) {
                    userActions.log(Level.WARNING, "Failed to register a disease", diagnosis + " is already registered");
                    Alert err = new Alert(Alert.AlertType.ERROR, "'" + diagnosis + "' is already registered");
                    err.show();
                    unique = false;
                }
            }

            for (Disease past : pastDiseases) {
                if (past.getDiseaseName().equals( diagnosis )) {
                    if (!past.getDiseaseState().toString().toLowerCase().equals( "cured" )) {
                        moveFromPastToCurrent( past, null );
                        unique = false;
                        changed = true;
                    } else {
                        userActions.log(Level.WARNING, "Failed to register a disease", diagnosis + " is already registered");
                        Alert err = new Alert(Alert.AlertType.ERROR, "'" + diagnosis + "' is already registered");
                        err.show();
                        unique = false;
                    }
                }
            }

            if (unique) {
                changed = true;
                currentDiseases.add( new Disease( diagnosis, null ) );
                userActions.log( Level.FINE, "Successfully registered a disease", "Registered a new disease for a donor" );
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
        if(currentDiseases == null || currentDiseases.isEmpty()) {
            currentDiseases = new ArrayList<>( Arrays.asList( null, null, null ) );
        } else if (currentDiseases.get(0).getDateDiagnosed() == null) {
            currentDiseases.remove( 0 );
        }
        ObservableList<Disease> observableCurrentDiseases = FXCollections.observableArrayList(currentDiseases);
        setCellValues(currentDateCol, currentDiagnosisCol, currentTagsCol);
        currentDiagnosesView.setItems(observableCurrentDiseases);
        setUpSortBehaviour(currentDiagnosesView, currentTagsCol);
    }

    /**
     * Loads the past diseases table
     */
    private void loadPastDiseases() {
        if(pastDiseases == null || pastDiseases.isEmpty()) {
            pastDiseases = new ArrayList<>( Arrays.asList( null, null, null ) );
        } else if (pastDiseases.get(0).getDateDiagnosed() == null) {
            pastDiseases.remove( 0 );
        }
        ObservableList <Disease> observablePastDiseases = FXCollections.observableArrayList( pastDiseases );
        setCellValues(pastDateCol, pastDiagnosisCol, pastTagsCol);
        pastDiagnosesView.setItems(observablePastDiseases);
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
            ScreenControl.removeScreen("donorProfile");
            try {
                ScreenControl.addScreen("donorProfile", FXMLLoader.load(getClass().getResource("/scene/donorProfile.fxml")));
                ScreenControl.activate("donorProfile");
            } catch (IOException e) {
                userActions.log(Level.SEVERE, "Error loading profile screen", "attempted to navigate from the clinician diagnoses page to the profile page");
                new Alert(Alert.AlertType.WARNING, "ERROR loading profile page", ButtonType.OK).showAndWait();
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void saveDiagnoses() {
        currentDonor.setCurrentDiseases(currentDiseases);
        currentDonor.setPastDiseases(pastDiseases);
        Database.saveToDisk();
        userActions.log( Level.FINE, "Successfully saved donor diseases", "Successfully saved donor " + currentDonor.getNhiNumber() + "diseases");
        new Alert(Alert.AlertType.CONFIRMATION, "Diagnosis saved successfully", ButtonType.OK).show();
        changed = false;
        goToProfile();
    }

    /**
     * Updates the date of a current diagnosis for a donor with a provided date
     * @param index the index in the currentDiseases ArrayList of the diagnosis being updated
     * @param date The given date for the update
     * @throws InvalidObjectException Indicates that one or more deserialized objects failed validation tests
     */
    private void updateCurrentDate(int index, LocalDate date) throws InvalidObjectException {
        if (date.isBefore(currentDonor.getBirth())) {
            new Alert(Alert.AlertType.WARNING, "Can not set date to before donor's DOB: " + currentDonor.getBirth(), ButtonType.OK).show();
            userActions.log(Level.WARNING, "Failed to update a disease date", "Disease date can not be set to before donor's DOB");
        } else if (date.isAfter( LocalDate.now() )) {
            new Alert(Alert.AlertType.WARNING, "Can not set date to after the current date", ButtonType.OK).show();
            userActions.log(Level.WARNING, "Failed to update a disease date", "Disease date can not be set to after current date");
        } else {
            currentDonor.getCurrentDiseases().get( index ).setDateDiagnosed( date, currentDonor );
            userActions.log(Level.FINE, "Updated a current disease date", "Updated a current disease date to " + date);
            changed = true;
        }
    }

    /**
     * Updates the date of a past diagnosis for a donor with a provided date
     * @param index the index in the pastDiseases ArrayList of the diagnosis being updated
     * @param date The given date for the update
     * @throws InvalidObjectException Indicates that one or more deserialized objects failed validation tests
     */
    private void updatePastDate(int index, LocalDate date) throws InvalidObjectException {
        if (date.isBefore( currentDonor.getBirth() )) {
            new Alert( Alert.AlertType.WARNING, "Can not set date to before donor's DOB: " + currentDonor.getBirth(), ButtonType.OK ).show();
            userActions.log(Level.WARNING, "Failed to update a disease date", "Disease date can not be set to before donor's DOB");
        } else if (date.isAfter( LocalDate.now() )) {
            new Alert( Alert.AlertType.WARNING, "Can not set date to after the current date", ButtonType.OK ).show();
            userActions.log(Level.WARNING, "Failed to update a disease date", "Disease date can not be set to after current date");
        } else {
            currentDonor.getPastDiseases().get( index ).setDateDiagnosed( date, currentDonor );
            userActions.log(Level.FINE, "Updated a past disease date", "Updated a past disease date to " + date);
            changed = true;
        }
    }

    /**
     * Updates the disease name for a current diagnosis
     * @param index the index in the currentDiseases ArrayList of the diagnosis being updated
     * @param name The Disease name update
     */
    private void updateCurrentName(int index, String name) {
        currentDonor.getCurrentDiseases().get( index ).setDiseaseName(name);
        userActions.log(Level.FINE, "Updated a current disease name", "Updated a current disease name to " + name);
        changed = true;
    }

    /**
     * Updates the disease name for a current diagnosis
     * @param index The index in the list of the diagnosis being updated
     * @param name The Disease name update
     */
    private void updatePastName(int index, String name) {
        currentDonor.getPastDiseases().get( index ).setDiseaseName(name);
        userActions.log(Level.FINE, "Updated a past disease name", "Updated a past disease name to " + name);
        changed = true;
    }

    /**
     * Updates the disease state for a current diagnosis if the update is not 'cured' when the current state is still 'chronic'
     * If state is set to cured, the disease will be automatically moved to the past diseases list
     * @param index The index in the list of the diagnosis being updated
     * @param status The Disease status update
     */
    private void updateCurrentStatus(int index, String status) {
        if (status.toLowerCase().equals( "cured" ) && !currentDonor.getCurrentDiseases().get( index ).getDiseaseState().toString().toLowerCase().equals("chronic")) {
            moveFromCurrentToPast(currentDonor.getCurrentDiseases().get( index ), status);
            changed = true;
        } else if (!(status.toLowerCase().equals( "cured" ) && currentDonor.getCurrentDiseases().get( index ).getDiseaseState().toString().toLowerCase().equals("chronic"))) {
            new Alert(Alert.AlertType.WARNING, "Can not set disease state to 'cured' if the current state is 'chronic'", ButtonType.OK).show();
            userActions.log(Level.WARNING, "Failed to update a disease state", "Disease state could not be changed to 'cured' when it is 'chronic'");
        } else {
            currentDonor.getCurrentDiseases().get( index ).setDiseaseState( null );
            userActions.log(Level.FINE, "Updated a current disease state", "Updated a current disease state to " + status);
            changed = true;
        }
    }

    /**
     * Updates the disease state for a past diagnosis
     * @param index The index in the list of the diagnosis being updated
     * @param status The Disease status update
     */
    private void updatePastStatus(int index, String status) {
        if (status.toLowerCase().equals("chronic")) {
            moveFromPastToCurrent(currentDonor.getPastDiseases().get( index ), status);
        } else {
            currentDonor.getPastDiseases().get( index ).setDiseaseState( (GlobalEnums.DiseaseState) GlobalEnums.DiseaseState.getEnumFromString( status ) );
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
                disease.setDiseaseState( (GlobalEnums.DiseaseState) GlobalEnums.DiseaseState.getEnumFromString( state ) );
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
                disease.setDiseaseState( (GlobalEnums.DiseaseState) GlobalEnums.DiseaseState.getEnumFromString( state ) );
            }
            pastDiseases.add( disease );
            currentDiseases.remove( disease );
            userActions.log(Level.FINE, "Updated a current disease state", "Updated a current disease state to " + state);
            userActions.log( Level.FINE, "Moved a disease from current to past", "Moved disease " + disease.getDiseaseName() + " from current to past" );
            loadPastDiseases();
            loadCurrentDiseases();
        }
    }

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
}
