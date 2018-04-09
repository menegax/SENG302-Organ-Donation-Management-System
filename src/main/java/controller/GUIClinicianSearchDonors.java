package controller;

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
    private TableColumn<Donor, String> fullNameColumn;

    @FXML
    private TableColumn<Donor, Double> ageColumn;

    @FXML
    private TableColumn<Donor, String> genderColumn;

    @FXML
    private TableColumn<Donor, String> regionColumn;

    @FXML
    private TextField searchEntry;

    private ObservableList<Donor> searchedDonors;

    private ArrayList<Donor> donors;


    /**
     * Initialises the data within the table to all donors
     *
     * @param url Required parameter that is not used in the function
     * @param rb  Required parameter that is not used in the function
     */
    public void initialize(URL url, ResourceBundle rb) {
        donors = new ArrayList<>(Database.getDonors());
        // todo implement below
        //        loadData();
        //        searchEntry.textProperty().addListener((observable, oldValue, newValue) -> {
        //            search();
        //        });
        //
        //        donorDataTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
        //            @Override
        //            public void handle(MouseEvent click) {
        //                if (click.getClickCount() == 2 && donorDataTable.getSelectionModel().getSelectedItem() != null) {
        //                    DonorEditWindow donorEditWindow = new DonorEditWindow(donorDataTable.getSelectionModel().getSelectedItem());
        //                }
        //                calculateFields();
        //                donorDataTable.refresh();
        //            }
        //        });
        //
        //        donorDataTable.setRowFactory(tv -> new TableRow<Donor>() {
        //            private Tooltip tooltip = new Tooltip();
        //            @Override
        //            public void updateItem(Donor donor, boolean empty) {
        //                super.updateItem(donor, empty);
        //                if (donor == null) {
        //                    setTooltip(null);
        //                } else {
        //                    String tooltipText = donor.getName() + ". Donor: ";
        //                    for (String organ : donor.getOrgans()) {
        //                        tooltipText += organ + ", ";
        //                    }
        //                    tooltipText = tooltipText.substring(0, tooltipText.length() - 2);
        //                    tooltip.setText(tooltipText);
        //                    setTooltip(tooltip);
        //                }
        //            }
        //        });
    }
    //
    //    /**
    //     * Loads the current donor data into the donor data table
    //     */
    //    public void loadData() {
    //        for (Donor donor : donors) {
    //            if (donor.getMiddleNames() == null) {
    //                donor.setName(donor.getFirstName() + " " + donor.getLastName());
    //            } else {
    //                donor.setName(donor.getFirstName() + " " + donor.getMiddleNames() + " " + donor.getLastName());
    //            }
    //            if (donor.isDead()) {
    //                donor.setAge(Utilities.calculateAge(donor.getDeath(), donor.getBirth()));
    //            } else {
    //                donor.setAge(Utilities.calculateAge(new Date(), donor.getBirth()));
    //            }
    //        }
    //        searchedDonors = FXCollections.observableArrayList(donors);
    //        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
    //        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
    //        genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));
    //        regionColumn.setCellValueFactory(new PropertyValueFactory<>("region"));
    //        donorDataTable.setItems(searchedDonors);
    //        search();
    //    }
    //
    //    /**
    //     * Calculates the required fields in the table for the current searched donor list
    //     */
    //    public void calculateFields() {
    //        for (Donor donor : searchedDonors) {
    //            if (donor.getMiddleNames() == null) {
    //                donor.setName(donor.getFirstName() + " " + donor.getLastName());
    //            } else {
    //                donor.setName(donor.getFirstName() + " " + donor.getMiddleNames() + " " + donor.getLastName());
    //            }
    //            if (donor.isDead()) {
    //                donor.setAge(Utilities.calculateAge(donor.getDeath(), donor.getBirth()));
    //            } else {
    //                donor.setAge(Utilities.calculateAge(new Date(), donor.getBirth()));
    //            }
    //        }
    //    }
    //
    //    /**
    //     * Updates the donors displayed based on the the search criteria in the search entry
    //     */
    //    private void search() {
    //        searchedDonors.clear();
    //        for (int i = 0; i < donors.size() && i < 30; i++) {
    //            if (donors.get(i).getName().toLowerCase().contains(searchEntry.getText().toLowerCase()) && !donors.get(i).getChanged().equals("Delete")) {
    //                searchedDonors.add(donors.get(i));
    //            }
    //        }
    //        calculateFields();
    //    }
    //
    //    /**
    //     * Deletes the currently selected donor
    //     */
    //    @FXML
    //    public void deleteDonor() {
    //        Donor currentDonor = donorDataTable.getSelectionModel().getSelectedItem();
    //        if (currentDonor != null) {
    //            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    //            alert.setTitle("Delete donor " + currentDonor.getNHI());
    //            alert.setContentText("Are you sure you wish to delete this donor?");
    //            ButtonType buttonTypeNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
    //            ButtonType buttonTypeYes = new ButtonType("Yes");
    //            alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
    //            Optional<ButtonType> answer = alert.showAndWait();
    //            if (answer.get() == buttonTypeYes) {
    //                System.out.println("yes");
    //                String toLog = String.format("%s: delete donor: NHI -> %s;", new Date().toString(), currentDonor.getNHI());
    //                currentDonor.change("Delete", toLog);
    //                DataWriter dataWriter = new DataWriter();
    //                dataWriter.writeLog(toLog);
    //                LoginWindow.isSaved = false;
    //                loadData();
    //            }
    //        }
    //
    //        //        DonorWindow.saveDonors();
    //    }

    public void goToClinicianHome() {
        ScreenControl.activate("home");
    }
}

