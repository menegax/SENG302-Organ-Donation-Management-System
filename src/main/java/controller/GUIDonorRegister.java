package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import model.Donor;
import service.Database;

import javax.xml.soap.Text;
import java.io.InvalidObjectException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

public class GUIDonorRegister {

    @FXML
    private TextField firstnameRegister;

    @FXML
    private TextField lastnameRegister;

    @FXML
    private TextField middlenameRegister;

    @FXML
    private TextField birthRegister;

    @FXML
    private TextField nhiRegister;

    @FXML
    public void goBackToLogin(){
        GUIScreenControl.activate("login");
    }

    @FXML
    public void goToHome(){
        if (!(firstnameRegister.getText().isEmpty() || lastnameRegister.getText().isEmpty()
                || birthRegister.getText().isEmpty() || nhiRegister.getText().isEmpty())) {
            try{
                Database.addDonor(new Donor(nhiRegister.getText(), firstnameRegister.getText(),
                        middlenameRegister.getText().isEmpty() ? new ArrayList<>() :
                                new ArrayList<>(Arrays.asList(middlenameRegister.getText().split("\\s*,\\s*"))),
                        lastnameRegister.getText(), LocalDate.parse(birthRegister.getText())));
                GUIScreenControl.activate("home");
            }catch (IllegalArgumentException e){
                e.printStackTrace();
            }

        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please enter all fields");
            alert.show();
        }
    }

}
