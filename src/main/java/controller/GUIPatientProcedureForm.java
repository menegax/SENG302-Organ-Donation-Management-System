package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.Patient;
import model.Procedure;
import utility.GlobalEnums;
import utility.GlobalEnums.Organ;
import utility.StatusObservable;
import utility.undoRedo.Action;
import utility.undoRedo.StatesHistoryScreen;
import utility.undoRedo.UndoableStage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Pattern;

import static utility.SystemLogger.systemLogger;
import static utility.UserActionHistory.userActions;

/**
 * Form to add and edit patient procedures only accessible by a clinician
 */
public class GUIPatientProcedureForm  {

    @FXML
    public Button doneButton;

    @FXML
    public Button closePane;

    @FXML
    public TextField summaryInput;

    @FXML
    public TextArea descriptionInput;

    @FXML
    public DatePicker dateInput;

    @FXML
    public MenuButton affectedInput;

    private Patient patient;
    private Patient patientClone;
    private boolean isEditInstance = false;
    private Procedure procedure; //The Procedure that is being edited (null in the case of adding a procedure)
    private Procedure procedureClone;
    private ScreenControl screenControl = ScreenControl.getScreenControl();
    private UserControl userControl = new UserControl();

    /**
     * Initial setup. Sets up undo/redo, Populates the affected organs dropdown
     */
    public void initialize() {
        patient = (Patient) new UserControl().getTargetUser();
        patientClone = (Patient) patient.deepClone();
        setupDonations();
        for (MenuItem menuItem : affectedInput.getItems()) { //Adding organ checkboxes to the undo/redo controls
            if (((CustomMenuItem) menuItem).getContent() instanceof CheckBox) {
            CheckBox checkbox = (CheckBox) ((CustomMenuItem) menuItem).getContent();
            }
        }
    }

    /**
     * Used to signify that the instance is for editing a procedure
     */
    void setupEditing(Procedure procedure) {
        isEditInstance = true;
        this.procedure = procedure;
        //todo modularise and design out into .equals
        for (Procedure iprocedure : patientClone.getProcedures()) {
            if (iprocedure.getDate().equals(procedure.getDate()) && iprocedure.getSummary().equals(procedure.getSummary())) {
                procedureClone = iprocedure;
            }
        }
        loadProcedure();
    }

    /**
     * Loads the details of an existing procedure into the form inputs
     */
    private void loadProcedure() {
        summaryInput.setText(procedure.getSummary());
        descriptionInput.setText(procedure.getDescription());
        dateInput.setValue(procedure.getDate());

        //Select the organ checkboxes to match the target procedures affectedOrgan set
        for (MenuItem organSelection : affectedInput.getItems()) {
            if (((CustomMenuItem) organSelection).getContent().getId() == null) {
                CheckBox organCheckBox = (CheckBox) ((CustomMenuItem) organSelection).getContent();
                Organ organ = (Organ) Organ.getEnumFromString(organCheckBox.getText());
                if (procedure.getAffectedDonations() != null && procedure.getAffectedDonations().contains(organ)) {
                    organCheckBox.setSelected(true);
                }
            }
        }
    }

    /**
     * Called when the form is submitted
     */
    @FXML
    public void onSubmit() {
        if (isEditInstance) {
            editProcedure();
        } else {
            addProcedure();
        }
    }

    /**
     * Converts the selected checkboxes in the affected organ selection menu into a set of organs
     * @return The resulting set of organs that are affected
     */
    private Set<Organ> getAffectedOrgansFromForm() {
        Set<Organ> affectedOrgans = new HashSet<>();
        for (MenuItem organSelection : affectedInput.getItems()) {
            if (((CustomMenuItem) organSelection).getContent().getId() == null) {
                CheckBox selectionCheckBox = (CheckBox) ((CustomMenuItem) organSelection).getContent();
                if (selectionCheckBox.isSelected()) {
                    affectedOrgans.add((Organ) Organ.getEnumFromString(selectionCheckBox.getText()));
                }
            }
        }
        return affectedOrgans;
    }

