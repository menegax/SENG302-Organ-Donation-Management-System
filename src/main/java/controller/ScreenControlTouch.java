package controller;

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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import model.User;
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

    private final UserControl userControl = UserControl.getUserControl();

    private boolean isLoginShowing;

    private Map<String, Integer> fontMap = new HashMap<>();

    private ScreenControlTouch() {
        isLoginShowing = true;
        populateFontMap();
    }

    /**
     * Populates the font-map with the id and font size of nodes in the application
     */
    private void populateFontMap() {
        fontMap.put("heading", 24);
        fontMap.put("paneTitle", 24);
        fontMap.put("userNameDisplay", 24);
        fontMap.put("nameTxt", 24);
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
            addCanvas(pane.getScene());
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
            Scene newScene = new Scene(touchPane);
            touchStage.setScene(newScene);
            addCanvas(newScene);
            pane.visibleProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue && parentController != null) {
                    parentController.windowClosed();
                }
            });
            resizeFonts(touchPane);
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
            setFonts();
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
        if(scene != null && !(scene.getRoot() instanceof TuioFXCanvas)) {
            TuioFXCanvas tuioFXCanvas = new TuioFXCanvas();
            Region oldRoot = (Region) scene.getRoot();
            tuioFXCanvas.getChildren().addAll(oldRoot);
            scene.setRoot(tuioFXCanvas);
            oldRoot.setTranslateX(0.0D);
            oldRoot.setTranslateY(0.0D);
            oldRoot.getStyleClass().removeAll("root");
        }
    }

    /**
     * Resizes the fonts of all objects in this node
     * @param pane the touch pane displayed currently
     */
    private void resizeFonts(Pane pane) {
        if (pane != null) {
            for (Node child : pane.getChildren()) {
                if (child instanceof Pane) {
                    resizeFonts((Pane) child);
                } else if (child instanceof TabPane) {
                    for (Tab tab : ((TabPane) child).getTabs()) {
                        resizeFonts((Pane) tab.getContent());
                    }
                } else if (child.getId() == null || (!fontMap.containsKey(child.getId()))) {
                    child.setStyle("-fx-font-size: 10px;");
                } else {
                    child.setStyle("-fx-font-size: " + String.valueOf(fontMap.get(child.getId())) + "px;");
                }
            }
        }
    }

    /**
     * Called when switching tabs with GuiHome
     * Resizes the fonts shown
     */
    public void setFonts() {
        resizeFonts(touchPane);
    }

    public void centerPanes() {
        for(Node pane : touchPane.getChildren()) {
            if(pane instanceof Pane) {
                pane.setTranslateX(0);
                pane.setTranslateY(0);
            }
        }
    }

}
