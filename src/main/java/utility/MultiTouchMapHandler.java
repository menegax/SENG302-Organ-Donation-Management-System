package utility;

import controller.GUIMap;
import javafx.event.Event;
import javafx.geometry.Point2D;
import javafx.scene.input.RotateEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.web.WebView;

public class MultiTouchMapHandler extends MultiTouchHandler {

    private Double originalDistance;
    
    private double MAPZOOMFACTOR = 0.3;

    @Override
    protected void processPaneMomentum() {
        //do nothing
    }

    public void initialiseHandler(WebView webViewMap1) {
//        ZOOMFACTOR = 0.3;
        webViewMap1.addEventFilter(TouchEvent.ANY, this::handleTouch);
        webViewMap1.addEventFilter(ZoomEvent.ANY, Event::consume);
        webViewMap1.addEventFilter(RotateEvent.ANY, Event::consume);

//        webViewMap1.setOnTouchReleased((event -> {
//            originalDistance = null;
//            //jsBridge.call("setJankaOriginal", null);
//        }));
//
//        webViewMap1.setOnTouchMoved((event -> {
//            if (event.getTouchCount() == 2) {
//                if(event.getTouchPoints().get(0).getTarget().equals(webViewMap1) &&
//                        event.getTouchPoints().get(1).getTarget().equals(webViewMap1)) {
//                    Point2D touchOne = new Point2D(event.getTouchPoints().get(0).getX(),
//                            event.getTouchPoints().get(0).getY());
//                    Point2D touchTwo = new Point2D(event.getTouchPoints().get(1).getX(),
//                            event.getTouchPoints().get(1).getY());
//                    if (originalDistance == null) {
//                        originalDistance = Math.sqrt(Math.pow(touchOne.getX() - touchTwo.getX(), 2) +
//                                Math.pow(touchOne.getY() - touchTwo.getY(), 2));
//                        GUIMap.getJSBridge().call("setJankaOriginal");
//                    }
//                    double currentDistance = Math.sqrt(Math.pow(touchOne.getX() - touchTwo.getX(), 2) +
//                            Math.pow(touchOne.getY() - touchTwo.getY(), 2));
//                    GUIMap.getJSBridge().call("setJankaZoom", Math.pow(currentDistance / originalDistance, MAPZOOMFACTOR));
//                }
//            }
//        }));
    }

    @Override
    protected void setPaneFocused() {
        //do nothing
    }

    @Override
    protected void processTwoTouchMovement(CustomTouchEvent previousEvent, CustomTouchEvent currentEvent) throws NullPointerException {
    	Point2D touchOne = previousEvent.getCoordinates();
        Point2D touchTwo = currentEvent.getCoordinates();
        if (originalDistance == null) {
            originalDistance = Math.sqrt(Math.pow(touchOne.getX() - touchTwo.getX(), 2) +
                Math.pow(touchOne.getY() - touchTwo.getY(), 2));
        GUIMap.getJSBridge().call("setJankaOriginal");
        }
        double currentDistance = Math.sqrt(Math.pow(touchOne.getX() - touchTwo.getX(), 2) +
            Math.pow(touchOne.getY() - touchTwo.getY(), 2));
        GUIMap.getJSBridge().call("setJankaZoom", Math.pow(currentDistance / originalDistance, MAPZOOMFACTOR));
    }
    
    @Override
    protected void checkTouchRelease(CustomTouchEvent touchEvent, TouchEvent event) {
    	originalDistance = null;
    }
    
    @Override
    protected void processOneTouchMovement(CustomTouchEvent previousEvent, CustomTouchEvent currentEvent) {
    	//do noth8ng
    }
    
    @Override
    protected void executeZoom(double distance) {
        if (originalDistance == null) {
            originalDistance = distance;
            GUIMap.getJSBridge().call("setJankaOriginal");
        }
//        double currentDistance = distance;
        GUIMap.getJSBridge().call("setJankaZoom", distance);
    }


}
