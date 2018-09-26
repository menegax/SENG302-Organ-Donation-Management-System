package utility;

import com.sun.javafx.scene.control.skin.DatePickerSkin;
import controller.ScreenControl;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import org.tuiofx.widgets.utils.Util;
import tornadofx.control.DateTimePicker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TouchDatePickerSkin extends DatePickerSkin implements ITouchSkin {

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    /**
     * Creates a new skin for a date picker for multi touch purposes.
     * @param datePicker datepicker to skin
     * @param pane pane the datepicker contains
     */
    public TouchDatePickerSkin(DatePicker datePicker, Pane pane) {
        super(datePicker);
        if (screenControl.isTouch()) {
            StackPane arrowButtonAlias = (StackPane) datePicker.lookup(".arrow-button");
            arrowButtonAlias.setStyle("-fx-padding: 0 15 0 15;");
            datePicker.showingProperty().addListener((observable, oldValue, newValue) -> {
                Node owner = getSkinnable();
                double offsetY = (getSkinnable()).prefHeight(-1.0D);
                double angle = Util.getRotationDegreesLocalToScene(owner);
                getPopupContent().getTransforms().setAll(new Rotate(angle));
                Rotate rotate = new Rotate(angle);
                Point2D transformedPoint = rotate.transform(0.0D, offsetY);
                double popupTopLeftX = owner.getLocalToSceneTransform().getTx();
                double popupTopLeftY = owner.getLocalToSceneTransform().getTy();
                double anchorX = popupTopLeftX + transformedPoint.getX() + Util.getOffsetX(owner);
                double anchorY = popupTopLeftY + transformedPoint.getY() + Util.getOffsetY(owner);
                getPopup().setAnchorX(anchorX);
                getPopup().setAnchorY(anchorY);
            });
            getPopup().setAutoHide(false);
            addTouchSkin(pane);
            datePicker.setOnTouchPressed(event -> {
                show();
            });
            datePicker.setOnMouseClicked(event -> {
                show();
            });
        }
    }

    /**
     * Adds this touchSkin to the static map for the provided pane
     * @param pane the pane that should close the datePicker when touched
     */
    private void addTouchSkin(Pane pane) {
        if (TouchSkinsHandler.touchSkins.get(pane) == null) {
            List<ITouchSkin> touchDatePickerSkins = new ArrayList<>();
            touchDatePickerSkins.add(this);
            TouchSkinsHandler.touchSkins.put(pane, touchDatePickerSkins);
            pane.setOnTouchPressed(event -> {
                TouchSkinsHandler.notifyTouchSkins(pane);
            });
            pane.setOnMouseClicked(event -> {
                TouchSkinsHandler.notifyTouchSkins(pane);
            });
        } else {
            TouchSkinsHandler.touchSkins.get(pane).add(this);
        }
    }

    /**
     * Called when the touch pane that this datePicker skin is on is pressed
     * Hides the datePicker
     */
    public void panePressed() {
        hide();
    }
}
