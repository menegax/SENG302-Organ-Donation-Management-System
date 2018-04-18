package controller;

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
import javafx.scene.input.MouseEvent;
import model.Donor;
import service.Database;

import java.net.URL;
import java.util.*;

public class GUIClinicianSearchDonors implements Initializable {

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
    public void initialize(URL url, ResourceBundle rb) {
        // todo implement below
        loadData();
    }


    /**
     * Loads the current donor data into the donor data table
     */
    private void loadData() {

        donorDataTable.getItems().addAll(Database.getDonors());

        // for each donor d
        columnName.setCellValueFactory(d-> d.getValue().getNameConcatenated() != null ? new SimpleStringProperty(d.getValue().getNameConcatenated()) : new SimpleStringProperty(""));
        columnAge.setCellValueFactory(d-> new SimpleStringProperty(String.valueOf(d.getValue().getAge())));
        columnGender.setCellValueFactory(d->  d.getValue().getGender() != null ? new SimpleStringProperty(d.getValue().getGender().toString()) : new SimpleStringProperty(""));
        columnRegion.setCellValueFactory(d-> d.getValue().getRegion() != null ? new SimpleStringProperty(d.getValue().getRegion().toString()) : new SimpleStringProperty(""));
    }

    //    /**
    //     * Updates the donors displayed based on the the search criteria in the search entry
    //     */
    //    private void search() {
    //        searchedDonors.clear();
    //        for (int i = 0; i < donors.size() && i < 30; i++) {
    //            if (donors.get(i)
    //                    .getName()
    //                    .toLowerCase()
    //                    .contains(searchEntry.getText()
    //                            .toLowerCase()) && !donors.get(i)
    //                    .getChanged()
    //                    .equals("Delete")) {
    //                searchedDonors.add(donors.get(i));
    //            }
    //        }
    //        calculateFields();
    //    }


    public void goToClinicianHome() {
        ScreenControl.activate("home");
    }
}

