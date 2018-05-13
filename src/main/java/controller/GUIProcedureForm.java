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

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class GUIProcedureForm implements IPopupable {

    @FXML
    public TextField summaryInput;

    @FXML
    public TextArea descriptionInput;

    @FXML
    public DatePicker dateInput;

    @FXML
    public MenuButton affectedInput;

    @FXML
    public AnchorPane procedureAnchorPane;

    private Patient patient;
    private boolean isEditInstance = false;
    private Procedure procedure; //The Procedure that is being edited (null in the case of adding a procedure)

    /**
     * Used to signify that the instance is for editing a procedure
     */
    void setupEditing(Procedure procedure) {
        isEditInstance = true;
        this.procedure = procedure;
        loadProcedure();
    }

    /**
     * Loads the details of an existing procedure into the form inputs
     */
    private void loadProcedure() {
        summaryInput.setText(procedure.getSummary());
        descriptionInput.setText(procedure.getDescription());
        dateInput.setValue(procedure.getDate());

        //Select the organ checkboxes to match the target procedures affectedOrgan set
        for (MenuItem organSelection : affectedInput.getItems()) {
            if (((CustomMenuItem) organSelection).getContent().getId() == null) {
                CheckBox organCheckBox = (CheckBox) ((CustomMenuItem) organSelection).getContent();
                Organ organ = (Organ) Organ.getEnumFromString(organCheckBox.getText());
                if (procedure.getAffectedDonations().contains(organ)) {
                    organCheckBox.setSelected(true);
                }
            }
        }
    }

    /**
     * Adds the procedure to the patient's list of procedures and closes the pop-up
     */
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
            ((Stage) procedureAnchorPane.getScene().getWindow()).close();
        } else {
            System.out.println( affectedDonations.size() );
            Alert alert = new Alert(Alert.AlertType.ERROR, "Field input(s) are invalid. " +
                    "Date must not be before patients DOB, there must be an affected organ, summary and description " +
                    "must each contain at least one of only alphabetic or numerical characters, hyphens or spaces");
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
                Pattern.matches("[A-Za-z0-9- ]+", summary) && !summary.substring(0,1).equals(" ") &&
                Pattern.matches("[A-Za-z0-9- ]+", description) && !description.substring( 0 ,1).equals(" ") &&
                organs.size() != 0;
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
        ((Stage) procedureAnchorPane.getScene().getWindow()).close();
    }
}
