package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import model.Donor;
import model.Human;
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


    private Human target;


    public void initialize() {
        List<String> bloodGroups =
                Arrays.asList("a positive", "a negative", "b positive", "b negative", "ab positive", "ab negative", "o positive", "o negative");
        ObservableList<String> bloodGroupsOL = FXCollections.observableList(bloodGroups);
        bloodGroupDD.setItems(bloodGroupsOL);
        loadProfile(ScreenControl.getLoggedInDonor().getNhiNumber());
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
            Human human = Database.getDonorByNhi(nhi);
            target = human;
            populateForm(human);
        }
        catch (InvalidObjectException e) {
            userActions.log(Level.SEVERE, "Error loading logged in user", "attempted to edit the logged in user");
        }
    }


    private void populateForm(Human human) {
        lastModifiedLbl.setText("Last Modified: " + human.getModified());
        nhiTxt.setText(human.getNhiNumber());
        firstnameTxt.setText(human.getFirstName());
        lastnameTxt.setText(human.getLastName());
        for (String name : human.getMiddleNames()) {
            middlenameTxt.setText(middlenameTxt.getText() + name + " ");
        }
        if (human.getGender() != null) {
            switch (human.getGender()
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
        dobDate.setValue(human.getBirth());
        if (human.getStreet1() != null) {
            street1Txt.setText(human.getStreet1());
        }
        if (human.getStreet2() != null) {
            street2Txt.setText(human.getStreet2());
        }
        if (human.getSuburb() != null) {
            suburbTxt.setText(human.getSuburb());
        }
        if (human.getRegion() != null) {
            regionTxt.setText(human.getRegion()
                    .getValue());
        }
        zipTxt.setText(String.valueOf(human.getZip()));
        weightTxt.setText(String.valueOf(human.getWeight()));
        heightTxt.setText(String.valueOf(human.getHeight()));
        if (human.getBloodGroup() != null) {
            bloodGroupDD.setValue(human.getBloodGroup()
                    .getValue());
        }
    }


    public void saveProfile() {
        Boolean valid = true;
        if (!Pattern.matches("[A-Z]{3}[0-9]{4}",
                nhiTxt.getText().toUpperCase())) {
            valid = false;
        }
        if (firstnameTxt.getText() == null) {
            valid = false;
        }
        if (lastnameTxt.getText() == null) {
            valid = false;
        }
        if (regionTxt.getText().length() > 0) {
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
            String bgStr = bloodGroupDD.getValue().toString().replace(' ', '_');
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
            new Alert(Alert.AlertType.CONFIRMATION, "User successfully updated", ButtonType.OK).showAndWait();
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
