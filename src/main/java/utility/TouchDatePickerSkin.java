package utility;

import com.sun.javafx.scene.control.skin.DatePickerSkin;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import org.tuiofx.widgets.skin.MTComboBoxListViewSkin;
import org.tuiofx.widgets.utils.Util;

public class TouchDatePickerSkin extends DatePickerSkin {

    /**
     * Creates a new skin for a date picker for multi touch purposes.
     * @param datePicker datepicker to skin
     * @param pane pane the datepicker contains
     */
    public TouchDatePickerSkin(DatePicker datePicker, Pane pane) {
        super(datePicker);
        StackPane arrowButtonAlias = (StackPane) datePicker.lookup(".arrow-button");
        arrowButtonAlias.setStyle("-fx-padding: 0 15 0 15;");
        getPopupContent().focusedProperty().addListener((observable, oldValue, newValue) -> {
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
        pane.setOnTouchPressed(event -> {
            hide();
        });
        pane.setOnMouseClicked(event -> {
            hide();
        });
        datePicker.setOnTouchPressed(event -> {
            show();
        });
        datePicker.setOnMouseClicked(event -> {
            show();
        });
    }
}
