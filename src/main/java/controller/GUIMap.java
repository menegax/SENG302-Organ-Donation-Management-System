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

    public static JSObject jsBridge;

    private Robot robot;

    private MapBridge mapBridge;

    private Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    private final double ZOOMFACTOR = 1/((screenBounds.getWidth()/screenBounds.getHeight())*100);

    private Point2D stationaryPoint1;

    private Point2D stationaryPoint2;

    private Double originalDistance;

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

        webViewMap1.setOnTouchPressed(event -> {
            if(screenControl.isTouch()) {
                System.out.println(event.getTouchCount());
                if (event.getTouchCount() == 2) {
                    Point2D touchOne = new Point2D(event.getTouchPoints().get(0).getX(), event.getTouchPoints().get(0).getY());
                    Point2D touchTwo = new Point2D(event.getTouchPoints().get(1).getX(), event.getTouchPoints().get(1).getY());
                    if(originalDistance == null) {
                        originalDistance = Math.sqrt(Math.pow(touchOne.getX() - touchTwo.getX(), 2) + Math.pow(touchOne.getY() - touchTwo.getY(), 2));
                    }
                    double currentDistance = Math.sqrt(Math.pow(touchOne.getX() - touchTwo.getX(), 2) + Math.pow(touchOne.getY() - touchTwo.getY(), 2));
                    jsBridge.call("setJankaZoom", originalDistance / currentDistance);
                    }

                }
            });
        webViewMap1.setOnTouchReleased((event -> {
            System.out.println("released");
            originalDistance = null;
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

    /**
     * Calculates the angle centering on the second parameter
     * Gives angle in radians from -pi to pi (-ve anti-clockwise)
     * @param stationaryPoint the point to draw the angle from
     * @param previousPoint the point at the centre of the angle
     * @param currentPoint the point to draw the angle to
     * @return the angle between the point
     */
    private double calculateAngle(Point2D stationaryPoint, Point2D previousPoint, Point2D currentPoint) {
        double p2s = calculateDisplacement(previousPoint, stationaryPoint);
        double p2c = calculateDisplacement(previousPoint, currentPoint);
        double s2c = calculateDisplacement(stationaryPoint, currentPoint);
        double angle = Math.PI - Math.acos((Math.pow(p2s, 2) + Math.pow(p2c, 2) - Math.pow(s2c, 2)) / (2 * p2s * p2c));
        if ((currentPoint.getX() - stationaryPoint.getX()) * (previousPoint.getY() - stationaryPoint.getY()) - (currentPoint.getY() - stationaryPoint.getY())*(previousPoint.getX() - stationaryPoint.getX()) >= 0) {
            return angle;
        } else {
            return -angle;
        }
    }

    /**
     * Returns the scalar displacement between two points
     * @param start the first point
     * @param end the second point to calculate the displacement to
     * @return the displacement between the two points
     */
    private double calculateDisplacement(Point2D start, Point2D end) {
        return Math.sqrt(Math.pow(start.getX() - end.getX(), 2) + Math.pow(start.getY() - end.getY(), 2));
    }

}