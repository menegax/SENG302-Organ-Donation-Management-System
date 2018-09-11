package utility;

import javafx.event.Event;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.input.RotateEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.Pane;

import javafx.geometry.Point2D;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handler class to deal with multiple users interacting with the application via touch events.
 * If a single pane isn't at 3 yet, but there are 10 touches on screen, no more gestures will be registered due to
 * hardware limitations.
 * Tap brings into focus
 * Tap - left click
 * Tap and hold - right click on release
 * One finger drag - move pane
 * Two finger pinch - zoom
 * Two finger rotate - rotate
 */
public class MultiTouchHandler {


    private Logger systemLogger = SystemLogger.systemLogger;

    /**
     * Root pane handled
     */
    private Pane rootPane;
    /**
     * Max number of touch events allowed on the root pane
     */
    private int MAXTOUCHESPERPANE = 2;

    private final double DEGREES45 = Math.PI / 4;
    private final double DEGREES135 = Math.PI - DEGREES45;
    private final double DEGREES180 = Math.PI;
    private final double RADS2DEGREES = 180 / Math.PI;

    //todo check against screen resolution
    private final double ZOOMFACTOR = 0.005;


    /**
     * List of touch events on the pane.
     */
    private CustomTouchEvent[] touches = new CustomTouchEvent[MAXTOUCHESPERPANE];

    private Point2D[] originCoordinates = new Point2D[MAXTOUCHESPERPANE];

    /**
     * Initialises a new MultiTouchHandler instance
     */
    public MultiTouchHandler() {

    }

    /**
     * Initialises event filters and sets the root pane
     * @param rootPane Pane for touch handler
     */
    public void initialiseHandler(Pane rootPane) {
        this.rootPane = rootPane;

        rootPane.addEventFilter(TouchEvent.ANY, this::handleTouch);
        rootPane.addEventFilter(ZoomEvent.ANY, Event::consume);
        rootPane.addEventFilter(RotateEvent.ANY, Event::consume);
//        rootPane.addEventFilter(ScrollEvent.ANY, Event::consume);
    }

    /**
     *
     * @param event touch event
     */
    private void handleTouch(TouchEvent event) {
        CustomTouchEvent touchEvent = new CustomTouchEvent(event.getTouchPoint().getId(), event.getTarget());
        Point2D coordinates = new Point2D(event.getTouchPoint().getScreenX(), event.getTouchPoint().getScreenY());
        touchEvent.setCoordinates(coordinates);

        //Assign id based on what touches are registered in the current pane
        CustomTouchEvent previousEvent = null;
        if (findIndexOfTouchEvent(touchEvent.getId()) != -1) {
            previousEvent = touches[findIndexOfTouchEvent(touchEvent.getId())];
        }

        if (previousEvent == null && touchEvent.getId() <= 10 &&
                notMaxTouches() && event.getEventType().equals(TouchEvent.TOUCH_PRESSED)) {
            setPaneFocused();
            addTouchEvent(touchEvent);
        } else {
            if (previousEvent != null && event.getEventType().equals(TouchEvent.TOUCH_RELEASED)) {
                checkLeftClick();
                originCoordinates[findIndexOfTouchEvent(touchEvent.getId())] = null;
                touches[findIndexOfTouchEvent(touchEvent.getId())] = null;
            } else if (previousEvent != null && event.getEventType().equals(TouchEvent.TOUCH_MOVED) && !(isNegligableMovement(touchEvent, previousEvent))) {
                processEventMovement(previousEvent, touchEvent);
            } else {
                checkRightClick();
            }
        }
    }

