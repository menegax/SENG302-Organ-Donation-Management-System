package controller;

import com.google.maps.model.LatLng;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.Disease;
import model.Patient;
import org.apache.commons.lang3.StringUtils;
import service.APIGoogleMaps;
import utility.GlobalEnums;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

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
    private Label pleaseSpecify;

    @FXML
    private Label reasonTitle;

    @FXML
    private Label curedLabel;

    @FXML
    private TextField locationDeathTxt;

    @FXML
    private MenuButton diseaseCured;

    private GlobalEnums.Organ organ;

    private UserControl userControl;

    private Patient target;

    @FXML
    private AnchorPane requiredOrganDeregistrationReasonPane;


    /**
     * Initializes the organ deregistration screen. Gets the target patient and sets optional elements as
     * disabled and not visible.
     */
    public void initialize() {
        userControl = new UserControl();
        target = (Patient) userControl.getTargetUser();
        System.out.println("target orgs: " + target.getRequiredOrgans()); //todo rm
        populateDropdown();
        populateForm();
        prepareHiddenForm();
    }

    private void prepareHiddenForm() {
        dateOfDeath.setDisable(true);
        dateOfDeath.setVisible(false);
        dateOfDeathLabel.setDisable(true);
        dateOfDeathLabel.setVisible(false);
        curedLabel.setDisable(true);
        curedLabel.setVisible(false);
        diseaseCured.setDisable(true);
        diseaseCured.setVisible(false);
        locationDeathTxt.setDisable(true);
        locationDeathTxt.setVisible(false);
    }


    /**
     * Populates drop down menu that represent enum data of reasons for deregistering patient required organs
     */
    private void populateDropdown() {
        // Populate blood group drop down with values from the deregistration reasons enum
        List<GlobalEnums.DeregistrationReason> deregistrationReasons = new ArrayList<>();
        if (target.getCurrentDiseases()
                .size() < 1) {
            for (GlobalEnums.DeregistrationReason reason : GlobalEnums.DeregistrationReason.values()) {
                if (reason != GlobalEnums.DeregistrationReason.CURED) {
                    deregistrationReasons.add(reason);
                }
            }
        }
        else {
            deregistrationReasons.addAll(Arrays.asList(GlobalEnums.DeregistrationReason.values()));
            Set<CustomMenuItem> diseaseItems = new HashSet<>();
            for (Disease disease : target.getCurrentDiseases()) {
                if (disease.getDiseaseState() != GlobalEnums.DiseaseState.CURED) {
                    CheckBox checkbox = new CheckBox(disease.getDiseaseName());
                    checkbox.setUserData(disease);
                    CustomMenuItem menuItem = new CustomMenuItem(checkbox);
                    menuItem.setHideOnClick(false);
                    diseaseItems.add(menuItem);
                }
            }
            diseaseCured.getItems()
                    .setAll(diseaseItems);
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
     * Sets the label with organ name
     *
     * @param organ the organ being set to label
     */
    public void setOrgan(GlobalEnums.Organ organ) {
        this.organ = organ;
        pleaseSpecify.setText("Please specify a reason for removing " + organ + ": ");
        reasonTitle.setText("Deregistration of " + StringUtils.capitalize(organ.toString()));
    }


    /**
     * set the reason selected
     */
    @FXML
    public void reasonSelected() {
        if (reasons.getValue() == GlobalEnums.DeregistrationReason.DIED) {
            curedLabel.setDisable(true);
            curedLabel.setVisible(false);
            diseaseCured.setDisable(true);
            diseaseCured.setVisible(false);
            dateOfDeath.setDisable(false);
            dateOfDeath.setVisible(true);
            dateOfDeathLabel.setDisable(false);
            dateOfDeathLabel.setVisible(true);
            locationDeathTxt.setDisable(false);
            locationDeathTxt.setVisible(true);
//            okButton.setLayoutY(169.0);
        }
        else if (reasons.getValue() == GlobalEnums.DeregistrationReason.CURED) {
            curedLabel.setDisable(false);
            curedLabel.setVisible(true);
            diseaseCured.setDisable(false);
            diseaseCured.setVisible(true);
            dateOfDeath.setDisable(true);
            dateOfDeath.setVisible(false);
            dateOfDeathLabel.setDisable(true);
            dateOfDeathLabel.setVisible(false);
//            okButton.setLayoutY(169.0);
        }
        else {
            dateOfDeath.setDisable(true);
            dateOfDeath.setVisible(false);
            dateOfDeathLabel.setDisable(true);
            dateOfDeathLabel.setVisible(false);
            curedLabel.setDisable(true);
            curedLabel.setVisible(false);
            diseaseCured.setDisable(true);
            diseaseCured.setVisible(false);
//            okButton.setLayoutY(100.0);
        }
    }


    /**
     * saves the reason why the clinician removed a organ from the patient required organs list
     */
    public void saveReason() {
        boolean valid = true;

        switch (reasons.getSelectionModel()
                .getSelectedItem()) {
            case ERROR:
                performErrorReasonActions();
                break;
            case CURED:
                performCuredReasonActions();
                break;
            case DIED:
                valid = validateAndPerformDiedReasonActions();
                break;
            case RECEIVED:
                performReceivedReasonActions();
                break;
        }

        if (valid) {
            Stage reasonStage = (Stage) reasons.getScene().getWindow();
            reasonStage.close();
        }
    }


    private void performErrorReasonActions() {
        userActions.log(Level.INFO,
                "Deregistered " + organ + " due to error",
                new String[] { "Attempted to deregister " + organ, target.getNhiNumber() });
    }


    private void performReceivedReasonActions() {
        userActions.log(Level.INFO,
                "Deregistered " + organ + " due to successful transplant",
                new String[] { "Attempted to deregister " + organ, target.getNhiNumber() });
    }


    private boolean validateAndPerformDiedReasonActions() {
        boolean valid = true;
        if (dateOfDeath.getValue()
                .isBefore(target.getBirth()) || dateOfDeath.getValue()
                .isAfter(LocalDate.now())) {
            valid = setInvalid(dateOfDeath);
        }
        else {
            setValid(dateOfDeath);
        }
        if (locationDeathTxt.getText()
                .equals("")) {
            valid = setInvalid(locationDeathTxt);
        }
        else {
            try {
                APIGoogleMaps apiGoogleMaps = APIGoogleMaps.getInstance();
                LatLng latLng = apiGoogleMaps.getLatLng(locationDeathTxt.getText()); //todo make the latLng var be set to patient profile instead of string version
                setValid(locationDeathTxt);
            }
            catch (Exception e) {
                valid = setInvalid(locationDeathTxt);
            }

            setValid(locationDeathTxt);
        }
        if (valid) {
            System.out.println("hey valid. organs: " + target.getRequiredOrgans());
            for (GlobalEnums.Organ organ : target.getRequiredOrgans()) {
                System.out.println("org" + organ);
                target.removeRequired(organ);
                userActions.log(Level.INFO, "Deregistered " + organ + " due to death", new String[] { "Attempted to deregister " + organ, target.getNhiNumber() });
            }
            target.setDeath(dateOfDeath.getValue());
            target.setDeathLocation(locationDeathTxt.getText()); //todo
        }
        return valid;
    }


    private void performCuredReasonActions() {
        List<Disease> selected = getSelectedDiseases();
        List<String> selectedStrings = selected.stream()
                .map(Disease::getDiseaseName)
                .collect(Collectors.toList());
        String diseaseCuredString = selected.size() == 0 ? "" : " Cured: " + String.join(",", selectedStrings);
        userActions.log(Level.INFO,
                "Deregistered " + organ + " due to cure." + diseaseCuredString,
                new String[] { "Attempted to deregister " + organ, target.getNhiNumber() });
        curePatientDiseases(selected);
    }


    /***
     * Applies the invalid class to the target control
     * @param target The target to add the class to
     */
    private boolean setInvalid(Control target) {
        target.getStyleClass()
                .add("invalid");
        return false;
    }


    /**
     * Removes the invalid class from the target control if it has it
     *
     * @param target The target to remove the class from
     */
    private void setValid(Control target) {
        target.getStyleClass()
                .remove("invalid");
    }


    /**
     * Fetches the list of diseases that have been selected within the dropdown menu
     *
     * @return The list of disease instances that are selected
     */
    private List<Disease> getSelectedDiseases() {
        List<Disease> selected = new ArrayList<>();
        for (MenuItem menuItem : diseaseCured.getItems()) {
            Node content = ((CustomMenuItem) menuItem).getContent();
            if (content instanceof CheckBox) {
                CheckBox checkbox = (CheckBox) content;
                if (checkbox.isSelected()) {
                    selected.add((Disease) checkbox.getUserData());
                }
            }
        }
        return selected;
    }


    /**
     * Sets the diseases in the patient to cured if their name matches a name in the selected diseases list
     */
    private void curePatientDiseases(List<Disease> selected) {
        for (Disease disease : selected) {
            disease.setDiseaseState(GlobalEnums.DiseaseState.CURED);
        }
    }
}
