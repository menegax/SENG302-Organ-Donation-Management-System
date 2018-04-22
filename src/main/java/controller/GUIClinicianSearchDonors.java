package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import model.Donor;
import service.Database;
import utility.SearchDonors;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

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

    private ObservableList<Donor> masterData = FXCollections.observableArrayList();

    /**
     * Adds all db data via constructor
     */
    public GUIClinicianSearchDonors() {
        masterData.addAll(Database.getDonors());
    }


    /**
     * Initialises the data within the table to all donors
     *
     * @param url URL not used
     * @param rb  Resource bundle not used
     */
    @FXML
    public void initialize(URL url, ResourceBundle rb) {

        // initialize columns
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

        // wrap ObservableList in a FilteredList
        FilteredList<Donor> filteredData = new FilteredList<>(masterData, d -> true);

        // 2. Set the filter Predicate whenever the filter changes.
        searchEntry.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(donor -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                if (donor.getFirstName().toLowerCase().contains(lowerCaseFilter)) { //todo replace with single call to fuzzy
                    return true; // Filter matches first name.
                } else if (donor.getLastName().toLowerCase().contains(lowerCaseFilter)) { //todo
                    return true; // Filter matches last name.
                }
                return false; // Does not match.
            });
        });

        // 3. Wrap the FilteredList in a SortedList.
        SortedList<Donor> sortedData = new SortedList<>(filteredData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(donorDataTable.comparatorProperty());

        // 5. Add sorted (and filtered) data to the table.
        donorDataTable.setItems(sortedData);
    }


    //todo remove if not needed
//    /**
//     * Refreshes the table's data
//     * <p>
//     * Say a donor is added to the db or updated with new attributes, a refresh would be needed
//     */
//    @FXML
//    private void refreshTable() {
//        donorDataTable.refresh();
//
//        //todo make refreshing clear the search results from the table and load default data
//    }


    public void goToClinicianHome() {
        ScreenControl.activate("clinicianHome");
    }
}

