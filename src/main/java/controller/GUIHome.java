package controller;

import javafx.fxml.FXML;

public class GUIHome {

    @FXML
    public void goToProfile(){
        GUIScreenControl.activate("donorProfile");
    }

    @FXML
    public void goToHistory(){
        GUIScreenControl.activate("donorHistory");
    }
}