    /**
     * Applies the edits made in the form to the target procedure and closes the pop-up
     */
    private void editProcedure() {
        Set<Organ> affectedDonations = getAffectedOrgansFromForm();

        if (validateInputs(summaryInput.getText(), descriptionInput.getText(), dateInput.getValue())) {
            this.procedureClone.setSummary(summaryInput.getText());
            this.procedureClone.setDescription(descriptionInput.getText());
            this.procedureClone.setAffectedDonations(affectedDonations);
            this.procedureClone.setDate(dateInput.getValue());
            // todo modularise out (into screenControl?? - undoredoControl??)
            Action action = new Action(patient, patientClone);
            for (Stage stage : screenControl.getUsersStages(userControl.getLoggedInUser())) {
                if (stage instanceof UndoableStage) {
                    for (StatesHistoryScreen statesHistoryScreen : ((UndoableStage) stage).getStatesHistoryScreens()) {
                        if (statesHistoryScreen.getUndoableScreen().equals(GlobalEnums.UndoableScreen.PATIENTPROCEDURES)) {
                            statesHistoryScreen.addAction(action);
                        }
                    }
                }
            }
            userActions.log(Level.INFO, "Updated procedure " + this.procedure.getSummary(), new String[]{"Attempted to update procedure", patient.getNhiNumber()});
            goBackToProcedures();
        } else {
            userActions.log(Level.WARNING, "Invalid procedure inputs entered", "Attempted to edit procedure with invalid inputs");
        }
    }

    /**
     * Adds the procedure to the patient's list of procedures and closes the pop-up
     */
    private void addProcedure() {
        Set <Organ> affectedDonations = getAffectedOrgansFromForm();
        dateInput.setStyle( null );
        summaryInput.setStyle( null );
        descriptionInput.setStyle( null );
        if (validateInputs(summaryInput.getText(), descriptionInput.getText(), dateInput.getValue())){
            if ( affectedDonations.size() == 0 ) {
                affectedDonations = null;
            }
            procedureClone = new Procedure( summaryInput.getText(), descriptionInput.getText(),
                    dateInput.getValue(), affectedDonations );
            patientClone.addProcedure( procedureClone );
            Action action = new Action(patient, patientClone);
            for (Stage stage : screenControl.getUsersStages(userControl.getLoggedInUser())) {
                if (stage instanceof UndoableStage) {
                    for (StatesHistoryScreen statesHistoryScreen : ((UndoableStage) stage).getStatesHistoryScreens()) {
                        if (statesHistoryScreen.getUndoableScreen().equals(GlobalEnums.UndoableScreen.PATIENTPROCEDURES)) {
                            statesHistoryScreen.addAction(action);
                        }
                    }
                }
            }
            userActions.log(Level.INFO, "Added procedure " + procedureClone.getSummary(), new String[]{"Attempted to add a procedure", patientClone.getNhiNumber()});
            goBackToProcedures();
        } else {
            userActions.log(Level.WARNING, "Invalid inputs for procedure entered", "Attempted to create procedure with invalid inputs");
        }
    }

    /**
     * Validates the input fields summary, description, date and organs on creation of a procedure
     * If any field is invalid, the field will be highlighted red ro display that there is an error
     * @param summary The procedure summary string
     * @param description The procedure description string
     * @param date The date of the procedure
     * @return True if date is not before patient DOB, one or more organs, or summary/description are more than 1 chars
     */
    private Boolean validateInputs(String summary, String description, LocalDate date) {
        Boolean isValid = true;
        if ( date == null || date.isBefore( patient.getBirth() )) {
            isValid = false;
            dateInput.setStyle( "-fx-base: red;" );
        }
        if ( summary.length() < 1 || !Pattern.matches("[A-Za-z0-9-,.'%() ]+", summary) ||
                summary.substring(0,1).equals(" ") ) {
            isValid = false;
            summaryInput.setStyle( "-fx-base: red;" );
        }
        if ( description.length() > 0 && (!Pattern.matches("[A-Za-z0-9-,.'%() ]+", description) ||
                description.substring( 0,1 ).equals(" ") )) {
            isValid = false;
            descriptionInput.setStyle( "-fx-base: red;" );
        }
        return isValid;
    }

    /**
     * Sets the items in the affected donations dropdown to all the donations registered to the patient
     */
    private void setupDonations() {
        ObservableList<CustomMenuItem> donations = FXCollections.observableArrayList();
        for (Organ organ : patient.getDonations()) {
            CustomMenuItem organSelection = new CustomMenuItem(new CheckBox(organ.getValue()));
            organSelection.setHideOnClick(false);
            donations.add(organSelection);
        }
        if (donations.size() == 0) {
            Label noneLabel = new Label("None");
            noneLabel.setId("noneLabel");
            donations.add(new CustomMenuItem(noneLabel));
        }
        affectedInput.getItems().setAll(donations);
    }

    /**
     * Closes the pop-up stage for the procedure form
     */
    public void goBackToProcedures() {
        screenControl.closeStage(((UndoableStage)summaryInput.getScene().getWindow()).getUUID());
    }
}
