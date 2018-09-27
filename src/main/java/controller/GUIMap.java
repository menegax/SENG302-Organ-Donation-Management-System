package controller;

import com.sun.javafx.webkit.WebConsoleListener;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import model.Patient;
import netscape.javascript.JSObject;
import utility.MapBridge;
import utility.MultiTouchMapHandler;
import utility.SystemLogger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.logging.Level;

/**
 * Bridge for the Google Maps window that holds the buttons' functions
 * and the javascript bridge
 */
public class GUIMap {

    @FXML
    public GridPane mapPane;

    @FXML
    private WebView webViewMap1;

    private WebEngine webEngine;

    private static JSObject jsBridge;

    private MapBridge mapBridge;

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    private Double originalDistance;

    private Collection<Patient> patients = new ArrayList<>();

    private MultiTouchMapHandler touchMapHandler;

    public static JSObject getJSBridge(){ return jsBridge; }

    /**
     * Loads the patients provided onto the map
     * @param patients a collection of patients to show on the map
     */
    public void setPatients(Collection<Patient> patients) {
        this.patients.clear();
        this.patients = patients;
        if (jsBridge != null) {
            jsBridge.call("setPatients", patients);
        }
    }

    /**
     * Initialises the widgets and bridge in the google map
     */
    void loadMap() {
        webEngine = webViewMap1.getEngine();
        SystemLogger.systemLogger.log(Level.INFO, "Loading map...", "Attempted to open map");

        setUpWebEngine();
        setUpJsLogging();
        if(screenControl.isTouch()) {
            touchMapHandler = new MultiTouchMapHandler();
            touchMapHandler.initialiseHandler(webViewMap1);
        }
    }


    /**
     * Sets up the web engine and jsbridge
     */
    private void setUpWebEngine() {
        webEngine.setJavaScriptEnabled(true);
        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
                    if (Worker.State.SUCCEEDED == newValue) {
                        jsBridge = (JSObject) webEngine.executeScript("window");
                        mapBridge = new MapBridge();
                        jsBridge.setMember("mapBridge", mapBridge);
                        jsBridge.call("init");
                        jsBridge.call("setPatients", patients);
                    }
                });
        webEngine.load(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("html/map.html"))
                .toExternalForm());

        // What to do with console.log statements
        WebConsoleListener.setDefaultListener((webView, message, lineNumber, sourceId) -> SystemLogger.systemLogger.log(Level.FINE, message));

    }


    /**
     * Forwards JavaScript console.log statements into java logging
     */
    private void setUpJsLogging() {
        // What to do with console.log statements
        WebConsoleListener.setDefaultListener((webView, message, lineNumber, sourceId) -> SystemLogger.systemLogger.log(Level.FINE, message));
    }

}