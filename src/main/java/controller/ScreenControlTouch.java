package controller;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import model.User;
import org.tuiofx.examples.demo.FXMLController;
import org.tuiofx.internal.base.TuioFXCanvas;
import service.UserDataService;
import utility.undoRedo.UndoableWrapper;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;
import static utility.SystemLogger.systemLogger;
import static utility.UserActionHistory.userActions;

class ScreenControlTouch extends ScreenControl {

    private Stage touchStage;

    private Region rootPane;

    private Pane touchPane = new Pane();

    private static ScreenControlTouch screenControlTouch;

    private UserControl userControl = UserControl.getUserControl();

    private boolean isLoginShowing;

    private ScreenControlTouch() {
        isLoginShowing = true;
    }

    public static ScreenControlTouch getScreenControl() {
        if (screenControlTouch == null) {
            screenControlTouch = new ScreenControlTouch();
        }
        return screenControlTouch;
    }

    /**
     * Displays a new pane with the loaded fxml
     * @param fxml the fxml to display
     * @param undoable if the pane to be displayed is undoable or not
     * @param parentController controller to notify when pane shown closes
     * @return the controller created for this fxml
     */
    public Object show(String fxml, Boolean undoable, IWindowObserver parentController, User targetUser) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxml));
            Region pane = fxmlLoader.load();
            Object controller = fxmlLoader.getController();
            if (undoable) {
                UndoableWrapper undoablePane = new UndoableWrapper(pane);
                undoableWrappers.add(undoablePane);
                userControl.setTargetUser(targetUser, undoablePane);
                if (controller instanceof GUIHome) {
                    undoablePane.setGuiHome((GUIHome) controller);
                    ((GUIHome) controller).setTarget(targetUser);
                }
                undoablePane.setPane((Pane) pane);
            }
            pane.getProperties().put("focusArea", "true");
            pane.setStyle("-fx-background-color: #2c2f34; -fx-border-color: #f5f5f5;");
            List<Node> panes;
            if(isLoginShowing) {
                panes = new ArrayList<>();
                rootPane = pane;
                setLoginShowing(false);
            } else {
                panes = new ArrayList<>(touchPane.getChildren());
            }
            panes.add(pane);
            Region root = new FXMLLoader(getClass().getResource("/scene/touchScene.fxml")).load();
            touchPane = new Pane(root);
            touchPane.getChildren().addAll(panes);
//            if(touchScene == null) {
//                touchScene = new Scene(touchPane);
//            } else {
//                touchScene.setRoot(root);
//            }
//            touchStage.setScene(touchScene);
            Scene newScene = new Scene(touchPane);
            touchStage.setScene(newScene);
            addCanvas(newScene);
