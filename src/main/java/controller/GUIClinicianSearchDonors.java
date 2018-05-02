package controller;

import static utility.UserActionHistory.userActions;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Donor;
import service.Database;
import utility.GlobalEnums;
import utility.SearchDonors;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.logging.Level;


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
     * Initialises the data within the table to all donors
     *
     * @param url URL not used
     * @param rb  Resource bundle not used
     */
    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        FilteredList<Donor> filteredData = setupTableColumnsAndData();

        setupSearchingListener(filteredData);
        setupDoubleClickToDonorEdit();
        setupRowHoverOverText();

    }


    /**
     * Sets up double-click functionality for each row to open a donor profile update
     */
    private void setupDoubleClickToDonorEdit() {

        // Add double-click event to rows
        donorDataTable.setOnMouseClicked(click -> {
            if (click.getClickCount() == 2 && donorDataTable.getSelectionModel()
                    .getSelectedItem() != null) {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scene/donorProfile.fxml"));
                    Scene scene = new Scene(fxmlLoader.load());
                    GUIDonorProfile controller = fxmlLoader.getController();
                    controller.setViewedDonor(donorDataTable.getSelectionModel()
                            .getSelectedItem());

                    Stage popUpStage = new Stage();
                    popUpStage.setX(ScreenControl.getMain()
                            .getX()); //offset popup
                    popUpStage.setScene(scene);

                    // When pop up is closed, refresh the table
                    popUpStage.setOnHiding(event -> Platform.runLater(this::tableRefresh));

                    //Add and show the popup
                    ScreenControl.addPopUp("searchPopup", popUpStage); //ADD to screen control
                    ScreenControl.displayPopUp("searchPopup"); //display the popup
                }
                catch (Exception e) {
                    userActions.log(Level.SEVERE,
                            "Failed to open donor profile scene from search donors table",
                            "attempted to open donor edit window from search donors table");
                    new Alert(Alert.AlertType.ERROR, "Unable to open donor edit window", ButtonType.OK).show();
                    e.printStackTrace();
                }
            }

        });
    }


    /**
     * Sets the table columns to pull the correct data from the donor objects
     *
     * @return a filtered list of donors
     */
    private FilteredList<Donor> setupTableColumnsAndData() {
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
        FilteredList<Donor> filteredData = new FilteredList<>(masterData, new Predicate<Donor>() {
            @Override
            public boolean test(Donor d) {
                return true;
            }
        });

        // 2. Set the filter Predicate whenever the filter changes.
        searchEntry.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            masterData.clear();
            masterData.addAll(SearchDonors.searchByName(newValue));

            filteredData.setPredicate(new Predicate<Donor>() {
                                          @Override
                                          public boolean test(Donor donor) {
                                              return true;
                                          }
                                      });
        });

        // wrap the FilteredList in a SortedList.
        SortedList<Donor> sortedData = new SortedList<>(filteredData);

        // bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty()
                .bind(donorDataTable.comparatorProperty());

        // add sorted (and filtered) data to the table.
        donorDataTable.setItems(sortedData);
        return filteredData;
    }


    /**
     * Sets the search textfield to listen for any changes and search for the entry on change
     *
     * @param filteredData the donors to be filtered/searched through
     */
    private void setupSearchingListener(FilteredList<Donor> filteredData) {
        // set the filter Predicate whenever the filter changes.
        searchEntry.textProperty()
                .addListener((observable, oldValue, newValue) -> filteredData.setPredicate(donor -> {
                    // If filter text is empty, display all persons.
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }

                    return SearchDonors.searchByName(newValue)
                            .contains(donor);

                }));
    }


    /**
     * Adds custom hover-over text to each row in the table
     */
    private void setupRowHoverOverText() {
        donorDataTable.setRowFactory(tv -> new TableRow<Donor>() {
            private Tooltip tooltip = new Tooltip();


            @Override
            public void updateItem(Donor donor, boolean empty) {
                super.updateItem(donor, empty);
                if (donor == null) {
                    setTooltip(null);
                }
                else if (donor.getDonations().isEmpty()) {

                    tooltip.setText(donor.getNameConcatenated() + ". No donations.");
                    setTooltip(tooltip);
                }
                else {
                    StringBuilder tooltipText = new StringBuilder(donor.getNameConcatenated() + ". Donations: ");
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


    /**
     * Adds all db data via constructor
     */
    public GUIClinicianSearchDonors() {
        masterData.addAll(Database.getDonors());
    }


    public void goToClinicianHome() {
        ScreenControl.activate("clinicianHome");
    }


    /**
     * Refreshes the table data
     */
    private void tableRefresh() {
        donorDataTable.refresh();
    }
}

