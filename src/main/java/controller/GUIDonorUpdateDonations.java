package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.input.KeyCode;
import javafx.scene.control.Control;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.AnchorPane;
import model.Donor;
import utility.undoRedo.StatesHistoryScreen;
import service.Database;
import utility.GlobalEnums;

import javafx.scene.control.CheckBox;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

public class GUIDonorUpdateDonations implements IPopupable {

    @FXML
    private CheckBox liverCB;

    @FXML
    private CheckBox kidneyCB;

    @FXML
    private CheckBox pancreasCB;

    @FXML
    private CheckBox heartCB;

    @FXML
    private CheckBox lungCB;

    @FXML
    private CheckBox intestineCB;

    @FXML
    private CheckBox corneaCB;

    @FXML
    private CheckBox middleearCB;

    @FXML
    private CheckBox skinCB;

    @FXML
    private CheckBox boneCB;

    @FXML
    private CheckBox bonemarrowCB;

    @FXML
    private CheckBox connectivetissueCB;


    @FXML
    private AnchorPane donorDonationsAnchorPane;


    @FXML
    private void redo() {
        statesHistoryScreen.redo();
    }


    @FXML
    private void undo() {
        statesHistoryScreen.undo();
    }


    private Donor target;

    private StatesHistoryScreen statesHistoryScreen;


