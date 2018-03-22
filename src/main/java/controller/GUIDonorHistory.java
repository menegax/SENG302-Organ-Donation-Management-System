package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import utility.UserActionRecord;

public class GUIDonorHistory {

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
        logHistoryTable.setItems(UserActionRecord.logHistory);
    }

}
