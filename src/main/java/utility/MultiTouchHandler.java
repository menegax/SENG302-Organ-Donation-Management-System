package utility;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.input.RotateEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.Pane;

import javafx.geometry.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    private Pane rootPane;
    /**
     * List of touch events on the pane. Max of three events per pane.
     */
    private List<CustomTouchEvent> touches = new ArrayList<>();

    private int MAXTOUCHESPERPANE = 3;

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

        rootPane.addEventFilter(TouchEvent.ANY, event -> {
            if(touches.size() == 0) {
                handleSingleTouch(event);
            } else {
                handleMultipleTouches(event);
            }
        });
        rootPane.addEventFilter(ZoomEvent.ANY, Event::consume);
        rootPane.addEventFilter(RotateEvent.ANY, Event::consume);
        rootPane.addEventFilter(ScrollEvent.ANY, Event::consume);
    }

    /**
     *
     * @param event touch event
     */
    private void handleMultipleTouches(TouchEvent event) {
        CustomTouchEvent touchEvent = new CustomTouchEvent(rootPane, event.getTouchPoint().getId());
        Point2D coordinates = new Point2D(event.getTouchPoint().getScreenX(), event.getTouchPoint().getScreenY());
        touchEvent.setCoordinates(coordinates);

        //Assign id based on what touches are registered in the current pane
        CustomTouchEvent previousEvent = null;
        for (CustomTouchEvent customTouchEvent : touches) {
            if (customTouchEvent.getId() == touchEvent.getId()) {
                previousEvent = customTouchEvent;
            }
        }

        if (previousEvent == null && touchEvent.getId() <= 10 && event.getEventType().equals(TouchEvent.TOUCH_PRESSED)) {
            setPaneFocused();
            this.touches.add(touchEvent);
            System.out.println(touchEvent.getId() + ", " + touchEvent.getCoordinates());
        } else {
            if (event.getEventType().equals(TouchEvent.TOUCH_RELEASED)) {
                checkLeftClick();
                this.touches.remove(previousEvent);
            } else if (event.getEventType().equals(TouchEvent.TOUCH_MOVED) && !(isNegligableMovement(touchEvent, previousEvent))) {
                processEventMovement();
            } else {
                checkRightClick();
            }
        }
    }

    /**
     * Brings the pane of this touch handler to the front
     */
    private void setPaneFocused() {

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
     *  and performs the appropriate actions
     */
    private void processEventMovement() {

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
     * Handles a single new touch point
     * @param event touch event
     */
    private void handleSingleTouch(TouchEvent event) {
        CustomTouchEvent touchEvent = new CustomTouchEvent(rootPane, event.getTouchPoint().getId());
        Point2D coordinates = new Point2D(event.getTouchPoint().getScreenX(), event.getTouchPoint().getScreenY());
        touchEvent.setCoordinates(coordinates);
        this.touches.add(touchEvent);
        System.out.println(touchEvent.getId());
        System.out.println(touchEvent.getCoordinates());
    }


}