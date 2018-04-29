package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import model.Donor;
import service.Database;
import utility.GlobalEnums;
import utility.undoRedo.StatesHistoryScreen;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Pattern;

import static utility.UserActionHistory.userActions;

public class GUIDonorUpdateProfile {

    @FXML
    private AnchorPane donorUpdateAnchorPane;

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
    private RadioButton genderMaleRadio;

    @FXML
    private RadioButton genderFemaleRadio;

    @FXML
    private RadioButton genderOtherRadio;

    @FXML
    private DatePicker dobDate;

    @FXML
    private DatePicker dateOfDeath;

    @FXML
    private TextField street1Txt;

    @FXML
    private TextField street2Txt;

    @FXML
    private TextField suburbTxt;

    @FXML
    private ChoiceBox<String> regionDD;

    @FXML
    private TextField zipTxt;

    @FXML
    private TextField weightTxt;

    @FXML
    private TextField heightTxt;

    @FXML
    private ChoiceBox<String> bloodGroupDD;

    private Donor target;

    private StatesHistoryScreen statesHistoryScreen;


    @FXML
    private void redo() {
        statesHistoryScreen.redo();
    }


    @FXML
    private void undo() {
        statesHistoryScreen.undo();
    }


    public void initialize() {
        List<String> bloodGroups =
                Arrays.asList("A positive", "A negative", "B positive", "B negative", "AB positive", "AB negative", "O positive", "O negative");
        ObservableList<String> bloodGroupsOL = FXCollections.observableList(bloodGroups);
        bloodGroupDD.setItems(bloodGroupsOL);
        populateDropdowns();
        loadProfile(ScreenControl.getLoggedInDonor()
                .getNhiNumber());

        // Enter key
        donorUpdateAnchorPane.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                saveProfile();
            }
        });
    }

    /**
     * Populates drop down menus that represent enum data
     */
    private void populateDropdowns() {
        // Populate blood group drop down with values from the Blood groups enum
        List<String> bloodGroups = new ArrayList<>();
        for (GlobalEnums.BloodGroup bloodgroup : GlobalEnums.BloodGroup.values()) {
            bloodGroups.add(bloodgroup.getValue());
        }
        ObservableList<String> bloodGroupsOL = FXCollections.observableList(bloodGroups);
        bloodGroupDD.setItems(bloodGroupsOL);

        // Populate region drop down with values from the Regions enum
        List<String> regions = new ArrayList<>();
        for (GlobalEnums.Region region : GlobalEnums.Region.values()) {
            regions.add(region.getValue());
        }
        ObservableList<String> regionsOL = FXCollections.observableList(regions);
        regionDD.setItems(regionsOL);

    }


    private void loadProfile(String nhi) {
        try {
            Donor donor = Database.getDonorByNhi(nhi);
            target = donor;
            populateForm(donor);

            ArrayList<Control> controls = new ArrayList<Control>() {{
                add(nhiTxt);
                add(firstnameTxt);
                add(lastnameTxt);
                add(middlenameTxt);
                add(bloodGroupDD);
                add(regionDD);
                add(dobDate);
                add(dateOfDeath);
                add(genderMaleRadio);
                add(genderFemaleRadio);
                add(genderOtherRadio);
                add(street1Txt);
                add(street2Txt);
                add(suburbTxt);
                add(weightTxt);
                add(heightTxt);
                add(zipTxt);
            }};
            statesHistoryScreen = new StatesHistoryScreen(donorUpdateAnchorPane, controls);

        } catch (InvalidObjectException e) {
            userActions.log(Level.SEVERE, "Error loading logged in user", "attempted to edit the logged in user");
            e.printStackTrace();
        }
    }



    private void populateForm(Donor donor) {
        lastModifiedLbl.setText("Last Modified: " + donor.getModified());
        nhiTxt.setText(donor.getNhiNumber());
        firstnameTxt.setText(donor.getFirstName());
        lastnameTxt.setText(donor.getLastName());
        for (String name : donor.getMiddleNames()) {
            middlenameTxt.setText(middlenameTxt.getText() + name + " ");
        }
        if (donor.getGender() != null) {
            switch (donor.getGender()
                    .getValue()) {
                case "male":
                    genderMaleRadio.setSelected(true);
                    break;
                case "female":
                    genderFemaleRadio.setSelected(true);
                    break;
                case "other":
                    genderOtherRadio.setSelected(true);
                    break;
            }
        }
        dobDate.setValue(donor.getBirth());
        if (donor.getStreet1() != null) {
            street1Txt.setText(donor.getStreet1());
        }
        if (donor.getStreet2() != null) {
            street2Txt.setText(donor.getStreet2());
        }
        if (donor.getSuburb() != null) {
            suburbTxt.setText(donor.getSuburb());
        }
        if (donor.getRegion() != null) {
            regionDD.setValue(donor.getRegion()
                    .getValue());
        }
        zipTxt.setText(String.valueOf(donor.getZip()));
        weightTxt.setText(String.valueOf(donor.getWeight()));
        heightTxt.setText(String.valueOf(donor.getHeight()));
        if (donor.getBloodGroup() != null) {
            bloodGroupDD.setValue(donor.getBloodGroup()
                    .getValue());
        }
    }

    /**
     * Checks for invalidity of a double used for height or weight.
     * Returns true if input is not a valid double or the input is a valid double with a value of less than 0.
     * @param input String input from text field
     * @return boolean is invalid
     */
    private boolean isInvalidDouble(String input) {
        try {
            double value = Double.parseDouble(input);
            return (value < 0);
        }
        catch (NumberFormatException e) {
            return true;
        }
    }

    public void saveProfile() {
        Boolean valid = true;
        if (!Pattern.matches("[A-Z]{3}[0-9]{4}",
                nhiTxt.getText()
                        .toUpperCase())) {
            valid = false;
        }
        if (firstnameTxt.getText() == null) {
            valid = false;
        }
        if (lastnameTxt.getText() == null) {
            valid = false;
        }
        if (regionDD.getSelectionModel().getSelectedIndex() != -1) {
            Enum region = GlobalEnums.Region.getEnumFromString(regionDD
                    .getSelectionModel().getSelectedItem());
            if (region == null) {
                valid = false;
            }
        }
        if (zipTxt.getText() != null) {
            try {
                int testZIP = Integer.parseInt(zipTxt.getText());
                if(testZIP < 1000 || testZIP > 9999) valid = false;
            }
            catch (NumberFormatException e) {
                valid = false;
            }
        }
        if (weightTxt.getText() != null) {
            if(isInvalidDouble(weightTxt.getText())) valid = false;
        }
        if (heightTxt.getText() != null) {
            if(isInvalidDouble(heightTxt.getText())) valid = false;
        }
        if (bloodGroupDD.getValue() != null) {
            String bgStr = bloodGroupDD.getValue()
                    .replace(' ', '_');
            Enum bloodgroup = GlobalEnums.BloodGroup.getEnumFromString(bgStr);
            if (bloodgroup == null) {
                valid = false;
            }
        }
        if (valid) {
            target.setNhiNumber(nhiTxt.getText());
            target.setFirstName(firstnameTxt.getText());
            target.setLastName(lastnameTxt.getText());
            List<String> middlenames = Arrays.asList(middlenameTxt.getText()
                    .split(" "));
            ArrayList<String> middles = new ArrayList<>(middlenames);
            target.setMiddleNames(middles);

            if (genderMaleRadio.isSelected()) {
                target.setGender((GlobalEnums.Gender) GlobalEnums.Gender.getEnumFromString("male"));
            }
            if (genderFemaleRadio.isSelected()) {
                target.setGender((GlobalEnums.Gender) GlobalEnums.Gender.getEnumFromString("female"));
            }
            if (genderOtherRadio.isSelected()) {
                target.setGender((GlobalEnums.Gender) GlobalEnums.Gender.getEnumFromString("other"));
            }
            if (dobDate.getValue() != null) {
                target.setBirth(dobDate.getValue());
            }
            if (dateOfDeath.getValue() != null) {
                target.setDeath(dateOfDeath.getValue());
            }
            if (street1Txt.getText()
                    .length() > 0) {
                target.setStreet1(street1Txt.getText());
            }
            if (street2Txt.getText()
                    .length() > 0) {
                target.setStreet2(street2Txt.getText());
            }
            if (suburbTxt.getText()
                    .length() > 0) {
                target.setSuburb(suburbTxt.getText());
            }
            if (regionDD.getValue() != null) {
                target.setRegion((GlobalEnums.Region) GlobalEnums.Region.getEnumFromString(regionDD.getSelectionModel()
                        .getSelectedItem()));
            }
            if (zipTxt.getText() != null) {
                target.setZip(Integer.parseInt(zipTxt.getText()));
            }
            if (weightTxt.getText() != null) {
                target.setWeight(Double.parseDouble(weightTxt.getText()));
            }
            if (heightTxt.getText() != null) {
                target.setHeight(Double.parseDouble(heightTxt.getText()));
            }
            if (bloodGroupDD.getValue() != null) {
                target.setBloodGroup((GlobalEnums.BloodGroup) GlobalEnums.BloodGroup.getEnumFromString(bloodGroupDD
                        .getSelectionModel().getSelectedItem()));
            }
            new Alert(Alert.AlertType.INFORMATION, "Donor successfully updated", ButtonType.OK).showAndWait();
            Database.saveToDisk();
            goBackToProfile();
        } else {
            new Alert(Alert.AlertType.WARNING, "Invalid fields", ButtonType.OK).showAndWait();
        }
    }


    public void goBackToProfile() {
        ScreenControl.removeScreen("donorProfile");
        try {
            ScreenControl.addScreen("donorProfile", FXMLLoader.load(getClass().getResource("/scene/donorProfile.fxml")));
            ScreenControl.activate("donorProfile");
        } catch (IOException e) {
            userActions.log(Level.SEVERE, "Error loading profile screen", "attempted to navigate from the edit page to the profile page");
            new Alert(Alert.AlertType.WARNING, "ERROR loading profile page", ButtonType.OK).showAndWait();
        }
    }

}
