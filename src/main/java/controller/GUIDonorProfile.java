package controller;

import model.Donor;
import service.Database;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.InvalidObjectException;

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
    private Label donationList;

    public void initialize() {

        loadProfile("ABC1238");
    }

    public void loadProfile(String nhi) {
        try {
            Donor donor = Database.getDonorByNhi(nhi);

            nhiLbl.setText(donor.getNhiNumber());
            nameLbl.setText(donor.getNameConcatenated());
            //genderLbl.setText(Genderdonor.getGender()); //TODO figure out enum string
            dobLbl.setText("TODO");
            heightLbl.setText(String.valueOf(donor.getHeight()));
            weightLbl.setText(String.valueOf(donor.getWeight()));
            //bloodGroupLbl.setText(donor.getBloodGroup().getValue());
            addLbl1.setText("TODO ADD_1");
            addLbl2.setText("TODO ADD_2");
            addLbl3.setText("TODO ADD_3");
            addLbl4.setText("TODO ADD_4");
            donationList.setText("ONE\nTWO\nTHREE");
        } catch(InvalidObjectException e) {
            e.printStackTrace();
        }


    }

    public void goToHome() {
        ScreenControl.activate("home");
    }


}
