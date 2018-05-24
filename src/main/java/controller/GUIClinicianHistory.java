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
import model.Clinician;
import utility.ClinicianActionRecord;

import java.io.IOException;
import java.text.SimpleDateFormat;

import static java.util.logging.Level.SEVERE;
import static utility.UserActionHistory.userActions;

public class GUIClinicianHistory {

    private Clinician target;

    @FXML
    private TableColumn<ClinicianActionRecord, String> timeStampColumn;

    @FXML
    private TableColumn<ClinicianActionRecord, String> levelColumn;

    @FXML
    private TableColumn<ClinicianActionRecord, String> actionColumn;

    @FXML
    private TableColumn<ClinicianActionRecord, String> messageColumn;

    @FXML
    private TableColumn<ClinicianActionRecord, String> targetColumn;

    @FXML
    private TableView<ClinicianActionRecord> logHistoryTable;

    private ObservableList<ClinicianActionRecord> masterData = FXCollections.observableArrayList();

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    public void initialize() {
        UserControl userControl = new UserControl();
        target = userControl.getLoggedInUser() instanceof Clinician ? (Clinician) userControl.getLoggedInUser() : null;
        masterData.addAll(target.getClinicianActionsList());
        populateTable();
    }


    /**
     * Go to home page action listener for back button
     */
    public void goToClinicianHome() {
        try {
            screenControl.show(logHistoryTable,"/scene/clinicianHome.fxml");
        } catch (IOException e) {
            new Alert((Alert.AlertType.ERROR), "Unable to load clinician home").show();
            userActions.log(SEVERE, "Failed to load clinician home", "Attempted to load clinician home");
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
        targetColumn.setCellValueFactory(r -> new ReadOnlyStringWrapper(r.getValue().getTarget()));

        // wrap ObservableList in a FilteredList
        FilteredList<ClinicianActionRecord> filteredData = new FilteredList<>(masterData, d -> true);

        masterData.sort((o1, o2) -> o2.getTimestamp().compareTo(o1.getTimestamp())); //sort so timestamp most recent at top

        // wrap the FilteredList in a SortedList.
        SortedList<ClinicianActionRecord> sortedData = new SortedList<>(filteredData);

        timeStampColumn.setComparator(timeStampColumn.getComparator().reversed()); // reverses comparator

        // bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty()
                .bind(logHistoryTable.comparatorProperty());

        // add sorted (and filtered) data to the table.
        logHistoryTable.setItems(sortedData);


    }

}
