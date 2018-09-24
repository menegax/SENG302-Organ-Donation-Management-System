package controller;

import data_access.localDAO.LocalDB;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.RotateEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.Patient;
import utility.GlobalEnums;
import utility.MultiTouchHandler;

import java.time.LocalDate;
import java.util.Set;

public class GUIAdministratorImportResults {
    @FXML
    private GridPane adminImportResultsPane;

    @FXML
    private TableView<Patient> resultsTable;

    private ObservableList<Patient> tableData;

    @FXML
    private TableColumn<Patient, String> nhiCol;

    @FXML
    private TableColumn<Patient, String> nameCol;

    @FXML
    private TableColumn<Patient, GlobalEnums.BirthGender> genderCol;

    @FXML
    private TableColumn<Patient, String> addressCol;

    @FXML
    private TableColumn<Patient, String> regionCol;

    @FXML
    private TableColumn<Patient, LocalDate> dobCol;

    @FXML
    private TableColumn<Patient, GlobalEnums.BloodGroup> bloodTypeCol;

    private ScreenControl screenControl = ScreenControl.getScreenControl();


    private MultiTouchHandler touchHandler;

    public void initialize() {
        nhiCol.setCellValueFactory(new PropertyValueFactory<>("nhiNumber"));
        nameCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getNameConcatenated()));
        addressCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getAddressString()));
        regionCol.setCellValueFactory(new PropertyValueFactory<>("region"));
        genderCol.setCellValueFactory(new PropertyValueFactory<>("birthGender"));
        dobCol.setCellValueFactory(new PropertyValueFactory<>("birth"));
        bloodTypeCol.setCellValueFactory(new PropertyValueFactory<>("bloodGroup"));

        Set<Patient> patients = LocalDB.getInstance().getImported();
        tableData = FXCollections.observableArrayList(patients);
        resultsTable.setItems(tableData);
        if(screenControl.isTouch()) {
            touchHandler = new MultiTouchHandler();
            touchHandler.initialiseHandler(adminImportResultsPane);
        }
    }

    /**
     * Closes the import results window
     */
    public void close() {
        screenControl.closeWindow(adminImportResultsPane);
    }

}
