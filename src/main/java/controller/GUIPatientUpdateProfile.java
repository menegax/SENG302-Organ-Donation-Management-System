package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import model.Patient;
import service.PatientDataService;
import tornadofx.control.DateTimePicker;
import utility.GlobalEnums;
import utility.GlobalEnums.*;
import utility.undoRedo.Action;
import utility.undoRedo.StatesHistoryScreen;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Pattern;

import static utility.SystemLogger.systemLogger;
import static utility.UserActionHistory.userActions;

public class GUIPatientUpdateProfile extends UndoableController {

    @FXML
    private GridPane patientUpdateAnchorPane;

    @FXML
    private Label lastModifiedLbl;

    @FXML
    private TextField nhiTxt;

    @FXML
    private TextField firstnameTxt;

    @FXML
    private TextField lastnameTxt;

    @FXML
    private TextField middlenameTxt;

    @FXML
    private TextField preferrednameTxt;

    @FXML
    private RadioButton birthGenderMaleRadio;

    @FXML
    private RadioButton birthGenderFemaleRadio;

    @FXML
    private RadioButton preferredGenderManRadio;

    @FXML
    private RadioButton preferredGenderWomanRadio;

    @FXML
    private RadioButton preferredGenderNonBinaryRadio;

    @FXML
    private DatePicker dobDate;

    @FXML
    private DateTimePicker dateOfDeath;

    @FXML
    private TextField deathLocationTxt;

    @FXML
    private TextField street1Txt;

    @FXML
    private TextField street2Txt;

    @FXML
    private TextField suburbTxt;

    @FXML
    private TextField zipTxt;

    @FXML
    private TextField weightTxt;

    @FXML
    private TextField heightTxt;

    @FXML
    private ChoiceBox<String> regionDD;

    @FXML
    private ChoiceBox<String> bloodGroupDD;

    private Patient target;

    private Patient after;

    private PatientDataService patientDataService = new PatientDataService();

