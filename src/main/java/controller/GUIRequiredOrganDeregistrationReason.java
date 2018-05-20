package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.Patient;
import service.Database;
import utility.GlobalEnums;

import java.io.InvalidObjectException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GUIRequiredOrganDeregistrationReason {

    @FXML
    private DatePicker dateOfDeath;

    @FXML
    private Label dateOfDeathLabel;

    @FXML
    private ChoiceBox<String> reasons;

    @FXML
    private Button okButton;

    @FXML
    private Button okButton1;

    @FXML
    private Label pleaseSpecify;

    private UserControl userControl;

    @FXML
    private AnchorPane requiredOrganDeregistrationReasonPane;

    public void initialize() {
        userControl = new UserControl();
        populateDropdown();
        populateForm();
        dateOfDeath.setDisable(true);
        dateOfDeath.setVisible(false);
        dateOfDeathLabel.setDisable(true);
        dateOfDeathLabel.setVisible(false);
        okButton1.setDisable(true);
        okButton1.setVisible(false);
    }

    /**
     * Populates drop down menu that represent enum data of reasons for deregistering patient required organs
     */
    private void populateDropdown() {
        // Populate blood group drop down with values from the Blood groups enum
        List<String> deregistrationReasons = new ArrayList<>();
        for (GlobalEnums.DeregistrationReason reason : GlobalEnums.DeregistrationReason.values()) {
            deregistrationReasons.add(reason.getValue());
        }
        ObservableList<String> deregistrationReasonsOL = FXCollections.observableList(deregistrationReasons);
        reasons.setItems(deregistrationReasonsOL);
    }

    /**
     * Populates the scene controls with values from the patient object
     */
    private void populateForm() {
        dateOfDeath.setValue(LocalDate.now());
        reasons.setValue(GlobalEnums.DeregistrationReason.ERROR.getValue());
        pleaseSpecify.setText("Please specify a reason for removing " + userControl.getTargetPatient().getRemovedOrgan() + ": ");
    }

    /**
     * saves the reason why the clinician removed a organ from the patient required organs list
     */
    public void saveReason() {
        Stage reasonStage = (Stage)requiredOrganDeregistrationReasonPane.getScene().getWindow();
        reasonStage.close();
    }
}
