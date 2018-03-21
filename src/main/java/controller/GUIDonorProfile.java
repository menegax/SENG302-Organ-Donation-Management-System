package controller;

import model.Donor;
import service.Database;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import utility.GlobalEnums;

import java.io.InvalidObjectException;
import java.time.format.DateTimeFormatter;

public class GUIDonorProfile {

    @FXML
    private Label nhiLbl;

    @FXML
    private Label nameLbl;

    @FXML
    private Label genderLbl;

    @FXML
    private Label dobLbl;

    @FXML
    private Label heightLbl;

    @FXML
    private Label weightLbl;

    @FXML
    private Label bloodGroupLbl;

    @FXML
    private Label addLbl1;

    @FXML
    private Label addLbl2;

    @FXML
    private Label addLbl3;

    @FXML
    private Label addLbl4;

    @FXML
    private Label addLbl5;

    @FXML
    private Label donationList;

    public void initialize() {

        loadProfile("ABC1238"); // todo remove
    }

    public void loadProfile(String nhi) {
        try { // todo remove this
            Donor donor = Database.getDonorByNhi(nhi);

            nhiLbl.setText(donor.getNhiNumber());
            nameLbl.setText(donor.getNameConcatenated());
            genderLbl.setText(donor.getGender() == null ? "Not set" : donor.getGender().toString());
            dobLbl.setText(donor.getBirth().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            heightLbl.setText(String.valueOf(donor.getHeight() + " kg"));
            weightLbl.setText(String.valueOf(donor.getWeight() + " m"));
            bloodGroupLbl.setText(donor.getBloodGroup() == null ? "Not set" : donor.getBloodGroup().getValue());
            addLbl1.setText(donor.getStreet1() == null ? "Not set" : donor.getStreet1());
            addLbl2.setText(donor.getStreet2() == null ? "Not set" : donor.getStreet2());
            addLbl3.setText(donor.getSuburb() == null ? "Not set" : donor.getSuburb());
            addLbl4.setText(donor.getRegion() == null ? "Not set" : donor.getRegion().getValue());
            addLbl5.setText(String.valueOf(donor.getZip()));
            for (GlobalEnums.Organ organ : donor.getDonations()) {
                donationList.setText(donationList.getText() + organ.getValue() + "\n");
            }
        } catch (InvalidObjectException e) {
            e.printStackTrace(); // todo remove
        }
    }

    public void goToHome() {
        ScreenControl.activate("home");
    }


}
