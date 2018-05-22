package controller;

import com.sun.xml.internal.ws.util.StringUtils;
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
import utility.GlobalEnums;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

public class GUIRequiredOrganDeregistrationReason {

    @FXML
    private DatePicker dateOfDeath;

    @FXML
    private Label dateOfDeathLabel;

    @FXML
    private ChoiceBox<GlobalEnums.DeregistrationReason> reasons;

    @FXML
    private Button okButton;

    @FXML
    private Button okButton1;

    @FXML
    private Label pleaseSpecify;

    @FXML
    private Label reasonTitle;

    private GlobalEnums.Organ organ;

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
        List<GlobalEnums.DeregistrationReason> deregistrationReasons = new ArrayList<>();
        for (GlobalEnums.DeregistrationReason reason : GlobalEnums.DeregistrationReason.values()) {
            deregistrationReasons.add(reason);
        }
        ObservableList<GlobalEnums.DeregistrationReason> deregistrationReasonsOL = FXCollections.observableList(deregistrationReasons);
        reasons.setItems(deregistrationReasonsOL);
    }

    /**
     * Populates the scene controls with values from the patient object
     */
    private void populateForm() {
        dateOfDeath.setValue(LocalDate.now());
        reasons.setValue(GlobalEnums.DeregistrationReason.ERROR);
    }

    /**
     * sets the label with organ name
     */
    public void setOrgan(GlobalEnums.Organ organ) {
        this.organ = organ;
        pleaseSpecify.setText("Please specify a reason for removing " + organ + ": ");
        reasonTitle.setText("Deregistration of " + StringUtils.capitalize(organ.toString()));
    }

    /**
     * saves the reason why the clinician removed a organ from the patient required organs list
     */
    public void saveReason() {
        GlobalEnums.DeregistrationReason reason = reasons.getSelectionModel().getSelectedItem();
        Patient target = userControl.getTargetPatient();
        if (reason == GlobalEnums.DeregistrationReason.ERROR) {
            userActions.log(Level.INFO, "Deregistered " + organ + " due to error", new String[]{"Attempted to deregister " + organ, target.getNhiNumber()});
        } else if (reason == GlobalEnums.DeregistrationReason.CURED) {
            userActions.log(Level.INFO, "Deregistered " + organ + " due to cure", new String[]{"Attempted to deregister " + organ, target.getNhiNumber()});
        } else if (reason == GlobalEnums.DeregistrationReason.DIED) {
            userActions.log(Level.INFO, "Deregistered " + organ + " due to death", new String[]{"Attempted to deregister " + organ, target.getNhiNumber()});
        } else if (reason == GlobalEnums.DeregistrationReason.RECEIVED) {
            userActions.log(Level.INFO, "Deregistered " + organ + " due to successful transplant", new String[]{"Attempted to deregister " + organ, target.getNhiNumber()});
        }
        Stage reasonStage = (Stage)requiredOrganDeregistrationReasonPane.getScene().getWindow();
        reasonStage.close();
        //GUIPatientUpdateRequirements.setClosed(true);
    }
}
