package service;

import com.sun.javafx.webkit.WebConsoleListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import utility.JSInjector;

import java.net.URL;
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

    @FXML
    private GridPane mapPane;

    private WebEngine webEngine1;

    private JSObject jsBridge1;

    private JSInjector jsInjector;

    private Stage mapStage;


    /**
     * Initialises the widgets and bridge in the google map
     *
     * @param url Required parameter that is not used
     * @param rb  Required parameter that is not used
     */
    public void initialize(URL url, ResourceBundle rb) {
        webEngine1 = webViewMap1.getEngine();

        webEngine1.setJavaScriptEnabled(true);
        webEngine1.getLoadWorker()
                .stateProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (Worker.State.SUCCEEDED == newValue) {
                        jsBridge1 = (JSObject) webEngine1.executeScript("window");
                        jsInjector = new JSInjector();
                        jsBridge1.setMember("jsInjector", jsInjector);
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

        webViewMap1.setOnZoom(event -> webViewMap1.setZoom(webViewMap1.getZoom() * event.getZoomFactor()));
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