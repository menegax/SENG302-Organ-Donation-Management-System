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
import java.util.*;
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
    private boolean changed = false; // Boolean for if there are any un-saved edits when leaving pane

    @FXML
    public void initialize() {
        currentDonor = ScreenControl.getLoggedInDonor();
        currentDiseases = currentDonor.getCurrentDiseases();
        pastDiseases = currentDonor.getPastDiseases();
        loadCurrentDiseases();
        loadPastDiseases();
        pastDiagnosesView.setOnMouseClicked(click -> {
            if (click.getButton() == MouseButton.SECONDARY && pastDiagnosesView.getSelectionModel().getSelectedItem() != null) {
                Disease selected = pastDiseases.get( pastDiseases.indexOf( pastDiagnosesView.getSelectionModel().getSelectedItem() ) );
                ContextMenu rightClickPast = new ContextMenu();
                MenuItem makeChronicAction = new MenuItem( "Mark as chronic" );
                MenuItem makeCuredAction = new MenuItem( "Mark as cured" );
                MenuItem makeNullAction = new MenuItem( "Mark as null" );
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
     * @param disease the disease in the currentDiseases ArrayList of the diagnosis being updated
     * @param date The given date for the update
     * @throws InvalidObjectException Indicates that one or more deserialized objects failed validation tests
     */
    private void updateCurrentDate(Disease disease, LocalDate date) throws InvalidObjectException {
        if (date.isBefore(currentDonor.getBirth())) {
            new Alert(Alert.AlertType.WARNING, "Can not set date to before donor's DOB: " + currentDonor.getBirth(), ButtonType.OK).show();
            userActions.log(Level.WARNING, "Failed to update a disease date", "Disease date can not be set to before donor's DOB");
        } else if (date.isAfter( LocalDate.now() )) {
            new Alert(Alert.AlertType.WARNING, "Can not set date to after the current date", ButtonType.OK).show();
            userActions.log(Level.WARNING, "Failed to update a disease date", "Disease date can not be set to after current date");
        } else {
            disease.setDateDiagnosed( date, currentDonor );
            userActions.log(Level.FINE, "Updated a current disease date", "Updated a current disease date to " + date);
            changed = true;
        }
    }

    /**
     * Updates the date of a past diagnosis for a donor with a provided date
     * @param disease the disease in the pastDiseases ArrayList of the diagnosis being updated
     * @param date The given date for the update
     * @throws InvalidObjectException Indicates that one or more deserialized objects failed validation tests
     */
    private void updatePastDate(Disease disease, LocalDate date) throws InvalidObjectException {
        if (date.isBefore( currentDonor.getBirth() )) {
            new Alert( Alert.AlertType.WARNING, "Can not set date to before donor's DOB: " + currentDonor.getBirth(), ButtonType.OK ).show();
            userActions.log(Level.WARNING, "Failed to update a disease date", "Disease date can not be set to before donor's DOB");
        } else if (date.isAfter( LocalDate.now() )) {
            new Alert( Alert.AlertType.WARNING, "Can not set date to after the current date", ButtonType.OK ).show();
            userActions.log(Level.WARNING, "Failed to update a disease date", "Disease date can not be set to after current date");
        } else {
            disease.setDateDiagnosed( date, currentDonor );
            userActions.log(Level.FINE, "Updated a past disease date", "Updated a past disease date to " + date);
            changed = true;
        }
    }

    /**
     * Updates the disease name for a current diagnosis
     * @param disease the disease in the currentDiseases ArrayList of the diagnosis being updated
     * @param name The Disease name update
     */
    private void updateCurrentName(Disease disease, String name) {
        disease.setDiseaseName(name);
        userActions.log(Level.FINE, "Updated a current disease name", "Updated a current disease name to " + name);
        changed = true;
    }

    /**
     * Updates the disease name for a current diagnosis
     * @param disease The disease in the list of the diagnosis being updated
     * @param name The Disease name update
     */
    private void updatePastName(Disease disease, String name) {
        disease.setDiseaseName(name);
        userActions.log(Level.FINE, "Updated a past disease name", "Updated a past disease name to " + name);
        changed = true;
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
