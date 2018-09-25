package utility;

import javafx.scene.layout.Pane;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TouchSkinsHandler {

    public static Map<Pane, List<ITouchSkin>> touchSkins = new HashMap<>();

    /**
     * Notifies the touchSkins associated with the pane that the pane was pressed
     * @param pane the pane which was pressed
     */
    public static void notifyTouchSkins(Pane pane) {
        for (ITouchSkin touchSkin : touchSkins.get(pane)) {
            touchSkin.panePressed();
        }
    }

}
