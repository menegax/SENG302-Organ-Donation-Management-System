package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.tuiofx.examples.demo.TUIOFXDemoApp;
import org.tuiofx.internal.test.TuioFXCanvasTest;
import org.tuiofx.internal.test.TuioFXTestApp;
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
        root.setStyle("-fx-background-color: #2c2f34; -fx-border-color: #f5f5f5; -fx-border-width: 1;");
        Pane newTouchPane = new Pane(root);
//        newTouchPane.getProperties().put("focusArea", "true");
        newTouchPane.getChildren().addAll(nodes);
        touchPane = newTouchPane;
//        for(Node n : touchPane.getChildren()) {
//            n.getProperties().put("usefocusArea", false);
//        }
        touchStage.setScene(new Scene(touchPane));

        systemLogger.log(INFO, "Showing new touch stage scene");
    }

//    public void setTouchStage(UndoableStage touchStage) {
//         this.touchStage = touchStage;
//    }

    @Override
    void setUpNewLogin() {
        try {
            screenControl.setTouchStage(touchStage);
            Parent root = FXMLLoader.load(getClass().getResource("/scene/login.fxml"));
            touchPane = new Pane();
            touchPane.getChildren().addAll(new Pane(root));
            touchStage.setScene(new Scene(touchPane));
//            touchStage.show();
        } catch (IOException e) {
            systemLogger.log(SEVERE, "Failed to recreate login scene in touch application");

        }
    }

}
