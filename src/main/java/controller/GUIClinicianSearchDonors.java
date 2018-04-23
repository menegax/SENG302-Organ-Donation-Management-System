package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import model.Donor;
import service.Database;
import utility.GlobalEnums;
import utility.SearchDonors;

import java.net.URL;
import java.util.ResourceBundle;

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

        // set the filter Predicate whenever the filter changes.
        searchEntry.textProperty()
                .addListener((observable, oldValue, newValue) -> {
                    filteredData.setPredicate(donor -> {
                        // If filter text is empty, display all persons.
                        if (newValue == null || newValue.isEmpty()) {
                            return true;
                        }

                        //                if (donor.getFirstName().toLowerCase().contains(lowerCaseFilter)) { //todo old code, using only for reference temporarily
                        //                    return true; // Filter matches first name.
                        //                }

                        if (SearchDonors.searchByName(newValue)
                                .contains(donor)) { //todo here is hte new code to call fuzzy
                            return true;
                        }

                        return false; // Does not match.
                    });
                });

        // wrap the FilteredList in a SortedList.
        SortedList<Donor> sortedData = new SortedList<>(filteredData);

        // bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty()
                .bind(donorDataTable.comparatorProperty());

        // add sorted (and filtered) data to the table.
        donorDataTable.setItems(sortedData);

        // Add double-click event to rows
        donorDataTable.setOnMouseClicked(click -> {
            if (click.getClickCount() == 2 && donorDataTable.getSelectionModel()
                    .getSelectedItem() != null) {
                GUIDonorProfileUpdate donorUpdate = new GUIDonorProfileUpdate(); //todo make show many windows filled with donor profile
            }
            donorDataTable.refresh(); //todo needs to be here? test
        });


        // add hover-over text to rows
        donorDataTable.setRowFactory(tv -> new TableRow<Donor>() {
            private Tooltip tooltip = new Tooltip();

            @Override
            public void updateItem(Donor donor, boolean empty) {
                super.updateItem(donor, empty);
                if (donor == null) {
                    setTooltip(null);
                }
                else {
                    StringBuilder tooltipText = new StringBuilder(donor.getNameConcatenated() + ". Donor: ");
                    for (GlobalEnums.Organ organ : donor.getDonations()) {
                        tooltipText.append(organ)
                                .append(", ");
                    }
                    tooltipText = new StringBuilder(tooltipText.substring(0, tooltipText.length() - 2));
                    tooltip.setText(tooltipText.toString());
                    setTooltip(tooltip);
                }
            }
        });

    }


    public void goToClinicianHome() {
        ScreenControl.activate("clinicianHome");
    }
}

