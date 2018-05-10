package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import utility.UserActionRecord;

public class GUIPatientHistory {

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


    public void initialize() {
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
        timeStampColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        levelColumn.setCellValueFactory(new PropertyValueFactory<>("level"));
        messageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
        actionColumn.setCellValueFactory(new PropertyValueFactory<>("action"));
        logHistoryTable.setItems(UserActionRecord.logHistory);
    }

}
