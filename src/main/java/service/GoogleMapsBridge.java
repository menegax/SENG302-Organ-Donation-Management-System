package service;

import com.sun.javafx.webkit.WebConsoleListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Bridge for the Google Maps window that holds the buttons' functions
 * and the javascript bridge
 * @author Joshua Meneghini
 */
public class GoogleMapsBridge implements Initializable{

    @FXML private WebView webViewMap1;

    private WebEngine webEngine1;
    /**
     * Initialises the widgets and bridge in the google map
     * @param url Required parameter that is not used
     * @param rb Required parameter that is not used
     */
    public void initialize(URL url, ResourceBundle rb) {
        webEngine1 = webViewMap1.getEngine();

        webEngine1.setJavaScriptEnabled(true);
        webViewMap1.setOnDragDetected(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent t) {
                // intentionally do nothing
            }
        });
        webEngine1.load(getClass().getClassLoader().getResource("HTML/GoogleMap.html").toExternalForm());

        WebConsoleListener.setDefaultListener(new WebConsoleListener() {
            @Override
            public void messageAdded(WebView webView, String message, int lineNumber, String sourceId) {
            }
        });
    }
}