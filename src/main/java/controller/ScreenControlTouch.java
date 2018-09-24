package controller;

import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;
import static utility.SystemLogger.systemLogger;
import static utility.UserActionHistory.userActions;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.User;
import org.tuiofx.internal.base.TuioFXCanvas;
import service.UserDataService;
import utility.TouchComboBoxSkin;
import utility.undoRedo.UndoableWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

class ScreenControlTouch extends ScreenControl {

	/* Defines the size of the initial pane */
    private final double INITIAL_PANE_SIZE = 0.85;
	
	private Stage touchStage;
	
	private Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

    private Region rootPane;

    private Pane touchPane = null;

    private static ScreenControlTouch screenControlTouch;

    private final UserControl userControl = UserControl.getUserControl();

    private boolean isLoginShowing;

    private Map<String, Integer> fontMap = new HashMap<>();
    
    private boolean fullScreen;

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
        fontMap.put("title", 28);
        fontMap.put("TEXT", 14);
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
     * @param targetUser The targeted user for the pane
     * @param parent The parent to orientate this fxml to
     * @return the controller created for this fxml
     */
    public Object show(String fxml, Boolean undoable, IWindowObserver parentController, User targetUser, Parent parent) {
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
            setPanePosition(pane, parent);
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
            if (fullScreen) {
            	ensureFullScreen();
            }
            addCanvas(newScene);
            pane.visibleProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue && parentController != null) {
                    parentController.windowClosed();
                }
            });
            resizeFonts(touchPane);
            if (fxml.equals(MAPFXML)) {
                // Cast should always be safe
            	setMapPanePosition(pane);
                mapController = (GUIMap) controller;
                mapController.loadMap();
                pane.visibleProperty().addListener(((observable, oldValue, newValue) -> {
                    if (!newValue) {
                        setMapOpen(false);
                    }
                }));
            }
            systemLogger.log(INFO, "Showing new touch stage scene");
            return controller;
        } catch (IOException e) {
            userActions.log(Level.SEVERE, "Unable to loadController window", "Attempted to loadController a new window");
            new Alert(Alert.AlertType.ERROR, "Unable to open window", ButtonType.OK).show();
        }
        return null;
    }

    /**
     * Ensures the application remains in full screen mode.
     */
    private void ensureFullScreen() {
    	touchStage.setFullScreenExitHint("");
        touchStage.setFullScreen(true);
    }
    
    /**
     * Sets the position, rotation and size of the map pane.
     * @param pane The map pane
     */
    private void setMapPanePosition(Region pane) {
    	pane.setPrefWidth(touchPane.getWidth());
    	pane.setPrefHeight(touchPane.getHeight());
    	pane.setTranslateX(0);
    	pane.setTranslateY(0);
    	pane.setScaleX(0.6);
    	pane.setScaleY(0.6);
    }
    
    /**
     * Sets the position, rotation and size of a pane.
     * @param pane The pane to format.
     * @param parent Parent that created the pane.
     */
    private void setPanePosition(Region pane, Parent parent) {
        if (parent != null) {
            double centerParentX = ((Pane)parent).getPrefWidth()/2;
            double centerParentY = ((Pane)parent).getPrefHeight()/2;
            double centerChildX = pane.getPrefWidth() / 2;
            double centerChildY = pane.getPrefHeight() / 2;
            pane.setTranslateX(parent.getTranslateX() + centerParentX - centerChildX);
            pane.setTranslateY(parent.getTranslateY() + centerParentY - centerChildY);
            pane.setLayoutX(parent.getLayoutX());
            pane.setLayoutY(parent.getLayoutY());
            pane.setRotate(parent.getRotate());
            pane.setScaleX(parent.getScaleX());
            pane.setScaleY(parent.getScaleY());
        } else {
        	setInitialPaneSize(pane);
        }
    }
    
    /**
     * Sets the correct location and size for the initial log in pane
     * @param pane The pane to set location and size for
     */
    private void setInitialPaneSize(Region pane) {
    	pane.setTranslateX((touchStage.getWidth() - pane.getPrefWidth()) / 2);
    	pane.setTranslateY((touchStage.getHeight() - pane.getPrefWidth()) / 2);
    	pane.setScaleX(INITIAL_PANE_SIZE);
    	pane.setScaleY(INITIAL_PANE_SIZE);
    }
    
    /**
     * Creates a login pane
     */
    public void setUpNewLogin() {
        try {
            new UserDataService().prepareApplication();
            Region root = FXMLLoader.load(getClass().getResource("/scene/login.fxml"));
            touchPane = new Pane();
            touchPane.getChildren().addAll(new Pane(root));           
            Scene newScene = new Scene(touchPane);        
            addCanvas(newScene);
            touchStage.setScene(newScene);
            if (fullScreen) {
            	ensureFullScreen();
            }
            setLoginShowing(true);
            setCSS();
            setInitialPaneSize(root);
            
        } catch (IOException e) {
            systemLogger.log(SEVERE, "Failed to recreate login scene in touch application");

        }
    }

    /**
     * Sets a new touch stage
     * @param touchStage The new touch stage
     */
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

    /**
     * Sets the login showing flag
     * @param showing The new value
     */
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

    /**
     * Adds a new tuiofx canvas for the given scene
     * @param scene The scene to add a canvas for
     */
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
                } else if (child.getId() != null && child.getId().equals("EXIT")) {
                    child.setStyle("-fx-font-size: 15px; "
                            + "-fx-test-fill: white; "
                            + "-fx-font-weight: bold; "
                            + "-fx-background-color: "
                            + "#e62e00 "
                            + "linear-gradient(#ffe6e6, #ffcccc),"
                            + "linear-gradient(#ff9999 0%, #ff8080 49%, #ff6666 50%, #ff4d4d 100%);");
                } else if (child.getStyleClass().toString().equals("TEXT")) {
                    child.setStyle("-fx-fill: white; -fx-font-size: 10px");
                } else if (child instanceof ListView || child instanceof TableView) {
                    child.setStyle("-fx-font-size: 10px; -fx-border-color: white;" +
                            "-fx-border-width: 1.5;");
                } else if (child.getId() == null || (!fontMap.containsKey(child.getId()))) {
                    child.setStyle("-fx-font-size: 10px;");
                } else if (child.getStyleClass().toString().equals("label title")) {
                    child.setStyle("-fx-text-fill: white; -fx-font-size: 28px; ");
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
    public void setCSS() {
        resizeFonts(touchPane);
    }
    
    public void setFullScreen(boolean fullScreen) {
    	this.fullScreen = fullScreen;
    }

    /**
     * Centers all of the panes in the touchpane
     */
    public void centerPanes() {
        for(Node pane : touchPane.getChildren()) {
            if(pane instanceof Pane) {
                pane.setTranslateX(0);
                pane.setTranslateY(0);
            }
        }
    }

}
