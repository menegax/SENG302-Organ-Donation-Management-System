package controller;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.Clinician;
import model.Medication;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import model.Administrator;
import model.Clinician;
import model.Medication;
import model.Patient;
import org.apache.commons.lang3.StringUtils;
import service.AdministratorDataService;
import service.PatientDataService;
import service.interfaces.IPatientDataService;
import service.PatientDataService;
import service.interfaces.IPatientDataService;
import utility.GlobalEnums;
import utility.undoRedo.Action;
import utility.undoRedo.StatesHistoryScreen;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static utility.UserActionHistory.userActions;

/**
 * Patient profile page where in patient view, they may view their attributes, donating organs and required organs.
 * In clinician view, they can see the highlighted cell if the donating organ is also required by the patient.
 * This class loads and controls this view.
 */
public class GUIPatientProfile extends TargetedController{

    @FXML
    private GridPane patientProfilePane;

    @FXML
    private Button deleteButton;

    @FXML
    private Label nhiLbl;

    @FXML
    public Label vitalLbl1;

    @FXML
    private Label dobLbl;

    @FXML
    private Label dateOfDeathLabel;

    @FXML
    private Label deathLocation;

    @FXML
    private Label deathCity;

    @FXML
    private Label deathRegion;

    @FXML
    private Label age;

    @FXML
    private Label heightLbl;

    @FXML
    private Label weightLbl;

    @FXML
    private Label bmi;

    @FXML
    private Label bloodGroupLbl;

    @FXML
    private Label addLbl1;

    @FXML
    private Label addLbl3;

    @FXML
    private Label cityLbl;

    @FXML
    private Label addLbl4;

    @FXML
    private Label addLbl5;

    @FXML
    private Label zipLbl;

    @FXML
    private Label genderDeclaration;

    @FXML
    private Label genderStatus;

    @FXML
    private ListView receivingList;

    @FXML
    private Label receivingTitle;

    @FXML
    private Label prefGenderLbl;

    @FXML
    private Label firstNameLbl;

    @FXML
    private Label firstNameValue;

    @FXML
    private RowConstraints genderRow;

    @FXML
    private RowConstraints firstNameRow;

    @FXML
    private ListView donationList;
    @FXML
    private ListView<String> medList;

    private UserControl userControl = UserControl.getUserControl();

    private ListProperty<String> donatingListProperty = new SimpleListProperty<>();

    private ListProperty<String> receivingListProperty = new SimpleListProperty<>();

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    private ListProperty<String> medListProperty = new SimpleListProperty<>();



private UndoRedoControl undoRedoControl = UndoRedoControl.getUndoRedoControl();
    /**
     * Initialize the controller depending on whether it is a clinician viewing the patient or a patient viewing itself
     */
    public void load() {
        try {
            IPatientDataService patientDataService = new PatientDataService();
            if (userControl.getLoggedInUser() instanceof Patient) {
                if (patientDataService.getPatientByNhi(((Patient) userControl.getLoggedInUser()).getNhiNumber()).getRequiredOrgans().size() == 0) {
                    receivingList.setDisable(true);
                    receivingList.setVisible(false);
                    receivingTitle.setDisable(true);
                    receivingTitle.setVisible(false);
                /* Hide the columns that would hold the receiving listview - this results in the visible nodes filling
                   up the whole width of the scene */
                    for (int i = 9; i <= 11; i++) {
                        patientProfilePane.getColumnConstraints()
                                .get(i)
                                .setMaxWidth(0);
                    }
                }

                genderDeclaration.setVisible(false);
                genderStatus.setVisible(false);
                genderRow.setMaxHeight(0);
                firstNameLbl.setVisible(false);
                firstNameValue.setVisible(false);
                firstNameRow.setMaxHeight(0);

                deleteButton.setVisible(false);
                deleteButton.setDisable(true);
            } else {
                deleteButton.setVisible(true);
                deleteButton.setDisable(false);
            }
            assert target != null;
            Patient patientToLoad = patientDataService.getPatientByNhi(((Patient) target).getNhiNumber());
            loadProfile(patientToLoad);
        }
        catch (IOException e) {
            userActions.log(Level.SEVERE, "Cannot load patient profile", new String[]{"Attempted to load patient profile", ((Patient) target).getNhiNumber()});
        }
    }


    /**
     * Sets the patient's attributes for the scene's labels
     *
     * @exception InvalidObjectException if the nhi of the patient does not exist in the database
     */
    private void loadProfile(Patient patient) throws InvalidObjectException {
        loadBasicDetails(patient);
        loadDeathDetails(patient);
        loadBodyDetails(patient);
        loadAddressDetails(patient);
        loadDonatingOrgans(patient);
        loadRequiredOrgans(patient);
        loadMedications(patient);

        //list view styling/highlighting
        highlightListCell(donationList, true);
        highlightListCell(receivingList, false);
    }


    private void loadMedications(Patient patient) {
        //Populate current medication listview
        Collection<Medication> meds = patient.getCurrentMedications();
        List<String> medsMapped = meds.stream()
                .map(Medication::getMedicationName)
                .collect(Collectors.toList());
        medListProperty.setValue(FXCollections.observableArrayList(medsMapped));
        medList.itemsProperty()
                .bind(medListProperty);
    }


