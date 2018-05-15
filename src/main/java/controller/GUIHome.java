package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class GUIHome {
    //JUST A TEST CLASS - or maybe not..

    @FXML private TabPane horizontalTabPane;


    @FXML
    public void initialize() throws IOException {
        //possibly create a smart way to check logged in user type
        // and then create the tabs based off that

        // create profile tab and add fxml into
        Tab profileViewTab = new Tab();
        profileViewTab.setText("Profile");
        profileViewTab.setContent(FXMLLoader.<Pane>load(getClass().getResource("/scene/patientProfile.fxml")));
        horizontalTabPane.getTabs().add(profileViewTab);

        // create history tab and add fxml into
        Tab historyViewTab = new Tab();
        historyViewTab.setText("History");
        historyViewTab.setContent(FXMLLoader.<Pane>load(getClass().getResource("/scene/patientHistory.fxml")));
        horizontalTabPane.getTabs().add(historyViewTab);
    }
}
