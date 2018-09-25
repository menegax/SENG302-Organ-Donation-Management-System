package controller;

import com.sun.javafx.webkit.WebConsoleListener;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import model.Patient;
import netscape.javascript.JSObject;
import org.jetbrains.annotations.NotNull;
import service.ClinicianDataService;
import service.PatientDataService;
import utility.MapBridge;
import utility.SystemLogger;
import utility.UserActionHistory;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.*;
import java.util.List;
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

    private Robot robot;

    private MapBridge mapBridge;

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    private Collection<Patient> patients = new ArrayList<>();

public static JSObject getJSBridge() {
        return jsBridge;
    }

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
        UserActionHistory.userActions.log(Level.INFO, "Loading map...", "Attempted to open map");

        webEngine = webViewMap1.getEngine();
        if (!screenControl.getIsCustomSetMap()) {
             patients = getInitialPatients();
        }

        setUpWebEngine();
        setUpJsLogging();
        setUpTouchControls();
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

        try {
            robot = new Robot();
        }
        catch (AWTException e) {
            SystemLogger.systemLogger.log(Level.SEVERE, "Failed to initialize map bridge: " + e.toString());
        }
    }


    /**
     * Sets scroll and touch events
     */
    private void setUpTouchControls() {
        webViewMap1.setOnScroll(event -> {
            if (screenControl.isTouch()) {
                if (event.getTouchCount() == 2) {
                    robot.keyPress(KeyEvent.VK_CONTROL);
                }
            }
        });
        webViewMap1.setOnTouchReleased(event -> {
            if (screenControl.isTouch()) {
                robot.keyRelease(KeyEvent.VK_CONTROL);
            }
        });
    }


    /**
     * Forwards JavaScript console.log statements into java logging
     */
    private void setUpJsLogging() {
        // What to do with console.log statements
        WebConsoleListener.setDefaultListener((webView, message, lineNumber, sourceId) -> SystemLogger.systemLogger.log(Level.FINE, message));
    }


    /**
     * Gets a collection of initial patients from the search results to auto-populate the map on startup
     * @return the results of the default search
     */
    @NotNull
    private List<Patient> getInitialPatients() {
        return new ArrayList<>(new ClinicianDataService().searchPatients("", null, 50));
    }
}