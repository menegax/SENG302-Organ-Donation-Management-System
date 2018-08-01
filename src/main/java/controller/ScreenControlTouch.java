package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import utility.undoRedo.UndoableStage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.util.logging.Level.INFO;
import static utility.SystemLogger.systemLogger;

class ScreenControlTouch extends ScreenControl {

    private Stage touchStage;

    private Pane touchPane;

    private ScreenControlTouch() {
        super();
    }

    public static ScreenControlTouch getScreenControl() {

        return new ScreenControlTouch();
    }

    public void addStage() {

    }

    public void closeStage() {

    }

//    public void setTouchStage(Stage touchStage) {
//        this.touchStage = touchStage;
//        touchPane = new Pane();
////        touchScene = new Scene(touchPane);
//        this.touchStage.setScene(new Scene(touchPane));
//    }

    public void show(Parent root) {
//            Parent root = FXMLLoader.load(getClass().getResource(fxml));
        Pane newPane = new AnchorPane(root);
        List<Node> nodes = new ArrayList<>(touchPane.getChildren());
        System.out.println(nodes.size());
//            touchPane.getChildren().add(newPane);
//            touchScene = null;
//            touchScene = new Scene(touchPane);
        Pane newTouchPane = new Pane();
        newTouchPane.getChildren().addAll(nodes);
        newTouchPane.getChildren().addAll(newPane);
        System.out.println(newTouchPane.getChildren().size());
        touchPane = newTouchPane;
//            touchScene = new Scene(touchPane);
//            touchStage.hide();
        touchStage.setScene(new Scene(touchPane));
        systemLogger.log(INFO, "Showing new touch stage scene");
    }

    public void setTouchStage(UndoableStage touchStage) {
         this.touchStage = touchStage;
    }
}
