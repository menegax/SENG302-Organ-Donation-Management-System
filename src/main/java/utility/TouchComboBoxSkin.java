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

public class TouchComboBoxSkin extends MTComboBoxListViewSkin implements ITouchSkin{

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    private Boolean doHide = false;
    
    private static Map<ComboBox, TouchComboBoxSkin> comboBoxSkins = new HashMap<>();

    public TouchComboBoxSkin(ComboBox comboBox, Pane pane) {
        super(comboBox);
        comboBoxSkins.put(comboBox, this);
        ((MTComboBoxListViewSkin) comboBox.getSkin()).getPopupContent().setVisible(false);
        if (screenControl.isTouch()) {
            getPopup().setAutoHide(false);
            comboBox.setOnTouchPressed(event -> {
                showDropDown();
            });
            comboBox.setOnMouseClicked(event -> {
                showDropDown();
            });
            addTouchSkin(pane);
            comboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                doHide = true;
                hide();
            });
        }
    }

    public static TouchComboBoxSkin getSkin(ComboBox comboBox) {
    	return comboBoxSkins.get(comboBox);
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

    public void showDropDown() {
        show();
        getPopupContent().setVisible(true);
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
    }

    /**
     * Adds this touchSkin to the static map for the provided pane
     * @param pane the pane that should close the comboBox when touched
     */
    private void addTouchSkin(Pane pane) {
        if (TouchSkinsHandler.touchSkins.get(pane) == null) {
            List<ITouchSkin> touchComboBoxSkins = new ArrayList<>();
            touchComboBoxSkins.add(this);
            TouchSkinsHandler.touchSkins.put(pane, touchComboBoxSkins);
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
     * Called when the touch pane that this comboBox skin is on is pressed
     * Hides the comboBox
     */
    public void panePressed() {
        if (getPopupContent().isVisible()) {
            doHide = true;
            hide();
        }
    }
}