    public void initialize() {
        if (ScreenControl.getLoggedInDonor() != null) {
            loadProfile(ScreenControl.getLoggedInDonor()
                    .getNhiNumber());
        }

        // Enter key triggers log in
        donorDonationsAnchorPane.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                saveDonations();
            }
            else if (KeyCodeCombination.keyCombination("Ctrl+Z").match(e)) {
                undo();
            }
            else if (KeyCodeCombination.keyCombination("Ctrl+Y").match(e)) {
                redo();
            }
        });
    }

    public void setViewedDonor(Donor donor) {
        target = donor;
        loadProfile(donor.getNhiNumber());
    }


    private void loadProfile(String nhi) {
        try {
            Donor donor = Database.getDonorByNhi(nhi);
            target = donor;
            populateForm(donor);
        }
        catch (InvalidObjectException e) {
            userActions.log(Level.SEVERE, "Error loading logged in user", "attempted to manage the donations for logged in user");
        }
        ArrayList<Control> controls = new ArrayList<Control>() {{
            add(liverCB);
            add(kidneyCB);
            add(pancreasCB);
            add(heartCB);
            add(lungCB);
            add(intestineCB);
            add(corneaCB);
            add(middleearCB);
            add(skinCB);
            add(boneCB);
            add(bonemarrowCB);
            add(connectivetissueCB);
        }};
        statesHistoryScreen = new StatesHistoryScreen(donorDonationsAnchorPane, controls);
    }



    private void populateForm(Donor donor) {
        ArrayList<GlobalEnums.Organ> organs = donor.getDonations();
        if (organs.contains(GlobalEnums.Organ.LIVER)) {
            liverCB.setSelected(true);
        }
        if (organs.contains(GlobalEnums.Organ.KIDNEY)) {
            kidneyCB.setSelected(true);
        }
        if (organs.contains(GlobalEnums.Organ.PANCREAS)) {
            pancreasCB.setSelected(true);
        }
        if (organs.contains(GlobalEnums.Organ.HEART)) {
            heartCB.setSelected(true);
        }
        if (organs.contains(GlobalEnums.Organ.LUNG)) {
            lungCB.setSelected(true);
        }
        if (organs.contains(GlobalEnums.Organ.INTESTINE)) {
            intestineCB.setSelected(true);
        }
        if (organs.contains(GlobalEnums.Organ.CORNEA)) {
            corneaCB.setSelected(true);
        }
        if (organs.contains(GlobalEnums.Organ.MIDDLEEAR)) {
            middleearCB.setSelected(true);
        }
        if (organs.contains(GlobalEnums.Organ.SKIN)) {
            skinCB.setSelected(true);
        }
        if (organs.contains(GlobalEnums.Organ.BONE)) {
            boneCB.setSelected(true);
        }
        if (organs.contains(GlobalEnums.Organ.BONE_MARROW)) {
            bonemarrowCB.setSelected(true);
        }
        if (organs.contains(GlobalEnums.Organ.CONNECTIVETISSUE)) {
            connectivetissueCB.setSelected(true);
        }
    }


    public void saveDonations() {
        if (liverCB.isSelected()) {
            target.addDonation(GlobalEnums.Organ.LIVER);
            userActions.log(Level.INFO, "Added liver to donor donations", "Attempted to add donation to a donor");
        }
        else {
            target.removeDonation(GlobalEnums.Organ.LIVER);
            userActions.log(Level.INFO, "Removed liver from donor donations", "Attempted to remove donation from a donor");

        }
        if (kidneyCB.isSelected()) {
            target.addDonation(GlobalEnums.Organ.KIDNEY);
            userActions.log(Level.INFO, "Added kidney to donor donations", "Attempted to add donation to a donor");

        }
        else {
            target.removeDonation(GlobalEnums.Organ.KIDNEY);
            userActions.log(Level.INFO, "Removed kidney from donor donations", "Attempted to remove donation from a donor");

        }
        if (pancreasCB.isSelected()) {
            target.addDonation(GlobalEnums.Organ.PANCREAS);
            userActions.log(Level.INFO, "Added pancreas to donor donations", "Attempted to add donation to a donor");

        }
        else {
            target.removeDonation(GlobalEnums.Organ.PANCREAS);
            userActions.log(Level.INFO, "Removed pancreas from donor donations", "Attempted to remove donation from a donor");

        }
        if (heartCB.isSelected()) {
            target.addDonation(GlobalEnums.Organ.HEART);
            userActions.log(Level.INFO, "Added heart to donor donations", "Attempted to add donation to a donor");

        }
        else {
            target.removeDonation(GlobalEnums.Organ.HEART);
            userActions.log(Level.INFO, "Removed heart from donor donations", "Attempted to remove donation from a donor");

        }
        if (lungCB.isSelected()) {
            target.addDonation(GlobalEnums.Organ.LUNG);
            userActions.log(Level.INFO, "Added lung to donor donations", "Attempted to add donation to a donor");

        }
        else {
            target.removeDonation(GlobalEnums.Organ.LUNG);
            userActions.log(Level.INFO, "Removed lung from donor donations", "Attempted to remove donation from a donor");

        }
        if (intestineCB.isSelected()) {
            target.addDonation(GlobalEnums.Organ.INTESTINE);
            userActions.log(Level.INFO, "Added intestine to donor donations", "Attempted to add donation to a donor");

        }
        else {
            target.removeDonation(GlobalEnums.Organ.INTESTINE);
            userActions.log(Level.INFO, "Removed intestine from donor donations", "Attempted to remove donation from a donor");

        }
        if (corneaCB.isSelected()) {
            target.addDonation(GlobalEnums.Organ.CORNEA);
            userActions.log(Level.INFO, "Added cornea to donor donations", "Attempted to add donation to a donor");

        }
        else {
            target.removeDonation(GlobalEnums.Organ.CORNEA);
            userActions.log(Level.INFO, "Removed cornea from donor donations", "Attempted to remove donation from a donor");

        }
        if (middleearCB.isSelected()) {
            target.addDonation(GlobalEnums.Organ.MIDDLEEAR);
            userActions.log(Level.INFO, "Added middle ear to donor donations", "Attempted to add donation to a donor");

        }
        else {
            target.removeDonation(GlobalEnums.Organ.MIDDLEEAR);
            userActions.log(Level.INFO, "Removed middle ear from donor donations", "Attempted to remove donation from a donor");

        }
        if (skinCB.isSelected()) {
            target.addDonation(GlobalEnums.Organ.SKIN);
            userActions.log(Level.INFO, "Added skin to donor donations", "Attempted to add donation to a donor");

        }
        else {
            target.removeDonation(GlobalEnums.Organ.SKIN);
            userActions.log(Level.INFO, "Removed skin from donor donations", "Attempted to remove donation from a donor");

        }
        if (boneCB.isSelected()) {
            target.addDonation(GlobalEnums.Organ.BONE);
            userActions.log(Level.INFO, "Added bone to donor donations", "Attempted to add donation to a donor");

        }
        else {
            target.removeDonation(GlobalEnums.Organ.BONE);
            userActions.log(Level.INFO, "Removed bone from donor donations", "Attempted to remove donation from a donor");

        }
        if (bonemarrowCB.isSelected()) {
            target.addDonation(GlobalEnums.Organ.BONE_MARROW);
            userActions.log(Level.INFO, "Added bone marrow to donor donations", "Attempted to add donation to a donor");

        }
        else {
            target.removeDonation(GlobalEnums.Organ.BONE_MARROW);
            userActions.log(Level.INFO, "Removed bone marrow from donor donations", "Attempted to remove donation from a donor");

        }
        if (connectivetissueCB.isSelected()) {
            target.addDonation(GlobalEnums.Organ.CONNECTIVETISSUE);
            userActions.log(Level.INFO, "Added connective tissue to donor donations", "Attempted to add donation to a donor");

        }
        else {
            target.removeDonation(GlobalEnums.Organ.CONNECTIVETISSUE);
            userActions.log(Level.INFO, "Removed connective tissue from donor donations", "Attempted to remove donation from a donor");

        }
        Database.saveToDisk();
        goToProfile();
    }


    public void goToProfile() {
        if (ScreenControl.getLoggedInDonor() != null) {
            ScreenControl.removeScreen("donorProfile");
            try {
                ScreenControl.addScreen("donorProfile", FXMLLoader.load(getClass().getResource("/scene/donorProfile.fxml")));
                ScreenControl.activate("donorProfile");
            } catch (IOException e) {
                userActions.log(Level.SEVERE, "Error loading profile screen", "attempted to navigate from the donation page to the profile page");
                new Alert(Alert.AlertType.WARNING, "Error loading profile page", ButtonType.OK).show();
            }
        } else {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scene/donorProfile.fxml"));
            try {
                ScreenControl.loadPopUpPane(donorDonationsAnchorPane.getScene(), fxmlLoader, target);
            } catch (IOException e) {
                userActions.log(Level.SEVERE, "Error loading profile screen in popup", "attempted to navigate from the donation page to the profile page in popup");
                new Alert(Alert.AlertType.WARNING, "Error loading profile page", ButtonType.OK).show();
            }
        }
    }
}
