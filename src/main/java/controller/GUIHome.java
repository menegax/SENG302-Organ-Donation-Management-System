package controller;

import javafx.fxml.FXML;

public class GUIHome {

    @FXML
    public void goToProfile(){
        ScreenControl.activate("donorProfile");
    }

    @FXML
    public void goToHistory(){
        ScreenControl.activate("donorHistory");
    }
}
