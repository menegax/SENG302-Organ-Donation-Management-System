package service;

import com.sun.javafx.webkit.WebConsoleListener;
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
                jsInjector = new JSInjector();
                jsBridge1.setMember("jsInjector", jsInjector);
                jsBridge1.call("init");
            }
        });
        webEngine1.load(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("html/GoogleMap.html"))
                .toExternalForm());

        WebConsoleListener.setDefaultListener(new WebConsoleListener() {
            @Override
            public void messageAdded(WebView webView, String message, int lineNumber, String sourceId) {
                System.out.println(message);
            }
        });

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

        addStageListener();
    }


    private void addStageListener() {
        // The following code waits for the stage to be loaded
        if (webViewMap1.getScene() == null) {
            webViewMap1.sceneProperty()
                    .addListener((observable, oldScene, newScene) -> {
                        if (newScene != null) {
                            if (newScene.getWindow() == null) {
                                newScene.windowProperty()
                                        .addListener((observable2, oldStage, newStage) -> {
                                            if (newStage != null) {
                                                mapStage = (Stage) newStage;
                                                // Methods to call after initialize
                                                setStageTitle();
                                            }
                                        });
                            }
                            else {
                                mapStage = (Stage) newScene.getWindow();
                                // Methods to call after initialize
                                setStageTitle();
                            }
                        }
                    });
        }
        else if (webViewMap1.getScene()
                .getWindow() == null) {
            webViewMap1.getScene()
                    .windowProperty()
                    .addListener((observable2, oldStage, newStage) -> {
                        if (newStage != null) {
                            mapStage = (Stage) newStage;
                            // Methods to call after initialize
                            setStageTitle();
                        }
                    });
        }
        else {
            mapStage = (Stage) mapStage.getScene()
                    .getWindow();
            // Methods to call after initialize
            setStageTitle();
        }
    }

    // todo for touch screen page title
//    private void addPaneListener() {
//        mapPane.parentProperty().addListener(new ChangeListener<Parent>() {
//            @Override
//            public void changed(ObservableValue<? extends Parent> observable, Parent oldValue, Parent newValue) {
//                setPaneTitle();
//                mapPane.parentProperty().removeListener(this);
//            }
//        });
//    }


    private void setStageTitle() {
        mapStage.setTitle("Map");
    }

//    private void setPaneTitle() {
//        mapPane.setText("Map");
//    }
}