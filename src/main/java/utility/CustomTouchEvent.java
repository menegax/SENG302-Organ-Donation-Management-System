package utility;

import javafx.event.EventTarget;
import javafx.geometry.Point2D;

import java.sql.Timestamp;

/**
 * Custom touch event for multi-touch functionality in one pane.
 * Each touch event has a random, unique id for identification
 */
class CustomTouchEvent {

    /**
     * Unique id for this event
     */
    private int id;

    /**
     * Coordinates of touch point on screen
     */
    private Point2D coordinates;

    /**
     * Target of touch event
     */
    private EventTarget target;

    /**
     * The system time the event took place (nanoseconds)
     */
    private long eventTime;

    /**
     * True if the event of this id has moved
     */
    private boolean hasMoved = false;

    /**
     * Creates a new CustomTouchEvent with the root pane given
     */
    public CustomTouchEvent(int id, EventTarget target) {
        this.id = id;
        this.target = target;
    }

    public Point2D getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Point2D coordinates) {
        this.coordinates = coordinates;
    }


    public int getId() {
        return id;
    }

    public EventTarget getTarget() {
        return target;
    }

    public long getEventTime() {
        return eventTime;
    }

    public void setEventTime(long eventTime) {
        this.eventTime = eventTime;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    public boolean isHasMoved() {
        return hasMoved;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof CustomTouchEvent) {
            if(this.id == ((CustomTouchEvent) obj).getId()) {
                if(this.coordinates.equals(((CustomTouchEvent) obj).getCoordinates())) {
                    return true;
                }
            }
        }
        return false;
    }
}
