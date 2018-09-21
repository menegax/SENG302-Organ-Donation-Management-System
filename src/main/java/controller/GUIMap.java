package controller;

import com.sun.javafx.webkit.WebConsoleListener;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Screen;
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

    private JSObject jsBridge;

    private MapBridge mapBridge;

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    private Double originalDistance;

    private Collection<Patient> patients = new ArrayList<>();



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
    public void initialize(URL url, ResourceBundle rb) {
        webEngine = webViewMap1.getEngine();
        UserActionHistory.userActions.log(Level.INFO, "Loading map...", "Attempted to open map");
        List<Patient> results;
        if (!screenControl.getIsCustomSetMap()) {
//             results = getInitialPatients();
            results = new ArrayList<>();
        } else {
            results = new ArrayList<>();
        }

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
                        jsBridge.call("setPatients", patients);
                    }
                });
        webEngine.load(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("html/map.html"))
                .toExternalForm());

        // What to do with console.log statements
        WebConsoleListener.setDefaultListener((webView, message, lineNumber, sourceId) -> SystemLogger.systemLogger.log(Level.FINE, message));

        webViewMap1.setOnTouchReleased((event -> {
            originalDistance = null;
            //jsBridge.call("setJankaOriginal", null);
        }));

        webViewMap1.setOnTouchMoved((event -> {

            if (screenControl.isTouch()) {
                if (event.getTouchCount() == 1) {
                    Point2D touchOne = new Point2D(event.getTouchPoints().get(0).getX(), event.getTouchPoints().get(0).getY());

                }
                if (event.getTouchCount() == 2) {
                    Point2D touchOne = new Point2D(event.getTouchPoints().get(0).getX(), event.getTouchPoints().get(0).getY());
                    Point2D touchTwo = new Point2D(event.getTouchPoints().get(1).getX(), event.getTouchPoints().get(1).getY());
                    if(originalDistance == null) {
                        originalDistance = Math.sqrt(Math.pow(touchOne.getX() - touchTwo.getX(), 2) + Math.pow(touchOne.getY() - touchTwo.getY(), 2));
                        jsBridge.call("setJankaOriginal");
                    }
                    double currentDistance = Math.sqrt(Math.pow(touchOne.getX() - touchTwo.getX(), 2) + Math.pow(touchOne.getY() - touchTwo.getY(), 2));
                    jsBridge.call("setJankaZoom", currentDistance/ originalDistance);
                }
            }
        }));
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