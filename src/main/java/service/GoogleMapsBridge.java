package service;

import com.sun.javafx.webkit.WebConsoleListener;
import javafx.concurrent.Worker;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
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

    private WebEngine webEngine1;
    private JSObject jsBridge1;
    private JSInjector jsInjector;


    /**
     * Initialises the widgets and bridge in the google map
     *
     * @param url Required parameter that is not used
     * @param rb  Required parameter that is not used
     */
    public void initialize(URL url, ResourceBundle rb) {
        webEngine1 = webViewMap1.getEngine();

        webEngine1.setJavaScriptEnabled(true);
        webEngine1.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
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
    }
}