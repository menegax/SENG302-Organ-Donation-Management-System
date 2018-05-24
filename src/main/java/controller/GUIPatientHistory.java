package controller;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.Patient;
import utility.PatientActionRecord;

import java.io.IOException;
import java.text.SimpleDateFormat;

import static java.util.logging.Level.SEVERE;
import static utility.UserActionHistory.userActions;

public class GUIPatientHistory {

    private Patient target;

    @FXML
    private TableColumn<PatientActionRecord, String> timeStampColumn;

    @FXML
    private TableColumn<PatientActionRecord, String> levelColumn;

    @FXML
    private TableColumn<PatientActionRecord, String> actionColumn;

    @FXML
    private TableColumn<PatientActionRecord, String> messageColumn;

    @FXML
    private TableView<PatientActionRecord> logHistoryTable;

    private ObservableList<PatientActionRecord> masterData = FXCollections.observableArrayList();


    private ScreenControl screenControl = ScreenControl.getScreenControl();

    public void initialize() {
        UserControl userControl = new UserControl();
        target = userControl.getLoggedInUser() instanceof Patient ? (Patient) userControl.getLoggedInUser() : null;
        masterData.addAll(target.getUserActionsList());
        populateTable();
    }


    /**
     * Go to home page action listener for back button
     */
    public void goToPatientHome() {
        try {
            screenControl.show(logHistoryTable,"/scene/patientHome.fxml");
        } catch (IOException e) {
            new Alert((Alert.AlertType.ERROR), "Unable to load patient home").show();
            userActions.log(SEVERE, "Failed to load patient home", "Attempted to load patient home");
        }
    }


    /**
     * Populate the table with records
     */
    private void populateTable() {

        // initialize columns
        timeStampColumn.setCellValueFactory(r -> new ReadOnlyStringWrapper(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(r.getValue().getTimestamp())));
        levelColumn.setCellValueFactory(r -> new ReadOnlyStringWrapper(r.getValue().getLevel().toString()));
        messageColumn.setCellValueFactory(r -> new ReadOnlyStringWrapper(r.getValue().getMessage()));
        actionColumn.setCellValueFactory(r -> new ReadOnlyStringWrapper(r.getValue().getAction()));

        // wrap ObservableList in a FilteredList
        FilteredList<PatientActionRecord> filteredData = new FilteredList<>(masterData, d -> true);

        masterData.sort((o1, o2) -> o2.getTimestamp().compareTo(o1.getTimestamp())); //sort so timestamp most recent at top

        // wrap the FilteredList in a SortedList.
        SortedList<PatientActionRecord> sortedData = new SortedList<>(filteredData);

        timeStampColumn.setComparator(timeStampColumn.getComparator().reversed()); // reverses comparator

        // bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty()
                .bind(logHistoryTable.comparatorProperty());

        // add sorted (and filtered) data to the table.
        logHistoryTable.setItems(sortedData);


    }

}
