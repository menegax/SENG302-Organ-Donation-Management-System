package controller;

import com.sun.javafx.robot.FXRobot;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Patient;
import model.Procedure;
import utility.GlobalEnums.Organ;

import javax.swing.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Set;
import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

/**
 * Controller class for the Patient procedures screen
 */
public class GUIPatientProcedures implements IPopupable{

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
    public Button deleteProcedureButton;

    private Patient patient;

    /**
     * Sets the TableViews to the appropriate procedures for the current patient
     */
    public void initialize() {
        if (ScreenControl.getLoggedInPatient() != null) {
            this.patient = ScreenControl.getLoggedInPatient();
            setupTables();
            addProcedureButton.setVisible(false);
            deleteProcedureButton.setVisible(false);
        }
    }

    /**
     * Sets up the tables to display the patient's procedures
     */
    private void setupTables() {
        ObservableList<Procedure> previousProcedures = FXCollections.observableArrayList();
        ObservableList<Procedure> pendingProcedures = FXCollections.observableArrayList();
        for (Procedure procedure : patient.getProcedures()) {
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
    }

    /**
     * Lets the current user edit the procedures of the viewed patient
     */
    private void enableEditing() {
        addProcedureButton.setVisible(true);
        deleteProcedureButton.setVisible(true);
    }

    /**
     * Brings up the add procedure pop-up that enables the user to add a procedure to the patient
     */
    @FXML
    public void addProcedure() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scene/addProcedure.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            GUIAddProcedure controller = fxmlLoader.getController();
            controller.setViewedPatient(patient);
            Stage popUpStage = new Stage();
            popUpStage.setScene(scene);

            // When pop up is closed, refresh the table
            popUpStage.setOnHiding(event -> Platform.runLater(this::refreshTables));

            //Add and show the popup
            ScreenControl.addPopUp("addProcedurePopup", popUpStage); //ADD to screen control
            ScreenControl.displayPopUp("addProcedurePopup"); //display the popup
        } catch (IOException e) {
            e.printStackTrace();
            userActions.log(Level.SEVERE,
                    "Failed to open add procedure popup from patient procedures",
                    "Attempted to open add procedure popup from patient procedures");
            new Alert(Alert.AlertType.ERROR, "Unable to open add procedure window", ButtonType.OK).show();
        }
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
            final Procedure finalProcedure = selectedProcedure;
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to remove: " + selectedProcedure.getSummary() + "?");
            Button dialogOK = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
            dialogOK.addEventFilter(ActionEvent.ACTION, event -> {
                patient.removeProcedure(finalProcedure);
                setupTables();
            });
            alert.show();
        }
    }

    /**
     * Refreshes the procedure tables
     */
    private void refreshTables() {
        previousProceduresView.refresh();
        pendingProceduresView.refresh();
    }

    /**
     * Sets the patient viewed by this scene and resets the display
     * Only called from a clinicians user
     * @param patient the patient which the screen represents
     */
    public void setViewedPatient(Patient patient) {
        this.patient = patient;
        setupTables();
        enableEditing();
    }

    @FXML
    public void goToProfile() {

    }
}
