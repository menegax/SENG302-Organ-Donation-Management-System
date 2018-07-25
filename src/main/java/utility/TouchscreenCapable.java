package utility;

import javafx.scene.input.RotateEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.input.ZoomEvent;

public interface TouchscreenCapable {

    void zoomWindow(ZoomEvent zoomEvent);

    void rotateWindow(RotateEvent rotateEvent);

    void scrollWindow(ScrollEvent scrollEvent);

}
