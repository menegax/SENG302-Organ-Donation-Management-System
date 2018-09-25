package controller;

import com.sun.javafx.webkit.WebConsoleListener;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import model.Patient;
import netscape.javascript.JSObject;
import org.jetbrains.annotations.NotNull;
import service.ClinicianDataService;
import utility.MapBridge;
import utility.SystemLogger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
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

    private Double originalDistance;

    private Collection<Patient> patients = new ArrayList<>();

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
        webEngine = webViewMap1.getEngine();
        SystemLogger.systemLogger.log(Level.INFO, "Loading map...", "Attempted to open map");

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

        // What to do with console.log statements
        WebConsoleListener.setDefaultListener((webView, message, lineNumber, sourceId) -> SystemLogger.systemLogger.log(Level.FINE, message));

        webViewMap1.setOnTouchReleased((event -> {
            originalDistance = null;
            //jsBridge.call("setJankaOriginal", null);
        }));

        webViewMap1.setOnTouchMoved((event -> {
            double ZOOMFACTOR = 0.3;
            if (screenControl.isTouch()) {
                if (event.getTouchCount() == 2) {
                    if(event.getTouchPoints().get(0).getTarget().equals(webViewMap1) &&
                            event.getTouchPoints().get(1).getTarget().equals(webViewMap1)) {
                        Point2D touchOne = new Point2D(event.getTouchPoints().get(0).getX(),
                                event.getTouchPoints().get(0).getY());
                        Point2D touchTwo = new Point2D(event.getTouchPoints().get(1).getX(),
                                event.getTouchPoints().get(1).getY());
                        if (originalDistance == null) {
                            originalDistance = Math.sqrt(Math.pow(touchOne.getX() - touchTwo.getX(), 2) +
                                    Math.pow(touchOne.getY() - touchTwo.getY(), 2));
                            jsBridge.call("setJankaOriginal");
                        }
                        double currentDistance = Math.sqrt(Math.pow(touchOne.getX() - touchTwo.getX(), 2) +
                                Math.pow(touchOne.getY() - touchTwo.getY(), 2));
                        jsBridge.call("setJankaZoom", Math.pow(currentDistance / originalDistance, ZOOMFACTOR));
                    }
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

}