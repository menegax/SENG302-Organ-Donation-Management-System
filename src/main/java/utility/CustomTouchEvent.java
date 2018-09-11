package utility;

import javafx.geometry.Point2D;

/**
 * Custom touch event for multi-touch functionality in one pane.
 * Each touch event has a random, unique UUID for identification
 */
class CustomTouchEvent {

    private int id;
    private Point2D coordinates;
    private Point2D originCoordinates = null;

    /**
     * Creates a new CustomTouchEvent with the root pane given
     */
    public CustomTouchEvent(int id) {
        this.id = id;
    }

    public Point2D getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Point2D coordinates) {
        this.coordinates = coordinates;
        if(this.originCoordinates == null) {
            this.originCoordinates = coordinates;
        }
    }

    public Point2D getOriginCoordinates() {
        return originCoordinates;
    }

    public void setOriginCoordinates(Point2D originCoordinates) {
        this.originCoordinates = originCoordinates;
    }

    public int getId() {
        return id;
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
