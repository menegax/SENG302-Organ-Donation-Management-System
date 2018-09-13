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
public class GUIMap implements Initializable {

    @FXML
    private WebView webViewMap1;

    private WebEngine webEngine;

    public static JSObject jsBridge;

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
        webEngine = webViewMap1.getEngine();
        System.out.println(webEngine.getUserAgent());
        webEngine.setUserAgent("Mozilla/5.0 (iPhone; CPU iPhone OS 10_3_2 like Mac OS X) AppleWebKit/603.2.4 (KHTML, like Gecko) FxiOS/7.5b3349 Mobile/14F89 Safari/603.2.4");
        UserActionHistory.userActions.log(Level.INFO, "Loading map...", "Attempted to open map");
        System.out.println(webEngine.getUserAgent());
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

        try {
            robot = new Robot();
        }
        catch (AWTException e) {
            SystemLogger.systemLogger.log(Level.SEVERE, "Failed to initialize map bridge: " + e.toString());
        }

//        webViewMap1.setOnScroll(event -> {
//            if (screenControl.isTouch()) {
//                if (event.getTouchCount() == 2) {
//                    robot.keyPress(KeyEvent.VK_CONTROL);
//                }
//            }
//       });
//        webViewMap1.setOnTouchReleased(event -> {
//            if (screenControl.isTouch()) {
//                robot.keyRelease(KeyEvent.VK_CONTROL);
//            }
//        });
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