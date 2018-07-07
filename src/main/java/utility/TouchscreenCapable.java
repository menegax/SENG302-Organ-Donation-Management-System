package utility;

import javafx.scene.input.RotateEvent;
import javafx.scene.input.ZoomEvent;

public interface TouchscreenCapable {

    void zoomWindow(ZoomEvent zoomEvent);

    void rotateWindow(RotateEvent rotateEvent);

}
