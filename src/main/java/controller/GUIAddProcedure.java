package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.Patient;
import model.Procedure;
import utility.GlobalEnums.Organ;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

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

    @FXML
    public void addProcedure() {
        Set <Organ> affectedDonations = new HashSet <>();
        for (MenuItem organSelection : affectedInput.getItems()) {
            if (((CustomMenuItem) organSelection).getContent().getId() == null) {
                CheckBox selectionCheckBox = (CheckBox) ((CustomMenuItem) organSelection).getContent();
                if (selectionCheckBox.isSelected()) {
                    affectedDonations.add( (Organ) Organ.getEnumFromString( selectionCheckBox.getText() ) );
                }
            }
        }
        if (validateInputs(summaryInput.getText(), descriptionInput.getText(), dateInput.getValue(), affectedDonations)){
            Procedure procedure = new Procedure( summaryInput.getText(), descriptionInput.getText(),
                    dateInput.getValue(), affectedDonations );
            patient.addProcedure( procedure );
            ((Stage) addProcedureAnchorPane.getScene().getWindow()).close();
        } else {
            System.out.println( affectedDonations.size() );
            Alert alert = new Alert(Alert.AlertType.ERROR, "Field input(s) are invalid. " +
                    "Date must not be before patients DOB, there must be an affected organ, summary and description " +
                    "must be at least one char/int");
            alert.show();
        }
    }

    /**
     * Validates the input fields summary, description, date and organs on creation of a procedure
     * @param summary The procedure summary string
     * @param description The procedure description string
     * @param date The date of the procedure
     * @param organs The selected organ(s) affected by the procedure
     * @return True if date is not before patient DOB, one or more organs, or summary/description are more than 1 chars
     */
    private Boolean validateInputs(String summary, String description, LocalDate date, Set <Organ> organs) {
        return !date.isBefore( patient.getBirth() ) && summary.length() >= 1 && description.length() >= 1 &&
                organs.size() != 0 && Pattern.matches("[A-Za-z0-9- ]+", summary) &&
                Pattern.matches("[A-Za-z0-9- ]+", description);
    }

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

    public void setViewedPatient(Patient patient) {
        this.patient = patient;
        setupDonations();
    }

    public void closeWindow() {
        ((Stage) addProcedureAnchorPane.getScene().getWindow()).close();
    }
}
