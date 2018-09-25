package controller;

import com.sun.javafx.webkit.WebConsoleListener;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import model.Patient;
import netscape.javascript.JSObject;
import utility.MapBridge;
import utility.SystemLogger;
import utility.UserActionHistory;

import java.util.Collection;
import java.util.Objects;
import java.util.logging.Level;

/**
 * Bridge for the Google Maps window that holds the buttons' functions
 * and the javascript bridge
 */
public class GUIMap {

    @FXML
    private WebView webViewMap1;

    private WebEngine webEngine;

    private static JSObject jsBridge;

    private MapBridge mapBridge;

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    public static JSObject getJSBridge(){ return jsBridge; }


    /**
     * Loads the patients provided onto the map
     * @param patients a collection of patients to show on the map
     */
    public void setPatients(Collection<Patient> patients) {
        if (jsBridge != null) {
            jsBridge.call("setPatients", patients);
        }
    }

    /**
     * Initialises the widgets and bridge in the google map
     */
    void loadMap() {
        UserActionHistory.userActions.log(Level.INFO, "Loading map...", "Attempted to open map");
        webEngine = webViewMap1.getEngine();
        setUpWebEngine();
        setUpJsLogging();
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
                    }
                });
        webEngine.load(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("html/map.html"))
                .toExternalForm());
    }

    /**
     * Forwards JavaScript console.log statements into java logging
     */
    private void setUpJsLogging() {
        // What to do with console.log statements
        WebConsoleListener.setDefaultListener((webView, message, lineNumber, sourceId) -> SystemLogger.systemLogger.log(Level.FINE, message));
    }
}