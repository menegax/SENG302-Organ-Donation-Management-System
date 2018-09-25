package utility;

import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;
import controller.ScreenControl;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Rotate;
import org.tuiofx.widgets.skin.MTComboBoxListViewSkin;
import org.tuiofx.widgets.utils.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TouchComboBoxSkin extends ComboBoxListViewSkin {

    private static Map<Pane, List<TouchComboBoxSkin>> touchSkins = new HashMap<>();

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    private Boolean doHide = false;

    private Boolean doShow = false;

    public TouchComboBoxSkin(ComboBox comboBox, Pane pane) {
        super(comboBox);
        if (screenControl.isTouch()) {
            getPopup().setAutoHide(false);
            comboBox.setOnTouchPressed(event -> {
                doShow = true;
                show();
            });
            comboBox.setOnMouseClicked(event -> {
                doShow = true;
                show();
            });
            addTouchSkin(pane);
            comboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                doHide = true;
                hide();
            });
        }
    }

    /**
     * Only hides if we specifically want it to hide through doHide
     */
    @Override
    public void hide() {
        if (doHide) {
            getPopupContent().setVisible(false);
            doHide = false;
        }
    }

    /**
     * Only hides if we specifically want it to hide through doHide
     */
    @Override
    public void show() {
        if (doShow) {
            getPopupContent().setVisible(true);
            doShow = false;
        }
    }

    /**
     * Adds this touchSkin to the static map for the provided pane
     * @param pane the pane that should close the comboBox when touched
     */
    private void addTouchSkin(Pane pane) {
        if (touchSkins.get(pane) == null) {
            List<TouchComboBoxSkin> touchComboBoxSkins = new ArrayList<>();
            touchComboBoxSkins.add(this);
            touchSkins.put(pane, touchComboBoxSkins);
            pane.setOnTouchPressed(event -> {
                notifyTouchSkins(pane);
            });
            pane.setOnMouseClicked(event -> {
                notifyTouchSkins(pane);
            });
        } else {
            touchSkins.get(pane).add(this);
        }
    }

    /**
     * Notifies the touchSkins associated with the pane that the pane was pressed
     * @param pane the pane which was pressed
     */
    private static void notifyTouchSkins(Pane pane) {
        for (TouchComboBoxSkin touchComboBoxSkin : touchSkins.get(pane)) {
            touchComboBoxSkin.panePressed();
        }
    }

    /**
     * Called when the touch pane that this comboBox skin is on is pressed
     * Hides the comboBox
     */
    private void panePressed() {
        if (getPopupContent().isVisible()) {
            doHide = true;
            hide();
        }
    }
}
