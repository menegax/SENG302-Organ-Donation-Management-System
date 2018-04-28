package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import model.Donor;
import utility.undoRedo.StatesHistoryScreen;
import service.Database;
import utility.GlobalEnums;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Pattern;

import static utility.UserActionHistory.userActions;

public class GUIDonorProfileUpdate {

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
    private TextField street1Txt;

    @FXML
    private TextField street2Txt;

    @FXML
    private TextField suburbTxt;

    @FXML
    private TextField regionTxt;

    @FXML
    private TextField zipTxt;

    @FXML
    private TextField weightTxt;

    @FXML
    private TextField heightTxt;

    @FXML
    private ChoiceBox<String> bloodGroupDD;

    @FXML
    private AnchorPane donorUpdateAnchorPane;

    private StatesHistoryScreen screenHistory;


    @FXML
    private void undo() {
        screenHistory.undo();
    }


    @FXML
    private void redo() {
        screenHistory.redo();
    }


    private Donor target;


    public void initialize() {
        List<String> bloodGroups =
                Arrays.asList("a positive", "a negative", "b positive", "b negative", "ab positive", "ab negative", "o positive", "o negative");
        ObservableList<String> bloodGroupsOL = FXCollections.observableList(bloodGroups);
        bloodGroupDD.setItems(bloodGroupsOL);
        loadProfile(ScreenControl.getLoggedInDonor()
                .getNhiNumber());
        setUpStateHistory();
    }


    private void setUpStateHistory() {
        ArrayList<Control> elements = new ArrayList<Control>() {{
            add(nhiTxt);
            add(firstnameTxt);
            add(lastnameTxt);
            add(middlenameTxt);
            add(street1Txt);
            add(street2Txt);
            add(suburbTxt);
            add(regionTxt);
            add(zipTxt);
            add(weightTxt);
            add(heightTxt);
            add(genderFemaleRadio);
            add(genderOtherRadio);
            add(genderMaleRadio);
            add(dobDate);
            add(bloodGroupDD);
        }};
        screenHistory = new StatesHistoryScreen(donorUpdateAnchorPane, elements);
    }


    private void loadProfile(String nhi) {
        try {
            Donor donor = Database.getDonorByNhi(nhi);
            target = donor;
            populateForm(donor);
        }
        catch (InvalidObjectException e) {
            userActions.log(Level.SEVERE, "Error loading logged in user", "attempted to edit the logged in user");
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
            regionTxt.setText(donor.getRegion()
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
        if (regionTxt.getText()
                .length() > 0) {
            Enum region = GlobalEnums.Region.getEnumFromString(regionTxt.getText());
            if (region == null) {
                valid = false;
            }
        }
        if (zipTxt.getText() != null) {
            try {
                Integer.parseInt(zipTxt.getText());
            }
            catch (NumberFormatException e) {
                valid = false;
            }
        }
        if (weightTxt.getText() != null) {
            try {
                Double.parseDouble(weightTxt.getText());
            }
            catch (NumberFormatException e) {
                valid = false;
            }
        }
        if (heightTxt.getText() != null) {
            try {
                Double.parseDouble(heightTxt.getText());
            }
            catch (NumberFormatException e) {
                valid = false;
            }
        }
        if (bloodGroupDD.getValue() != null) {
            String bgStr = bloodGroupDD.getValue()
                    .toString()
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
            ArrayList<String> middles = new ArrayList<String>();
            middles.addAll(middlenames);
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
            if (regionTxt.getText()
                    .length() > 0) {
                target.setRegion((GlobalEnums.Region) GlobalEnums.Region.getEnumFromString(regionTxt.getText()));
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
                target.setBloodGroup((GlobalEnums.BloodGroup) GlobalEnums.BloodGroup.getEnumFromString(bloodGroupDD.getValue()
                        .toString()
                        .replace(' ', '_')));
            }
            new Alert(Alert.AlertType.CONFIRMATION, "Donor successfully updated", ButtonType.OK).showAndWait();
            goBackToProfile();
        }
        else {
            new Alert(Alert.AlertType.WARNING, "Invalid fields", ButtonType.OK).showAndWait();
        }
    }


    public void goBackToProfile() {
        ScreenControl.removeScreen("donorProfile");
        try {
            ScreenControl.addScreen("donorProfile", FXMLLoader.load(getClass().getResource("/scene/donorProfile.fxml")));
            ScreenControl.activate("donorProfile");
        }
        catch (IOException e) {
            userActions.log(Level.SEVERE, "Error loading profile screen", "attempted to navigate from the edit page to the profile page");
            new Alert(Alert.AlertType.WARNING, "ERROR loading profile page", ButtonType.OK).showAndWait();
        }
    }

}
