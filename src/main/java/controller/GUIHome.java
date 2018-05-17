package controller;

import static java.util.logging.Level.SEVERE;
import static javafx.scene.control.Alert.AlertType.ERROR;
import static utility.UserActionHistory.userActions;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.LoadException;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.Arrays;

public class GUIHome {

    @FXML
    private TabPane horizontalTabPane;


    @FXML
    public void initialize() { //Todo catch exception
        //Todo possibly create a smart way to check logged in user type
        // and then create the tabs based off that

        try {
            // create profile tab and add fxml into
            Tab profileViewTab = new Tab();
            profileViewTab.setText("Profile");

            Pane pane = FXMLLoader.load(getClass().getResource("/scene/test.fxml"));

            profileViewTab.setContent(pane);
            horizontalTabPane.getTabs()
                    .add(profileViewTab);

            // create history tab and add fxml into
            Tab historyViewTab = new Tab();
            historyViewTab.setText("History");
            historyViewTab.setContent(FXMLLoader.<Pane>load(getClass().getResource("/scene/patientHistory.fxml")));
            horizontalTabPane.getTabs()
                    .add(historyViewTab);
        }
        catch (IOException e) {
            new Alert(ERROR, "Unable to load home").show();
//            systemLog.log(SEVERE, "Failed to load home scene and its fxmls", Arrays.toString(e.getStackTrace())); //Todo
        }
    }



}
