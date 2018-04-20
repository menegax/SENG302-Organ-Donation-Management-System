package controller;

import static utility.UserActionHistory.userActions;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import model.Donor;
import service.Database;
import utility.Search;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;

public class GUIClinicianSearchDonors implements Initializable {

    @FXML
    private AnchorPane pane;

    @FXML
    private TableView<Donor> donorDataTable;

    @FXML
    private TableColumn<Donor, String> columnName;

    @FXML
    private TableColumn<Donor, String> columnAge;

    @FXML
    private TableColumn<Donor, String> columnGender;

    @FXML
    private TableColumn<Donor, String> columnRegion;

    @FXML
    private TextField searchEntry;


    /**
     * Initialises the data within the table to all donors
     *
     * @param url Required parameter that is not used in the function
     * @param rb  Required parameter that is not used in the function
     */
    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        loadData();

        // Enter key triggers search
        pane.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                donorSearch();
            }
        });
    }


    /**
     * Loads the current donor data into the donor data table
     */
    @FXML
    private void loadData() {

        donorDataTable.getItems()
                .addAll(Database.getDonors());

        // for each donor d
        columnName.setCellValueFactory(d -> d.getValue()
                .getNameConcatenated() != null ? new SimpleStringProperty(d.getValue()
                .getNameConcatenated()) : new SimpleStringProperty(""));
        columnAge.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue()
                .getAge())));
        columnGender.setCellValueFactory(d -> d.getValue()
                .getGender() != null ? new SimpleStringProperty(d.getValue()
                .getGender()
                .toString()) : new SimpleStringProperty(""));
        columnRegion.setCellValueFactory(d -> d.getValue()
                .getRegion() != null ? new SimpleStringProperty(d.getValue()
                .getRegion()
                .toString()) : new SimpleStringProperty(""));
    }


    /**
     * Searches the indices of donors
     */
    @FXML
    private void donorSearch() {
        try {
            ArrayList<Donor> searchResults = Search.searchByName(searchEntry.getText());

            //todo make searchResults populate the table

            System.out.println("DONORS HAVE BEEN SEARCHED THROUGH"); //todo remove
        }
        catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Failed to search donors");
            alert.show();
        }
    }


    /**
     * Refreshes the table's data
     */
    @FXML
    private void refreshTable() {
        donorDataTable.refresh();
    }


    public void goToClinicianHome() {
        ScreenControl.activate("home");
    }
}

