package utility;

import controller.GUIMap;
import javafx.event.Event;
import javafx.geometry.Point2D;
import javafx.scene.input.RotateEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.web.WebView;

import java.util.logging.Level;

/**
 * Multi touch handler for the map pane
 */
public class MultiTouchMapHandler extends MultiTouchHandler {

    private WebView webViewMap1;

    private Double originalDistance;
    
    private double MAPZOOMFACTOR = 0.3;

    private static String[] bounds;

    @Override
    protected void processPaneMomentum() {
        //do nothing
    }

    /**
     * Initialises webview multitouch handler by filtering out native zoom and rotate events, and handling touch
     * events
     * @param webViewMap1 map webview
     */
    public void initialiseHandler(WebView webViewMap1) {
        this.webViewMap1 = webViewMap1;
        webViewMap1.addEventFilter(TouchEvent.ANY, this::handleTouch);
        webViewMap1.addEventFilter(ZoomEvent.ANY, Event::consume);
        webViewMap1.addEventFilter(RotateEvent.ANY, Event::consume);
    }

    /**
     * Does not bring map pane to front when tapped
     */
    @Override
    protected void setPaneFocused() {
        //do nothing
    }

    /**
     * Checks what type of movement the touch events represent
     * and performs the appropriate actions for a map pane
     *
     * @param previousEvent the previous touch event before movement
     * @param currentEvent  the current touch event after movement
     */
    @Override
    protected void processEventMovement(CustomTouchEvent previousEvent, CustomTouchEvent currentEvent) {
        int numberOfTouches = 0;
        for (CustomTouchEvent touchEvent : touches) {
            if (touchEvent != null) {
                numberOfTouches += 1;
            }
        }
        if (numberOfTouches == 1) {
            processOneTouchMovement(previousEvent, currentEvent);
        }
        else if (numberOfTouches == 2) {
            processTwoTouchMovement(touches[0], touches[1]);
        }
        touches[findIndexOfTouchEvent(previousEvent.getId())] = currentEvent;
    }

    /**
     * Processes zoom events on map by calling javascript methods to reset zoom based on distance between points
     * @param previousEvent the previous touch event before movement
     * @param currentEvent  the current touch event after movement
     * @throws NullPointerException events are null
     */
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

    /**
     * Sets original distance to null to reset zoom events
     * @param touchEvent custom touch event
     * @param event touch event
     */
    @Override
    protected void checkTouchRelease(CustomTouchEvent touchEvent, TouchEvent event) {
        originalDistance = null;
    }

    /**
     * Processes a translate movement by getting the bounds and then calling calculate distance.
     * Catches NullPointer exception if map is touched before instantiation
     * Catches NumberFormatException if doubles do not parse correctly
     * @param previousEvent previous event
     * @param currentEvent  current event
     */
    @Override
    protected void processOneTouchMovement(CustomTouchEvent previousEvent, CustomTouchEvent currentEvent) {
    	try {
            GUIMap.getJSBridge().call("getMapBounds");
            Double[] NE = {Double.parseDouble(bounds[0]), Double.parseDouble(bounds[1])};
            Double[] SW = {Double.parseDouble(bounds[2]), Double.parseDouble(bounds[3])};
            calculateDistanceChange(NE, SW, previousEvent, currentEvent);
        } catch (NullPointerException e) {
            systemLogger.log(Level.FINE, "Accessed map bounds before map initialised");
        } catch (NumberFormatException e) {
            systemLogger.log(Level.SEVERE, "Coordinates are not numbers");
        }

    }

    /**
     * Calculates change in distance from previous and current events, scales them to the pane and find the
     * difference in lat and long values. This then calls a javascript method to translate the map center
     * by the displacement.
     * @param ne north east corner coordinates
     * @param sw south west corner coordinates
     * @param previous previous touch event
     * @param current current touch event
     */
    private void calculateDistanceChange(Double[] ne, Double[] sw, CustomTouchEvent previous, CustomTouchEvent current) {
        System.out.println(ne[0] + ", " + ne[1]);
        System.out.println(sw[0] + ", " + sw[1]);
        Double width = Math.abs(sw[1]) - Math.abs(ne[1]);
        Double height = ne[0] - sw[0];
        System.out.println(width);
        System.out.println(height);
        Double widthPx = webViewMap1.getWidth();
        Double heightPx = webViewMap1.getHeight();
        Point2D displacement = new Point2D(current.getCoordinates().getX() - previous.getCoordinates().getX(),
                current.getCoordinates().getY() - previous.getCoordinates().getY());
        double displacementRatioX = displacement.getX() / widthPx;
        double displacementRatioY = displacement.getY() / heightPx;
        Point2D newPoint = new Point2D(displacementRatioX * width, displacementRatioY * height);
        System.out.println();
        if(Math.round(width) >= 331 && Math.round(height) >= 10 ) {
            newPoint = new Point2D(0,0);
        }
        GUIMap.getJSBridge().call("translateMap", newPoint.getX(), newPoint.getY());
    }

    /**
     *Sets bounds of map shown
     * @param strings strings of coordinates
     */
    public static void setBounds(String[] strings) {
        bounds = strings;
    }


}
