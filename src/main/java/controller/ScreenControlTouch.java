package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import utility.undoRedo.UndoableStage;

import java.io.IOException;
import java.util.*;

import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;
import static utility.SystemLogger.systemLogger;

class ScreenControlTouch extends ScreenControl {

    private static Map<String, Pane> applicationPanes;

    private UndoableStage touchStage;

    private Pane touchPane;

    private ScreenControlTouch() {
        super();
        applicationPanes = new HashMap<>();
    }

    public static ScreenControlTouch getScreenControl() {

        return new ScreenControlTouch();
    }

    @Override
    public void setTouchStage(UndoableStage touchStage) {
        this.touchStage = touchStage;
        touchPane = new Pane();
//        touchScene = new Scene(touchPane);
        this.touchStage.setScene(new Scene(touchPane));
    }

    @Override
    public void show(UUID stageName, Parent root) {
        List<Node> nodes = new ArrayList<>(touchPane.getChildren());
        Pane newTouchPane = new Pane(root);
        newTouchPane.getChildren().addAll(nodes);
        touchPane = newTouchPane;
        touchStage.setScene(new Scene(touchPane));

        systemLogger.log(INFO, "Showing new touch stage scene");
    }

//    public void setTouchStage(UndoableStage touchStage) {
//         this.touchStage = touchStage;
//    }

    @Override
    void setUpNewLogin() {
        try {
//        ScreenControl screenControl = ScreenControl.getScreenControl();
//        touchPane.getChildren().clear();
//        System.out.println(touchPane.getChildren().size());
//        Pane loginScreen = FXMLLoader.load(getClass().getResource("/scene/login.fxml"));
//        screenControl.show(touchStage.getUUID(), loginScreen);
//        System.out.println(touchPane.getChildren().size());
            screenControl.setTouchStage(touchStage);
            Parent root = FXMLLoader.load(getClass().getResource("/scene/login.fxml"));
            touchStage.setScene(new Scene(root));
            touchStage.show();
        } catch (IOException e) {
            systemLogger.log(SEVERE, "Failed to recreate login scene in touch application");

        }
    }

}
