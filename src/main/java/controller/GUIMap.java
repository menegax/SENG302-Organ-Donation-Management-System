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
public class GUIMap implements Initializable {

    @FXML
    private WebView webViewMap1;

    private WebEngine webEngine;

    private static JSObject jsBridge;

    private MapBridge mapBridge;

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    private Double originalDistance;

    /**
     * Initialises the widgets and bridge in the google map
     *
     * @param url Required parameter that is not used
     * @param rb  Required parameter that is not used
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

        webEngine.setJavaScriptEnabled(true);
        webEngine.getLoadWorker()
                .stateProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (Worker.State.SUCCEEDED == newValue) {
                        jsBridge = (JSObject) webEngine.executeScript("window");
                        jsBridge.setMember("patients", results);
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
            System.out.println("released");
            originalDistance = null;
            jsBridge.call("setJankaOriginal", null);
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
                        jsBridge.call("setJankaOriginal", originalDistance);
                    }
                    double currentDistance = Math.sqrt(Math.pow(touchOne.getX() - touchTwo.getX(), 2) + Math.pow(touchOne.getY() - touchTwo.getY(), 2));
                    jsBridge.call("setJankaZoom", originalDistance / currentDistance);
                }
            }
        }));
    }


    /**
     * Gets a collection of initial patients from the search results to auto-populate the map on startup
     * @return the results of the default search
     */
    @NotNull
    private List<Patient> getInitialPatients() {
        List<Patient> patients = new ArrayList<>(new ClinicianDataService().searchPatients("", null, 50));
        List<Patient> results = new ArrayList<>();
        for (Patient p : patients) {
            results.add(new PatientDataService().getPatientByNhi(p.getNhiNumber()));
        }
        return results;
    }
}