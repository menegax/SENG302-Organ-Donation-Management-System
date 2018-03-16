package controller;

import javafx.fxml.FXML;

public class GUILogin {

    @FXML
    public void goToRegister(){
        GUIScreenControl.activate("donorRegister");
    }


}
