package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.Patient;
import model.Procedure;
import utility.GlobalEnums.Organ;

import java.util.HashSet;
import java.util.Set;

public class GUIAddProcedure implements IPopupable {

    @FXML
    public TextField summaryInput;

    @FXML
    public TextArea descriptionInput;

    @FXML
    public DatePicker dateInput;

    @FXML
    public MenuButton affectedInput;

    @FXML
    public AnchorPane addProcedureAnchorPane;

    private Patient patient;

    /**
     * Adds the procedure to the patient's list of procedures and closes the pop-up
     */
    @FXML
    public void addProcedure() {
        Set<Organ> affectedDonations = new HashSet<>();
        for (MenuItem organSelection : affectedInput.getItems()) {
            if (((CustomMenuItem) organSelection).getContent().getId() == null) {
                CheckBox selectionCheckBox = (CheckBox) ((CustomMenuItem) organSelection).getContent();
                if (selectionCheckBox.isSelected()) {
                    affectedDonations.add((Organ) Organ.getEnumFromString(selectionCheckBox.getText()));
                }
            }
        }
        Procedure procedure = new Procedure(summaryInput.getText(), descriptionInput.getText(), dateInput.getValue(), affectedDonations);
        patient.addProcedure(procedure);
        ((Stage) addProcedureAnchorPane.getScene().getWindow()).close();
    }

    /**
     * Sets the items in the affected donations dropdown to all the donations registered to the patient
     */
    private void setupDonations() {
        ObservableList<CustomMenuItem> donations = FXCollections.observableArrayList();
        for (Organ organ : patient.getDonations()) {
            CustomMenuItem organSelection = new CustomMenuItem(new CheckBox(organ.getValue()));
            organSelection.setHideOnClick(false);
            donations.add(organSelection);
        }
        if (donations.size() == 0) {
            Label noneLabel = new Label("None");
            noneLabel.setId("noneLabel");
            donations.add(new CustomMenuItem(noneLabel));
        }
        affectedInput.getItems().setAll(donations);
    }

    /**
     * Sets the viewed patient to the patient provided and sets up the screen accordingly
     * @param patient the patient to add a procedure to
     */
    public void setViewedPatient(Patient patient) {
        this.patient = patient;
        setupDonations();
    }

    public void closeWindow() {
        ((Stage) addProcedureAnchorPane.getScene().getWindow()).close();
    }
}
