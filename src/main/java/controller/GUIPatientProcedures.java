package controller;

import static java.util.logging.Level.INFO;
import static utility.UserActionHistory.userActions;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import model.Administrator;
import model.Clinician;
import model.Patient;
import model.Procedure;
import service.PatientDataService;
import utility.GlobalEnums;
import utility.GlobalEnums.Organ;
import utility.undoRedo.SingleAction;
import utility.undoRedo.StatesHistoryScreen;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Level;

/**
 * Controller class for the Patient procedures screen
 */
public class GUIPatientProcedures extends UndoableController implements IWindowObserver{

    @FXML
    private Pane patientProceduresPane;

    @FXML
    public TableView<Procedure> previousProceduresView;

    @FXML
    public TableView<Procedure> pendingProceduresView;

    @FXML
    public TableColumn<Procedure, String> previousProcedureCol;

    @FXML
    public TableColumn<Procedure, String> previousDescriptionCol;

    @FXML
    public TableColumn<Procedure, LocalDate> previousDateCol;

    @FXML
    public TableColumn<Procedure, Set<Organ>> previousAffectedCol;

    @FXML
    public TableColumn<Procedure, String> pendingProcedureCol;

    @FXML
    public TableColumn<Procedure, String> pendingDescriptionCol;

    @FXML
    public TableColumn<Procedure, LocalDate> pendingDateCol;

    @FXML
    public TableColumn<Procedure, Set<Organ>> pendingAffectedCol;

    @FXML
    public Button addProcedureButton;

    @FXML
    public Button editProcedureButton;

    @FXML
    public Button deleteProcedureButton;

    private Patient patientClone;

    private UserControl userControl = UserControl.getUserControl();

    private PatientDataService patientDataService = new PatientDataService();

    private ScreenControl screenControl = ScreenControl.getScreenControl();


    /**
     * Sets the TableViews to the appropriate procedures for the current patient
     */
    public void loadController() {
        if (userControl.getLoggedInUser() instanceof Patient) {
            this.patientClone = (Patient) this.target.deepClone();
            setupTables();
            //Disable any add, edit, or delete functionality for patients
            addProcedureButton.setVisible(false);
            editProcedureButton.setVisible(false);
            deleteProcedureButton.setVisible(false);
        } else if (userControl.getLoggedInUser() instanceof Clinician || userControl.getLoggedInUser() instanceof Administrator) {
            this.patientClone = (Patient) this.target.deepClone();
            setupTables();
        }
        setupUndoRedo();
    }

    /**
     * Sets up undo redo for the patient procedures screen
     */
    private void setupUndoRedo() {
        controls = new ArrayList<Control>(){{
            add(pendingProceduresView);
            add(previousProceduresView);
        }};
        statesHistoryScreen = new StatesHistoryScreen(controls, GlobalEnums.UndoableScreen.PATIENTPROCEDURES, target);
    }

