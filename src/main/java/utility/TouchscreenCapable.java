package utility;

import javafx.scene.input.RotateEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;

/**
 * Interface for touch events
 */
public interface TouchscreenCapable {

    /**
     * Zoom in and out of a window
     * @param zoomEvent zoom event
     */
    void zoomWindow(ZoomEvent zoomEvent);

    /**
     * Rotate a window
     * @param rotateEvent rotate event
     */
    void rotateWindow(RotateEvent rotateEvent);

    /**
     * Scroll up, down, left and right in a window
     * @param scrollEvent scroll event
     */
    void scrollWindow(ScrollEvent scrollEvent);

}