//            newScene.getRoot().getProperties().put("focusArea", "true");
            pane.visibleProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue && parentController != null) {
                    parentController.windowClosed();
                }
            });
            System.out.println("new pane");
            resizeButtonFont(touchPane);
            systemLogger.log(INFO, "Showing new touch stage scene");
            return controller;
        } catch (IOException e) {
            userActions.log(Level.SEVERE, "Unable to load window", "Attempted to load a new window");
            new Alert(Alert.AlertType.ERROR, "Unable to open window", ButtonType.OK).show();
        }
        return null;
    }

    /**
     * Creates a login pane
     */
    void setUpNewLogin() {
        try {
            new UserDataService().prepareApplication();
            Parent root = FXMLLoader.load(getClass().getResource("/scene/login.fxml"));
            touchPane = new Pane();
            touchPane.getChildren().addAll(new Pane(root));
            Scene newScene = new Scene(touchPane);
            addCanvas(newScene);
            touchStage.setScene(newScene);
            setLoginShowing(true);
        } catch (IOException e) {
            systemLogger.log(SEVERE, "Failed to recreate login scene in touch application");

        }
    }

    void setTouchStage(Stage touchStage) {
        this.touchStage = touchStage;
    }

    /**
     * Adds asterisks to all panes with a coloured bar
     */
    void addUnsavedAsterisks() {
        for (UndoableWrapper undoablePane : undoableWrappers) {
            if (undoablePane.getGuiHome() != null) {
                undoablePane.getGuiHome().addAsterisk();
            }
        }
    }

    /**
     * Removes all asterisks from panes with a coloured bar
     */
    void removeUnsavedAsterisks() {
        for (UndoableWrapper undoablePane : undoableWrappers) {
            if(undoablePane.getGuiHome() != null) {
                undoablePane.getGuiHome().removeAsterisk();
            }
        }
    }

    void setLoginShowing(boolean showing) {
        this.isLoginShowing = showing;
    }

    /**
     * Closes the provided pane
     * @param pane the pane to close
     * @return if a pane was closed successfully
     */
    boolean closeWindow(Pane pane) {
        List<Node> nodes = new ArrayList<>(touchPane.getChildren());
        for(Node n : nodes) {
            if (n.getId() != null && n.getId().equals("homePane")) {
                for (Tab tab : ((TabPane) ((BorderPane) n).getCenter()).getTabs()) {
                    if (tab.getContent() != null && (tab.getContent()).equals(pane) && !(n.equals(rootPane))) {
                        touchPane.getChildren().remove(n);
                        n.setVisible(false);
                        return true;
                    }
                }
            }
            if(n.equals(pane) && !(pane.equals(rootPane))) {
                touchPane.getChildren().remove(n);
                n.setVisible(false);
                return true;
            }
        }
        return false;
    }

    private void addCanvas(Scene scene) {
        if(!(scene.getRoot() instanceof TuioFXCanvas)) {
            TuioFXCanvas tuioFXCanvas = new TuioFXCanvas();
            Region oldRoot = (Region) scene.getRoot();
            tuioFXCanvas.getChildren().addAll(oldRoot);
            scene.setRoot(tuioFXCanvas);
            oldRoot.setTranslateX(0.0D);
            oldRoot.setTranslateY(0.0D);
            oldRoot.getStyleClass().removeAll("root");
        }
    }

    private void resizeButtonFont(Node node) {
        System.out.println(node.getClass().getName());
        if(node instanceof Button) {
            System.out.println("button");
            ((Button) node).setFont(Font.font(6));
        } else if(node instanceof VBox) {
            VBox vBox = (VBox) node;
            for(Node vNode : vBox.getChildren()) {
                resizeButtonFont(vNode);
            }
        } else if(node instanceof HBox) {
            HBox hBox = (HBox) node;
            for(Node hNode : hBox.getChildren()) {
                resizeButtonFont(hNode);
            }
        } else if (node instanceof GridPane) {
            GridPane gridPane = (GridPane) node;
            for(Node gridNode : gridPane.getChildren()) {
                resizeButtonFont(gridNode);
            }
        } else if(node instanceof TabPane) {
            TabPane tabPane = (TabPane) node;
            List<Tab> tabs = tabPane.getTabs();
            List<Node> tabNodes = tabs.stream().map(Tab::getContent).collect(Collectors.toList());
            for(Node tabNode : tabNodes) {
                resizeButtonFont(tabNode);
            }
        } else if (node instanceof AnchorPane) {
            AnchorPane anchorPane = (AnchorPane) node;
            for(Node anchorNode : anchorPane.getChildren()) {
                resizeButtonFont(anchorNode);
            }
        } else if (node instanceof TuioFXCanvas) {
            TuioFXCanvas canvas = (TuioFXCanvas) node;
            for(Node canvasNode : canvas.getChildren()) {
                resizeButtonFont(canvasNode);
            }
        } else if (node instanceof Pane) {
            Pane pane = (Pane) node;
            for(Node paneNode : pane.getChildren()) {
                resizeButtonFont(paneNode);
            }
        }

    }

}