    /**
     * Sets up the tables to display the patient's procedures
     */
    private void setupTables() {
        this.patientClone = (Patient) this.target.deepClone();
        ObservableList<Procedure> previousProcedures = FXCollections.observableArrayList();
        ObservableList<Procedure> pendingProcedures = FXCollections.observableArrayList();
        for (Procedure procedure : patientClone.getProcedures()) {
            if (procedure.getDate().isBefore(LocalDate.now())) {
                previousProcedures.add(procedure);
            } else {
                pendingProcedures.add(procedure);
            }
        }
        previousProceduresView.setItems(previousProcedures);
        pendingProceduresView.setItems(pendingProcedures);
        previousProcedureCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getSummary()));
        previousDescriptionCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDescription()));
        previousDateCol.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getDate()));
        previousAffectedCol.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getAffectedDonations()));
        pendingProcedureCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getSummary()));
        pendingDescriptionCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDescription()));
        pendingDateCol.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getDate()));
        pendingAffectedCol.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getAffectedDonations()));

        //Setting previous procedures to initially sort by date descending (most recent first)
        previousDateCol.setSortType(TableColumn.SortType.DESCENDING);
        //Setting the tables to sort by the date column when loaded
        previousProceduresView.getSortOrder().add(previousDateCol);
        pendingProceduresView.getSortOrder().add(pendingDateCol);

        //Clear the selection in the pending procedures table when an item in the previous procedures table is selected
        previousProceduresView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> Platform.runLater(() -> {
            if (previousProceduresView.getSelectionModel().getSelectedItems().size() > 0) {
                pendingProceduresView.getSelectionModel().clearSelection();
            }
        }));
        //Clear the selection in the previous procedures table when an item in the pending procedures table is selected
        pendingProceduresView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> Platform.runLater(() -> {
            if (pendingProceduresView.getSelectionModel().getSelectedItems().size() > 0) {
                previousProceduresView.getSelectionModel().clearSelection();
            }
        }));

        //Registers event for when an item is selected in either table
        previousProceduresView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> onItemSelect());
        pendingProceduresView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> onItemSelect());
        previousProceduresView.refresh();
        pendingProceduresView.refresh();
    }

    /**
     * Called when an item is selected in either the previous or pending procedures table.
     * Checks if there are no selected items in both tables, and if so disables the delete procedure button,
     * else the delete procedure button is enabled
     */
    private void onItemSelect() {
        if (previousProceduresView.getSelectionModel().getSelectedItems().size() == 0 &&
                pendingProceduresView.getSelectionModel().getSelectedItems().size() == 0) {
            editProcedureButton.setDisable(true);
            deleteProcedureButton.setDisable(true);
        } else {
            editProcedureButton.setDisable(false);
            deleteProcedureButton.setDisable(false);
        }
    }

    /**
     * Brings up the add procedure pop-up that enables the user to add a procedure to the patient
     */
    @FXML
    public void addProcedure() {
        Parent parent = screenControl.getTouchParent(patientProceduresPane);
        GUIPatientProcedureForm controller = (GUIPatientProcedureForm) screenControl.show("/scene/patientProcedureForm.fxml", false, this, target, parent);
        controller.setTarget(target);
    }

    /**
     * Called when the add/edit procedure window is closed
     */
    public void windowClosed() {
        tableRefresh();
    }

    /**
     * Opens an edit popup for the procedure that is currently selected
     */
    public void editProcedure() {
        //Grabbing the currently selected procedure to pass to the form controller for loading
        Procedure selectedProcedure = null;
        if (previousProceduresView.getSelectionModel().getSelectedItem() != null) {
            selectedProcedure = previousProceduresView.getSelectionModel().getSelectedItem();
        }
        if (pendingProceduresView.getSelectionModel().getSelectedItem() != null) {
            selectedProcedure = pendingProceduresView.getSelectionModel().getSelectedItem();
        }
        if (selectedProcedure == null) {
            userActions.log(Level.WARNING, "No procedure selected", new String[]{"Attempted to edit a procedure", ((Patient) target).getNhiNumber()});
            return;
        }
        Parent parent = screenControl.getTouchParent(patientProceduresPane);
        GUIPatientProcedureForm controller = (GUIPatientProcedureForm) screenControl.show("/scene/patientProcedureForm.fxml", false, this, target, parent);
        controller.setTarget(target);
        controller.setupEditing(selectedProcedure);
    }

    /**
     * Refreshes the tables shown
     */
    private void tableRefresh() {
        setupTables();
    }

    /**
     * Deletes the procedure that is currently selected. A confirmation dialog displays for each selected procedure
     */
    @FXML
    public void deleteProcedure() {
        Procedure selectedProcedure = null;
        if (previousProceduresView.getSelectionModel().getSelectedItem() != null) {
            selectedProcedure = previousProceduresView.getSelectionModel().getSelectedItem();
        }
        if (pendingProceduresView.getSelectionModel().getSelectedItem() != null) {
            selectedProcedure = pendingProceduresView.getSelectionModel().getSelectedItem();
        }
        if (selectedProcedure != null) {
            patientClone.removeProcedure(selectedProcedure);
            statesHistoryScreen.addAction(new SingleAction(target, patientClone));
            userActions.log(INFO, "Removed procedure " + selectedProcedure.getSummary(), new String[]{"Attempted to remove a procedure", ((Patient) target).getNhiNumber()});
            setupTables();
        }
    }

}