    private void loadDonatingOrgans(Patient patient) {
        if (patient.getRequiredOrgans() == null) {
            patient.setRequiredOrgans(new ArrayList<>());
        }
        Collection<GlobalEnums.Organ> organsD = patient.getDonations();
        List<String> organsMappedD = organsD.stream()
                .map(e -> StringUtils.capitalize(e.getValue()))
                .collect(Collectors.toList());
        donatingListProperty.setValue(FXCollections.observableArrayList(organsMappedD));
        donationList.itemsProperty()
                .bind(donatingListProperty);
        receivingList.itemsProperty()
                .bind(receivingListProperty);
    }


    private void loadRequiredOrgans(Patient patient) {
        Collection<GlobalEnums.Organ> organsR = patient.getRequiredOrgans();
        List<String> organsMappedR = organsR.stream()
                .map(e -> StringUtils.capitalize(e.getValue()))
                .collect(Collectors.toList());
        receivingListProperty.setValue(FXCollections.observableArrayList(organsMappedR));
    }


    private void loadBodyDetails(Patient patient) {
        age.setText(String.valueOf(patient.getAge()));
        heightLbl.setText(String.valueOf(patient.getHeight() + " m"));
        weightLbl.setText(String.valueOf(patient.getWeight() + " kg"));
        bmi.setText(String.valueOf(patient.getBmi()));
        bloodGroupLbl.setText(patient.getBloodGroup() == null ? "Not set" : patient.getBloodGroup()
                .getValue());
    }


    private void loadAddressDetails(Patient patient) {
        addLbl1.setText((patient.getStreetNumber() == null || patient.getStreetNumber()
                .length() == 0) ? "Not set" : patient.getStreetNumber());
        cityLbl.setText(patient.getStreetName() == null || patient.getStreetName().length() < 1 ? "Not set" : patient.getStreetName());
        addLbl3.setText((patient.getSuburb() == null || patient.getSuburb()
                .length() == 0) ? "Not set" : patient.getSuburb());
        addLbl4.setText((patient.getCity() == null || patient.getCity()
                .length() == 0) ? "Not set" : patient.getCity());
        addLbl5.setText(patient.getRegion() == null ? "Not set" : patient.getRegion()
                .getValue());
        if (patient.getZip() != 0) {
            zipLbl.setText(String.valueOf(patient.getZip()));
            while (zipLbl.getText()
                    .length() < 4) {
                zipLbl.setText("0" + addLbl5.getText());
            }
        }
        else {
            zipLbl.setText("Not set");
        }
    }


    private void loadDeathDetails(Patient patient) {
        dateOfDeathLabel.setText(patient.getDeathDate() == null ? "Not set" : patient.getDeathDate()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        deathLocation.setText(patient.getDeathStreet() == null || patient.getDeathStreet().length() == 0 ? "Not set" : patient.getDeathStreet());
        deathCity.setText(patient.getDeathCity() == null || patient.getDeathCity().length() == 0 ? "Not set" : patient.getDeathCity());
        deathRegion.setText(patient.getDeathRegion() == null ? "Not set" : patient.getDeathRegion()
                .getValue());
    }


    private void loadBasicDetails(Patient patient) {
        nhiLbl.setText(patient.getNhiNumber());
        firstNameValue.setText(patient.getFirstName());
        genderDeclaration.setText("Birth Gender: ");
        genderStatus.setText(patient.getBirthGender() == null ? "Not set" : patient.getBirthGender().getValue());
        prefGenderLbl.setText(patient.getPreferredGender() == null ? "Not set" : patient.getPreferredGender().getValue());
        vitalLbl1.setText(patient.getDeathDate() == null ? "Alive" : "Deceased");
        dobLbl.setText(patient.getBirth()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }


    /**
     * Highlights the listview cell if the organ donating is also required by the patient in clinician view. If in
     * patient view, the listview cells are just styled.
     *
     * @param listView    The listView that the cells being highlighted are in
     * @param isDonorList boolean for if the receiving organ is also in the donating list
     */
    private void highlightListCell(ListView<String> listView, boolean isDonorList) {
        listView.setCellFactory(column -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (userControl.getLoggedInUser() instanceof Clinician) {
                    if (isDonorList) {
                        setListInvalidStyle(item, receivingListProperty);
                    }
                    else {
                        setListInvalidStyle(item, donatingListProperty);
                    }
                }
                else {
                    this.setStyle("-fx-background-color: WHITE");
                    this.setText(item);
                }
            }

            private void setListInvalidStyle(String item, ListProperty<String> receivingListProperty) {
                if (receivingListProperty.contains(item)) {
                    this.getStyleClass()
                            .add("invalid");
                    this.setStyle("-fx-background-color: #e6b3b3");
                    this.setText(item);
                }
                else {
                    this.setStyle("-fx-background-color: WHITE");
                    this.setText(item);
                }
            }
        });
    }

    /**
     * Deletes the current profile from the HashSet in Database, not from disk, not until saved
     */
    public void deleteProfile() {
        Action action = new Action(target, null);
        new AdministratorDataService().deleteUser(target);
        undoRedoControl.addAction(action, GlobalEnums.UndoableScreen.ADMINISTRATORSEARCHUSERS);
        userActions.log(Level.INFO, "Successfully deleted patient profile", new String[]{"Attempted to delete patient profile", ((Patient) target).getNhiNumber()});
        ((Stage) patientProfilePane.getScene().getWindow()).close();
    }

}
