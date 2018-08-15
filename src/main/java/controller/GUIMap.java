package controller;

import com.sun.javafx.webkit.WebConsoleListener;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import model.Patient;
import netscape.javascript.JSObject;
import service.ClinicianDataService;
import service.PatientDataService;
import utility.MapBridge;
import utility.SystemLogger;
import utility.UserActionHistory;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;

/**
 * Bridge for the Google Maps window that holds the buttons' functions
 * and the javascript bridge
 *
 */
public class GUIMap implements Initializable {

    @FXML
    private WebView webViewMap1;

    private WebEngine webEngine1;

    private JSObject jsBridge1;

    private Robot robot;

    private MapBridge mapBridge;

    private ScreenControl screenControl = ScreenControl.getScreenControl();


    /**
     * Initialises the widgets and bridge in the google map
     *
     * @param url Required parameter that is not used
     * @param rb  Required parameter that is not used
     */
    public void initialize(URL url, ResourceBundle rb) {
        webEngine1 = webViewMap1.getEngine();

        UserActionHistory.userActions.log(Level.INFO, "Loading map...", "Attempted to open map");

        List<Patient> patients = new ArrayList<>(new ClinicianDataService().searchPatients("", null, 50));
        List<Patient> results = new ArrayList<>();
        for (Patient p : patients) {
            results.add(new PatientDataService().getPatientByNhi(p.getNhiNumber()));
        }

        webEngine1.setJavaScriptEnabled(true);
        webEngine1.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (Worker.State.SUCCEEDED == newValue) {
                jsBridge1 = (JSObject) webEngine1.executeScript("window");
                jsBridge1.setMember("patients", results);
                mapBridge = new MapBridge();
                jsBridge1.setMember("mapBridge", mapBridge);
                jsBridge1.call("init");
            }
        });
        webEngine1.load(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("html/map.html"))
                .toExternalForm());

        // What to do with console.log statements
        WebConsoleListener.setDefaultListener((webView, message, lineNumber, sourceId) -> SystemLogger.systemLogger.log(Level.FINE, message));

        try {
            robot = new Robot();
        }
        catch (AWTException e) {
            SystemLogger.systemLogger.log(Level.SEVERE, "Failed to initialize map bridge: " + e.toString());
        }

        webViewMap1.setOnScroll(event -> {
            if(screenControl.isTouch()) {
                if (event.getTouchCount() == 2) {
                    robot.keyPress(KeyEvent.VK_CONTROL);
                }
            }
        });
        webViewMap1.setOnTouchReleased(event -> {
            if(screenControl.isTouch()) {
                robot.keyRelease(KeyEvent.VK_CONTROL);
            }
        });
        //        webViewMap1.setOnRotate(event -> webViewMap1.setRotate(webViewMap1.getRotate() + event.getAngle() * 0.8));

    }

}