    /**
     * Returns true if any index of the touches array is null (i.e. not all touches are used)
     * @return boolean max touches made
     */
    private boolean notMaxTouches() {
        for(int i = 0; i < MAXTOUCHESPERPANE; i++) {
            if (touches[i] == null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Finds the index of a touch event with the same id as the provided id
     * @param id the id of the touch event to find
     * @return the index of the event in the touches array or -1 if not found
     */
    private int findIndexOfTouchEvent(int id) {
        for (int i = 0; i < MAXTOUCHESPERPANE; i++) {
            if (touches[i] != null && touches[i].getId() == id) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Brings the pane of this touch handler to the front
     */
    private void setPaneFocused() {
        rootPane.toFront();
    }

    /**
     * Evaluates whether the current states of the touch events meet right click requirements
     */
    private void checkRightClick() {

    }

    /**
     * Evaluates whether the current states of touch events meet left click requirements
     */
    private void checkLeftClick() {

    }

    /**
     * Checks what type of movement the touch events represent
     * and performs the appropriate actions
     * @param previousEvent the previous touch event before movement
     * @param currentEvent the current touch event after movement
     */
    private void processEventMovement(CustomTouchEvent previousEvent, CustomTouchEvent currentEvent) {
        int numberOfTouches = 0;
        for (CustomTouchEvent touchEvent : touches) {
            if (touchEvent != null) {
                numberOfTouches += 1;
            }
        }
        if (numberOfTouches == 1) {
            processOneTouchMovement(previousEvent, currentEvent);
        } else if (numberOfTouches == 2) {
            processTwoTouchMovement(previousEvent, currentEvent);
        }
        touches[findIndexOfTouchEvent(previousEvent.getId())] = currentEvent;
    }

    /**
     * Processes single touch events to translate or scroll on the pane
     * @param previousEvent previous event
     * @param currentEvent current event
     */
    private void processOneTouchMovement(CustomTouchEvent previousEvent, CustomTouchEvent currentEvent) {
        if (!(currentEvent.getTarget() instanceof ListView) && !(currentEvent.getTarget() instanceof TableView)) {
            executeTranslate(previousEvent, currentEvent);
        } else {
            executeScroll(previousEvent, currentEvent);
        }
    }

    private void executeScroll(CustomTouchEvent previousEvent, CustomTouchEvent currentEvent) {
    }

    /**
     * Translates the pane by the difference in coordinates between the current and previous event
     * @param previousEvent previous event
     * @param currentEvent current event
     */
    private void executeTranslate(CustomTouchEvent previousEvent, CustomTouchEvent currentEvent) {
        Point2D delta = currentEvent.getCoordinates().subtract(previousEvent.getCoordinates());
        rootPane.setTranslateX(rootPane.getTranslateX() + delta.getX());
        rootPane.setTranslateY(rootPane.getTranslateY() + delta.getY());
    }


    /**
     * Finds the best fit for a two touch movement and executes it
     * @param previousEvent the previous touch event before movement
     * @param currentEvent the current touch event after movement
     * @throws NullPointerException should not occur (less than two touches)
     */
    private void processTwoTouchMovement(CustomTouchEvent previousEvent, CustomTouchEvent currentEvent) throws NullPointerException {
        int movingPointIndex = findIndexOfTouchEvent(currentEvent.getId());
        CustomTouchEvent stationaryPoint = null;
        for (int i = 0; i < MAXTOUCHESPERPANE; i++) {
            if (touches[i] != null && i != movingPointIndex) {
                stationaryPoint = touches[i];
            }
        }
        if (stationaryPoint != null) {
            double angle = calculateAngle(stationaryPoint.getCoordinates(), previousEvent.getCoordinates(), currentEvent.getCoordinates());
            double displacement = calculateDisplacement(previousEvent.getCoordinates(), currentEvent.getCoordinates());
            if (Math.abs(angle) > DEGREES45 && Math.abs(angle) <= DEGREES135) {
                double rotatedAngle = calculateAngle(previousEvent.getCoordinates(), stationaryPoint.getCoordinates(), currentEvent.getCoordinates());
                executeRotate(DEGREES180 - rotatedAngle);
            } else {
                double distance = (Math.cos(angle))*displacement;
                executeZoom(distance);
            }

        } else {
            systemLogger.log(Level.SEVERE, "Two touch movement processed with less than two touches", "Attempted to process a two touch movement with less than two touches");
            throw new NullPointerException();
        }
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

    /**
     * executes a zoom action proportional to the provide distance
     * @param distance the distance moved by the touch gesture in the relevant direction
     */
    private void executeZoom(double distance) {
        if (rootPane.getScaleX() > 0.25 && rootPane.getScaleY() > 0.25) {
            rootPane.setScaleX(rootPane.getScaleX() + (distance * ZOOMFACTOR));
            rootPane.setScaleY(rootPane.getScaleX() + (distance * ZOOMFACTOR));
        } else if (distance > 0) {
            rootPane.setScaleX(rootPane.getScaleX() + (distance * ZOOMFACTOR));
            rootPane.setScaleY(rootPane.getScaleX() + (distance * ZOOMFACTOR));
        }
    }

    /**
     * Executes a rotate on the pane to the amount specified by the angle given
     * @param angle the angle to rotate given in radians
     */
    private void executeRotate(double angle) {
        rootPane.setRotate(rootPane.getRotate() + angle * RADS2DEGREES);
    }

    /**
     * Checks if the difference location of the new event compared to the old event is less than 2 pixels, and so is the same event,
     * and returns true if that is the case
     * @param newEvent new event
     * @param oldEvent old event
     * @return boolean movement less than 2 pixels
     */
    private boolean isNegligableMovement(CustomTouchEvent newEvent, CustomTouchEvent oldEvent) {
        Point2D delta = newEvent.getCoordinates().subtract(oldEvent.getCoordinates());
        return Math.sqrt(delta.getX() * delta.getX() + delta.getY() * delta.getY()) < 2;
    }


    /**
     * Adds a touch event to the array at the first null index
     * @param touchEvent the touch event to add to the touches array
     */
    private void addTouchEvent(CustomTouchEvent touchEvent) {
        for (int i = 0; i < MAXTOUCHESPERPANE; i++) {
            if (touches[i] == null) {
                this.touches[i] = touchEvent;
                this.originCoordinates[i] = touchEvent.getCoordinates();
                i = MAXTOUCHESPERPANE;
            }
        }
    }

}