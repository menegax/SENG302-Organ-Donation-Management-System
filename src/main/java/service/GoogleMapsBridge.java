package service;

import com.sun.javafx.webkit.WebConsoleListener;
import controller.ScreenControl;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import model.Patient;
import netscape.javascript.JSObject;
import utility.JSInjector;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Bridge for the Google Maps window that holds the buttons' functions
 * and the javascript bridge
 *
 * @author Joshua Meneghini
 */
public class GoogleMapsBridge implements Initializable {

    @FXML
    private WebView webViewMap1;

    private WebEngine webEngine1;

    private JSObject jsBridge1;

    private Stage mapStage;

    private Robot robot;
    private JSInjector jsInjector;


    /**
     * Initialises the widgets and bridge in the google map
     *
     * @param url Required parameter that is not used
     * @param rb  Required parameter that is not used
     */
    public void initialize(URL url, ResourceBundle rb) {
        webEngine1 = webViewMap1.getEngine();

        List<Patient> patients = new ArrayList<>(new ClinicianDataService().searchPatients("", null, 30));
        List<Patient> results = new ArrayList<>();
        for (Patient p : patients) {
            results.add(new PatientDataService().getPatientByNhi(p.getNhiNumber()));
        }

        webEngine1.setJavaScriptEnabled(true);
        webEngine1.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (Worker.State.SUCCEEDED == newValue) {
                jsBridge1 = (JSObject) webEngine1.executeScript("window");
                jsBridge1.setMember("patients", results);
                jsBridge1.setMember("jsInjector", jsInjector);
                jsBridge1.call("init");
            }
        });
        webEngine1.load(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("html/GoogleMap.html"))
                .toExternalForm());

        WebConsoleListener.setDefaultListener((webView, message, lineNumber, sourceId) -> System.out.println(message));

        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }

//        webViewMap1.setOnZoom(event -> {
//            webViewMap1.setZoom(webViewMap1.getZoom() * event.getZoomFactor());
//            webViewMap1.getEngine();
//        });
        webViewMap1.setOnScroll(event -> {
            if(event.getTouchCount() == 2) {
                robot.keyPress(KeyEvent.VK_CONTROL);
            }
        });
        webViewMap1.setOnTouchReleased(event -> robot.keyRelease(KeyEvent.VK_CONTROL));
        //        webViewMap1.setOnRotate(event -> webViewMap1.setRotate(webViewMap1.getRotate() + event.getAngle() * 0.8));

    }

}