    /**
     * Initializes the profile update screen. Gets the logged in or viewed user and loads the user's profile.
     * Dropdown menus are populated. The enter key press event for saving changes is set up
     */
    public void initialize() {
        populateDropdowns();
        UserControl userControl = new UserControl();
        Object user = userControl.getLoggedInUser();
        if (user instanceof Patient) {
            loadProfile(((Patient) user).getNhiNumber());
            disablePatientElements();
        } else if (userControl.getTargetUser() != null) {
            loadProfile(((Patient) userControl.getTargetUser()).getNhiNumber());
        }
        // Enter key
        patientUpdateAnchorPane.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                saveProfileUpdater();
            }
        });
    }


    private void disablePatientElements() {
        deathLocationTxt.setDisable(true);
        dateOfDeath.setDisable(true);
    }


    /**
     * Populates drop down menus that represent enum data
     */
    private void populateDropdowns() {
        // Populate blood group drop down with values from the Blood groups enum
        List<String> bloodGroups = new ArrayList<>();
        for (BloodGroup bloodgroup : BloodGroup.values()) {
            bloodGroups.add(bloodgroup.getValue());
        }
        ObservableList<String> bloodGroupsOL = FXCollections.observableList(bloodGroups);
        bloodGroupDD.setItems(bloodGroupsOL);

        // Populate region drop down with values from the Regions enum
        List<String> regions = new ArrayList<>();
        for (Region region : Region.values()) {
            regions.add(region.getValue());
        }
        ObservableList<String> regionsOL = FXCollections.observableList(regions);
        regionDD.setItems(regionsOL);
    }

    /**
     * Loads the patient's profile into the gui
     *
     * @param nhi the NHI of the patient to load
     */
    private void loadProfile(String nhi) {
        Patient patient = patientDataService.getPatientByNhi(nhi);
        if (patient != null) {
            target = patient;
            after = (Patient) patient.deepClone();
            populateForm(after);

            controls = new ArrayList<Control>() {{
                add(nhiTxt);
                add(firstnameTxt);
                add(lastnameTxt);
                add(middlenameTxt);
                add(preferrednameTxt);
                add(bloodGroupDD);
                add(regionDD);
                add(dobDate);
                add(dateOfDeath);
                add(deathLocationTxt);
                add(birthGenderMaleRadio);
                add(birthGenderFemaleRadio);
                add(preferredGenderManRadio);
                add(preferredGenderWomanRadio);
                add(preferredGenderNonBinaryRadio);
                add(street1Txt);
                add(street2Txt);
                add(suburbTxt);
                add(weightTxt);
                add(heightTxt);
                add(zipTxt);
            }};
            statesHistoryScreen = new StatesHistoryScreen(controls, UndoableScreen.PATIENTUPDATEPROFILE);
        } else {
            userActions.log(Level.SEVERE, "Error loading logged in user", "attempted to edit the logged in user");
        }
    }


    /**
     * Populates the scene controls with values from the patient object
     *
     * @param patient the patient object whose attributes are used to load into the form
     */
    private void populateForm(Patient patient) {
        lastModifiedLbl.setText("Last Modified: " + patient.getModified());
        nhiTxt.setText(patient.getNhiNumber());
        firstnameTxt.setText(patient.getFirstName());
        lastnameTxt.setText(patient.getLastName());
        if (patient.getMiddleNames() != null) {
            for (String name : patient.getMiddleNames()) {
                middlenameTxt.setText(middlenameTxt.getText() + name + " ");
            }
        }
        preferrednameTxt.setText(patient.getPreferredName());
        if (patient.getBirthGender() != null) {
            switch (patient.getBirthGender().getValue()) {
                case "Male":
                    birthGenderMaleRadio.setSelected(true);
                    break;
                case "Female":
                    birthGenderFemaleRadio.setSelected(true);
                    break;
            }
        }
        if (patient.getPreferredGender() != null) {
            switch (patient.getPreferredGender().getValue()) {
                case "Man":
                    preferredGenderManRadio.setSelected(true);
                    break;
                case "Woman":
                    preferredGenderWomanRadio.setSelected(true);
                    break;
                case "Non-binary":
                    preferredGenderNonBinaryRadio.setSelected(true);
                    break;
            }
        }
        dobDate.setValue(patient.getBirth());
        dateOfDeath.setDateTimeValue(patient.getDeathDate());
        deathLocationTxt.setText(patient.getDeathLocation());
        if (patient.getStreet1() != null) {
            street1Txt.setText(patient.getStreet1());
        }
        if (patient.getStreet2() != null) {
            street2Txt.setText(patient.getStreet2());
        }
        if (patient.getSuburb() != null) {
            suburbTxt.setText(patient.getSuburb());
        }
        if (patient.getRegion() != null) {
            regionDD.setValue(patient.getRegion()
                    .getValue());
        }
        if (patient.getZip() != 0) {
            zipTxt.setText(String.valueOf(patient.getZip()));
            while (zipTxt.getText().length() < 4) {
                zipTxt.setText("0" + zipTxt.getText());
            }
        }
        weightTxt.setText(String.valueOf(patient.getWeight()));
        heightTxt.setText(String.valueOf(patient.getHeight()));
        if (patient.getBloodGroup() != null) {
            bloodGroupDD.setValue(patient.getBloodGroup()
                    .getValue());
        }
    }

    /**
     * Sets profile changes after checking each field for validity
     */
    @FXML
    public void saveProfileUpdater() {
        Boolean valid = true;
        StringBuilder invalidContent = new StringBuilder();

        valid = validateNhi(valid, invalidContent);
        valid = validateFirstName(valid, invalidContent);
        valid = validateLastName(valid, invalidContent);
        valid = validateMiddleNames(valid, invalidContent);
        valid = validatePreferredName(valid);
        valid = validateRegion(valid, invalidContent);
        valid = validateZip(valid, invalidContent);
        valid = validateWeight(valid, invalidContent);
        valid = validateHeight(valid, invalidContent);
        valid = validateBloodGroup(valid, invalidContent);
        valid = validateBirthDate(valid, invalidContent);
        valid = validateDeathDate(valid, invalidContent);
        valid = validateDeathLocation(valid, invalidContent);

        // if all are valid
        if (valid) {
            after.setNhiNumber(nhiTxt.getText());
            after.setFirstName(firstnameTxt.getText());
            after.setLastName(lastnameTxt.getText());

            if (middlenameTxt.getText()
                    .equals("")) {
                after.setMiddleNames(new ArrayList<>());
            } else {
                List<String> middlenames = Arrays.asList(middlenameTxt.getText()
                        .split(" "));
                ArrayList<String> middles = new ArrayList<>(middlenames);
                after.setMiddleNames(middles);
            }
            if (preferrednameTxt.getText() != null) {
                after.setPreferredName(preferrednameTxt.getText());
            }
            if (birthGenderMaleRadio.isSelected()) {
                after.setBirthGender(BirthGender.getEnumFromString("male"));
            }
            if (birthGenderFemaleRadio.isSelected()) {
                after.setBirthGender(BirthGender.getEnumFromString("female"));
            }
            if (preferredGenderManRadio.isSelected()) {
                after.setPreferredGender(PreferredGender.getEnumFromString("man"));
            }
            if (preferredGenderWomanRadio.isSelected()) {
                after.setPreferredGender(PreferredGender.getEnumFromString("woman"));
            }
            if (preferredGenderNonBinaryRadio.isSelected()) {
                after.setPreferredGender(PreferredGender.getEnumFromString("nonbinary"));
            }
            if (dobDate.getValue() != null) {
                after.setBirth(dobDate.getValue());
            }
//            if (dateOfDeath.getValue() != null) {
                after.setDeathDate(dateOfDeath.getDateTimeValue());
                after.setDeathLocation(deathLocationTxt.getText());
//            }
            after.setStreet1(street1Txt.getText());
            after.setStreet2(street2Txt.getText());
            after.setSuburb(suburbTxt.getText());
            if (regionDD.getValue() != null) {
                after.setRegion(Region.getEnumFromString(regionDD.getSelectionModel()
                        .getSelectedItem()));
            }
            if (zipTxt.getText() != null) {
                after.setZip(zipTxt.getText()
                        .equals("") ? 0 : Integer.parseInt(zipTxt.getText()));
            }
            if (weightTxt.getText() != null) {
                after.setWeight(Double.parseDouble(weightTxt.getText()));
            }
            if (heightTxt.getText() != null) {
                after.setHeight(Double.parseDouble(heightTxt.getText()));
            }
            if (bloodGroupDD.getValue() != null) {
                after.setBloodGroup(BloodGroup.getEnumFromString(bloodGroupDD.getSelectionModel()
                        .getSelectedItem()));
            }

            Action action = new Action(target, after);
            statesHistoryScreen.addAction(action);
            patientDataService.save(after);
            userActions.log(Level.INFO, "Successfully updated patient profile", new String[]{"Attempted to update patient profile", after.getNhiNumber()});
        } else {
            userActions.log(Level.WARNING, invalidContent.toString(), new String[]{"Attempted to update patient profile", after.getNhiNumber()});
        }
    }


    private Boolean validateDeathLocation(Boolean valid, StringBuilder invalidContent) {
        // death location
        if (deathLocationTxt.getText().length() != 0) {
            if (!deathLocationTxt.getText().matches(UIRegex.DEATH_LOCATION.getValue())) {
                valid = setInvalid(deathLocationTxt);
                invalidContent.append("Incorrect death location format. ");
            } else {
                setValid(deathLocationTxt);
            }
        }
        else if (dateOfDeath.getValue() != null) {
            // make sure if deathLoc set, that deathDate is also set otherwise deathLoc is invalid
            valid = setInvalid(deathLocationTxt);
            invalidContent.append("Death location required if death date set. ");
        } else {
            setValid(deathLocationTxt);
        }
        return valid;
    }


    private Boolean validateDeathDate(Boolean valid, StringBuilder invalidContent) {
        // date of death
        if (dateOfDeath.getValue() != null) {
            if ((dobDate.getValue() != null && dateOfDeath.getValue()
                    .isBefore(dobDate.getValue())) || dateOfDeath.getValue()
                    .isAfter(LocalDate.now())) {
                valid = setInvalid(dateOfDeath);
                invalidContent.append("Date of death must be a valid date either today or earlier and must be after date of birth. ");
            } else {
                setValid(dateOfDeath);
            }
        }
        else if (deathLocationTxt.getText().length() != 0) {
            // make sure if deathDate set, that deathLoc also set otherwise deathDate invalid
            valid = setInvalid(dateOfDeath);
            invalidContent.append("Death date required if death location set. ");
        } else {
            setValid(dateOfDeath);
        }
        return valid;
    }


    private Boolean validateBirthDate(Boolean valid, StringBuilder invalidContent) {
        // date of birth
        if (dobDate.getValue() != null) {
            if (dobDate.getValue().isAfter(LocalDate.now())) {
                valid = setInvalid(dobDate);
                invalidContent.append("Date of birth must be a valid date either today or earlier and must be before date of death. ");
            } else {
                setValid(dobDate);
            }
        } else {
            valid = setInvalid(dobDate);
        }
        return valid;
    }


    private Boolean validateBloodGroup(Boolean valid, StringBuilder invalidContent) {
        // blood group
        if (bloodGroupDD.getValue() != null) {
            String bgStr = bloodGroupDD.getValue();
            Enum bloodgroup = BloodGroup.getEnumFromString(bgStr);
            if (bloodgroup == null) {
                valid = setInvalid(bloodGroupDD);
                invalidContent.append("Blood group must be a valid selection. ");
            } else {
                setValid(bloodGroupDD);
            }
        } else {
            setValid(bloodGroupDD);
        }
        return valid;
    }


    private Boolean validateHeight(Boolean valid, StringBuilder invalidContent) {
        // height
        if (!Pattern.matches(UIRegex.HEIGHT.getValue(), heightTxt.getText())) {
            valid = setInvalid(heightTxt);
            invalidContent.append("Height must be a valid decimal number. ");
        } else {
            setValid(heightTxt);
        }
        return valid;
    }


    private Boolean validateWeight(Boolean valid, StringBuilder invalidContent) {
        // weight
        if (!Pattern.matches(UIRegex.WEIGHT.getValue(), weightTxt.getText())) {
            valid = setInvalid(weightTxt);
            invalidContent.append("Weight must be a valid decimal number. ");
        } else {
            setValid(weightTxt);
        }
        return valid;
    }


    private Boolean validateZip(Boolean valid, StringBuilder invalidContent) {
        // zip
        if (!zipTxt.getText().equals("")) {
            try {
                if (!Pattern.matches(UIRegex.ZIP.getValue(), zipTxt.getText())) {
                    valid = setInvalid(zipTxt);
                    invalidContent.append("Zip must be four digits. ");
                } else {
                    Integer.parseInt(zipTxt.getText());
                    setValid(zipTxt);
                }
            } catch (NumberFormatException e) {
                valid = setInvalid(zipTxt);
                invalidContent.append("Zip must be four digits. ");
            }
        } else {
            setValid(zipTxt);
        }
        return valid;
    }


    private Boolean validateRegion(Boolean valid, StringBuilder invalidContent) {
        // region
        if (regionDD.getSelectionModel()
                .getSelectedIndex() != -1) {
            Enum region = Region.getEnumFromString(regionDD.getSelectionModel()
                    .getSelectedItem());
            if (region == null) {
                valid = setInvalid(regionDD);
                invalidContent.append("Region must be a valid selection from the dropdown. ");
            } else {
                setValid(regionDD);
            }
        } else {
            setValid(regionDD);
        }
        return valid;
    }


    private Boolean validatePreferredName(Boolean valid) {
        // preferred name
        if (!Pattern.matches(UIRegex.FNAME.getValue(), preferrednameTxt.getText())) {
            valid = setInvalid(preferrednameTxt);
        } else {
            setValid(preferrednameTxt);
        }
        return valid;
    }


    private Boolean validateMiddleNames(Boolean valid, StringBuilder invalidContent) {
        //middle names
        if (!Pattern.matches(UIRegex.MNAME.getValue(), middlenameTxt.getText())) {
            valid = setInvalid(middlenameTxt);
            invalidContent.append("Middle name(s) must be letters, ., or -.");
        } else {
            setValid(middlenameTxt);
        }
        return valid;
    }


    private Boolean validateLastName(Boolean valid, StringBuilder invalidContent) {
        // last name
        if (!Pattern.matches(UIRegex.LNAME.getValue(), lastnameTxt.getText())) {
            valid = setInvalid(lastnameTxt);
            invalidContent.append("Last name must be letters, ., or -. ");
        } else {
            setValid(lastnameTxt);
        }
        return valid;
    }


    private Boolean validateFirstName(Boolean valid, StringBuilder invalidContent) {
        // first name
        if (!Pattern.matches(UIRegex.FNAME.getValue(), firstnameTxt.getText())) {
            valid = setInvalid(firstnameTxt);
            invalidContent.append("First name must be letters, ., or -. ");
        } else {
            setValid(firstnameTxt);
        }
        return valid;
    }


    private Boolean validateNhi(Boolean valid, StringBuilder invalidContent) {
        // nhi
        if (!Pattern.matches(UIRegex.NHI.getValue(), nhiTxt.getText().toUpperCase())) {
            valid = setInvalid(nhiTxt);
            invalidContent.append("NHI must be three letters followed by four numbers. ");
        }

        // if the nhi in use doesn't belong to the logged in patient already then it must be taken by someone else
        if (!patientDataService.getPatientByNhi(nhiTxt.getText()).getNhiNumber().equals(target.getNhiNumber())) {
            valid = setInvalid(nhiTxt);
            invalidContent.append("NHI is already in use. ");
        } else {
            setValid(nhiTxt);
        }
        return valid;
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

}
