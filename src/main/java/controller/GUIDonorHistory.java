package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import utility.CustomLogRecord;

public class GUIDonorHistory {

    @FXML
    private TableColumn<CustomLogRecord, String> timeStampColumn;

    @FXML
    private TableColumn<CustomLogRecord, String> levelColumn;

    @FXML
    private TableColumn<CustomLogRecord, String> actionColumn;

    @FXML
    private TableColumn<CustomLogRecord, String> messageColumn;

    @FXML
    private TableView<CustomLogRecord> logHistoryTable;

    public void goToHome() {
        ScreenControl.activate("home");
    }

    public void initialize(){
        populateTable();
    }



    public void populateTable(){
        timeStampColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        levelColumn.setCellValueFactory(new PropertyValueFactory<>("level"));
        messageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
        actionColumn.setCellValueFactory(new PropertyValueFactory<>("action"));
        logHistoryTable.setItems(CustomLogRecord.logHistory);
    }

}
