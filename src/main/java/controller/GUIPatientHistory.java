package controller;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Patient;
import utility.UserActionRecord;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GUIPatientHistory {

    private Patient target;

    @FXML
    private TableColumn<UserActionRecord, String> timeStampColumn;

    @FXML
    private TableColumn<UserActionRecord, String> levelColumn;

    @FXML
    private TableColumn<UserActionRecord, String> actionColumn;

    @FXML
    private TableColumn<UserActionRecord, String> messageColumn;

    @FXML
    private TableView<UserActionRecord> logHistoryTable;

    private ObservableList<UserActionRecord> masterData = FXCollections.observableArrayList();

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
        ScreenControl.activate("patientHome");
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
        FilteredList<UserActionRecord> filteredData = new FilteredList<>(masterData, d -> true);

        // wrap the FilteredList in a SortedList.
        SortedList<UserActionRecord> sortedData = new SortedList<>(filteredData);

        // bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty()
                .bind(logHistoryTable.comparatorProperty());

        // add sorted (and filtered) data to the table.
        logHistoryTable.setItems(sortedData);

    }

    public void refresh() {
        logHistoryTable.refresh();
        masterData.clear();
        masterData.addAll(target.getUserActionsList());
        populateTable();
    }

}
